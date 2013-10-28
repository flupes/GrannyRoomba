/*
 * GrannyRoomba - Telepresence robot based on a Roomba and Android tablet
 * Copyright (C) 2013 Lorenzo Flueckiger
 *
 * This file is part of GrannyRoomba.
 *
 * GrannyRoomba is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GrannyRoomba is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GrannyRoomba.  If not, see <http://www.gnu.org/licenses/>.
 */

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
				s_logger.debug("ServerStop try to terminate the context");
				m_context.term();
				// TODO how can we return the errno? (removed from the jeromq api)
				//				s_logger.debug("ServerStop now returns errno");
				//				return ZError.errno();
				s_logger.debug("ServerStop done");
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
		if ( !isThreadRunning() ) {
			s_logger.warn("cannot cancel a non-running ZmqServer");
			return;
		}
		s_logger.debug("Terminating service running on ["+m_url+"]");

		// Indicate that the service loop should stop
		m_state = State.STOPPED;

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
		}

		// Take care of the proper SimpleService termination
		super.cancel();
		
		s_logger.debug("ZmqServer is down");
	}


}
