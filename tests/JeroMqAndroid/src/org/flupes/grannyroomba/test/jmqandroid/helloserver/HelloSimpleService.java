package org.flupes.grannyroomba.test.jmqandroid.helloserver;

import org.flupes.ljf.grannyroomba.net.ZmqServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQException;

import zmq.ZError;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HelloSimpleService extends Service {

	private ZmqServer m_server;
	
	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba.test");

	class SimpleServer extends ZmqServer {

		public SimpleServer(int port) {
			super(port);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void loop() throws InterruptedException {
			try {
				byte[] reply = m_socket.recv(0);
			}
			catch (ZMQException e) {
				if ( ZError.ETERM == e.getErrorCode() ) {
					s_logger.info("recv exection is ETERM -> just terminate now...");
				}
				else {
					s_logger.info("recv exection unexpected: " + e.getErrorCode());
				}
			}
            if ( isLooping() ) { // is_Alive may have changed while in the blocking recv
            	s_logger.info("Received Hello");
            	String request = "World" ;
            	m_socket.send(request.getBytes (), 0);
            }
            s_logger.warn("out of loop now!");
		}

	}

	
	@Override
	public void onCreate() {
		s_logger.info("entering HelloSimpleService.onCreate");
		m_server = new SimpleServer(8888);
		s_logger.info("leaving HelloSimpleService.onCreate");
	}

	@Override
	public void onDestroy() {
		s_logger.info("HelloSimpleService.onDestroy");
		if ( m_server != null ) {
			s_logger.info("HelloSimpleService calling cancel on the server");
			m_server.cancel();
			s_logger.info("HelloSimpleService canceled the Server");
		}
	}

	@Override
	public void onStart(Intent intent, int startid) {
		s_logger.info("HelloSimpleService.onStart("+intent+", "+startid+")");
		m_server.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		s_logger.info("onBind should not be called!");
		return null;
	}

}
