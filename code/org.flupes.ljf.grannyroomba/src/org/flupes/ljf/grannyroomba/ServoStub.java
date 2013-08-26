package org.flupes.ljf.grannyroomba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServoStub implements IServo {

	private float m_position;
	private float m_low, m_high;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");
	
	public ServoStub() {
		m_low = -90;
		m_high = 90;
		m_position = 0;
	}
	
	@Override
	public float getPosition() {
		return m_position;
	}

	@Override
	public float[] getLimits() {
		float[] limits = new float[2];
		limits[0] = m_low;
		limits[1] = m_high;
		return limits;
	}

	@Override
	public boolean setPosition(float position) {
		if ( m_low <= position && position <= m_high) {
			m_position = position;
			s_logger.info("setPosition("+m_position+") succeeded.");
			return true;
		}
		s_logger.info("setPosition("+m_position+") failed because out of range.");
		return false;
	}

	@Override
	public boolean changePosition(float offset) {
		s_logger.info("trying to execute changePosition("+offset+") from current position="+m_position);
		return setPosition(m_position+offset);
	}

}
