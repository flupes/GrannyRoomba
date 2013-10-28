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
		byte[] reply = reqrep(builder.build().toByteArray());
		return parseReply(reply);
	}

	@Override
	public int driveVelocity(float speed, float spin, float timeout) {
		if ( ! isConnected() ) {
			s_logger.warn("LocomotorClient is not connected!");
		}
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.DRIVE_VELOCITY).setDriveVelocity(
				DriveVelocityMsg.newBuilder().setSpeed(speed)
				.setSpin(spin)
				.setTimeout(timeout)
				);
		byte[] reply = reqrep(builder.build().toByteArray());
		return parseReply(reply);
	}

	protected int parseReply(byte[] reply) {
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
