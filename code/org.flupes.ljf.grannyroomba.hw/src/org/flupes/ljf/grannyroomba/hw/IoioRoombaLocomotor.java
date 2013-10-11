package org.flupes.ljf.grannyroomba.hw;

import ioio.lib.api.exception.ConnectionLostException;

import org.flupes.ljf.grannyroomba.IRoombaLocomotor;

public class IoioRoombaLocomotor implements IRoombaLocomotor {

	RoombaCreate m_roomba;
	
	int m_oiMode;
	int m_bumps;
	int m_velocity;
	int m_radius;
	
	public IoioRoombaLocomotor(RoombaCreate roomba) {
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
	public int driveVelocity(float speed, float curvature, float timeout) {
		try {
			m_roomba.drive(Math.round(speed), Math.round(curvature));
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			return -1;
		}
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
