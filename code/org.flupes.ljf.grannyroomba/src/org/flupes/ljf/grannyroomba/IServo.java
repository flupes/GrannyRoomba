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

public interface IServo {

	/**
	 * Returns the current position of the servo.
	 * (based on the last command, not on an external sensor)
	 * @return
	 */
	Float getPosition();
	
	/**
	 * Returns the limits (low and high stop) of the servo.
	 * @return
	 */
	float[] getLimits(float[] store);
	
	/**
	 * Drive the servo to the desired position.
	 * @return
	 */
	boolean setPosition(float position);
	
	/**
	 * Move the servo by the given offset.
	 * @return
	 */
	boolean changePosition(float offset);
	
}
