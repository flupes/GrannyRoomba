package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.MotorProto.MotorCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServoClient extends Client implements IServo {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public ServoClient(String server, int port) {
		super(server, port);
	}
	
	@Override
	public float getPosition() {
		s_logger.warn("request getPosition is not functional yet!");
		return 0;
	}

	@Override
	public float[] getLimits() {
		s_logger.warn("request getLimits is not functional yet!");
		return null;
	}

	@Override
	public boolean setPosition(float position) {
		if ( ! isConnected() ) {
			s_logger.warn("Client is not connected!");
		}
		MotorCmd.Builder builder = MotorCmd.newBuilder();
		builder.setMode(MotorCmd.Mode.CTRL_ABS_POS);
		builder.setPosition(position);
		MotorCmd cmd = builder.build();
		m_socket.send(cmd.toByteArray(), 0);
		byte[] reply = m_socket.recv(0);
		try {
			CommandStatus status = CommandStatus.parseFrom(reply);
			if ( status.getStatus() == CommandStatus.Status.COMPLETED ) {
				return true;
			}
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("got invalid response from server!");
			s_logger.error("Exception: "+e);
		}
		return false;
	}

	@Override
	public boolean changePosition(float offset) {
		if ( ! isConnected() ) {
			s_logger.warn("Client is not connected!");
		}
		MotorCmd.Builder builder = MotorCmd.newBuilder();
		builder.setMode(MotorCmd.Mode.CTRL_REL_POS);
		builder.setPosition(offset);
		MotorCmd cmd = builder.build();
		m_socket.send(cmd.toByteArray(), 0);
		byte[] reply = m_socket.recv(0);
		try {
			CommandStatus status = CommandStatus.parseFrom(reply);
			if ( status.getStatus() == CommandStatus.Status.COMPLETED ) {
				return true;
			}
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("got invalid response from server!");
			s_logger.error("Exception: "+e);
		}
		return false;
	}

}
