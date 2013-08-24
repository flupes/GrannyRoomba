package org.flupes.grannyroomba.test.jmqandroid.helloserver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import zmq.ZError;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

@Deprecated
public class HelloServerService extends Service {

	private ServerRunnable m_server;
	private ExecutorService m_executor;
	
	private ZMQ.Context m_context;

	private void info(String str) {
		Log.d("HelloServerService", str);
	}

	private class ServerRunnable implements Runnable {

		private volatile boolean m_isAlive = false;
		
		private ZMQ.Socket m_socket;
		
		@Override
		public void run() {
			info("Entering ServerRunnable.run()");
			m_isAlive = true;

			info("Setting up the ZMQ server...");
			m_socket = m_context.socket(ZMQ.REP);
			m_socket.bind ("tcp://*:8888");
			
			info("Starting to listen on the tcp port...");
			while ( m_isAlive ) {
				
				try {
					byte[] reply = m_socket.recv(0);
				}
				catch (ZMQException e) {
					if ( ZError.ETERM == e.getErrorCode() ) {
						info("recv exection is ETERM -> just terminate now...");
					}
					else {
						info("recv exection unexpected: " + e.getErrorCode());
					}
				}
	            if ( m_isAlive ) { // is_Alive may have changed while in the blocking recv
	            	info("Received Hello");
	            	String request = "World" ;
	            	m_socket.send(request.getBytes (), 0);
		            try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						info("Thread was interupted!");
					}
	            }
//	            Thread.yield();
			}
			
			info("Cleaning up the server");
			m_socket.close();
			
			info("Leaving ServerRunnable.run()");
			
		}
		
		public void cancel() {
			m_isAlive = false;
		}

	}

	private class ServerStart implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			info("ServerStart called");
			m_context = ZMQ.context(1);
			return ZError.errno();
		}
		
	}
	
	private class ServerStop implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			info("ServerStop called");
			if ( m_context != null ) {
				m_context.term();
				return ZError.errno();
			}
			return null;
		}
		
	}
	
	
	@Override
	public void onCreate() {
		info("entering HelloServerService.onCreate");
//		m_executor = Executors.newSingleThreadExecutor();
		// Need more one thread: the termination initiation need to run in its 
		// thread which can be neither the server thread or the main thread
		m_executor = Executors.newFixedThreadPool(3);
		
	}

	@Override
	public void onDestroy() {
		info("HelloServerService.onDestroy");

		// Just tell the server it is not alive anymore (isAlive -> false)
		m_server.cancel();
		
		if ( m_server != null ) {
			// To stop the server that may be in a blocking receive state,
			// we need to send "term" to the context, which in turn
			// interrupt the recv.
			// However, because Context.term needs NETWORK access, it cannot run
			// on the main thread: we launch the stop in a separate thread!
			Future<Integer> future = m_executor.submit(new ServerStop());
			try {
				int result = future.get(1, TimeUnit.SECONDS);
				if ( result > 0 ) {
					info("ServerStop failed and returned: " + result);
				}
				else {
					info("ServerStop returned without error");
				}
			} catch (InterruptedException e) {
				info("ServerStop was interrupted");
				e.printStackTrace();
			} catch (ExecutionException e) {
				info("Something went wrong with ServerStop execution");
				e.printStackTrace();
			} catch (TimeoutException e) {
				info("Context was not terminated in the allocated time");
				e.printStackTrace();
			}
		}

		m_executor.shutdown();
        try {
            if ( !m_executor.awaitTermination(3, TimeUnit.SECONDS) ) {
                info("server thread did not terminate properly");
            }
        } catch (InterruptedException e) {
            info("error in termination:" + e);
        }

	}

	@Override
	public void onStart(Intent intent, int startid) {
		info("HelloServerService.onStart("+intent+", "+startid+")");
		m_server = new ServerRunnable();

		Future<Integer> future = m_executor.submit(new ServerStart());
		
		try {
			// Get is blocking
			int result = future.get(1, TimeUnit.SECONDS);
			if ( result > 0 ) {
				info("ServerStart failed and returned: " + result);
			}
			else {
				info("ServerStart returned without error");
			}
		} catch (InterruptedException e) {
			info("ServerStart was interrupted");
			e.printStackTrace();
		} catch (ExecutionException e) {
			info("Something went wrong with ServerStart execution");
			e.printStackTrace();
		} catch (TimeoutException e) {
			info("Context was not created in the allocated time");
			e.printStackTrace();
		}

        m_executor.execute(m_server);
        
	}

	@Override
	public IBinder onBind(Intent intent) {
		info("onBind should not be called!");
		return null;
	}

}
