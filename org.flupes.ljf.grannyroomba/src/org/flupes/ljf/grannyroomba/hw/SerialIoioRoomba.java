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
	protected static final int CMD_START = 128;
	protected static final int CMD_BAUD = 139;
	protected static final int CMD_CONTROL = 130;
	protected static final int CMD_SAFE = 131;
	protected static final int CMD_FULL = 132;
	protected static final int CMD_POWER = 133;
	protected static final int CMD_SPOT = 134;
	protected static final int CMD_DRIVE = 137;
	protected static final int CMD_SENSORS = 142;

	protected static final int CMD_WAIT_MS = 20;

	protected static final int DEFAULT_TX_PIN = 6;
	protected static final int DEFAULT_RX_PIN = 7;

	protected IOIO m_ioio;
	protected Uart m_uart;
	protected byte[] m_buffer = new byte[128];

	protected DigitalOutput m_deviceDetect; 
	protected InputStream m_input;
	protected OutputStream m_output;

	public enum CtrlModes { DISCONNECTED, PASSIVE, CONTROL, FULL };
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
			m_input = m_uart.getInputStream();
			m_output = m_uart.getOutputStream();

			s_logger.info("Send START to Roomba");
			writeByte( CMD_START );
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

	public void driveForward() throws ConnectionLostException {
		s_logger.info("Drive Forward");
		writeByte( CMD_CONTROL );
		delay(50);

		writeByte( CMD_DRIVE );
		writeByte( 0x00 );
		writeByte( 0xC8 );
		writeByte( 0x80 );
		writeByte( 0x00 );
		writeByte( 0 );
	}

	public void safe() throws ConnectionLostException {
		s_logger.info("Switch to SAFE mode.");
		writeByte( CMD_SAFE );
		delay(CMD_WAIT_MS);
	}

	public void full() throws ConnectionLostException {
		s_logger.info("Switch to FULL mode");
		writeByte( CMD_FULL );
		delay(CMD_WAIT_MS);
	}

	public void spot() throws ConnectionLostException {
		s_logger.info("Spot Cleaning");
		writeByte( CMD_CONTROL );
		delay(CMD_WAIT_MS);
		writeByte( CMD_SPOT );
		delay(CMD_WAIT_MS);
	}

	public void stop() throws ConnectionLostException {
		s_logger.debug("Stop Drive");
		drive(0, 0x8000);
	}
	
	public void drive(int velocity, int radius)
			throws ConnectionLostException {
		s_logger.debug("drive("+velocity+", "+radius+")");
		writeByte( CMD_DRIVE );
		writeWord( velocity );
		writeWord( radius );
		delay(CMD_WAIT_MS);
	}
	
	protected void writeByte(int b) 
			throws ConnectionLostException {
		try {
			m_output.write( b );
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
			m_output.write(m_buffer, 0, 2);
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	protected int readByte() throws ConnectionLostException {
		int b = 0;
		try {
			b = m_input.read();
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
		return b;
	}

	protected int readUnsignedWord() throws ConnectionLostException {
		int u = 0;
		try {
			int high = m_input.read(); 
			int low = m_input.read(); 
			u = (high << 8) | low;
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
		return u;
	}

	protected int readSignedWord() throws ConnectionLostException {
		int s = 0;
		try {
			int high = m_input.read(); 
			int low = m_input.read(); 
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
