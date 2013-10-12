package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.IRoombaLocomotor;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd.Command;
import org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus;

import com.google.protobuf.InvalidProtocolBufferException;

public class RoombaLocomotorClient extends LocomotorClient implements
		IRoombaLocomotor {

	int m_oiMode;
	int m_bumps;
	int m_velocity;
	int m_radius;

	public RoombaLocomotorClient(String server, int port) {
		super(server, port);
	}

	@Override
	public int getStatus() {
		if ( ! isConnected() ) {
			s_logger.warn("LocomotorClient is not connected!");
			return -1;
		}
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.STATUS_REQUEST);
		m_socket.send(builder.build().toByteArray());
		byte[] reply = m_socket.recv(0);
		if ( reply == null ) throw new IllegalStateException("Did not get a reply for getStatus");
		try {
			RoombaStatus status = RoombaStatus.parseFrom(reply);
			m_oiMode = status.getOimode();
			m_bumps = status.getBumps();
			m_velocity = status.getVelocity();
			m_radius = status.getRadius();
			return 0;
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("got invalid response from server!");
			s_logger.error("Exception: "+e);
			throw new IllegalStateException("Could not parse the reply from getStatus");
		}
	}

	@Override
	public int getOiMode() {
		return m_oiMode;
	}

	@Override
	public int getBumps() {
		return m_bumps;
	}

	@Override
	public int getVelocity() {
		return m_velocity;
	}

	@Override
	public int getRadius() {
		return m_radius;
	}

}
