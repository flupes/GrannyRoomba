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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import zmq.ZError;

public class ZmqClient {

	protected String m_server;
	protected int m_port;
	protected String m_url;
	protected ZMQ.Context m_context;
	protected ZMQ.Socket m_socket;
	protected boolean m_connected;
	protected int m_sendTimeoutMs;
	protected int m_recvTimeoutMs;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	static final int REQUEST_RETRIES = 3;

	public ZmqClient(String server, int port) {
		m_server = server;
		m_port = port;
		m_url = "tcp://"+m_server+":"+Integer.toString(m_port);
		m_connected = false;
		m_sendTimeoutMs = Integer.getInteger("send_timeout", 1000);
		m_recvTimeoutMs = Integer.getInteger("recv_timeout", 2000);
		m_context = ZMQ.context(1);
	}

	public boolean isConnected() {
		return m_connected;
	}

	public synchronized void connect() {
		if ( ! m_connected ) {
			m_socket = m_context.socket(ZMQ.REQ);
			m_socket.connect(m_url);
			m_socket.setSendTimeOut(m_sendTimeoutMs);
			m_socket.setReceiveTimeOut(m_recvTimeoutMs);
			m_connected = true;
			s_logger.info("Client connected to ["+m_url+"]");
		}
	}

	public synchronized void disconnect() {
		if ( m_connected ) {
			m_socket.disconnect(m_url);
			m_socket.close();
			m_context.term();
			m_connected = false;
			s_logger.info("Client of [" + m_url + "] disconnected.");
		}
	}

	protected synchronized byte[] reqrep(byte[] buffer) throws ZMQException {
		byte[] rep = null;
		if ( m_connected ) {
			int retriesLeft = REQUEST_RETRIES;
			while ( retriesLeft > 0 && rep == null  && !Thread.currentThread().isInterrupted() ) {
				boolean req = m_socket.send(buffer);
				if ( ! req ) {
					s_logger.error("could not send request!");
					throw new ZMQException(m_socket.base().errno());
				}
				// this recv call will time out using the value recv_timeout
				rep = m_socket.recv(0);
				if ( null == rep ) {
					if ( m_socket.base().errno() == ZError.EAGAIN) {
						s_logger.warn("timeout while waiting for response!");
						retriesLeft--;
						s_logger.warn("Close socket to [" + m_url + "]");
						m_socket.disconnect(m_url);
						m_socket.close();
						m_connected = false;
						connect();
					}
					else {
						s_logger.error("did not receive response!");
						throw new ZMQException(m_socket.base().errno());
					}
				}
			} // while
			if ( rep == null ) {
				// give up!
				s_logger.error("could not get response after " + REQUEST_RETRIES
						+ " tries -> terminate this connection!");
				// this behavior is OK for now for our simple client
				// which is monitoring the connected status and will exit
				// if not connected anymore.
				// we may want to do something else in the future!
				disconnect();
			}
		}
		else {
			s_logger.warn("client is not connected to ["+ m_url + "]... skip reqrep");
		}
		return rep;
	}
}
