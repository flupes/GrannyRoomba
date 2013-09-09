package org.flupes.ljf.grannyroomba.hw;

import ioio.lib.api.exception.ConnectionLostException;

import org.flupes.ljf.grannyroomba.ILocomotor;

public class IoioRoombaLocomotor implements ILocomotor {

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

}
