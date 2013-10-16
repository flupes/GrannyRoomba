package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;

public class TestLocomotorService {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	public static void main(String[] args) {
		String host = System.getProperties().getProperty("host");
		if ( host == null ) {
			host = "172.16.0.39";
		}
		int port = Integer.getInteger("port", 4444);

		int repeat = Integer.getInteger("repeat", 1);
		
		// initialize logger
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		CreateLocomotorClient locoClient = new CreateLocomotorClient(host, port);
		locoClient.connect();
		
		try {
			for ( int i=0; i< repeat; i++ ) {
				locoClient.getStatus();
				Thread.sleep(200);
			}
			s_logger.info("getStatus request returned correctly");
		} catch (Exception e) {
			s_logger.error("Could not get status from Locomotor!");
		}

		locoClient.disconnect();

	}

}
