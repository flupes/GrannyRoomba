package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.ILocomotor;
import org.flupes.ljf.grannyroomba.LocomotorStub;
import org.flupes.ljf.grannyroomba.RoombaLocomotorModel;

/**
 * Should become a unit test when I have more time...
 */
public class TestRoombaLocomotorModel {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	public static void main(String[] args) {

		// initialize logger
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		
		ILocomotor locomotor = new LocomotorStub();
		RoombaLocomotorModel model = new RoombaLocomotorModel(locomotor);
		
		model.stop();
		
		model.setVelocities(0, 0);

		model.setVelocities(0.2f, 0);
		model.setVelocities(0.4f, 0);
		model.setVelocities(0.5f, 0);
		model.setVelocities(0.6f, 0);
		model.setVelocities(-0.2f, 0);
		model.setVelocities(-0.4f, 0);
		model.setVelocities(-0.5f, 0);
		model.setVelocities(-0.6f, 0);
		
		model.setVelocities(0, 0.5f);
		model.setVelocities(0, 1.5f);
		model.setVelocities(0, 2.0f);
		model.setVelocities(0, 4.0f);
		model.setVelocities(0, -0.5f);
		model.setVelocities(0, -1.5f);
		model.setVelocities(0, -2.0f);
		model.setVelocities(0, -4.0f);

		model.setVelocities(0.2f, 1.5f);
		model.setVelocities(0.2f, -1.5f);
		model.setVelocities(-0.2f, 1.5f);
		model.setVelocities(-0.2f, -1.5f);

		model.setVelocities(0.4f, 1.5f);
		model.setVelocities(0.4f, -1.5f);
		model.setVelocities(-0.4f, 1.5f);
		model.setVelocities(-0.4f, -1.5f);

	}

}
