package org.flupes.ljf.grannyroomba.hw;

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
