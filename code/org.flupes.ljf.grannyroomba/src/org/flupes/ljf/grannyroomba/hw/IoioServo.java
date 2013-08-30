package org.flupes.ljf.grannyroomba.hw;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;

import org.flupes.ljf.grannyroomba.IServo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoioServo implements IServo {

	public static final int DEFAULT_CENTER_PULSE_WIDTH = 1500;
	public static final int DEFAULT_PULSE_RANGE = 1000;
	public static final int DEFAULT_LOW_LIMIT = -90;
	public static final int DEFAULT_HIGH_LIMIT = 90;

	public final int m_servoPin;
	public final int m_centerPulseWidth;
	public final int m_pulseRange;
	public final float m_lowLimitDegrees;
	public final float m_highLimitDegrees;
	
	protected IOIO m_ioio;
	protected int m_calibrationOffset;
	protected float m_currentPositionDegrees;
	protected PwmOutput m_servoPwmOutput;
	protected final int PWM_FREQ = 100;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	public IoioServo(int ctrlPin, IOIO ioio) {
		m_servoPin = ctrlPin;
		m_centerPulseWidth = DEFAULT_CENTER_PULSE_WIDTH;
		m_pulseRange = DEFAULT_PULSE_RANGE;
		m_lowLimitDegrees = DEFAULT_LOW_LIMIT;
		m_highLimitDegrees = DEFAULT_HIGH_LIMIT;
		m_calibrationOffset = 0;
		m_ioio = ioio;
		init();
	}
	
	public IoioServo(int ctrlPin, IOIO ioio, int center, int range, float low, float high) {
		m_servoPin = ctrlPin;
		m_centerPulseWidth = center;
		m_pulseRange = range;
		m_lowLimitDegrees = low;
		m_highLimitDegrees = high;
		m_calibrationOffset = 0;
		m_ioio = ioio;
		init();
	}
	
	protected boolean init() {
		while ( m_ioio == null ) {
			s_logger.info("Waiting for IOIO to come up...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			m_servoPwmOutput = m_ioio.openPwmOutput(new DigitalOutput.Spec(m_servoPin, Mode.OPEN_DRAIN), PWM_FREQ);
			m_currentPositionDegrees = 0;
			m_servoPwmOutput.setPulseWidth(position2pulse(m_currentPositionDegrees));
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void setCalibrationOffset(int offset) {
		m_calibrationOffset = offset;
	}
	
	public int getCalibrationOffset() {
		return m_calibrationOffset;
	}

	@Override
	synchronized public Float getPosition() {
		return m_currentPositionDegrees;
	}

	@Override
	public float[] getLimits(float[] store) {
		if ( store == null ) {
			store = new float[2];
		}
		store[0] = m_lowLimitDegrees;
		store[1] = m_highLimitDegrees;
		return store;
	}

	@Override
	public synchronized boolean setPosition(float position) {
		if ( m_ioio == null ) return false;
		if ( m_lowLimitDegrees <= position && position <= m_highLimitDegrees ) {
			try {
				m_servoPwmOutput.setPulseWidth(position2pulse(position));
			} catch (ConnectionLostException e) {
				e.printStackTrace();
				return false;
			}
			m_currentPositionDegrees = position;
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean changePosition(float offset) {
		return setPosition(m_currentPositionDegrees+offset);
	}

	protected int position2pulse(float position) {
		return m_centerPulseWidth + m_calibrationOffset 
		+(int)(m_pulseRange*position/(m_highLimitDegrees-m_lowLimitDegrees));

	}
}
