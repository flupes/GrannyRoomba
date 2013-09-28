package org.flupes.ljf.grannyroomba.net;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.flupes.ljf.grannyroomba.SimpleService;
import org.zeromq.ZMQ;

public abstract class ZmqServer extends SimpleService {

	protected int m_port;
	protected String m_url;
	protected ZMQ.Context m_context;
	protected ZMQ.Socket m_socket;
	protected boolean m_active;
	protected int m_cmdid;

	private class ServerStop implements Callable<Integer> {
		@Override
		public Integer call() throws Exception {
			s_logger.debug("ServerStop called");
			if ( m_context != null ) {
				s_logger.debug("terminating the context");
				m_context.term();
				// TODO how can we return the errno? (removed from the jeromq api)
				//				s_logger.debug("ServerStop now returns errno");
				//				return ZError.errno();
				return 0;
			}
			return null;
		}
	}

	public ZmqServer(int port) {
		super(10);		// wait 10ms between successive loop
		init(port);
	}

	public ZmqServer(int port, int msdelay) {
		super(msdelay);
		init(port);
	}

	private void init(int port) {
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
			s_logger.debug("Creating ZMQ context...");
			m_context = ZMQ.context(1);
			s_logger.debug("Creating ZMQ socket...");
			m_socket = m_context.socket(ZMQ.REP);
			s_logger.debug("Binding socket to port...");
			m_socket.bind (m_url);
			s_logger.debug("Started server on ["+m_url+"]");
		}
		return 0;
	}

	@Override
	public int fini() {
		s_logger.debug("entering Server.fini");
		if ( m_socket != null ) {
			s_logger.debug("closing the socket");
			m_socket.close();
		}
		m_socket = null;
		m_context = null;
		m_active = false;
		s_logger.debug("Server running on ["+m_url+"] terminated.");
		return 0;
	}

	@Override
	public synchronized void cancel() {
		s_logger.debug("Terminating service running on ["+m_url+"]");
		// To stop the server that may be in a blocking receive state,
		// we need to send "term" to the context, which in turn
		// interrupt the recv.
		// However, because Context.term needs NETWORK access, it cannot run
		// on the main thread: we launch the stop in a separate thread!
		Future<Integer> future = s_executor.submit(new ServerStop());
		try {
			Integer result = future.get(400, TimeUnit.MILLISECONDS);
			if ( result == null ) {
				s_logger.debug("ServerStop found no context to terminate!");
			}
			else if ( result > 0 ) {
				s_logger.debug("ServerStop failed and returned: " + result);
			}
			else {
				s_logger.debug("ServerStop returned without error.");
			}
		} catch (InterruptedException e) {
			s_logger.debug("ServerStop was interrupted");
			e.printStackTrace();
		} catch (ExecutionException e) {
			s_logger.debug("Something went wrong with ServerStop execution");
			e.printStackTrace();
		} catch (TimeoutException e) {
			s_logger.debug("Context was not terminated in the allocated time");
			//			e.printStackTrace();
		}
		super.cancel();
	}

}
