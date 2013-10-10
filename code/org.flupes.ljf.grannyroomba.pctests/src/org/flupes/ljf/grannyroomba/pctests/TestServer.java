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
	
	private int m_counter;
	private volatile boolean up = true;
	
	class SimpleServer extends ZmqServer {

		private final String response = "World";
		
		public SimpleServer(int port, int sleepMs) {
			super(port, sleepMs);
			s_logger.info("Server initialized");
		}

		@Override
		public void loop() throws InterruptedException {
			byte[] data = m_socket.recv();
			if ( data != null ) {
				m_socket.send(response.getBytes());
			}
			m_counter++;
		}
		
	}
	
	public TestServer() {
		
		s_logger.info("building server");
		int port = Integer.getInteger("port", 8888);
		int delay = Integer.getInteger("delay", 1000);
		
		m_server = new SimpleServer(port, delay);
		
		s_logger.info("starting server");
		m_server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				up = false;
				s_logger.warn("Process received INT signal");
				s_logger.info("Processed " + m_counter + " requests");
				s_logger.info("stoping the server");
				m_server.cancel();
				s_logger.info("terminate");
			}
		});

		int loop = 0;
		int lastCounter = m_counter;
		while ( up ) {
			try {
				loop++;
				System.out.print(".");
				if ( m_counter > lastCounter ) {
					System.out.print(" "+m_counter+" ");
					lastCounter = m_counter;
				}
				if ( 30 == loop ) {
					System.out.println();
				}
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
