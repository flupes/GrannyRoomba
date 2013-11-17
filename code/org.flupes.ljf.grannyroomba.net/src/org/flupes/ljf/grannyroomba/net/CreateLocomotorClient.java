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

import org.flupes.ljf.grannyroomba.ICreateLocomotor;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd.Command;
import org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus;

import com.google.protobuf.InvalidProtocolBufferException;

public class CreateLocomotorClient extends LocomotorClient implements
		ICreateLocomotor {

	int m_oiMode;
	int m_bumps;
	int m_velocity;
	int m_radius;

	public CreateLocomotorClient(String server, int port) {
		super(server, port);
	}

	@Override
	public int getStatus() {
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.STATUS_REQUEST);
		byte[] reply = reqrep(builder.build().toByteArray());
		if ( reply == null ) {
			s_logger.warn("CreateLocomotorClient.getStatus failed!");
			return -1;
//			throw new IllegalStateException("Did not get a reply for getStatus");
		}
		try {
			RoombaStatus status = RoombaStatus.parseFrom(reply);
			m_oiMode = status.getOimode();
			m_bumps = status.getBumps();
			m_velocity = status.getVelocity();
			m_radius = status.getRadius();
			return 0;
		} catch (InvalidProtocolBufferException e) {
			s_logger.error("got invalid response from server!");
			s_logger.error("Exception: "+e);
			throw new IllegalStateException("Could not parse the reply from getStatus");
		}
	}

	@Override
	public int getOiMode() {
		return m_oiMode;
	}

	@Override
	public int getBumps() {
		return m_bumps;
	}

	@Override
	public int getVelocity() {
		return m_velocity;
	}

	@Override
	public int getRadius() {
		return m_radius;
	}

	@Override
	public int drivePosition(float distance, float angle, float velocity) {
		// TODO Auto-generated method stub
		return 0;
	}

}
