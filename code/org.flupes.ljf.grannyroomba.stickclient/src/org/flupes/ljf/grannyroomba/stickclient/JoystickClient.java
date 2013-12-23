package org.flupes.ljf.grannyroomba.stickclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import org.flupes.ljf.grannyroomba.RoombaLocomotorModel;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class JoystickClient {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected enum State { REST, MOVE, ROTATE };
	protected State m_state;

	protected Controller m_stick;
	protected Component m_axis[] = new Component[3]; // 0=X 1=Y 2=RZ
	protected Component m_slider;
	protected Component m_pov;

	private float m_currSlider;
	private float m_prevSlider;
	private float m_minTilt;
	private float m_maxTilt;
	private Float m_currentTilt;

	protected ServoClient m_servoClient;
	protected CreateLocomotorClient m_locoClient;


	protected static final float DEAD_ZONE = 0.1f;
	protected static final float TILT_INCR = 10.0f;

	JoystickClient(ServoClient servo, CreateLocomotorClient loco) {
		m_stick = findSuitableStick();
		if ( m_stick == null ) {
			s_logger.error("Could not find an appropriate joystick!");
		}
		else {
			m_state = State.REST;
			m_servoClient = servo;
			m_locoClient = loco;
			connect();
			s_logger.info("JoystickClient started.");
		}
	}

	Controller getController() {
		return m_stick;
	}

	boolean isConnected() {
		return (m_stick==null)?false:true;
	}

	public boolean poll() {

		m_stick.poll();

		float x = m_axis[0].getPollData();
		float y = m_axis[1].getPollData();
		float rz = m_axis[2].getPollData();
		float hat = m_pov.getPollData();
		m_currSlider = m_slider.getPollData();
		//		s_logger.trace("x= "+x+" / y="+y+" / rz="+rz+" / slider="+m_currSlider);

		/*
		if ( Math.abs(m_currSlider - m_prevSlider) > DEAD_ZONE/4 ) {
			m_prevSlider = m_currSlider;
			float angle = (m_minTilt+m_maxTilt)/2 + m_currSlider*(m_maxTilt-m_minTilt)/2;
			m_servoClient.changePosition(angle);
			s_logger.debug("new slider value=" + m_currSlider + " -> angle="+angle);
		}
		 */
		float speedScale = (1.0f-m_currSlider)/2.0f*0.9f+0.1f;

		if ( hat == Component.POV.UP ) {
			m_currentTilt += TILT_INCR;
			if ( m_currentTilt > m_maxTilt ) {
				m_currentTilt = m_maxTilt;
			}
			m_servoClient.changePosition(m_currentTilt);
		}
		if ( hat == Component.POV.DOWN ) {
			m_currentTilt -= TILT_INCR;
			if ( m_currentTilt < m_minTilt ) {
				m_currentTilt = m_minTilt;
			}
			m_servoClient.changePosition(m_currentTilt);
		}

		boolean deadX = Math.abs(x)<DEAD_ZONE;
		boolean deadY = Math.abs(y)<DEAD_ZONE;
		boolean deadRZ = Math.abs(rz)<DEAD_ZONE;

		if ( deadX && deadY && deadRZ ) {
			if ( m_state != State.REST ) {
				m_state = State.REST;
				m_locoClient.stop(0);
				s_logger.debug("new state: REST");
			}
		}
		else {
			if ( m_state == State.REST ) {
				if ( deadRZ ) {
					m_state = State.MOVE;
					s_logger.debug("new state: MOVE");
				}
				else {
					m_state = State.ROTATE;
					s_logger.debug("new state: ROTATE");
				}
			}
		}
		if ( m_state == State.ROTATE ) {
			float spin = -rz * RoombaLocomotorModel.ALLOWED_ANGULAR_VELOCITY;
			s_logger.debug("rz="+rz+" -> rotate spin="+spin);
			m_locoClient.driveVelocity(0, spin*speedScale, 0);
		}
		if ( m_state == State.MOVE ) {
			float speed = -y * RoombaLocomotorModel.ALLOWED_LINEAR_VELOCITY;
			float spin = x * RoombaLocomotorModel.ALLOWED_ANGULAR_VELOCITY/2;
			if ( speed > 0 ) {
				spin = -spin;
			}
			s_logger.info("x="+x+" | y="+y+" -> driveVelocity("+speed+","+spin+")");
			m_locoClient.driveVelocity(speed*speedScale, spin*speedScale, 0);
		}

		return active();
	}

	private boolean active() {
		return true;
	}

	private void connect() {
		m_servoClient.connect();
		m_locoClient.connect();
		float limits[] = m_servoClient.getLimits(null);
		if ( limits == null ) {
			s_logger.error("Could not get servo position limits!");
		}
		else {
			m_minTilt = limits[0];
			m_maxTilt = limits[1];
			s_logger.info("Tilt limits: [" + m_minTilt + ", " + m_maxTilt + "]");
		}
		m_currentTilt = m_servoClient.getPosition();
		if ( m_currentTilt == null ) {
			s_logger.error("Could not get current servo position!");
		}
		else {
			s_logger.info("Tilt current position: " + m_currentTilt);
		}


	}
	private Controller findSuitableStick() {
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		for ( Controller ca : ce.getControllers() ) {
			String name = ca.getName();
			if ( ca.getControllers().length == 0 ) {
				Controller.Type type = ca.getType();
				if ( type == Controller.Type.STICK ) {
					for ( Component co : ca.getComponents() ) {
						Component.Identifier ident = co.getIdentifier();
						if ( co.isAnalog()==true && co.isRelative()==false ) {
							if ( ident == Component.Identifier.Axis.X ) {
								m_axis[0] = co;
							}
							if ( ident == Component.Identifier.Axis.Y ) {
								m_axis[1] = co;
							}
							if ( ident == Component.Identifier.Axis.RZ ) {
								m_axis[2] = co;
							}
							if ( ident == Component.Identifier.Axis.SLIDER ) {
								m_slider = co;
							}
						}
						if ( co.isAnalog()==false ) {
							if ( ident == Component.Identifier.Axis.POV ) {
								m_pov = co;
							}
						}
					}
					if ( m_axis[0]!= null && m_axis[1]!=null && m_axis[2]!=null 
							&& m_slider!=null && m_pov!=null ) {
						s_logger.info("First STICK controller found with all necessary axes is: " + name);
						return ca;
					}
					else {
						s_logger.info("skipping controller ["+name+"] because it is missing some axis");
					}
				}
				else {
					s_logger.info("skipping controller ["+name+"] of type: "+type);
				}
			}
			else {
				s_logger.info("skipping controller ["+name+"] with sub-controllers");
			}
		}
		return null;
	}

}
