package org.flupes.ljf.grannyroomba;

public interface IServo {

	/**
	 * Returns the current position of the servo.
	 * (based on the last command, not on an external sensor)
	 * @return
	 */
	Float getPosition();
	
	/**
	 * Returns the limits (low and high stop) of the servo.
	 * @return
	 */
	float[] getLimits(float[] store);
	
	/**
	 * Drive the servo to the desired position.
	 * @return
	 */
	boolean setPosition(float position);
	
	/**
	 * Move the servo by the given offset.
	 * @return
	 */
	boolean changePosition(float offset);
	
}
