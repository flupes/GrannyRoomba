package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.Status;
import org.flupes.ljf.grannyroomba.messages.MotorProto.MotorCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQException;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServoServer extends Server {

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
		try {
			byte[] data = m_socket.recv(0);
			MotorCmd cmd = MotorCmd.parseFrom(data); 
			m_cmdid += 1;
			switch ( cmd.getMode() ) {
			case CTRL_ABS_POS:
				response = m_servo.setPosition(cmd.getPosition());
				break;	
			case CTRL_REL_POS:
				response = m_servo.setPosition(cmd.getPosition());
				break;	
			default:	
				s_logger.error("Command "+cmd.getMode()+" not supported!");
			}

		}
		catch (ZMQException e) {
			if ( zmq.ZError.ETERM == e.getErrorCode() ) {
				s_logger.info("Received ETERM exception while waiting for command");
				// Mark the service has stopped in case of the ETERM was not issuee
				// internally by cancel, but by another process
				m_state = State.STOPPED;
			}
			else {
				s_logger.warn("Received unexpected exception: " + e.getErrorCode());
				s_logger.warn("  -> ignore silently for now!");
			}
		} catch (InvalidProtocolBufferException e) {
			s_logger.warn("Could not decode MotorCmd message properly!");
			s_logger.warn("  -> ignore silently for now!");
		}
		if ( isLooping() ) {
			// is_Alive may have changed while in the blocking recv
			CommandStatus.Builder builder = CommandStatus.newBuilder();
			builder.setId(m_cmdid).setStatus(response?Status.COMPLETED:Status.FAILED);
			CommandStatus reply = builder.build();
			m_socket.send(reply.toByteArray(), 0);
		}
	}

}
