package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.MotorConfigProto.MotorConfig;
import org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg;
import org.flupes.ljf.grannyroomba.messages.MotorStateProto.MotorState;
import org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServoClient extends Client implements IServo {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public ServoClient(String server, int port) {
		super(server, port);
	}

	@Override
	public Float getPosition() {
		SingleAxisCmd.Builder builder = SingleAxisCmd.newBuilder();
		builder.setCmd(SingleAxisCmd.Command.GET_STATE);
		SingleAxisCmd cmd = builder.build();
		m_socket.send(cmd.toByteArray());
		byte[] reply = m_socket.recv(0);
		try {
			MotorState state = MotorState.parseFrom(reply);
			if ( state.hasPosition() ) {
				return state.getPosition();
			}
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("Got invalid MotorState response!");
			s_logger.error("Exception: "+e);
		}
		return null;
	}

	@Override
	public float[] getLimits(float[] store) {
		SingleAxisCmd.Builder builder = SingleAxisCmd.newBuilder();
		builder.setCmd(SingleAxisCmd.Command.GET_CONFIG);
		SingleAxisCmd cmd = builder.build();
		m_socket.send(cmd.toByteArray());
		byte[] reply = m_socket.recv(0);
		try {
			MotorConfig config = MotorConfig.parseFrom(reply);
			if ( config.hasLowLimit() && config.hasHighLimitl() ) {
				if ( store == null ) {
					store = new float[2];
				}
				store[0] = config.getLowLimit();
				store[1] = config.getHighLimitl();
				return store;
			}
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("Got invalid MotorConfig response!");
			s_logger.error("Exception: "+e);
		}
		return null;
	}

	@Override
	public boolean setPosition(float position) {
		if ( ! isConnected() ) {
			s_logger.warn("Client is not connected!");
		}
		SingleAxisCmd.Builder builder = SingleAxisCmd.newBuilder();
		builder.setCmd(SingleAxisCmd.Command.SET_MOTOR);
		builder.setMsg(
				MotorMsg.newBuilder().setMode(MotorMsg.Mode.CTRL_ABS_POS).setPosition(position)
				);
		SingleAxisCmd cmd = builder.build();
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
		SingleAxisCmd.Builder builder = SingleAxisCmd.newBuilder();
		builder.setCmd(SingleAxisCmd.Command.SET_MOTOR);
		builder.setMsg(
				MotorMsg.newBuilder().setMode(MotorMsg.Mode.CTRL_REL_POS).setPosition(offset)
				);
		SingleAxisCmd cmd = builder.build();
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
