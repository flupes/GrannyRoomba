package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.RoombaLocomotorStub;
import org.flupes.ljf.grannyroomba.ServoStub;
import org.flupes.ljf.grannyroomba.net.LocomotorServer;
import org.flupes.ljf.grannyroomba.net.ServoServer;

public class RobotStubServer {

	private volatile boolean interrupted = false;
	
	public RobotStubServer() {
		
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);

		ServoStub servoStub = new ServoStub();
		ServoServer servoService = new ServoServer(6666, servoStub);
		servoService.start();
		
		RoombaLocomotorStub locoStub = new RoombaLocomotorStub();
		LocomotorServer locoService = new LocomotorServer(7777, locoStub);
		locoService.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("process interrupted!");
				interrupted = true;
			}
		});

		
		while ( ! interrupted ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("interrupted while sleeping");
			}
		}
		
		locoService.cancel();
		servoService.cancel();
		
	}
	
	public static void main(String[] args) {
		new RobotStubServer();
	}

}
