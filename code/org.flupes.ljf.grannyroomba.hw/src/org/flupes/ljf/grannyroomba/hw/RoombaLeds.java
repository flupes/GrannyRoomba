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
