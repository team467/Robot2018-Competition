package org.usfirst.frc.team467.robot.Autonomous;

/**
 * A collection of methods that a drive class must implement to run our autonomous modes
 */
public interface AutoDrive {

	void moveLinearFeet(double distance);

	/**
	 * Positive is left, negative is right
	 */
	void rotateByAngle(double rotationInDegrees);
	
	/**
	 * Both inputs in feet.
	 * Positive signedRadius is to the right, negative is to the left.
	 */
	void arcDrive(double arcLength, double radius);

	/**
	 * Move each side independently. Distances must be equal or opposite.
	 */
	void moveFeet(double leftDistance, double rightDistance);

	boolean isStopped();

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	double absoluteDistanceMoved();

	/**
	 * Resets the current sensor position to zero.
	 */
	void zero();
}