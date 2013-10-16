package org.flupes.ljf.grannyroomba;

public interface ILocomotor {

		int stop(int mode);

		int driveVelocity(float speed, float spin, float timeout);
}
