package org.flupes.ljf.grannyroomba.pctests;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.flupes.ljf.grannyroomba.net.RoombaLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class KeyboardController {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	private static final float SPEED_INCR = 0.2f;
	private static final float SPIN_INCR = 0.2f;
	private static final float MAX_VELOCITY = 500;
	private static final float MAX_RADIUS = 2000;
	private static final float TITL_INCR = 5.0f;

	private ServoClient m_servoClient;
	private RoombaLocomotorClient m_locoClient;

	private Float m_tilt = null;
	private volatile float speed = 0;
	private volatile float prevSpeed = 0;
	private volatile float spin = 0;

	private boolean m_connected;
	private Timer m_timer;

	public KeyboardController(ServoClient sclient, RoombaLocomotorClient lclient) {
		m_servoClient = sclient;
		m_locoClient = lclient;
		m_connected = true;
		m_timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					synchronized(m_timer) {
						m_locoClient.getStatus();
					}
				} catch (Exception e) {
					stop();
				}

				// Reset the speed and spin if the robot was stopped.
				// otherwise just ignore the current velocity
				// and radius, assuming that the robot would have
				// always done what ask!
				//				s_logger.debug("Velocity="+m_locoClient.getVelocity()
				//						+" / Radius="+m_locoClient.getRadius());
				if ( m_locoClient.getVelocity() == 0 ) {
					speed = 0;
					prevSpeed = 0;
				}
				if ( m_locoClient.getRadius() == 0x8000 ||
						m_locoClient.getBumps() == 0x7FFF ) {
					spin = 0;
				}
			}
		};
		m_timer.schedule(task, 20, 200);
	}

	public boolean connected() {
		return m_connected;
	}

	public KeyAdapter controller() {
		return new ControlListener();
	}

	protected void stop() {
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
					msg.setMessage("Connection to GrannyRoomba interrupted!\nThe application will terminated now.\nYou can try to restart it when the robot is up again.");
					msg.open();
					s_logger.warn("a message should have poped!");
				}
				m_connected = false;
			}
		});
	}

	// Static inner class because we do not create an instance of the
	// outer class since it is a main entry point
	class ControlListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (!m_connected) return;
			if ( m_tilt == null ) {
				m_tilt = m_servoClient.getPosition();
			}
			boolean ret;
			prevSpeed = speed;
			boolean newDrive = false; 
			switch (e.keyCode) {
			case SWT.PAGE_UP:
				m_tilt += TITL_INCR;
				ret = m_servoClient.setPosition(m_tilt);
				s_logger.info("PAGE_UP -> setPosition("+m_tilt+") => "+((ret)?"true":"false"));
				break;
			case SWT.PAGE_DOWN:
				m_tilt -= TITL_INCR;
				ret = m_servoClient.setPosition(m_tilt);
				s_logger.info("PAGE_DOWN -> setPosition("+m_tilt+") => "+((ret)?"true":"false"));
				break;
			case SWT.HELP:
				s_logger.info("HELP -> get config and state");
				float[] limits = m_servoClient.getLimits(null);
				m_tilt = m_servoClient.getPosition();
				if ( limits != null ) {
					s_logger.info("  low limit = " + limits[0]);
					s_logger.info("  high limit = " + limits[1]);
				}
				else {
					s_logger.warn("  not motor limits returned!");
				}
				if ( m_tilt != null ) {
					s_logger.info("  current position = " + m_tilt);
				}
				else {
					s_logger.warn("  invalid position returned!");
				}
				break;
			case SWT.ARROW_UP: 
				if ( speed < 1-SPEED_INCR/2 ) speed += SPEED_INCR;
				s_logger.trace("UP pressed -> speed="+speed+" / spin="+spin);
				newDrive=true;
				break;
			case SWT.ARROW_DOWN:
				if ( speed > -1+SPEED_INCR/2 ) speed -= SPEED_INCR;
				s_logger.trace("DOWN pressed -> speed="+speed+" / spin="+spin);
				newDrive=true;
				break;
			case SWT.ARROW_RIGHT:
				if ( spin < 1-SPIN_INCR/2 ) spin += SPIN_INCR;
				s_logger.trace("RIGHT pressed -> speed="+speed+" / spin="+spin);
				newDrive=true;
				break;
			case SWT.ARROW_LEFT: 
				if ( spin > -1+SPIN_INCR/2 ) spin -= SPIN_INCR;
				s_logger.trace("LEFT pressed -> speed="+speed+" / spin="+spin);
				newDrive=true;
				break;
			case SWT.SPACE:
				speed = 0;
				spin = 0;
				s_logger.trace("SPACE pressed -> speed="+speed+" / spin="+spin);
				newDrive=true;
				break;
				//				case KeyEvent.VK_CONTROL:
				//					s_logger.trace("CONTROL pressed -> print telemetry");
				//					m_roomba.printRawTelemetry();
				//					break;

			default:
				// just ignore silently
			}
			// From a slow speed, small radius, we should transition
			// to a slow point turn when speed becomes zero (without 
			// this check, small radius is transformed into high point
			// turn rate!)
			s_logger.trace("prevSpeed=" + prevSpeed + " / currSpeed=" + speed);
			if ( (Math.abs(prevSpeed)>SPEED_INCR/2) && (Math.abs(speed)<SPEED_INCR/2) ) {
				float absSpin = Math.abs(prevSpeed);
				if ( spin > SPIN_INCR/2 ) {
					spin = absSpin;
				}
				else if ( spin < -SPIN_INCR/2 ) {
					spin = -absSpin;
				}
				s_logger.trace("reset spin to :" + spin);
			}
			if ( newDrive ) {
				changeDrive(speed, spin);
			}
		}
	}

	private void changeDrive(float speed, float spin) {
		int velocity;
		int radius;
		if ( Math.abs(speed) < SPEED_INCR/2 ) {
			if ( Math.abs(spin) < SPIN_INCR/2 ) {
				velocity = 0;
				radius = 0x8000;
			}
			else {
				// Zero speed, this is a point turn
				if ( spin > 0 ) {
					radius = 0xFFFF;
				}
				else {
					radius = 0x0001; 
				}
				velocity = (int)(MAX_VELOCITY*Math.abs(spin)/2.5);
			}
		}
		else {
			velocity = (int)(MAX_VELOCITY*speed);
			if ( Math.abs(spin) < SPIN_INCR/2 ) {
				// Zero spin, straight forward or backward move
				radius = 0x8000;
			}
			else {
				// We want the radius to decay exponentially, reduced by half
				// for each increment of the spin (starting at 2000).
				float steps = 1/SPIN_INCR;
				float factor = MAX_RADIUS / (float)Math.pow(2, steps-1);
				float absRadius = factor*(float)Math.pow(2, steps*(1-Math.abs(spin)));
				if ( spin > 0 ) {
					radius = -(int)absRadius;
				}
				else {
					radius = (int)absRadius;
				}
			}
		}
		try {
			synchronized(m_timer) {
				m_locoClient.driveVelocity(velocity, radius, 1.0f);
			}
		} catch (Exception e) {
			stop();
		}
	}

}