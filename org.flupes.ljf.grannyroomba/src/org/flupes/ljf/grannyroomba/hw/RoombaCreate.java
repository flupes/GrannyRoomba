package org.flupes.ljf.grannyroomba.hw;

import java.util.EnumMap;
import java.util.Map;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class RoombaCreate extends SerialIoioRoomba {

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
	
	enum DataType {
		BYTE(1),
		UNSIGNED_WORD(2),
		SIGNED_WORD(2);
		
		final int size;
		
		DataType(int size) {
			this.size = size;
		}
	}
	
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
	
	static private final int CMD_STREAM = 148;
	static private final int CMD_TOGGLESTREAM = 150;
	
	/** Header maker starting a Roomba Create telemetry message */
	static private final int TELEM_MSG_HEADER = 19;
	
	private Map<SensorPackets, Integer> m_telemetry;
	
	/**
	 * Returns the number of different sensor packets requested
	 */
	private int NumberOfSensorPackets() {
		return SensorPackets.values().length;
	}
	
	/**
	 * Returns the number of byte necessary to store the requested
	 * sensor packets. This number does not include the header, packet
	 * ids and checksum.
	 */
	private int SizeOfSensorsData() {
		int sz = 0;
		for ( SensorPackets p : SensorPackets.values() ) {
			sz += p.size;
		}
		return sz;
	}

	private int TelemetryMessageLength() {
		return NumberOfSensorPackets()+SizeOfSensorsData()+2;
	}
	
	public RoombaCreate(IOIO ioio) {
		super(ioio);
		m_telemetry = new EnumMap(SensorPackets.class);
		
		s_logger.info("Opening communication with a Roomba Create");
		s_logger.info("Telemetry contains " + NumberOfSensorPackets()
				+ " and the message length is " + TelemetryMessageLength()
				+ " bytes");
	}
	
	public void startTelemetry() throws ConnectionLostException {
		writeByte( CMD_STREAM );
		writeByte( NumberOfSensorPackets() );
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
}
