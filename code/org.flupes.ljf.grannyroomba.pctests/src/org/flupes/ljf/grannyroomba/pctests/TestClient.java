package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.net.ZmqClient;
import org.zeromq.ZMQException;

// Run from shell (after exporting as runnable jar):
// java -classpath test_zmq_client.jar -Drequests="100" -Dhost="172.16.0.23" -Dport="8888" org/flupes/ljf/grannyroomba/pctests/TestClient

public class TestClient {

	private static Logger s_logger = Logger.getLogger("grannyroomba");
	private SimpleClient m_client;

	private int m_received = 0;

	private final String request = "Hello";
	
	class SimpleClient extends ZmqClient {

		public SimpleClient(String server, int port) {
			super(server, port);
			s_logger.info("Client initialized");
		}

		public void test() {
			byte[] reply = m_client.reqrep(request.getBytes());
			if ( reply != null ) {
				m_received++;
			}
		}

	}

	public TestClient() {
		
		String hostname = "localhost";
		String hostProp = System.getProperties().getProperty("host");
		if ( hostProp != null ) {
			hostname = hostProp;
		}
		int port = Integer.getInteger("port", 8888);
		int requests = Integer.getInteger("requests", 10);
		
		s_logger.info("building client");
		m_client = new SimpleClient(hostname, port);
		m_client.connect();

		try {
			long start = System.nanoTime();
			for (int i=0; i<requests; i++) {
				m_client.test();
			}
			long stop = System.nanoTime();
			s_logger.info("Received " + m_received + " valid responses to "
					+ requests + " requests in "
					+ (stop-start)/1E6+ "ms");
		} catch ( ZMQException e) {
			s_logger.warn("Error in REQ/REP: " + e.getErrorCode());
		}

		
		m_client.disconnect();
	}


	public static void main(String[] args) {
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);
		new TestClient();
	}

}
