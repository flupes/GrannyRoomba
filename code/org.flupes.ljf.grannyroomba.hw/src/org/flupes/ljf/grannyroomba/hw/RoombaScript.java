package org.flupes.ljf.grannyroomba.hw;

/**
 * Simple holder for Roomba Create scripts.
 * 
 * The class always:
 *  - add a mode change to SAFE at the beginning of the script (on creation)
 *  - add a mode change to FULL at the end of the script (on close)
 *  This hack allow the telemetry system to monitor if the script is still
 *  executing or not.
 */
public class RoombaScript {

	private final int CAPACITY = 100;
	protected byte[] m_script;
	protected int m_pos;
	protected boolean m_closed;

	RoombaScript() {
		m_script = new byte[CAPACITY];
		m_script[0] = 0;
		m_pos = 1;
		m_closed = false;
		addByte(RoombaCmds.CMD_SAFE);
	}

	public RoombaScript addByte(int b) {
		if ( m_closed ) throw new IllegalStateException("script was already closed");
		if ( m_pos < CAPACITY-1 ) {
			m_script[m_pos++] = (byte)b;
		}
		else {
			throw new IndexOutOfBoundsException("script is limited to "+CAPACITY+" bytes");
		}
		return this;
	}

	public RoombaScript addWord(int w) {
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
		addByte(RoombaCmds.CMD_FULL);
		m_script[0] = (byte)(m_pos-1);
		m_closed = true;
		return m_pos;
	}

	public int length() {
		return m_pos;
	}

	public byte[] buffer() {
		return m_script;
	}
}
