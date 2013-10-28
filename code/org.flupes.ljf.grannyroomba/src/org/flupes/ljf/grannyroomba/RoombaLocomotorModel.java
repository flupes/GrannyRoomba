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

/**
 * Simple locomotion model to help control the Roomba in 
 * velocity mode while respecting bounds and giving priority
 * on turns.
 * <br>
 * Roomba is a differential drive robot. Its linear velocity(v)
 * and it angular velocity(w) can be directly derived from the
 * left wheel speed(vL) and right wheel speed(vR) and distance
 * between the two wheels(D):
 * <pre>
 *   v = (vR+vL)/2
 *   w = (vR-vL)/D
 * </pre>
 *   
 * Since vR and vL are bounded, the acceptable (v,w) space is
 * a diamond shape with the vertices on the w axis = +/- wMax
 * and the vertices on the v axis = +/- vMax.
 * <pre>
 *   vMax = vR = vL
 *   wMax = 2.vR / D
 * </pre> 
 * Within this acceptable space, v and w are linked by the following
 * equation (if interested in v for a given w):
 * <pre>
 *   vMax = vRmax - w.D/2   
 * </pre>
 * This model let the user set the linear velocity and angular velocity,
 * and when out of the acceptable space, it give priority to the angular
 * velocity (it is more important to turn that go full speed into and 
 * obstacle).
 */
public class RoombaLocomotorModel {

	public static final float MAX_WHEEL_SPEED = 0.5f;
	public static final float WHEEL_BASE = 0.260f;
	
	public static final float MAX_LINEAR_VELOCITY = MAX_WHEEL_SPEED;
	public static final float MAX_ANGULAR_VELOCITY = 2f * MAX_WHEEL_SPEED / WHEEL_BASE;
	
	public static final float ALLOWED_LINEAR_VELOCITY = 0.6f * MAX_LINEAR_VELOCITY;
	public static final float ALLOWED_ANGULAR_VELOCITY = (float)(Math.PI/2);
	
	private ILocomotor m_locomotor;
	private float m_speed;
	private float m_spin;
	
	static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public RoombaLocomotorModel(ILocomotor locomotor) {
		m_locomotor = locomotor;
	}

	public void stop() {
		reset();
		m_locomotor.stop(0);
	}

	public float getSpeed() {
		return m_speed;
	}
	
	public float getSpin() {
		return m_spin;
	}
	
	public void reset() {
		reset(0,0);
	}
	
	public void reset(float linearSpeed, float angularSpeed) {
		m_speed = linearSpeed;
		m_spin = angularSpeed;
	}
	
	public boolean setVelocities(float speed, float spin) {
		s_logger.trace("setVelocities("+speed+","+spin+")");
		int clamp = 0;
		// Clamp the spin to allowed angular velocity
		if ( spin > ALLOWED_ANGULAR_VELOCITY ) {
			spin = ALLOWED_ANGULAR_VELOCITY;
			clamp += 1;
		}
		if ( spin < -ALLOWED_ANGULAR_VELOCITY ) {
			spin = -ALLOWED_ANGULAR_VELOCITY;
			clamp += 1;
		}
		// Compute vMax to respect vR and vL bounds
		float vMax = MAX_WHEEL_SPEED - Math.abs(spin*WHEEL_BASE/2f);
		// Reduce the speed to stay in acceptable (v,w) space
		if ( speed > vMax ) {
			speed = vMax;
			clamp += 1;
		}
		if ( speed < -vMax ) {
			speed = -vMax;
			clamp += 1;
		}
		// Clamp the speed to allowed linear velocity
		if ( Math.abs(speed) > ALLOWED_LINEAR_VELOCITY ) {
			speed = Math.signum(speed)*ALLOWED_LINEAR_VELOCITY;
			clamp += 1;
		}
		m_speed = speed;
		m_spin = spin;
		m_locomotor.driveVelocity(speed, spin, 0);
		return ( clamp == 0 );
	}
	
	public boolean incrementVelocity(float increment) {
		return setVelocities(m_speed+increment, m_spin);
	}
	
	public boolean incrementSpin(float increment) {
		return setVelocities(m_speed, m_spin+increment);
	}
}
