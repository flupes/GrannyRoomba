package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.net.ZmqServer;

public class TestServer {

	private static Logger s_logger = Logger.getLogger("grannyroomba");
	private ZmqServer m_server;
	
	class SimpleServer extends ZmqServer {

		private int m_counter;
		
		public SimpleServer(int port) {
			super(port, 1000);
			s_logger.info("Server initialized");
		}

		@Override
		public void loop() throws InterruptedException {
			m_counter++;
			s_logger.info("SimpleServer loop " + m_counter);
		}
		
	}
	
	public TestServer() {
		s_logger.info("building server");
		m_server = new SimpleServer(8888);
		s_logger.info("starting server");
		m_server.start();
		s_logger.info("sleeping for 4s");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s_logger.info("stoping the server");
		m_server.cancel();
		s_logger.info("terminate");
	}

	public static void main(String[] args) {
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);
		new TestServer();
		s_logger.info("done.");
	}

}
