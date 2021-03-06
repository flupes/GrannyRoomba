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
// Send commands to tcp://localhost:4444
// using the REQ/REP pattern
//

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.zeromq.ZMQ;

import org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus;
import org.flupes.ljf.grannyroomba.messages.DrivePositionProto.DrivePositionMsg;
import org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd.Command;
import org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg;
import org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg.Mode;

import com.google.protobuf.InvalidProtocolBufferException;


public class LocomotorCmdClientTest {

	public static void main (String[] args){
		ZMQ.Context context = ZMQ.context(1);

		//  Socket to talk to server
		System.out.println("Connecting to command server");

		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.connect ("tcp://localhost:4444");

		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		boolean up = true;
		while ( up ) {
			System.out.println("Available commands:");
			System.out.println("  0: EXIT");
			System.out.println("  1: STOP");
			System.out.println("  2: DRIVE_POSITION");
			System.out.println("  3: DRIVE_VELOCITY");
			System.out.println("Choice = ");
			String str;
			int r = 0;
			try {
				str = input.readLine();
				if ( str != null ) {
					r = Integer.parseInt(str);
				}
				else {
					r = -1;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			LocomotionCmd.Builder builder = null;
			switch (r) {
			case 0: up=false; break; 
			case 1: 
				builder = LocomotionCmd.newBuilder();
				builder.setCmd(Command.STOP);
				builder.setStop(
						StopMsg.newBuilder().setMode(Mode.EMMERGENCY).build()
						);
				break;
			case 2:
				builder = LocomotionCmd.newBuilder();
				builder.setCmd(Command.DRIVE_POSITION);
				builder.setDrivePosition(
						DrivePositionMsg.newBuilder()
						.setDistance(2)
						.setAngle((float)Math.PI/2)
						.build()
						);
				break;
			case 3:
				builder = LocomotionCmd.newBuilder();
				builder.setCmd(Command.DRIVE_VELOCITY);
				builder.setDriveVelocity(
						DriveVelocityMsg.newBuilder()
						.setSpeed(0.5f)
						.setSpin(1)
						.build()
						);
				break;
			default: System.err.println("Not a valid entry!");
			}
			if ( builder != null ) {
				if ( builder.isInitialized() ) {
					LocomotionCmd cmd2send = builder.build();
					System.out.println("Sending: " + cmd2send);
					socket.send(cmd2send.toByteArray(), 0);

					byte[] reply = socket.recv(0);
					try {
						CommandStatus status = CommandStatus.parseFrom(reply);
						System.out.println("Response: " + status);
					} catch (InvalidProtocolBufferException e) {
						System.err.println("Invalid response!");
						e.printStackTrace();
					}
				}
				else {
					System.err.println("Malformed message!");
				}
			}
		}

		socket.close();
		context.term();
	}
}