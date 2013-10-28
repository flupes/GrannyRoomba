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

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.flupes.ljf.grannyroomba.hw.RoombaCreate.SensorPackets;

public class RoombaLeds {

	private int m_charge;
	private int m_velocity;
	private boolean m_danger;
	
	private Timer m_timer;
	private Map<SensorPackets, Integer> m_telemetry;
	
	RoombaLeds(Map<SensorPackets, Integer> telemetry) {
		m_telemetry = telemetry;
		m_timer = new Timer();
		TimerTask update = new TimerTask() {
			@Override
			public void run() {
				update();
			}
		};
		m_timer.schedule(update, 0, 100);
	}
	
	/**
	 * Light the "power" led according the the charge
	 * @param percent	charge percentage: 0->100
	 */
	public void setCharge(int percent) {
		m_charge = percent;
	}
	
	/**
	 * Light the "advance" led if danger was detected
	 * @param flag		danger or not detected
	 */
	public void showDanger(boolean flag) {
		m_danger = flag;
	}
	
	
	/**
	 * Blink the "play" led according the velocity
	 * @param percent	percentage of the max velocity
	 * 		- 0 -> stopped -> slow blink
	 * 		- 100 -> full speed -> very rapid blink
	 */
	public void setVelocity(int percent) {
		m_velocity = percent;
	}
	
	private void update() {
		// read the telemetry map, check changes and update leds
	}
}
