package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.net.RoombaLocomotorClient;

public class GrannyRoombaConsoleCtrl {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	private RoombaLocomotorClient m_locomotor;
	
	public GrannyRoombaConsoleCtrl(String host, int locoPort) {

		m_locomotor = new RoombaLocomotorClient(host, locoPort);
		m_locomotor.connect();

		KeyboardController controller = new KeyboardController(System.in, m_locomotor);

		try {
			while ( controller.execute() ) {
				// nothing
			}
			m_locomotor.driveVelocityCurvature(0, 0x8000, 1f);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		m_locomotor.disconnect();
	}

	public static void main(String[] args) {

		String host = System.getProperties().getProperty("host");
		if ( host ==  null ) {
			host = "172.16.0.39";
		}
		int locoPort = Integer.getInteger("port", 4444);
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		new GrannyRoombaConsoleCtrl(host, locoPort);

	}

}
