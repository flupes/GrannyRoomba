package org.flupes.ljf.grannyroomba.stickclient;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class TestJoystickClient {

	private static Logger s_logger = Logger.getLogger("grannyroomba");

	public static void main(String[] args) {
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		String host = System.getProperties().getProperty("serverAddr");
		if ( host ==  null ) {
			host = "172.16.0.39";
		}
		int servoPort = Integer.getInteger("servoPort", 3333);
		int locoPort = Integer.getInteger("locoPort", 4444);
		
		ServoClient servo = new ServoClient(host, servoPort);
		CreateLocomotorClient loco = new CreateLocomotorClient(host, locoPort);
		
		JoystickClient stick = new JoystickClient(servo, loco);
		
		boolean up = true;
		while ( up ) {
			up = stick.poll();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
