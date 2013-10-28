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

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;

/** Implementation of the Roomba OI for the Series 400
 * 
 * @warning This class is very incomplete and has never been tested
 * since I could never initiate communication with my Roomba Discovery!
 */
public class RoombaSeries400 extends SerialIoioRoomba {

	protected static final int DEFAULT_DD_PIN = 12;

	private int m_chargingState;
	private int m_voltage;
	private int m_current;
	private int m_temperature;
	private int m_charge;
	private int m_capacity;

	public RoombaSeries400(IOIO ioio) {
		super(ioio);
		s_logger.info("Opening communication with a Roomba Serie 400");
	}

	public void wakeup(int ddpin) throws ConnectionLostException {
		s_logger.info("Wake up Roomba using DD pin " + ddpin);
		m_deviceDetect = m_ioio.openDigitalOutput(ddpin, false);
		delay(500);
		m_deviceDetect.close();
		delay(2000);		
	}
		
	public void connect(int rxpin, int txpin, int ddpin)
			throws ConnectionLostException {
		wakeup(ddpin);
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
			writeByte( CMD_START );
			m_mode = CtrlModes.PASSIVE;
			delay(100);			
		}
	}
	
	public void power() throws ConnectionLostException {
		writeByte ( CMD_POWER );
		delay(CMD_WAIT_MS);
	}
	
	public void getPowerInfo() throws ConnectionLostException {
		requestSensorGroup(3);
		m_chargingState = readByte();
		m_voltage = readUnsignedWord();
		m_current = readSignedWord();
		m_temperature = readByte();
		m_charge = readUnsignedWord();
		m_capacity = readUnsignedWord();
		s_logger.debug("Charging State: " + m_chargingState);
		s_logger.debug("Voltage (mV): " + m_voltage);
		s_logger.debug("Current (mA): " + m_current);
		s_logger.debug("Temperature (C): " + m_temperature);
		s_logger.debug("Charge (mAh): " + m_charge);
		s_logger.debug("Capacity (mAh): " + m_capacity);
	}

	public void requestSensorGroup(int groupId) throws ConnectionLostException {
		writeByte ( CMD_SENSORS );
		writeByte ( groupId );
		delay(CMD_WAIT_MS);
	}

}
