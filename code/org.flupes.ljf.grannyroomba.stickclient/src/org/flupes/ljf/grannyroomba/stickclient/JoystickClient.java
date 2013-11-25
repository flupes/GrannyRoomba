package org.flupes.ljf.grannyroomba.stickclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class JoystickClient {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected enum State { REST, MOVE, ROTATE };
	protected State m_state;

	protected Controller m_stick;
	protected Component m_axis[] = new Component[3]; // 0=X 1=Y 2=RZ
	protected Component m_slider;
	
	private float m_currSlider;
	private float m_prevSlider;
	
	protected ServoClient m_servoClient;
	protected CreateLocomotorClient m_locoClient;

	protected static final float DEAD_ZONE = 0.1f;

	JoystickClient(ServoClient servo, CreateLocomotorClient loco) {
		m_stick = findSuitableStick();
		if ( m_stick == null ) {
			s_logger.error("Could not find an appropriate joystick!");
		}
		else {
			m_state = State.REST;
			m_servoClient = servo;
			m_locoClient = loco;
//			m_servoClient.connect();
//			m_locoClient.connect();
			s_logger.info("JoystickClient started.");
		}
	}
	
	public boolean poll() {

		m_stick.poll();
		
		float x = m_axis[0].getPollData();
		float y = m_axis[1].getPollData();
		float rz = m_axis[2].getPollData();
		m_currSlider = m_slider.getPollData();
//		s_logger.trace("x= "+x+" / y="+y+" / rz="+rz+" / slider="+m_currSlider);
		
		if ( Math.abs(m_currSlider - m_prevSlider) > DEAD_ZONE ) {
			m_prevSlider = m_currSlider;
			s_logger.debug("new slider value: " + m_currSlider);
		}

		boolean deadX = Math.abs(x)<DEAD_ZONE;
		boolean deadY = Math.abs(y)<DEAD_ZONE;
		boolean deadRZ = Math.abs(rz)<DEAD_ZONE;
		
		if ( deadX && deadY && deadRZ ) {
			if ( m_state != State.REST ) {
				m_state = State.REST;
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
		
		return active();
	}
	
	private boolean active() {
		return true;
	}

	private Controller findSuitableStick() {
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		for ( Controller ca : ce.getControllers() ) {
			String name = ca.getName();
			if ( ca.getControllers().length == 0 ) {
				Controller.Type type = ca.getType();
				if ( type == Controller.Type.STICK ) {
					for ( Component co : ca.getComponents() ) {
						if ( co.isAnalog()==true && co.isRelative()==false ) {
							Component.Identifier ident = co.getIdentifier();
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
					}
					if ( m_axis[0]!= null && m_axis[1]!=null && m_axis[2]!=null && m_slider!=null ) {
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
