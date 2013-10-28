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

//
// Binds REP socket to tcp://*:4444
// Expects LocomotionCmd and returns a CommandStatus
//

import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.Status;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.zeromq.ZMQ;

public class LocomotorCmdServerTest {

	public static void main (String[] args) throws Exception{
		ZMQ.Context context = ZMQ.context(1);

		//  Socket to talk to clients
		System.out.println("Starting command server...");
		
		ZMQ.Socket socket = context.socket(ZMQ.REP);
		socket.bind ("tcp://*:4444");

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
			id++;
		}

		socket.close();
		context.term();
	}

}