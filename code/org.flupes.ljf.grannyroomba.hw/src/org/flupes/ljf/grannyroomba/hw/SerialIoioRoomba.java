/*
 * GrannyRoomba - Telepresence robot based on a Roomba and Android tablet
 * Copyright (C) 2013 Lorenzo Flueckiger
 *
 * This file is part of GrannyRoomba.
 *
 * GrannyRoomba is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GrannyRoomba is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GrannyRoomba.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flupes.ljf.grannyroomba.hw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic class for serial communication with Roomba using a IOIO interface.
 * 
 * This class needs to be extended to provide specific model capabilities.
 * 
 * Note: this class could be easily be abstracted from the IOIO business by
 * just been given an {Input,Output}Stream. However the RoombaSerie400 also
 * uses the IOIO to wake up the Roomba using the Device Detect pin...
 * So let's keeps things minimal for now and keep the dependency on the IOIO.
 *
 */
public abstract class SerialIoioRoomba {

	protected static final int BAUD_RATE = 57600;
	protected static final int CMD_WAIT_MS = 20;

	protected static final int DEFAULT_TX_PIN = 6;
	protected static final int DEFAULT_RX_PIN = 7;

	protected IOIO m_ioio;
	protected Uart m_uart;
	protected byte[] m_buffer = new byte[128];
	protected int m_lastDriveCmd;

	protected DigitalOutput m_deviceDetect; 
	protected volatile InputStream m_serialReceive;
	protected volatile OutputStream m_serialTransmit;

	public enum CtrlModes { DISCONNECTED, PASSIVE, SAFE, FULL };
	protected CtrlModes m_mode;
	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	//	public enum CHARCHING_STATES { NOT_CHARGING, CHARGING_RECOVERY, CHARGING, TRICKLE_CHARGE, WAITING, CHARGING_ERROR }

	public SerialIoioRoomba(IOIO ioio) {
		m_ioio = ioio;
		m_mode = CtrlModes.DISCONNECTED;
	}

	public void connect() throws ConnectionLostException {
		connect(DEFAULT_RX_PIN, DEFAULT_TX_PIN);
	}

	public void connect(int rxpin, int txpin) throws ConnectionLostException {
		if ( m_mode == CtrlModes.DISCONNECTED ) {
			DigitalInput.Spec rxspec = new DigitalInput.Spec(rxpin);
			DigitalOutput.Spec txspec = new DigitalOutput.Spec(txpin, Mode.OPEN_DRAIN);
			s_logger.info("Opening communication with Roomba:\n"
					+ "    RX pin = " + rxspec.pin + " (" + rxspec.mode +")" 
					+ "\n    TX pin = " + txspec.pin + " (" + txspec.mode +")");
			m_uart = m_ioio.openUart(rxspec, txspec,
					BAUD_RATE, Uart.Parity.NONE, Uart.StopBits.ONE);
			m_serialReceive = m_uart.getInputStream();
			m_serialTransmit = m_uart.getOutputStream();

			s_logger.info("Send START to Roomba");
			writeByte( RoombaCmds.CMD_START );
			m_mode = CtrlModes.PASSIVE;
			delay(100);			
		}
	}

	protected void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void safeControl() throws ConnectionLostException {
		s_logger.info("Switch to SAFE mode.");
		m_mode = CtrlModes.SAFE;
		writeByte( RoombaCmds.CMD_SAFE );
		delay(CMD_WAIT_MS);
	}

	public synchronized void fullControl() throws ConnectionLostException {
		s_logger.info("Switch to FULL mode");
		m_mode = CtrlModes.FULL;
		writeByte( RoombaCmds.CMD_FULL );
		delay(CMD_WAIT_MS);
	}

	public synchronized void stop() throws ConnectionLostException {
		s_logger.debug("stop locomotion");
		baseDrive(0, 0x8000);
	}

	public synchronized void baseDrive(int velocity, int radius)
			throws ConnectionLostException {
		s_logger.debug("baseDrive("+velocity+", "+radius+")");
		writeByte( RoombaCmds.CMD_DRIVE );
		writeWord( velocity );
		writeWord( radius );
		m_lastDriveCmd = RoombaCmds.CMD_DRIVE;
	}

	public synchronized void spot() throws ConnectionLostException {
		s_logger.info("Spot Cleaning");
		safeControl();
		writeByte( RoombaCmds.CMD_SPOT );
	}

	public synchronized void leds(int leds, int color, int intensity) throws ConnectionLostException {
		writeByte( RoombaCmds.CMD_LEDS );
		writeByte( leds );
		writeByte( color );
		writeByte( intensity );
	}
	
	protected void writeByte(int b)
			throws ConnectionLostException {
		try {
			m_serialTransmit.write( b );
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	protected void writeWord(int s) 
			throws ConnectionLostException {
		try {
			// Note: Java bytes are signed, 
			// so writing a signed or unsigned word 
			// is equivalent
			m_buffer[0] = (byte) (s >> 8);
			m_buffer[1] = (byte) (s & 0xFF);
			m_serialTransmit.write(m_buffer, 0, 2);
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	protected void writeBytes(byte[] buffer, int size) throws ConnectionLostException {
		try {
			m_serialTransmit.write(buffer, 0, size);
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}

	}

	protected int readByte() throws ConnectionLostException {
		int b = 0;
		try {
			b = m_serialReceive.read();
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
		return b;
	}

	protected int readUnsignedWord() throws ConnectionLostException {
		int u = 0;
		try {
			int high = m_serialReceive.read(); 
			int low = m_serialReceive.read(); 
			u = (high << 8) | low;
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
		return u;
	}

	protected int readSignedWord() throws ConnectionLostException {
		int s = 0;
		try {
			int high = m_serialReceive.read(); 
			int low = m_serialReceive.read(); 
			s = (high << 8) | low;
			if ( s > 0x7FFF) {
				s -= 0x10000;
			}
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
		return s;
	}

}
