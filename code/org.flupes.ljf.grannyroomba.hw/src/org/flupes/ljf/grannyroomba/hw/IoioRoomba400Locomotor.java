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

import ioio.lib.api.exception.ConnectionLostException;

import org.flupes.ljf.grannyroomba.ILocomotor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @warning This class has not been tested!
 * It is mainly here to keep the old "driveVelocity" for future
 * reference if a Roomba Serie 400 becomes available. 
 */
public class IoioRoomba400Locomotor implements ILocomotor {

	protected RoombaSeries400 m_roomba;

	protected static final float EPSILON = 1E-3f;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public IoioRoomba400Locomotor(RoombaSeries400 roomba) {
		m_roomba = roomba;
	}

	@Override
	public int stop(int mode) {
		try {
			m_roomba.stop();
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	@Override
	public int driveVelocity(float speed, float spin, float timeout) {
		try {
			int velocity;
			int radius;
			if ( Math.abs(speed) < EPSILON ) {
				if ( Math.abs(spin) < EPSILON ) {
					// stop
					velocity = 0;
					radius = 0x8000;
				}
				else {
					// point turn
					if ( spin > 0 ) {
						radius = 0xFFFF;
					}
					else {
						radius = 0x0001; 
					}
					// wheel base = 0.260m
					velocity = Math.round(260 * spin); 
				}
			}
			else {
				if ( Math.abs(spin) < EPSILON ) {
					// straight drive
					radius = 0x8000;
					velocity = Math.round(1000*speed);
				}
				else {
					// arc circle
					velocity = Math.round(1000*speed);
					radius = Math.round(1000*speed / spin);
				}
			}
			s_logger.trace("driveVelocity("+speed+","
					+spin+") -> velocity="+velocity+" / radius="+radius);
			m_roomba.baseDrive(velocity, radius);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}


}
