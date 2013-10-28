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

package org.flupes.ljf.grannyroomba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServoStub implements IServo {

	private float m_position;
	private float m_low, m_high;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");
	
	public ServoStub() {
		init(-90, 90, 0);
	}
	
	public ServoStub(float low, float high, float start) {
		init(low, high, start);
	}
	
	private void init(float low, float high, float start) {
		m_low = low;
		m_high = high;
		m_position = start;
	}
	
	@Override
	public Float getPosition() {
		return m_position;
	}

	@Override
	public float[] getLimits(float[] store) {
		if ( store == null ) {
			store = new float[2];
		}
		store[0] = m_low;
		store[1] = m_high;
		return store;
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
