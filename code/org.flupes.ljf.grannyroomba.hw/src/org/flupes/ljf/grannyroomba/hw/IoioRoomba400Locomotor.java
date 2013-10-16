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
