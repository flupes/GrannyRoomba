package org.flupes.grannyroomba.test.zmqpc;

import java.util.concurrent.TimeUnit;

import org.flupes.ljf.grannyroomba.net.ZmqServer;
import org.zeromq.ZMQException;

public class ZmqServerTest {

	// Turn off to have a long running server...
	private boolean debug_cancel = true;
	
	class SimpleServer extends ZmqServer {

		public SimpleServer(int port) {
			super(port, 1000);
		}

		@Override
		public void loop() throws InterruptedException {
			byte[] reply = null;
			try {
				reply = m_socket.recv(0);
			}
			catch (ZMQException e) {
				if ( zmq.ZError.ETERM == e.getErrorCode() ) {
					s_logger.info("recv exection is ETERM -> just terminate now...");
				}
				else {
					s_logger.info("recv exection unexpected: " + e.getErrorCode());
				}
			}
			if ( reply != null ) {
				System.out.println("Received Hello");
				String request = "World" ;
				m_socket.send(request.getBytes (), 0);
			}
		}
	}

	ZmqServerTest() {
		ZmqServer server = new SimpleServer(6666);
		server.start();
		try {
			if ( debug_cancel ) {
				TimeUnit.SECONDS.sleep(4);
			}
			else {
				TimeUnit.MINUTES.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.cancel();
	}

	public static void main(String[] args) {
		new ZmqServerTest();
	}

}
