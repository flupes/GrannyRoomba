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
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;

public class GrannyRoombaConsoleCtrl {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	private CreateLocomotorClient m_locomotor;
	
	public GrannyRoombaConsoleCtrl(String host, int locoPort) {

		m_locomotor = new CreateLocomotorClient(host, locoPort);
		m_locomotor.connect();

		KeyboardController controller = new KeyboardController(System.in, m_locomotor);

		try {
			while ( controller.execute() ) {
				// nothing
			}
			m_locomotor.stop(0);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		m_locomotor.disconnect();
	}

	public static void main(String[] args) {

		String host = System.getProperties().getProperty("host");
		if ( host ==  null ) {
			host = "172.16.0.39";
		}
		int locoPort = Integer.getInteger("port", 3141);
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		new GrannyRoombaConsoleCtrl(host, locoPort);

	}

}
