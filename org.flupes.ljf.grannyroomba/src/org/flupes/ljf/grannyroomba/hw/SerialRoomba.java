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

public class SerialRoomba {

	private static final int BAUD_RATE = 57600;
	private static final int CMD_START = 128;
	private static final int CMD_CONTROL = 130;
	private static final int CMD_SAFE = 131;
	private static final int CMD_FULL = 132;
	private static final int CMD_POWER = 133;
	private static final int CMD_SPOT = 134;
	private static final int CMD_DRIVE = 137;
	private static final int CMD_SENSORS = 142;

	private static final int MS_SLEEP_AFTER_CMD = 40;

	private static final int DEFAULT_TX_PIN = 6;
	private static final int DEFAULT_RX_PIN = 7;
	private static final int DEFAULT_DD_PIN = 12;

	private IOIO m_ioio;
	private Uart m_uart;
	private DigitalOutput m_deviceDetect; 
	private InputStream m_input;
	private OutputStream m_output;

	public enum CTRL_MODES { DISCONNECTED, PASSIVE, CONTROL, FULL };
	private CTRL_MODES m_mode;

	private static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	//	public enum CHARCHING_STATES { NOT_CHARGING, CHARGING_RECOVERY, CHARGING, TRICKLE_CHARGE, WAITING, CHARGING_ERROR }
	private int m_chargingState;
	private int m_voltage;
	private int m_current;
	private int m_temperature;
	private int m_charge;
	private int m_capacity;

	public SerialRoomba(IOIO ioio) {
		m_ioio = ioio;
		m_mode = CTRL_MODES.DISCONNECTED;
	}

	private void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void start() throws ConnectionLostException {
		start(DEFAULT_RX_PIN, DEFAULT_TX_PIN, DEFAULT_DD_PIN);
	}

	public void start(int rxpin, int txpin, int ddpin) 
			throws ConnectionLostException {
		if ( m_mode == CTRL_MODES.DISCONNECTED ) {
			s_logger.info("Wake up Roomba using DD pin " + ddpin);
			m_deviceDetect = m_ioio.openDigitalOutput(DEFAULT_DD_PIN, false);
			delay(500);
			m_deviceDetect.close();
			delay(2000);

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
			delay(40);			
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
	
	protected void safe() throws ConnectionLostException {
		writeByte( CMD_SAFE );
		delay(40);
	}
	
	protected void full() throws ConnectionLostException {
		writeByte( CMD_FULL );
		delay(40);
	}

	public void spot() throws ConnectionLostException {
		s_logger.info("Spot Cleaning");
		writeByte( CMD_CONTROL );
		delay(20);
		writeByte( CMD_SPOT );
		delay(20);
	}

	public void power() throws ConnectionLostException {
		writeByte ( CMD_POWER );
		delay(40);
	}

	public void getPowerInfo() throws ConnectionLostException {
		requestSensorGroup(3);
		m_chargingState = readByte();
		m_voltage = readUnsignedWord();
		m_current = readSignedWord();
		s_logger.debug("Charging State: " + m_chargingState);
		s_logger.debug("Voltage (mV): " + m_voltage);
		s_logger.debug("Current (mA): " + m_current);
	}

	public void requestSensorGroup(int groupId) throws ConnectionLostException {
		writeByte ( CMD_SENSORS );
		writeByte ( groupId );
		delay(20);
	}

	protected void writeByte(int b) 
			throws ConnectionLostException {
		try {
//			m_output.write( b & 0xFF );
			m_output.write( b );
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
