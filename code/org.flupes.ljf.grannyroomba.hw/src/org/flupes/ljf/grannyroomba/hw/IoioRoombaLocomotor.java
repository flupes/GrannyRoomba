package org.flupes.ljf.grannyroomba.hw;

import ioio.lib.api.exception.ConnectionLostException;

import org.flupes.ljf.grannyroomba.IRoombaLocomotor;

public class IoioRoombaLocomotor implements IRoombaLocomotor {

	RoombaCreate m_roomba;
	
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
	public int getOiMode() {
		return m_roomba.getOiMode();
	}

	@Override
	public int getBumps() {
		return m_roomba.getBumps();
	}

	@Override
	public int getVelocity() {
		return m_roomba.getVelocity();
	}

	@Override
	public int getRadius() {
		return m_roomba.getRadius();
	}

}
