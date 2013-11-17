package org.flupes.ljf.grannyroomba.hw;

public class RoombaCmds {
	
	// Generic Roomba commands
	static final int CMD_START = 128;
	static final int CMD_BAUD = 139;
	static final int CMD_CONTROL = 130;
	static final int CMD_SAFE = 131;
	static final int CMD_FULL = 132;
	static final int CMD_POWER = 133;
	static final int CMD_SPOT = 134;
	static final int CMD_CLEAN = 135;
	static final int CMD_MAX = 136;
	static final int CMD_DRIVE = 137;
	static final int CMD_MOTORS = 138;
	static final int CMD_LEDS = 139;
	static final int CMD_SENSORS = 142;

	// Roomba Create specific commands
	static final int CMD_DEMO = 136;
	static final int CMD_DIRECT = 145;
	static final int CMD_STREAM = 148;
	static final int CMD_QUERY = 149;
	static final int CMD_TOGGLESTREAM = 150;
	static final int CMD_SCRIPT = 152;
	static final int CMD_PLAY_SCRIPT = 153;
	static final int CMD_WAIT_TIME = 155;
	static final int CMD_WAIT_DISTANCE = 156;
	static final int CMD_WAIT_ANGLE = 157;
	static final int CMD_WAIT_EVENT = 158;

	static final int EVENT_NO_CLIFF = 246;
	static final int EVENT_NO_BUMPER = 251;
	static final int EVENT_NO_WHEELDROP = 255;

}
