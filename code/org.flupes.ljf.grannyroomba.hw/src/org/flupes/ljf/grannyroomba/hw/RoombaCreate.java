package org.flupes.ljf.grannyroomba.hw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.flupes.ljf.grannyroomba.ByteUtils;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class RoombaCreate extends SerialIoioRoomba {

	public enum DataType {
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

	private final boolean debug_serial = false;

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
	 * 1(head) + 1(count) + 12(ids) + 18(data) + 1(checksum) = 33 < 86
	 * 
	 * Sensors Values: 5 + 10 -> total=48
	 * 
	 */
	static private final int MAX_MSG_SIZE = 92;

	static private final int CMD_DEMO = 136;
	static private final int CMD_STREAM = 148;
	static private final int CMD_TOGGLESTREAM = 150;

	/** Header maker starting a Roomba Create telemetry message */
	static private final int TELEM_MSG_HEADER = 19;

	private Map<SensorPackets, Integer> m_telemetry;
	private final int m_msgSize;
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

	/**
	 * Returns the total length of a telemetry message, including header, byte
	 * count, and checksum.
	 * @return
	 */
	private int telemetryMessageLength() {
		return numberOfSensorPackets()+sizeOfSensorsData()+3;
	}

	public RoombaCreate(IOIO ioio) {
		super(ioio);
		m_telemetry = new EnumMap<SensorPackets, Integer>(SensorPackets.class);
		m_msgSize = telemetryMessageLength();

		s_logger.info("Opening communication with a Roomba Create");
		s_logger.debug("Telemetry:"
				+ "\n    sensor packets = " + numberOfSensorPackets()
				+ "\n    sensor data size = " + sizeOfSensorsData()
				+ "\n    total message length = " + telemetryMessageLength());

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

	public void demo(int d) throws ConnectionLostException {
		writeByte( CMD_DEMO );
		writeByte( d );
	}

	public void printRawTelemetry() {
		if ( m_telemetry.size() > 0 ) {
			for ( Entry<SensorPackets, Integer> e : m_telemetry.entrySet() ) {
				System.out.println(e.getKey().name+" = "+e.getValue());
			}
		}
		else {
			System.out.println("No telemetry has been received!");
		}
	}

	private class TelemetryListening implements Runnable {

		protected Deque<Integer> m_message;
		protected byte[] m_buffer;
		protected ByteArrayInputStream m_input;

		protected TelemetryListening() {
			// Queue to store a message in construction
			m_message = new ArrayDeque<Integer>(MAX_MSG_SIZE);
			// Byte buffer equivalent of the message
			m_buffer = new byte[telemetryMessageLength()];
			// Stream on the byte buffer
			m_input = new ByteArrayInputStream(m_buffer, 0, telemetryMessageLength());
		}

		private boolean checksumOk(Deque<Integer> q) {
			// Note: the documentation is wrong: the checksum also
			// includes the message header (19)
			byte checksum = TELEM_MSG_HEADER;
			for ( Iterator<Integer> it = q.iterator(); it.hasNext(); ) {
				checksum += it.next().byteValue();
			}
			return ( (checksum & 0xFF) == 0); 
		}

		protected int processMessage() {
			int processed = 0;
			m_input.reset();
			int numBytes = ByteUtils.readByte(m_input);
			if ( numBytes == 0 ) {
				s_logger.trace("received empty telemetry message!");
				// Note: This looks like an error, but I think it is Roomba's fault
				// not a bug in this code (how presumptuous;-)
				// Indeed, to get here we had to 1) receive a telemetry header and 
				// 2) receive a correct checksum... Just nothing was sent in
				// between!
				return processed;
			}
			if ( numBytes != (numberOfSensorPackets()+sizeOfSensorsData()) ) {
				s_logger.error("telemetry message does not have the expected length "
						+numBytes+" vs. "+numberOfSensorPackets()+sizeOfSensorsData());
				return -1;
			}
			for ( SensorPackets p : SensorPackets.values() ) {
				int id = ByteUtils.readByte(m_input);
				if ( id != p.id ) {
					s_logger.error("sensor packet id does not match "
							+id+" vs. "+p.id);
					return -1;
				}
				if ( DataType.BYTE == p.type ) {
					m_telemetry.put(p, ByteUtils.readByte(m_input));
					processed += 1;
				}
				else if ( DataType.SIGNED_WORD == p.type ) {
					m_telemetry.put(p, ByteUtils.readSignedWord(m_input));
					processed += 1;
				}
				else if ( DataType.UNSIGNED_WORD == p.type ) {
					m_telemetry.put(p, ByteUtils.readUnsignedWord(m_input));
					processed += 1;
				}
				else {
					s_logger.error("data type mismatch!");
				}
			}
			return processed;
		}

		protected void readSerial() {
			int dataByte;
			boolean msgComplete = true;
			try {
				int bufferedBytes = m_serialReceive.available();
				for ( int i=bufferedBytes; i>0; i--) {
					dataByte = m_serialReceive.read();
					//					s_logger.trace("got: " + dataByte);
					if ( dataByte == -1 ) break;
					if ( m_message.size() > MAX_MSG_SIZE ) {
						s_logger.error("Something went wrong (msg growing too much!");
					}
					if ( (dataByte == TELEM_MSG_HEADER) && msgComplete ) {
						m_message.clear();
						msgComplete = false;
					}
					else {
						m_message.add(dataByte);
						if ( m_message.size() == m_msgSize-1) {
//							if ( debug_serial ) {
//								if ( s_logger.isTraceEnabled() ) {
//									String str = new String("MSG: ");
//									for ( Iterator<Integer> it = m_message.iterator(); 
//											it.hasNext(); ) {
//										str += it.next() + ", ";
//									}
//									s_logger.trace(str);
//								}
//							}
							// we did not insert the header, hence the -1
							if ( checksumOk(m_message) ) {
								if ( debug_serial )
									s_logger.trace("We have a new valid message!");
								// Copy to a byte buffer
								/*
								int b=0;
								for ( Iterator<Integer> it = m_message.iterator(); 
										it.hasNext(); b++) {
									m_buffer[b] = it.next().byteValue();
								}
								processMessage();
								 */
								msgComplete=true;
							}
							else {
								//								s_logger.trace("Not the right message header...");
								// this was not a real header, drop
								// the bytes until the next potential start
								Integer b;
								do {
									b = m_message.pollFirst();
									if ( b == null ) break;
								} while ( (b != TELEM_MSG_HEADER) ); 
							}
						}
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		@Override
		public void run() {
			s_logger.info("Telemetry Thread Started");

			while ( !m_exec.isShutdown() ) {
				if ( m_serialReceive == null ) {
					// Telemetry thread is started at construction,
					// however serial connection is not available
					// until "connect" is called... In this case,
					// just wait and do not process anything!
					delay(10);
				}
				else {
					readSerial();
					// Sleep a little bit (less than 15ms ?)
					delay(1);
				}
			}
			s_logger.info("Exiting Telemetry Thread.");
		}

	}
}
