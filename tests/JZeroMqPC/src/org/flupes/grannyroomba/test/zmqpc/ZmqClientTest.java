package org.flupes.grannyroomba.test.zmqpc;

import org.flupes.ljf.grannyroomba.net.ZmqClient;

public class ZmqClientTest {

	class SimpleClient extends ZmqClient {

		public SimpleClient(String server, int port) {
			super(server, port);
		}
		
		public String sendRequest(String request) {
			m_socket.send(request.getBytes (), 0);
			byte[] reply = m_socket.recv(0);
			return new String (reply);
		}
		
	}
	
	ZmqClientTest() {
		SimpleClient client = new SimpleClient("localhost", 6666);
		client.connect();       
		for(int requestNbr = 0; requestNbr != 50; requestNbr++) {
            System.out.println("Sending Hello " + requestNbr );
            String reply = client.sendRequest("Hello");
            System.out.println("Received:" + reply);
		}
		client.disconnect();
	}
	
	public static void main(String[] args) {
		new ZmqClientTest();
	}

}
