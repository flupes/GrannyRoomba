package org.flupes.ljf.grannyroomba.net;

import org.flupes.ljf.grannyroomba.ILocomotor;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.Status;
import org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.flupes.ljf.grannyroomba.messages.MotorConfigProto.MotorConfig;
import org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg;
import org.flupes.ljf.grannyroomba.messages.MotorStateProto.MotorState;
import org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd;

import org.zeromq.ZMQException;

import com.google.protobuf.InvalidProtocolBufferException;

public class LocomotorServer extends ZmqServer {

	protected final ILocomotor m_locomotor;

	public LocomotorServer(int port, ILocomotor servo) {
		super(port);
		m_locomotor = servo;
	}

	@Override
	public void loop() throws InterruptedException {
		LocomotionCmd cmd = null;
		try {
			byte[] data = m_socket.recv();
			CommandStatus.Builder builder = CommandStatus.newBuilder();
			cmd = LocomotionCmd.parseFrom(data); 
			m_cmdid += 1;
			switch ( cmd.getCmd() ) {

			case STOP:
				m_locomotor.stop(cmd.getStop().getMode().getNumber());
				builder.setId(m_cmdid).setStatus(Status.BUSY);
				m_socket.send(builder.build().toByteArray());
				break;

			case DRIVE_VELOCITY:
				DriveVelocityMsg msg = cmd.getDriveVelocity();
				m_locomotor.driveVelocity(msg.getSpeed(), msg.getCurvature(), msg.getTimeout());
				builder.setId(m_cmdid).setStatus(Status.BUSY);
				m_socket.send(builder.build().toByteArray());
				break;

			default:
				s_logger.warn("LocomotionCmd " + cmd.getCmd() + " not supported!");
			} // switch cmd.getCmd
		}
		catch (ZMQException e) {
			//			if ( zmq.ZError.EAGAIN != e.getErrorCode() ) {
			//				s_logger.info("loop received an exception different from EAGAIN -> stop now!");
			//			}
			if ( zmq.ZError.ETERM == e.getErrorCode() ) {
				s_logger.info("Received ETERM exception while waiting for command");
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
			s_logger.warn("Could not decode LocomotionCmd message properly!");
			s_logger.warn("  -> ignore silently for now!");
		}
		if ( cmd != null ) {
			// is_Alive may have changed while in the blocking recv
		}
		else {
			s_logger.info("server was not looping anymore -> this should be correct!");
		}
	}

}
