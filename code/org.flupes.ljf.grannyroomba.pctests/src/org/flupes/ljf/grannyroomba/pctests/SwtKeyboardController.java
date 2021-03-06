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

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;
import org.flupes.ljf.grannyroomba.RoombaLocomotorModel;

public class SwtKeyboardController {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	private static final float SPEED_INCR = RoombaLocomotorModel.ALLOWED_LINEAR_VELOCITY/5f;
	private static final float SPIN_INCR = RoombaLocomotorModel.ALLOWED_ANGULAR_VELOCITY/5f;

	private static final float TITL_INCR = 5.0f;

	private ServoClient m_servoClient;
	private CreateLocomotorClient m_locoClient;
	private RoombaLocomotorModel m_model;

	private Float m_tilt = null;
	private Float m_minTilt = null;
	private Float m_maxTilt = null;
	private volatile int m_speed;
	private volatile int m_radius;

	private boolean m_connected;
	private Timer m_timer;

	public SwtKeyboardController(ServoClient sclient, CreateLocomotorClient lclient) {
		m_servoClient = sclient;
		m_locoClient = lclient;
		m_model = new RoombaLocomotorModel(m_locoClient);

		float limits[] = m_servoClient.getLimits(null);
		if ( limits == null ) {
			s_logger.error("Could not get servo position limits!");
		}
		else {
			m_minTilt = limits[0];
			m_maxTilt = limits[1];
			s_logger.info("Tilt limits: [" + m_minTilt + ", " + m_maxTilt + "]");
		}
		m_tilt = m_servoClient.getPosition();
		if ( m_tilt == null ) {
			s_logger.error("Could not get current servo position!");
		}
		else {
			s_logger.info("Tilt current position: " + m_tilt);
		}

		m_connected = true;
		
		m_timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					int ret = m_locoClient.getStatus();
					if ( ret < 0 ) {
						s_logger.error("getStatus REQ/REP in Timer thread failed after timeout");
						stop();
					}
					else {
						// Reset the speed and spin if the robot was stopped.
						// otherwise just ignore the current velocity
						// and radius, assuming that the robot would have
						// always done what ask!
						//				s_logger.debug("Velocity="+m_locoClient.getVelocity()
						//						+" / Radius="+m_locoClient.getRadius());
						m_speed = m_locoClient.getVelocity();
						m_radius = m_locoClient.getRadius();
						if ( m_speed == 0 ) {
							m_model.reset(0, m_model.getSpin());
						}
						if ( m_radius == 0x8000 ||
								m_locoClient.getBumps() == 0x7FFF ) {
							m_model.reset(m_model.getSpeed(), 0);
						}
					}
				} catch (Exception exception) {
					s_logger.error("getStatus REQ/REP in Timer thread failed for an unknown reason");
					s_logger.debug(exception);
					stop();
				}
			}
		};
		m_timer.schedule(task, 50, 500);
	}

	public boolean connected() {
		return m_connected;
	}

	public KeyAdapter controller() {
		return new ControlListener();
	}

	public FocusAdapter stopper() {
		return new FocusLost();
	}

	public void cancel() {
		m_connected = false;
		m_timer.cancel();
	}

	protected void stop() {
		s_logger.warn("stoping the keyboard controller and send a popup notification");
		m_timer.cancel();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell[] shells = Display.getDefault().getShells();
				if ( shells.length < 1 ) {
					s_logger.error("Could not find a parent shell!");
				}
				else {
					if ( shells.length > 1 ) {
						s_logger.warn("More than one shell for this simple UI?");
					}
					MessageBox msg = new MessageBox(shells[0], SWT.OK);
					msg.setMessage("Connection to GrannyRoomba interrupted!\nThe application will terminate now.\nYou can try to restart it when the robot is up again.");
					msg.open();
				}
				m_connected = false;
			}
		});
	}

	class FocusLost extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			s_logger.info("Lost focus -> stop the robot!");
			try {
				m_model.stop();
			} catch (Exception exception) {
				s_logger.error("REQ/REP failed for focusLost");
				s_logger.debug(exception);
				stop();
			}
		}
	}

	class ControlListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (!m_connected) return;
			try {
				boolean ret;
				switch (e.keyCode) {
				case SWT.PAGE_UP:
				case 'h':					
					m_tilt += TITL_INCR;
					if ( m_tilt > m_maxTilt ) {
						m_tilt = m_maxTilt;
					}
					ret = m_servoClient.setPosition(m_tilt);
					s_logger.info("PAGE_UP -> setPosition("+m_tilt+") => "+((ret)?"true":"false"));
					break;
				case SWT.PAGE_DOWN:
				case 'b':
					m_tilt -= TITL_INCR;
					if ( m_tilt < m_minTilt ) {
						m_tilt = m_minTilt;
					}
					ret = m_servoClient.setPosition(m_tilt);
					s_logger.info("PAGE_DOWN -> setPosition("+m_tilt+") => "+((ret)?"true":"false"));
					break;
				case SWT.ARROW_UP:
					m_model.incrementVelocity(SPEED_INCR);
					break;
				case SWT.ARROW_DOWN:
					m_model.incrementVelocity(-SPEED_INCR);
					break;
				case SWT.ARROW_RIGHT:
					m_model.incrementSpin((Math.abs(m_speed)<SPEED_INCR/2)?-SPIN_INCR:-SPIN_INCR/4);
					break;
				case SWT.ARROW_LEFT: 
					m_model.incrementSpin((Math.abs(m_speed)<SPEED_INCR/2)?SPIN_INCR:SPIN_INCR/4);
					break;
				case SWT.SPACE:
					m_model.stop();
					break;
				case SWT.CTRL:
					s_logger.trace("CONTROL pressed -> print telemetry");
					s_logger.info("  Velocity = " + m_speed);
					s_logger.info("  Radius = " + m_radius);
					m_tilt = m_servoClient.getPosition();
					if ( m_tilt != null ) {
						s_logger.info("  Tilt angle = " + m_tilt);
					}
					else {
						s_logger.warn("  invalid position returned!");
					}
					break;

				default:
					// just ignore silently
				}
			} catch (Exception exception) {
				s_logger.error("REQ/REP failed for getStatus in keyPressed processing");
				s_logger.debug(exception);
				stop();
			}
		}
	}

}
