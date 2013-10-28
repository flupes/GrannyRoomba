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

import java.io.InputStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;

public class KeyboardController {

	private boolean m_active;
	private Scanner m_scanner;
	private CreateLocomotorClient m_locomotor;

	private Timer m_timer;

	KeyboardController(InputStream in, CreateLocomotorClient lclient) {
		m_active = true;
		m_scanner = new Scanner(in);
		m_locomotor = lclient;
		showCommands();

		m_timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					synchronized(m_timer) {
						m_locomotor.getStatus();
					}
				} catch (Exception e) {
					System.err.println("getStatus (on separate thread) failed!");
					e.printStackTrace(System.err);
					m_active = false;
					m_timer.cancel();
				}
			}
		};
		m_timer.schedule(task, 20, 200);

	}

	public boolean execute() {
		String line = m_scanner.next();
		//		System.out.println("line length="+line.length() + " content=["+line+"]");
		//		if ( line.isEmpty() ) {
		//			System.out.println("stop");
		//		}
		//		else {
		synchronized( m_timer ) {
			switch ( line.toLowerCase().charAt(0) ) {
			case 'r':
				System.out.println("forward");
				m_locomotor.driveVelocity(0.2f, 0, 1f);
				break;	
			case 'v':
				System.out.println("backward");
				m_locomotor.driveVelocity(-0.2f, 0, 1f);
				break;	
			case 'd':
				System.out.println("turn left");
				m_locomotor.driveVelocity(0, -1, 1f);
			case 'g':
				System.out.println("turn right");
				m_locomotor.driveVelocity(0, 1, 1f);
				break;	
			case 's':
				System.out.println("status");
				System.out.println("Status:");
				System.out.println("  OI mode  = "+m_locomotor.getOiMode());
				System.out.println("  bumps    = "+m_locomotor.getBumps());
				System.out.println("  velocity = "+m_locomotor.getVelocity());
				System.out.println("  radius   = "+m_locomotor.getRadius());
				break;	
			case 'h':
				showCommands();
				break;	
			case 'q':
				System.out.println("quit");
				m_active = false;
				break;	
			default:
				System.out.println("stop");
				m_locomotor.stop(0);
			}
		}
		//		}
		if ( !m_active ) {
			m_timer.cancel();
		}
		return m_active;
	}

	protected void showCommands() {
		System.out.println("r: forward");
		System.out.println("v: backward");
		System.out.println("d: left turn");
		System.out.println("g: right turn");
		System.out.println("s: get status");
		System.out.println("h: prints this message");
		System.out.println("q: quit");
		System.out.println("any other key: stop");
	}
}
