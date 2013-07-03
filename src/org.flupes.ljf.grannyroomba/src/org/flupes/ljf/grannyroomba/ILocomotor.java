package org.flupes.ljf.grannyroomba;

public interface ILocomotor extends ISubsystem {

	/** Translate the base forward or backward by the given amount.
	 * Positive distances move forward, negative move backward.
	 * @param distance		distance to travel in meters
	 */
	void translate(float distance) throws EOutOfRange, EBusy, EInterrupted, ETimeout, EFailure;
	
	/** Rotate in place by the given angle.
	 * 	Positive angles rotates to the right, negative to the left.
	 * @param angle			angle to rotate (radians)
	 */
	void rotate(float angle) throws EOutOfRange, EBusy, EInterrupted, ETimeout, EFailure;
}
