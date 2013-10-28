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

package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.CreateLocomotorStub;
import org.flupes.ljf.grannyroomba.ServoStub;
import org.flupes.ljf.grannyroomba.net.LocomotorServer;
import org.flupes.ljf.grannyroomba.net.ServoServer;

public class RobotStubServer {

	private volatile boolean interrupted = false;
	
	public RobotStubServer() {
		
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);

		ServoStub servoStub = new ServoStub();
		ServoServer servoService = new ServoServer(6666, servoStub);
		servoService.start();
		
		CreateLocomotorStub locoStub = new CreateLocomotorStub();
		LocomotorServer locoService = new LocomotorServer(7777, locoStub);
		locoService.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("process interrupted!");
				interrupted = true;
			}
		});

		
		while ( ! interrupted ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("interrupted while sleeping");
			}
		}
		
		locoService.cancel();
		servoService.cancel();
		
	}
	
	public static void main(String[] args) {
		new RobotStubServer();
	}

}
