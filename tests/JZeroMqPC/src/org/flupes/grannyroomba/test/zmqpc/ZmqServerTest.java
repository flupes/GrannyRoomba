package org.flupes.grannyroomba.test.zmqpc;

import org.flupes.ljf.grannyroomba.net.ZmqServer;

public class ZmqServerTest {

	class SimpleServer extends ZmqServer {

		public SimpleServer(int port) {
			super(port);
		}

		@Override
		public void loop() throws InterruptedException {
            byte[] reply = m_socket.recv(0);
            System.out.println("Received Hello");
            String request = "World" ;
            m_socket.send(request.getBytes (), 0);
 		}
	}
	
	ZmqServerTest() {
		ZmqServer server = new SimpleServer(6666);
		server.start();
	}
	
	public static void main(String[] args) {
		new ZmqServerTest();
	}

}
