package org.flupes.ljf.grannyroomba.hw;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class RoombaCreate extends SerialIoioRoomba {

	enum DataType {
		BYTE(1),
		UNSIGNED_WORD(2),
		SIGNED_WORD(2);

		final int size;

		DataType(int size) {
			this.size = size;
		}
	}

	/** Description of the telemetry to be requested */
	enum SensorPackets {
		BUMPS(7, DataType.BYTE, "Bumps and Wheel Drops"),
		IR(17, DataType.BYTE, "IR Byte"),
		BUTTONS(18, DataType.BYTE, "Buttons"),
		DISTANCE(19, DataType.SIGNED_WORD, "Distance"),
		ANGLE(20, DataType.SIGNED_WORD, "Angle"),
		CHARGING(21, DataType.BYTE, "Charging State"),
		VOLTAGE(22, DataType.UNSIGNED_WORD, "Voltage"),
		CURRENT(23, DataType.SIGNED_WORD, "Current"),
		TEMPERATURE(24, DataType.BYTE, "Battery Temperature"),
		CHARGE(25, DataType.UNSIGNED_WORD, "Battery Charge"),
		CAPACITY(26, DataType.UNSIGNED_WORD, "Battery Capcity"),
		OIMODE(35, DataType.BYTE, "OI Mode");

		final int id;
		final int size;
		final DataType type;
		final String name;

		SensorPackets(int id, DataType type, String name) {
			this.id = id;
			this.size = type.size;
			this.type = type;
			this.name = name;
		}
	}

	/*
	 * Data requested:
	 * (full groups 2 + 3 + first of group 1 and 5)
	 * 
	 * 07	Bumps & drops	1
	 * 17	IR Byte			1
	 * 18	Buttons 		1
	 * 19	Distance		2
	 * 20	Angle			2
	 * 21	Charging State	1
	 * 22	Voltage			2
	 * 23	Current			2
	 * 24	Battery Temp.	1
	 * 25	Battery Charge	2
	 * 26	Battery Cap.	2
	 * 35	OI Mode			1
	 * -----------------------
	 * 		12				18
	 * Message Total Length:
	 * 1(head) + 1(count) + 12(ids) + 18(data) + 1(checksum) = 34 < 86
	 * 
	 * Sensors Values: 5 + 10 -> total=49
	 * 
	 */

	static private final int CMD_STREAM = 148;
	static private final int CMD_TOGGLESTREAM = 150;

	/** Header maker starting a Roomba Create telemetry message */
	static private final int TELEM_MSG_HEADER = 19;

	private Map<SensorPackets, Integer> m_telemetry;

	private ExecutorService m_exec;

	/**
	 * Returns the number of different sensor packets requested
	 */
	private int numberOfSensorPackets() {
		return SensorPackets.values().length;
	}

	/**
	 * Returns the number of byte necessary to store the requested
	 * sensor packets. This number does not include the header, packet
	 * ids and checksum.
	 */
	private int sizeOfSensorsData() {
		int sz = 0;
		for ( SensorPackets p : SensorPackets.values() ) {
			sz += p.size;
		}
		return sz;
	}

	private int telemetryMessageLength() {
		return numberOfSensorPackets()+sizeOfSensorsData()+2;
	}

	public RoombaCreate(IOIO ioio) {
		super(ioio);
		m_telemetry = new EnumMap(SensorPackets.class);

		Integer number = 256;
		byte checksum = number.byteValue();
		System.out.println("checksum = " + checksum);
		if ( (checksum & 0xFF) == 0 ) System.out.println(" -> OK");
		else System.out.println(" -> Failed");

		s_logger.info("Opening communication with a Roomba Create");
		s_logger.info("Telemetry contains " + numberOfSensorPackets()
				+ " and the message length is " + telemetryMessageLength()
				+ " bytes");

		// Start a separate thread for telemetry listening
		s_logger.info("Launching Telemetry Thread");
		m_exec = Executors.newSingleThreadExecutor();
		TelemetryListening telem = new TelemetryListening();
		m_exec.execute(telem);
	}

	public void startTelemetry() throws ConnectionLostException {
		writeByte( CMD_STREAM );
		writeByte( numberOfSensorPackets() );
		for ( SensorPackets p : SensorPackets.values() ) {
			writeByte( p.id );
		}
	}

	public void toogleTelemetry(boolean state) throws ConnectionLostException {
		writeByte( CMD_TOGGLESTREAM );
		if ( state ) {
			writeByte( 1 );
		}
		else {
			writeByte( 0 );
		}
	}

	private class TelemetryListening implements Runnable {

		private boolean checksumOk(Deque<Integer> q) {
			byte checksum = 0;
			for ( Iterator<Integer> it = q.iterator(); it.hasNext(); ) {
				checksum = it.next().byteValue();
			}
			return ( (checksum & 0xFF) == 0); 
		}

		@Override
		public void run() {
			s_logger.info("Telemetry Thread Started");

			int dataByte;
			boolean msgComplete = true;
			Deque<Integer> message = new ArrayDeque<Integer>();

			while ( !m_exec.isShutdown() ) {
				if ( m_input == null ) {
					// Telemetry thread is started at construction,
					// however serial connection is not available
					// until "connect" is called... In this case,
					// just wait and do not process anything!
					delay(10);
				}
				else {
					try {
						int bufferedBytes = m_input.available();
						for ( int i=bufferedBytes; i>0; i--) {
							dataByte = m_input.read();
							if ( dataByte == -1 ) break;
							if ( message.size() > 86 ) {
								s_logger.error("Something went wrong (msg growing too much!");
							}
							if ( (dataByte == TELEM_MSG_HEADER) && msgComplete ) {
								message.clear();
								msgComplete = false;
							}
							else {
								message.add(dataByte);
								if ( message.size() == telemetryMessageLength()-1) {
									// we did not insert the header, hence the -1
									if ( checksumOk(message) ) {
										s_logger.debug("We have a message!");
										msgComplete=true;
									}
									else {
										// this was not a real header, drop
										// the byte until the next potential start
									}
								}
							}
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// Sleep a little bit (less than 15ms ?)
					delay(5);
				}
			}
		}

	}
}
