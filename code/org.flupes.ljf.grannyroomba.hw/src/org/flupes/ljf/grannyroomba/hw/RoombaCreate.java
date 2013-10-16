package org.flupes.ljf.grannyroomba.hw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.flupes.ljf.grannyroomba.ByteUtils;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class RoombaCreate extends SerialIoioRoomba {

	/** Distance between the 2 motorized wheels */
	public static final float WHEEL_BASE = 0.260f;
	
	/** Maximum linear velocity of each wheel (m/s) */ 
	public static final float MAX_VELOCITY = 0.5f;
	
	/** Larger radius accepted by Roomba (m) */
	public static final float MAX_RADIUS = 2.0f;
	
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
//		IR(17, DataType.BYTE, "IR Byte"),
//		BUTTONS(18, DataType.BYTE, "Buttons"),
//		CHARGING(21, DataType.BYTE, "Charging State"),
//		VOLTAGE(22, DataType.UNSIGNED_WORD, "Voltage"),
//		CURRENT(23, DataType.SIGNED_WORD, "Current"),
//		TEMPERATURE(24, DataType.BYTE, "Battery Temperature"),
		CHARGE(25, DataType.UNSIGNED_WORD, "Battery Charge"),
		CAPACITY(26, DataType.UNSIGNED_WORD, "Battery Capcity"),
		OIMODE(35, DataType.BYTE, "OI Mode"),
		VELOCITY(39, DataType.SIGNED_WORD, "Velocity"),
		RADIUS(40, DataType.SIGNED_WORD, "Radius");
/*
 * Distance and angle were removed from the telemetry stream.
 * Every time these values are requested, they are reset to 0  (even
 * if part stream):
 * - since telemetry is pushed every 15ms, the value are very small and
 * accumulation would be really inncacurate
 * - since telemetry continues while the script are playing, the WAIT_DISTANCE
 * and WAIT_ANGLE methods do not work anymore: the counter never reach the 
 * since they are reset every 15ms
 * 
		DISTANCE(19, DataType.SIGNED_WORD, "Distance"),
		ANGLE(20, DataType.SIGNED_WORD, "Angle"),
*/
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

	protected static final int CMD_DEMO = 136;
	protected static final int CMD_DIRECT = 145;
	protected static final int CMD_STREAM = 148;
	protected static final int CMD_TOGGLESTREAM = 150;
	protected static final int CMD_SCRIPT = 152;
	protected static final int CMD_PLAY_SCRIPT = 153;
	protected static final int CMD_WAIT_TIME = 155;
	protected static final int CMD_WAIT_DISTANCE = 156;
	protected static final int CMD_WAIT_ANGLE = 157;
	protected static final int CMD_WAIT_EVENT = 158;
	
	protected static final int EVENT_NO_CLIFF = 246;
	protected static final int EVENT_NO_BUMPER = 251;
	protected static final int EVENT_NO_WHEELDROP = 255;

	private final boolean debug_serial = true;

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

	/** Header maker starting a Roomba Create telemetry message */
	static private final int TELEM_MSG_HEADER = 19;

	private Map<SensorPackets, Integer> m_telemetry;
	private final int m_msgSize;
	private ExecutorService m_exec;
	private Runnable m_monitor;

	protected Script m_backupScript;
	protected Script m_waitSafeScript;
	
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

		EnumMap<SensorPackets, Integer> telemetry_store = new EnumMap<SensorPackets, Integer>(SensorPackets.class);
		m_telemetry = Collections.synchronizedMap(telemetry_store);

		m_msgSize = telemetryMessageLength();

		s_logger.info("Opening communication with a Roomba Create");
		s_logger.debug("Telemetry:"
				+ "\n    sensor packets = " + numberOfSensorPackets()
				+ "\n    sensor data size = " + sizeOfSensorsData()
				+ "\n    total message length = " + telemetryMessageLength());

		createScripts();
		
		// Start a separate thread for telemetry listening
		s_logger.trace("Launching Telemetry Thread");
		m_exec = Executors.newFixedThreadPool(2);
		m_monitor = new MonitorSafety();
		m_exec.execute(m_monitor);
		TelemetryListening telem = new TelemetryListening();
		m_exec.execute(telem);
	}

	public void directDrive(int leftWheelSpeed, int rightWheelSpeed)
			throws ConnectionLostException {
		s_logger.debug("direcDrive("+leftWheelSpeed+", "+rightWheelSpeed+")");
		writeByte( CMD_DIRECT );
		writeWord( rightWheelSpeed );
		writeWord( leftWheelSpeed );
		delay(CMD_WAIT_MS);
	}

	private void createScripts() {
		m_backupScript = new Script();
		m_backupScript.addByte(CMD_FULL);
		m_backupScript.addByte(CMD_DRIVE).addWord(-100).addWord(0x8000);
		m_backupScript.addByte(CMD_WAIT_EVENT).addByte(EVENT_NO_BUMPER);
//		m_backupScript.addByte(CMD_WAIT_DISTANCE).addWord(-8);
		m_backupScript.addByte(CMD_DRIVE).addWord(0).addWord(0x8000);
		m_backupScript.addByte(CMD_SAFE);
		m_backupScript.close();
		
		m_waitSafeScript = new Script();
		m_waitSafeScript.addByte(CMD_WAIT_EVENT).addByte(EVENT_NO_CLIFF);
		m_waitSafeScript.addByte(CMD_WAIT_EVENT).addByte(EVENT_NO_WHEELDROP);
		m_waitSafeScript.addByte(CMD_SAFE);
		m_waitSafeScript.close();
	}
	
	public int getOiMode() {
		return m_telemetry.get(SensorPackets.OIMODE);
	}

	public int getBumps() {
		return m_telemetry.get(SensorPackets.BUMPS);
	}
	
	public int getVelocity() {
		return m_telemetry.get(SensorPackets.VELOCITY);
	}
	
	public int getRadius() {
		return m_telemetry.get(SensorPackets.RADIUS);
	}
	
	public void startTelemetry() throws ConnectionLostException {
		writeByte( CMD_STREAM );
		writeByte( numberOfSensorPackets() );
		for ( SensorPackets p : SensorPackets.values() ) {
			writeByte( p.id );
		}
	}

	public void toggleTelemetry(boolean state) throws ConnectionLostException {
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

	protected void storeBackupScript() throws ConnectionLostException {
		writeByte( CMD_SCRIPT );
		writeByte( 15 );
		writeByte ( CMD_FULL );
		writeByte( CMD_DRIVE );
		writeWord( 100 );
		writeWord( 0x8000  );
		//		writeByte( CMD_WAIT_EVENT );
		//		writeByte( 251 );
		writeByte( CMD_WAIT_DISTANCE );
		writeWord( 100 );
		//		writeByte( CMD_WAIT_TIME );
		//		writeByte( 150 );
		writeByte( CMD_DRIVE );
		writeWord( 0 );
		writeWord( 0x8000  );
		writeByte( CMD_SAFE );
		delay(CMD_WAIT_MS);
	}

	public void backup() {
			try {
				stop();
				runScript(m_backupScript);
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void waitForSafety() {
		try {
			runScript(m_waitSafeScript);
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printRawTelemetry() {
		synchronized(m_telemetry) {
			if ( m_telemetry.size() > 0 ) {
				for ( Entry<SensorPackets, Integer> e : m_telemetry.entrySet() ) {
					System.out.println(e.getKey().name+" = "+e.getValue());
				}
			}
			else {
				System.out.println("No telemetry has been received!");
			}
		}
	}

	//	@Override
	//	public void drive(int velocity, int radius) throws ConnectionLostException {
	//		if ( m_bumperPushed ) {
	//			if ( velocity<0 && Math.abs(radius) > 0.1 ) {
	//				super.drive(velocity, radius);
	//			}
	//		}
	//		else {
	//			super.drive(velocity, radius);
	//		}
	//	}

	private class Script {

		private final int CAPACITY = 100;
		protected byte[] m_script;
		protected int m_pos;
		protected boolean m_closed;

		Script() {
			m_script = new byte[CAPACITY];
			m_script[0] = 0;
			m_pos = 1;
			m_closed = false;
		}

		public Script addByte(int b) {
			if ( m_closed ) throw new IllegalStateException("script was already closed");
			if ( m_pos < CAPACITY-1 ) {
				m_script[m_pos++] = (byte)b;
			}
			else {
				throw new IndexOutOfBoundsException("script is limited to "+CAPACITY+" bytes");
			}
			return this;
		}

		public Script addWord(int w) {
			if ( m_closed ) throw new IllegalStateException("script was already closed");
			if ( m_pos < CAPACITY-2 ) {
				m_script[m_pos++] = (byte)(w >> 8);
				m_script[m_pos++] = (byte)(w & 0xFF);
			}
			else {
				throw new IndexOutOfBoundsException("script is limited to "+CAPACITY+" bytes");
			}
			return this;
		}

		public int close() {
			m_script[0] = (byte)(m_pos-1);
			m_closed = true;
			return m_pos;
		}
		
		public int length() {
			System.err.println("script length = " + m_pos);
			return m_pos;
		}

		public byte[] buffer() {
			return m_script;
		}
	}

	protected void runScript(Script script) throws ConnectionLostException {
		writeByte(CMD_SCRIPT);
		writeBytes(script.buffer(), script.length());
		delay(CMD_WAIT_MS);
		writeByte(CMD_PLAY_SCRIPT);
	}
	
	private class MonitorSafety implements Runnable {

		MonitorSafety() {
			s_logger.info("MonitoSafety thread created");
		}

		@Override
		public void run() {
			try {
				while ( !m_exec.isShutdown() ) {
					// Block until triggered by the telemetry thread
					synchronized(this) {
						// TODO should change this synchronization method:
						// we do not gain anything since the telemetry reading 
						// thread will block not matter what until 
						// we arrive again at this wait: it does not decouple
						// the telemetry and safety checks!
						wait();
					}
					int bumps;
					int mode;
					synchronized(m_telemetry) {
						bumps = m_telemetry.get(SensorPackets.BUMPS);
						mode = m_telemetry.get(SensorPackets.OIMODE);
					}
					if ( 0 != (bumps&(2+1)) ) {
						if ( mode == 2 ) {
							// run the script if not already running!
							s_logger.warn("bump -> backup");
							backup();
						}
					}
					if ( mode == 1 && m_mode != CtrlModes.PASSIVE ) {
						// Roomba reverted to passive because safe condition
						// was triggered
						m_mode = CtrlModes.PASSIVE;
						s_logger.warn("passive -> wait for safety");;
						waitForSafety();
					}
					if ( mode == 2 && m_mode != CtrlModes.CONTROL) {
						m_mode = CtrlModes.CONTROL;
					}
					if ( mode == 3 && m_mode != CtrlModes.FULL) {
						m_mode = CtrlModes.FULL;
					}
				} // while executor is up
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class TelemetryListening implements Runnable {

		protected byte[] m_buffer;
		protected ByteArrayInputStream m_input;
		protected int m_offset = 0;
		protected boolean m_newMsg = false;

		protected TelemetryListening() {
			// Queue to store a message in construction
			//			m_message = new ArrayDeque<Integer>(MAX_MSG_SIZE);
			// Byte buffer equivalent of the message
			m_buffer = new byte[m_msgSize];
			// Stream on the byte buffer
			m_input = new ByteArrayInputStream(m_buffer, 0, m_msgSize);
		}

		private boolean validChecksum() {
			byte checksum = TELEM_MSG_HEADER;
			for ( int i=0; i<m_msgSize; i++ ) {
				checksum += m_buffer[i];
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

			synchronized(m_telemetry) {
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
			}
			synchronized(m_monitor) {
				m_monitor.notify();
			}
			return processed;
		}

		protected void readSerial() {
			Integer dataByte;
			try {
				int bufferedBytes = m_serialReceive.available();
				for ( int i=bufferedBytes; i>0; i--) {
					dataByte = m_serialReceive.read();
					if ( dataByte == -1 ) break; // WTF ?
					if ( m_newMsg ) {
						m_buffer[m_offset] = dataByte.byteValue();
						m_offset++;
					}
					else {
						if ( dataByte == TELEM_MSG_HEADER ) {
							m_newMsg = true;
							m_offset = 0;
						}
					}
					if ( m_offset == m_msgSize-1 ) {
						boolean valid = validChecksum();
						if ( valid ) {
							// if ( debug_serial ) s_logger.trace("Valid message received.");
							processMessage();
						}
						else {
							if ( debug_serial ) s_logger.trace("Checksum error!");
						}
						m_offset = 0;
						m_newMsg = false;
					}
				} // for all buffered bytes
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
					delay(50);
				}
				else {
					readSerial();
					// Sleep a little bit (less than 15ms ?)
					delay(5);
				}
			}
			s_logger.info("Exiting Telemetry Thread.");
		}
	}
		
}
