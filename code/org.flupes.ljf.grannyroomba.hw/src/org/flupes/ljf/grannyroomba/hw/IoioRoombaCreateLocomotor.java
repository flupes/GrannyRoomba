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

import org.flupes.ljf.grannyroomba.ICreateLocomotor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoioRoombaCreateLocomotor implements ICreateLocomotor {

	protected RoombaCreate m_roomba;

	protected int m_oiMode;
	protected int m_bumps;
	protected int m_velocity;
	protected int m_radius;

	protected static final float EPSILON = 1E-3f;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public IoioRoombaCreateLocomotor(RoombaCreate roomba) {
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
			float leftWheelSpeed;
			float rightWheelSpeed;
			if ( Math.abs(speed) < EPSILON ) {
				if ( Math.abs(spin) < EPSILON ) {
					// stop
					rightWheelSpeed = 0;
					leftWheelSpeed = 0;
				}
				else {
					// point turn
					rightWheelSpeed = spin*RoombaCreate.WHEEL_BASE/2f;
					leftWheelSpeed = -rightWheelSpeed;
				}
			}
			else {
				if ( Math.abs(spin) < EPSILON ) {
					// straight drive
					rightWheelSpeed  = speed;
					leftWheelSpeed = speed;
				}
				else {
					// arc circle
					float angularVel = spin*RoombaCreate.WHEEL_BASE/2f;
					rightWheelSpeed = speed+ angularVel;
					leftWheelSpeed = speed-angularVel;
				}
			}
			s_logger.trace("driveVelocity("+speed+","
					+spin+") -> leftWS="+leftWheelSpeed+" / rightWS="+rightWheelSpeed);
			if ( Math.abs(rightWheelSpeed) <= RoombaCreate.MAX_VELOCITY
					&& Math.abs(leftWheelSpeed) <= RoombaCreate.MAX_VELOCITY ) {
				// reminder: roomba speed are expressed in mm, hence 
				// the 1000 factor
				m_roomba.directDrive(
						Math.round(1000*leftWheelSpeed), 
						Math.round(1000*rightWheelSpeed) );
			}
			else {
				s_logger.warn("wheel velocity exceeded bounds!");
			}
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	@Override
	public int drivePosition(float distance, float angle, float velocity) {
		m_roomba.positionDrive((int)(distance*1000));
		return 0;
	}

	@Override
	public int getStatus() {
		m_oiMode = m_roomba.getOiMode();
		m_bumps =  m_roomba.getBumps();
		m_velocity = m_roomba.getVelocity();
		m_radius = m_roomba.getRadius();
		return 0;
	}

	@Override
	public int getOiMode() {
		return  m_oiMode;
	}

	@Override
	public int getBumps() {
		return m_bumps;
	}

	@Override
	public int getVelocity() {
		return m_velocity;
	}

	@Override
	public int getRadius() {
		return m_radius;
	}

}
