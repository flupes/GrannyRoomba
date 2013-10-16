package org.flupes.ljf.grannyroomba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocomotorStub implements ILocomotor {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected float m_speed, m_curvature;
	
	public LocomotorStub() {
		// nothing
	}
	
	@Override
	public int stop(int mode) {
		s_logger.info("STOP(mode="+mode+")");
		return 0;
	}

	@Override
	public int driveVelocity(float speed, float spin, float timeout) {
		s_logger.info("DRIVE_VELOCITY(speed="+speed+", spin="+spin+", timeout="+timeout+")");
		return 0;
	}

}
