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

public class LocomotorStub implements ILocomotor {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected float m_speed, m_curvature;
	
	public LocomotorStub() {
		// nothing
	}
	
	@Override
	public int stop(int mode) {
		s_logger.info("STOP(mode="+mode+")");
		return 0;
	}

	@Override
	public int driveVelocity(float speed, float spin, float timeout) {
		s_logger.info("DRIVE_VELOCITY(speed="+speed+", spin="+spin+", timeout="+timeout+")");
		return 0;
	}

}
