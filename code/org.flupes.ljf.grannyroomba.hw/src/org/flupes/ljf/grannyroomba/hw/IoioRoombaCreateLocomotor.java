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
					velocity = Math.round(RoombaCreate.WHEEL_BASE * spin); 
				}
			}
			else {
				if ( Math.abs(spin) < EPSILON ) {
					// straight drive
					radius = 0x8000;
					velocity = Math.round(speed);
				}
				else {
					// arc circle
					velocity = Math.round(speed);
					radius = Math.round(speed / spin);
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
