package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.ILocomotor;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd.Command;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg;
import org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

public class LocomotorClient extends ZmqClient implements ILocomotor {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public LocomotorClient(String server, int port) {
		super(server, port);
	}

	@Override
	public int stop(int mode) {
		if ( ! isConnected() ) {
			s_logger.warn("LocomotorClient is not connected!");
		}
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.STOP).setStop(
				StopMsg.newBuilder().setMode( StopMsg.Mode.valueOf(mode) ) 
				);
		m_socket.send(builder.build().toByteArray());
		return waitForReply();
	}

	@Override
	public int driveVelocity(float speed, float curvature, float timeout) {
		if ( ! isConnected() ) {
			s_logger.warn("LocomotorClient is not connected!");
		}
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.DRIVE_VELOCITY).setDriveVelocity(
				DriveVelocityMsg.newBuilder().setSpeed(speed)
				.setCurvature(curvature)
				.setTimeout(timeout)
				);
		m_socket.send(builder.build().toByteArray());
		return waitForReply();
	}
	
	@Override
	public int getStatus() {
		if ( ! isConnected() ) {
			s_logger.warn("LocomotorClient is not connected!");
		}
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.STATUS_REQUEST);
		m_socket.send(builder.build().toByteArray());
		return waitForReply();
	}

	protected int waitForReply() {
		byte[] reply = m_socket.recv(0);
		try {
			CommandStatus status = CommandStatus.parseFrom(reply);
			return status.getStatus().getNumber();
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("got invalid response from server!");
			s_logger.error("Exception: "+e);
		}
		return -1;
	}

}
