package org.flupes.grannyroomba.test.jmqandroid.helloserver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flupes.ljf.grannyroomba.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import zmq.ZError;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HelloSimpleService extends Service {

	private SimpleService m_server;
	
	private ZMQ.Context m_context;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba.test");

	private class ServerService extends SimpleService {

		private ZMQ.Socket m_socket;
		
		public ServerService() {
			super(100);
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
		}

		@Override
		public int init() {
			s_logger.info("Setting up the ZMQ server...");
			m_context = ZMQ.context(1);
			m_socket = m_context.socket(ZMQ.REP);
			m_socket.bind ("tcp://*:8888");
			s_logger.info("Starting to listen on the tcp port...");
			return 0;
		}

		@Override
		public int fini() {
			s_logger.info("Cleaning up the server");
			m_socket.close();
			s_logger.info("Leaving ServerRunnable.run()");
			return 0;
		}

	}
	
	private class ServerStop implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			s_logger.info("ServerStop called");
			if ( m_context != null ) {
				m_context.term();
				return ZError.errno();
			}
			return null;
		}
		
	}

	
	@Override
	public void onCreate() {
		s_logger.info("entering HelloSimpleService.onCreate");
//		Logger logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("jeromqtests");

		//		m_executor = Executors.newSingleThreadExecutor();
		// Need more one thread: the termination initiation need to run in its 
		// thread which can be neither the server thread or the main thread
		m_server = new ServerService();
		s_logger.info("leaving HelloSimpleService.onCreate");
	}

	@Override
	public void onDestroy() {
		s_logger.info("HelloSimpleService.onDestroy");

		// Just tell the server it is not alive anymore (isAlive -> false)
		m_server.cancel();
		
		if ( m_server != null ) {
			// To stop the server that may be in a blocking receive state,
			// we need to send "term" to the context, which in turn
			// interrupt the recv.
			// However, because Context.term needs NETWORK access, it cannot run
			// on the main thread: we launch the stop in a separate thread!
			Future<Integer> future = Executors.newFixedThreadPool(1).submit(new ServerStop());
			try {
				int result = future.get(1, TimeUnit.SECONDS);
				if ( result > 0 ) {
					s_logger.info("ServerStop failed and returned: " + result);
				}
				else {
					s_logger.info("ServerStop returned without error");
				}
			} catch (InterruptedException e) {
				s_logger.info("ServerStop was interrupted");
				e.printStackTrace();
			} catch (ExecutionException e) {
				s_logger.info("Something went wrong with ServerStop execution");
				e.printStackTrace();
			} catch (TimeoutException e) {
				s_logger.info("Context was not terminated in the allocated time");
				e.printStackTrace();
			}
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
