package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.SimpleService;
import org.zeromq.ZMQ;

public abstract class Server extends SimpleService {

	protected int m_port;
	protected String m_url;
	protected ZMQ.Context m_context;
	protected ZMQ.Socket m_socket;
	protected boolean m_active;
	protected int m_cmdid;
	
	public Server(int port) {
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
			m_context = ZMQ.context(1);
			m_socket = m_context.socket(ZMQ.REP);
			m_socket.bind (m_url);
			s_logger.info("Started server on ["+m_url+"]");
		}
		return 0;
	}

	@Override
	public int fini() {
		m_socket.close();
		m_context.term();
		m_active = false;
		s_logger.info("Server running on ["+m_url+"] termintated.");
		return 0;
	}

}
