package org.flupes.ljf.grannyroomba.net;

//
// Binds REP socket to tcp://*:5555
// Expects LocomotionCmd and returns a CommandStatus
//

import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.Status;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.zeromq.ZMQ;

public class cmdServer{

	public static void main (String[] args) throws Exception{
		ZMQ.Context context = ZMQ.context(1);

		//  Socket to talk to clients
		System.out.println("Starting command server...");
		
		ZMQ.Socket socket = context.socket(ZMQ.REP);
		socket.bind ("tcp://*:5555");

		int id = 1;
		while (!Thread.currentThread ().isInterrupted ()) {
			byte[] data = socket.recv(0);

			LocomotionCmd message = LocomotionCmd.parseFrom(data);

			CommandStatus.Builder builder = CommandStatus.newBuilder(); 
			switch ( message.getCmd() ) {
			case STOP:
				System.out.println("got STOP cmd: " + message.getStop());
				builder.setId(id).setStatus(Status.INTERRUPTED);
				break;
			case DRIVE_POSITION:
				System.out.println("got DRIVE_POSITION cmd: " + message.getDrivePosition());
				builder.setId(id).setStatus(Status.COMPLETED);
				break;
			case DRIVE_VELOCITY:
				System.out.println("got DRIVE_VELOCITY cmd: " + message.getDriveVelocity());
				builder.setId(id).setStatus(Status.BUSY);
				break;
			default:
				System.err.println("Invalid command type!");
			}
			CommandStatus reply = builder.build();
			socket.send(reply.toByteArray(), 0);
		}

		socket.close();
		context.term();
	}

}