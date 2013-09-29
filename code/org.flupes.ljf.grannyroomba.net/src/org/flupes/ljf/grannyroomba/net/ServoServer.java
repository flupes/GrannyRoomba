package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.Status;
import org.flupes.ljf.grannyroomba.messages.MotorConfigProto.MotorConfig;
import org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg;
import org.flupes.ljf.grannyroomba.messages.MotorStateProto.MotorState;
import org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd;
import org.zeromq.ZMQException;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServoServer extends ZmqServer {

	protected IServo m_servo;

	public ServoServer(int port, IServo servo) {
		super(port);
		m_servo = servo;
	}

	IServo getServo() {
		return m_servo;
	}

	@Override
	public void loop() throws InterruptedException {
		boolean response = false;
		SingleAxisCmd cmd = null;
		try {
			byte[] data = m_socket.recv();
			cmd = SingleAxisCmd.parseFrom(data); 
			m_cmdid += 1;
			switch ( cmd.getCmd() ) {

			case SET_MOTOR:
				MotorMsg msg = cmd.getMsg();
				switch ( msg.getMode() ) {
				case CTRL_ABS_POS:
					response = m_servo.setPosition(msg.getPosition());
					break;	
				case CTRL_REL_POS:
					response = m_servo.setPosition(msg.getPosition());
					break;	
				default:	
					s_logger.error("MotorMsg mode "+msg.getMode()+" not supported!");
				} // switch msg.getMode
				CommandStatus.Builder builder = CommandStatus.newBuilder();
				builder.setId(m_cmdid).setStatus(response?Status.COMPLETED:Status.FAILED);
				CommandStatus reply = builder.build();
				s_logger.info("servo server send reply");
				m_socket.send(reply.toByteArray());
				break;

			case GET_STATE:
				MotorState state = MotorState.newBuilder().setPosition(m_servo.getPosition()).build();
				m_socket.send(state.toByteArray());
				break;

			case GET_CONFIG:
				float[] limits = m_servo.getLimits(null);
				MotorConfig config = MotorConfig.newBuilder().
						setLowLimit(limits[0]).setHighLimitl(limits[1]).
						build();
				m_socket.send(config.toByteArray());
				break;

			default:
				s_logger.warn("SingleAxisCmd " + cmd.getCmd() + " not supported!");
			} // switch cmd.getCmd
		}
		catch (ZMQException e) {
			//			if ( zmq.ZError.EAGAIN != e.getErrorCode() ) {
			//				s_logger.info("loop received an exception different from EAGAIN -> stop now!");
			//			}
			if ( zmq.ZError.ETERM == e.getErrorCode() ) {
				s_logger.debug("ServoServer received ETERM exception while waiting for command");
				// Mark the service has stopped in case of the ETERM was not issued
				// internally by cancel, but by another process
				//				m_state = State.STOPPED;
			}
			else {
				s_logger.warn("Received unexpected exception: " + e.getErrorCode());
				s_logger.warn("  -> ignore silently for now!");
			}
		}
		catch (InvalidProtocolBufferException e) {
			s_logger.warn("Could not decode SingleAxisCmd message properly!");
			s_logger.warn("  -> ignore silently for now!");
		}
		if ( cmd == null ) {
			s_logger.debug("servo server was interrupted before receiving a command");
		}
	}

	@Override
	public int fini() {
		int ret = super.fini();
		s_logger.info("ServoServer canceled");
		return ret;
	}

}
