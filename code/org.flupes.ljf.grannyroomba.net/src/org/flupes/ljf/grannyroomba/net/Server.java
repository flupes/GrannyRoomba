package org.flupes.ljf.grannyroomba.net;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flupes.ljf.grannyroomba.SimpleService;
import org.zeromq.ZMQ;

import zmq.ZError;

public abstract class Server extends SimpleService {

	protected int m_port;
	protected String m_url;
	protected ZMQ.Context m_context;
	protected ZMQ.Socket m_socket;
	protected boolean m_active;
	protected int m_cmdid;

	private class ServerStop implements Callable<Integer> {
		@Override
		public Integer call() throws Exception {
			s_logger.info("ServerStop called");
			if ( m_context != null ) {
				m_context.term();
				s_logger.info("ServerStop now returns errno");
				return ZError.errno();
			}
			return null;
		}
	}

	public Server(int port) {
		super(10);		// wait 10ms between successive loop
		m_port = port;
		m_url = "tcp://*:"+Integer.toString(m_port);
		m_active = false;
		m_cmdid = port << 16;
	}

	public boolean isActive() {
		return m_active;
	}

	int getPort() {
		return m_port;
	}

	@Override
	public int init() {
		if ( ! m_active ) {
			s_logger.info("Creating ZMQ context...");
			m_context = ZMQ.context(1);
			s_logger.info("Creating ZMQ socket...");
			m_socket = m_context.socket(ZMQ.REP);
			s_logger.info("Binding socket to port...");
			m_socket.bind (m_url);
			s_logger.info("Started server on ["+m_url+"]");
		}
		return 0;
	}

	@Override
	public int fini() {
		s_logger.info("entering Server.fini");
//		m_socket.close();
//		m_context.term();
		if ( m_socket != null ) {
			m_socket.close();
		}
		m_socket = null;
		m_context = null;
		m_active = false;
		s_logger.info("Server running on ["+m_url+"] termintated.");
		return 0;
	}

	@Override
	public synchronized void cancel() {
		s_logger.info("Terminating service running on ["+m_url+"]");
		m_state = State.STOPPED;

//		interrupt();
//		s_logger.info("interrupt returned.");
		
		// To stop the server that may be in a blocking receive state,
		// we need to send "term" to the context, which in turn
		// interrupt the recv.
		// However, because Context.term needs NETWORK access, it cannot run
		// on the main thread: we launch the stop in a separate thread!
		Future<Integer> future = s_executor.submit(new ServerStop());
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
//			e.printStackTrace();
		}

	}

}
