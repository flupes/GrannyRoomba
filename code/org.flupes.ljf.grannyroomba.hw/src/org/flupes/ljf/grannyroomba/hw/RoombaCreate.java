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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	public enum RobotState {
		READY,
		UNBUMPING,
		SCRIPT_EXEC,
		SAGEGUARDING,
		DISCONNECTED
	}

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
		BUMPS_DROPS(7, DataType.BYTE, "Bumps and Wheel Drops"),
		CLIFF_LEFT(9, DataType.BYTE, "Cliff Left"),
		CLIFF_FRONT_LEFT(10, DataType.BYTE, "Cliff Front Left"),
		CLIFF_FRONT_RIGHT(11, DataType.BYTE, "Cliff Front Right"),
		CLIFF_RIGHT(12, DataType.BYTE, "Cliff Right"),
		VIRTUAL_WALL(13, DataType.BYTE, "Virtual Wall"),
		OVERCURRENTS(14, DataType.BYTE, "Low side drivers and wheels Overcurrents"),
		IR(17, DataType.BYTE, "IR Byte"),
		BUTTONS(18, DataType.BYTE, "Buttons"),
		CHARGING(21, DataType.BYTE, "Charging State"),
		VOLTAGE(22, DataType.UNSIGNED_WORD, "Voltage"),
		CURRENT(23, DataType.SIGNED_WORD, "Current"),
		TEMPERATURE(24, DataType.BYTE, "Battery Temperature"),
		CHARGE(25, DataType.UNSIGNED_WORD, "Battery Charge"),
		CAPACITY(26, DataType.UNSIGNED_WORD, "Battery Capcity"),
		OIMODE(35, DataType.BYTE, "OI Mode"),
		VELOCITY(39, DataType.SIGNED_WORD, "Velocity"),
		RADIUS(40, DataType.SIGNED_WORD, "Radius"),
		RIGHT_VELOCITY(41, DataType.SIGNED_WORD, "Right Velocity"),
		LEFT_VELOCITY(42, DataType.SIGNED_WORD, "Left Velocity");

		/*
		 * 1)
		 * If telemetry streaming is desired but processing power limited,
		 * the telemetry message size can be reduced by commenting out
		 * lines 2 (IR) to 9 (CAPACIY) 
		 * 
		 * 2)
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

	protected volatile RobotState m_state;
	protected RoombaScript m_backupScript;
	protected RoombaScript m_waitSafeScript;
	protected ExecutorService m_exec;
	protected TelemetryListening m_telemThread;
	protected MonitorSafety m_monitorThread;
	// Check if we will use a continuous telemetry stream (need a fast
	// enough cpu), or request it at given interval (slow cpu)
	protected static final boolean m_continuousTelemetry =
			System.getProperty("continuous_telemetry", "false").equalsIgnoreCase("true");

	Chronometer m_processChrono = new Chronometer("processMessage");
	Chronometer m_readChrono = new Chronometer("readSerial");
	Chronometer m_loopChrono = new Chronometer("readChrono");

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

		m_state = RobotState.DISCONNECTED;

		EnumMap<SensorPackets, Integer> telemetry_store = new EnumMap<SensorPackets, Integer>(SensorPackets.class);
		m_telemetry = Collections.synchronizedMap(telemetry_store);

		m_msgSize = telemetryMessageLength();

		s_logger.info("Opening communication with a Roomba Create");
		s_logger.debug("Telemetry:"
				+ "\n    sensor packets = " + numberOfSensorPackets()
				+ "\n    sensor data size = " + sizeOfSensorsData()
				+ "\n    total message length = " + telemetryMessageLength());

		// Start a separate thread for telemetry listening
		s_logger.trace("Launching Telemetry Thread");
		m_exec = Executors.newFixedThreadPool(2);
		m_monitorThread = new MonitorSafety();
		if ( m_continuousTelemetry ) {
			m_exec.execute(m_monitorThread);
		}
		m_telemThread = new TelemetryListening();
		m_exec.execute(m_telemThread);
	}

	public void shutdown() {
		s_logger.info("shuting down the controller threads...");
		m_exec.shutdown();
		try {
			if ( !m_exec.awaitTermination(500, TimeUnit.MILLISECONDS) ) {
				m_exec.shutdownNow();
			}
		} catch (InterruptedException e) {
			s_logger.warn("monitor and telemetry thread did not respond to shutdown!");
			m_exec.shutdownNow();
			Thread.currentThread().interrupt();
		}
		s_logger.info("controller threads terminated.");
	}

	synchronized public void baseDrive(int velocity, int radius)
			throws ConnectionLostException {
		if ( m_state == RobotState.READY ) {
			super.baseDrive(velocity, radius);
		}
	}

	synchronized public void directDrive(int leftWheelSpeed, int rightWheelSpeed)
			throws ConnectionLostException {
		if ( m_state == RobotState.READY ) {
			s_logger.debug("directDrive("+leftWheelSpeed+", "+rightWheelSpeed+")");
			writeByte( RoombaCmds.CMD_DIRECT );
			writeWord( rightWheelSpeed );
			writeWord( leftWheelSpeed );
			delay(CMD_WAIT_MS);
			m_lastDriveCmd = RoombaCmds.CMD_DIRECT;
		}
	}

	synchronized public void positionDrive(int distance) {
		if ( m_state == RobotState.READY ) {
			RoombaScript driveScript = new RoombaScript();
			driveScript.addByte(RoombaCmds.CMD_DRIVE).addWord(200).addWord(0x8000);
			driveScript.addByte(RoombaCmds.CMD_WAIT_DISTANCE).addWord(distance);
			driveScript.addByte(RoombaCmds.CMD_DRIVE).addWord(0).addWord(0x8000);
			driveScript.close();
			try {
				runScript(driveScript);
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() throws ConnectionLostException {
		s_logger.debug("Stop Drive");
		directDrive(0, 0);
	}

	public int getOiMode() {
		return m_telemetry.get(SensorPackets.OIMODE);
	}

	public int getBumps() {
		return m_telemetry.get(SensorPackets.BUMPS_DROPS);
	}

	public int getVelocity() {
		if ( m_lastDriveCmd == RoombaCmds.CMD_DRIVE ) {
			return m_telemetry.get(SensorPackets.VELOCITY);
		}
		else {
			return (m_telemetry.get(SensorPackets.RIGHT_VELOCITY)
					+m_telemetry.get(SensorPackets.LEFT_VELOCITY))/2;
		}
	}

	public int getRadius() {
		if ( m_lastDriveCmd == RoombaCmds.CMD_DRIVE ) {
			return m_telemetry.get(SensorPackets.RADIUS);
		}
		else {
			float v = getVelocity();
			float w = (m_telemetry.get(SensorPackets.RIGHT_VELOCITY)
					-m_telemetry.get(SensorPackets.LEFT_VELOCITY))/(1000f*WHEEL_BASE);
			if ( Math.abs(w) < 1E-3 ) {
				return 0x8000;
			}
			return (int)(v/w);
		}
	}

	public void demo(int d) throws ConnectionLostException {
		writeByte( RoombaCmds.CMD_DEMO );
		writeByte( d );
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
			System.out.println("robot state = " + m_state);
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

	protected void runScript(RoombaScript script) throws ConnectionLostException {
		m_state = RobotState.SCRIPT_EXEC;
		writeByte(RoombaCmds.CMD_SCRIPT);
		writeBytes(script.buffer(), script.length());
		delay(CMD_WAIT_MS);
		writeByte(RoombaCmds.CMD_PLAY_SCRIPT);
	}

	private class MonitorSafety implements Runnable {

		MonitorSafety() {
			s_logger.info("MonitorSafety thread created");
		}

		@Override
		public void run() {
			try {
				while ( !m_exec.isShutdown() ) {
					// Block until triggered by the telemetry thread
					// The telemetry can continue at its own pace
					// even if this thread is working on something 
					// else: the notify will just not awaken anything
					synchronized(this) {
						wait();
					}
					check();
				} // while executor is up
			} catch (InterruptedException e) {
				s_logger.info("Monitor thread was interrupted while waiting");
			}
			s_logger.info("Exiting monitor thread.");
		}

		public void check() {
			int bumpsAndDrops;
			int mode;
			int charging;
			int cliff;
			int vwall;
			int overcurrent;
			synchronized(m_telemetry) {
				bumpsAndDrops = m_telemetry.get(SensorPackets.BUMPS_DROPS);
				mode = m_telemetry.get(SensorPackets.OIMODE);
				charging = m_telemetry.get(SensorPackets.CHARGING);
				cliff = m_telemetry.get(SensorPackets.CLIFF_LEFT) 
						| m_telemetry.get(SensorPackets.CLIFF_FRONT_LEFT)
						| m_telemetry.get(SensorPackets.CLIFF_FRONT_RIGHT)
						| m_telemetry.get(SensorPackets.CLIFF_RIGHT);
				vwall = m_telemetry.get(SensorPackets.VIRTUAL_WALL);
				overcurrent = m_telemetry.get(SensorPackets.VIRTUAL_WALL)&(8+16);
			}

			try {

				if ( 0 != (bumpsAndDrops&(2+1)) ) {
					if ( m_state != RobotState.UNBUMPING && m_state != RobotState.SAGEGUARDING) {
						// run the script if not already running!
						s_logger.warn("bump -> backup!");
						directDrive(-100, -100);
						m_state = RobotState.UNBUMPING;
					}
				}
				else {
					if ( m_state == RobotState.UNBUMPING ) {
						m_state = RobotState.READY;
						directDrive(0, 0);
						s_logger.warn("bump cleared.");
					}
				}

				int drops = bumpsAndDrops&(4+8+16);

				if ( 0 != (charging|cliff|drops|overcurrent) ) {
					if ( m_state != RobotState.SAGEGUARDING ) {
						stop();
						m_state = RobotState.SAGEGUARDING;
					}
				}
				else {
					if ( m_state == RobotState.SAGEGUARDING ) {
						m_state = RobotState.READY;
					}
				}

				if ( mode == 2 && m_mode != CtrlModes.CONTROL) {
					m_mode = CtrlModes.CONTROL;
				}

				if ( mode == 3 && m_mode != CtrlModes.FULL) {
					m_mode = CtrlModes.FULL;
					// back from SAFE mode that was entered because 
					// a script was push to Roomba.
					m_state = RobotState.READY;
				}

			} catch (ConnectionLostException e) {
				m_state = RobotState.DISCONNECTED;
			}

		}
	}

	private class TelemetryListening implements Runnable {

		protected byte[] m_telemBuffer;
		protected ByteArrayInputStream m_telemInput;
		protected int m_offset = 0;
		protected boolean m_newMsg = false;

		protected TelemetryListening() {
			// Queue to store a message in construction
			//			m_message = new ArrayDeque<Integer>(MAX_MSG_SIZE);
			// Byte buffer equivalent of the message
			m_telemBuffer = new byte[m_msgSize];
			// Stream on the byte buffer
			m_telemInput = new ByteArrayInputStream(m_telemBuffer, 0, m_msgSize);
		}

		public void startTelemetry() throws ConnectionLostException {
			writeByte( RoombaCmds.CMD_STREAM );
			writeByte( numberOfSensorPackets() );
			for ( SensorPackets p : SensorPackets.values() ) {
				writeByte( p.id );
			}
		}

		public void toggleTelemetry(boolean state) throws ConnectionLostException {
			writeByte( RoombaCmds.CMD_TOGGLESTREAM );
			if ( state ) {
				writeByte( 1 );
			}
			else {
				writeByte( 0 );
			}
		}

		public void requestTelemetry() throws ConnectionLostException {
			// flush input stream
			int remaining;
			try {
				remaining = m_serialReceive.available();
				if ( remaining > 0 ) {
					s_logger.warn("the serial input stream is not empty -> clear it before the request!");
					m_serialReceive.skip(remaining);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// send request
			writeByte( RoombaCmds.CMD_QUERY );
			writeByte( numberOfSensorPackets() );
			for ( SensorPackets p : SensorPackets.values() ) {
				writeByte( p.id );
			}
		}

		private boolean validChecksum() {
			byte checksum = TELEM_MSG_HEADER;
			for ( int i=0; i<m_msgSize; i++ ) {
				checksum += m_telemBuffer[i];
			}
			return ( (checksum & 0xFF) == 0); 
		}

		protected int processRequest() {
			int processed = 0;
			int telemLength = sizeOfSensorsData();
			int receivedBytes = 0;
			try {
				long start = System.nanoTime();
				while ( receivedBytes < telemLength ) {
					receivedBytes += m_serialReceive.read(m_telemBuffer, receivedBytes, telemLength-receivedBytes);
					long stop = System.nanoTime();
					long duration = (stop-start)/1000000;
					if ( duration > 500 ) {
						s_logger.warn("could not read telemetry requested in allocated time ("+duration+">500)!");
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ( receivedBytes == telemLength ) {
				m_telemInput.reset();
				//				s_logger.debug("got the right number of bytes :-)");
				//				for (int b=0; b<telemLength; b++) {
				//					System.out.format("%02X ", m_buffer[b]);
				//				}
				//				System.out.println();
				synchronized(m_telemetry) {
					for ( SensorPackets p : SensorPackets.values() ) {
						int value = -1;
						if ( DataType.BYTE == p.type ) {
							value = ByteUtils.readByte(m_telemInput);
							m_telemetry.put(p, value);
							processed += 1;
						}
						else if ( DataType.SIGNED_WORD == p.type ) {
							value = ByteUtils.readSignedWord(m_telemInput);
							m_telemetry.put(p, value);
							processed += 1;
						}
						else if ( DataType.UNSIGNED_WORD == p.type ) {
							value = ByteUtils.readUnsignedWord(m_telemInput);
							m_telemetry.put(p, value);
							processed += 1;
						}
						//						s_logger.debug("sensor packet: " + p.name + " -> " + value);
					}
				}
				m_monitorThread.check();
				return processed;
			}
			else {
				s_logger.warn("skip telemetry request!");
				return -1;
			}
		}

		protected int processMessage() {
			int processed = 0;
			m_telemInput.reset();
			int numBytes = ByteUtils.readByte(m_telemInput);
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
					int id = ByteUtils.readByte(m_telemInput);
					if ( id != p.id ) {
						s_logger.error("sensor packet id does not match "
								+id+" vs. "+p.id);
						return -1;
					}
					if ( DataType.BYTE == p.type ) {
						m_telemetry.put(p, ByteUtils.readByte(m_telemInput));
						processed += 1;
					}
					else if ( DataType.SIGNED_WORD == p.type ) {
						m_telemetry.put(p, ByteUtils.readSignedWord(m_telemInput));
						processed += 1;
					}
					else if ( DataType.UNSIGNED_WORD == p.type ) {
						m_telemetry.put(p, ByteUtils.readUnsignedWord(m_telemInput));
						processed += 1;
					}
					else {
						s_logger.error("data type mismatch!");
					}
				}
			}
			synchronized(m_monitorThread) {
				m_monitorThread.notify();
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
						m_telemBuffer[m_offset] = dataByte.byteValue();
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
							m_processChrono.start();
							processMessage();
							m_processChrono.stop();
							m_processChrono.show(15);
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
			try {
				while ( m_serialReceive == null ) {
					// Telemetry thread is started at construction,
					// however serial connection is not available
					// until "connect" is called... In this case,
					// just wait and do not process anything!
					s_logger.info("wait for the ioio serial port to be available");
					delay(100);
				}
				fullControl();
				if ( m_continuousTelemetry ) {
					startTelemetry();
					s_logger.info("Use continuous telemetry mode");
				}
				else {
					requestTelemetry();
					s_logger.info("Use telemetry on request mode");
				}
				m_state = RobotState.READY;

				while ( !m_exec.isShutdown() ) {
					m_loopChrono.start();
					if ( m_state == RobotState.DISCONNECTED ) {
						delay(100);
					}
					else {
						m_readChrono.start();
						if ( m_continuousTelemetry ) {
							readSerial();
							m_readChrono.stop();
							// Sleep a little bit (less than 15ms ?)
							delay(5);
						}
						else {
							processRequest();
							m_readChrono.stop();
							delay(100);
							requestTelemetry();
						}
						m_readChrono.show(65);
					}
					m_loopChrono.stop();
					m_loopChrono.show(300);
				} // while
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s_logger.info("Exiting Telemetry Thread.");
		}

	}

	@Deprecated
	private void createScripts() {
		m_backupScript = new RoombaScript();
		m_backupScript.addByte(RoombaCmds.CMD_DRIVE).addWord(-100).addWord(0x8000);
		m_backupScript.addByte(RoombaCmds.CMD_WAIT_EVENT).addByte(RoombaCmds.EVENT_NO_BUMPER);
		m_backupScript.addByte(RoombaCmds.CMD_DRIVE).addWord(0).addWord(0x8000);
		m_backupScript.close();

		m_waitSafeScript = new RoombaScript();
		m_waitSafeScript.addByte(RoombaCmds.CMD_WAIT_EVENT).addByte(RoombaCmds.EVENT_NO_CLIFF);
		m_waitSafeScript.addByte(RoombaCmds.CMD_WAIT_EVENT).addByte(RoombaCmds.EVENT_NO_WHEELDROP);
		m_waitSafeScript.close();
	}

	@Deprecated
	public void backup() {
		try {
			stop();
			runScript(m_backupScript);
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Deprecated
	public void waitForSafety() {
		try {
			runScript(m_waitSafeScript);
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
