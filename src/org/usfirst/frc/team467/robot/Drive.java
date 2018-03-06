package org.usfirst.frc.team467.robot;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.simulator.communications.RobotData;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends DifferentialDrive {
	private ControlMode controlMode;

	private static final Logger LOGGER = Logger.getLogger(Drive.class);
	private DecimalFormat df = new DecimalFormat("####0.00");

	// Single instance of this class
	private static Drive instance = null;

	private final TalonSpeedControllerGroup left;
	private final TalonSpeedControllerGroup right;

	// Private constructor

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		if (instance == null) {
			TalonSpeedControllerGroup left;
			TalonSpeedControllerGroup right;

			LOGGER.info("Number of Motors:" + RobotMap.DRIVEMOTOR_NUM);
			if (RobotMap.HAS_WHEELS && RobotMap.DRIVEMOTOR_NUM > 0) {
				LOGGER.info("Creating  Lead Motors");
				WPI_TalonSRX leftLead = new WPI_TalonSRX(RobotMap.LEFT_LEAD_CHANNEL);
				WPI_TalonSRX rightLead = new WPI_TalonSRX(RobotMap.RIGHT_LEAD_CHANNEL);
				WPI_TalonSRX leftFollower1 = null;
				WPI_TalonSRX rightFollower1 = null;
				WPI_TalonSRX leftFollower2 = null;
				WPI_TalonSRX rightFollower2= null;

				if (RobotMap.DRIVEMOTOR_NUM > 2) {
					LOGGER.info("Creating  first set of follower motors");
					leftFollower1 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_1_CHANNEL);
					rightFollower1 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_1_CHANNEL);
				}

				if (RobotMap.DRIVEMOTOR_NUM > 4) {
					LOGGER.info("Creating second set of follower motors");
					leftFollower2 = new WPI_TalonSRX(RobotMap.LEFT_FOLLOWER_2_CHANNEL);
					rightFollower2 = new WPI_TalonSRX(RobotMap.RIGHT_FOLLOWER_2_CHANNEL);
				}

				left = new TalonSpeedControllerGroup(ControlMode.PercentOutput,
						RobotMap.LEFT_DRIVE_SENSOR_IS_INVERTED, leftLead, leftFollower1, leftFollower2);
				right = new TalonSpeedControllerGroup(ControlMode.PercentOutput,
						RobotMap.RIGHT_DRIVE_SENSOR_IS_INVERTED, rightLead, rightFollower1, rightFollower2);
			} else {
				left = new TalonSpeedControllerGroup();
				right = new TalonSpeedControllerGroup();
			}
			instance = new Drive(left, right);

		}
		return instance;
	}

	private Drive(TalonSpeedControllerGroup left, TalonSpeedControllerGroup right) {
		super(left, right);
		this.left = left;
		this.right = right;

		setPIDSFromRobotMap();
	}

	public void readPIDSFromSmartDashboard() {
		

		double kPLeft = Double.parseDouble(SmartDashboard.getString("DB/String 1", "1.6")); // 1.6
		double kPRight = Double.parseDouble(SmartDashboard.getString("DB/String 6", "1.4")); // 1.4
		

		double kIRight = 0.0;
		double kILeft = 0.0;
		
		double kDLeft = Double.parseDouble(SmartDashboard.getString("DB/String 3", "198")); //198
		double kDRight = Double.parseDouble(SmartDashboard.getString("DB/String 8", "165")); //165
		
		double kFLeft = Double.parseDouble(SmartDashboard.getString("DB/String 4", "1.1168")); // 0.0
		double kFRight = Double.parseDouble(SmartDashboard.getString("DB/String 9", "1.2208")); // 0.0
		//		double kFall = 1023.0 / 1402.0;

		left.setPIDF(kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(kPRight, kIRight, kDRight, kFRight);
	}
	public void setPIDSFromRobotMap() {
		double kFRight = RobotMap.RIGHT_DRIVE_PID_F;
		double kFLeft = RobotMap.LEFT_DRIVE_PID_F;

		double kPRight = RobotMap.RIGHT_DRIVE_PID_P;
		double kPLeft = RobotMap.LEFT_DRIVE_PID_P;

		double kIRight = RobotMap.RIGHT_DRIVE_PID_I;
		double kILeft = RobotMap.LEFT_DRIVE_PID_I;

		double kDRight = RobotMap.RIGHT_DRIVE_PID_D;
		double kDLeft = RobotMap.LEFT_DRIVE_PID_D;

		left.setPIDF(kPLeft, kILeft, kDLeft, kFLeft);
		right.setPIDF(kPRight, kIRight, kDRight, kFRight);
	}
	
	public void configPeakOutput(double percentOut) {
		left.configPeakOutput(percentOut);
		right.configPeakOutput(percentOut);
	}

	public void logClosedLoopErrors() {
		left.logClosedLoopErrors("Left");
		right.logClosedLoopErrors("Right");
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

	public void zero() {
		LOGGER.trace("Zeroed the motor sensors.");
		left.zero();
		right.zero();
	}

	public void sendData() {
		RobotData.getInstance().updateDrivePosition(getRightPositionFeet(), getLeftPositionFeet());
	}

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		right.stopMotor();
		left.stopMotor();
	}

	public boolean isStopped() {
		return left.isStopped() && right.isStopped();
	}

	private static final double MAX_POSITION_CONTROL_DISTANCE = 3.0;

	/**
	 * Move the specified number of feet.
	 * 
	 * @param distanceInFeet number of feet to move
	 */
    public void moveFeet(double straightDistanceInFeet) {
        LOGGER.trace("moveFeet dist=" + straightDistanceInFeet);

        // Get the current position of each side.
        double currentLeftPosition = getLeftPositionFeet();
        double currentRightPosition = getRightPositionFeet();
        
        // Get the difference to correct for drift and move it back to straight
        double averageTargetDistance = 0.5 * (currentLeftPosition + currentRightPosition);
        
        // Use the minimum to go either the max allowed distance or to the target
        double sharedTargetDistance = Math.min(straightDistanceInFeet, averageTargetDistance + MAX_POSITION_CONTROL_DISTANCE);
        
        LOGGER.trace("moveFeet sharedTarget=" + df.format(sharedTargetDistance)
                    + " currentPos(L,R)=(" + df.format(currentLeftPosition) + "," + df.format(currentRightPosition) + ")");

        // Convert move distances from feet to ticks.
        double sharedTargetTicks = feetToTicks(sharedTargetDistance);

        // Set the motors.
        left.set(ControlMode.Position, sharedTargetTicks);
        right.set(ControlMode.Position, sharedTargetTicks);
    }
    
    /**
     * Turn the specified number of degrees. 
     * 
     * @param rotationInDegrees number of degrees to turn, positive is CW, negative is CCW.
     */
    public void turnDegrees(double rotationInDegrees) {
        LOGGER.trace("turnDegrees degrees=" + rotationInDegrees);

        // Convert the turn to a distance based on the circumference of the robot wheel base.
        double radius = RobotMap.WHEEL_BASE_WIDTH / 2;
        double angleInRadians = Math.toRadians(rotationInDegrees);
        double turnDistanceInFeet = radius * angleInRadians;

        // Get the current position of each side.
        double currentLeftPosition = getLeftPositionFeet();
        double currentRightPosition = getRightPositionFeet();
        
        // Get the difference to correct for drift and move it back to straight.
        double averageTargetDistance = 0.5 * (currentLeftPosition - currentRightPosition);
        
        // Use the minimum to go either the max allowed distance or to the target
        double sharedTargetDistance = Math.min(turnDistanceInFeet, averageTargetDistance + MAX_POSITION_CONTROL_DISTANCE);
        
        LOGGER.trace("turnDegrees sharedTarget=" + df.format(sharedTargetDistance)
                + " currentPos(L,R)=(" + df.format(currentLeftPosition) + "," + df.format(currentRightPosition) + ")");

        // Convert move distances from feet to ticks.
        double sharedTargetTicks = feetToTicks(sharedTargetDistance);

        // Set the motors.
        left.set(ControlMode.Position, sharedTargetTicks);
        right.set(ControlMode.Position, -sharedTargetTicks);
    }
    
	public double getLeftPositionFeet() {
		double leftLeadSensorPos = ticksToFeet(left.sensorPosition());
		return leftLeadSensorPos;
	}

	public double getRightPositionFeet() {
		double rightLeadSensorPos = -1 * ticksToFeet(right.sensorPosition());
		return rightLeadSensorPos;
	}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		double lowestAbsDist;
		double leftLeadSensorPos = Math.abs(getLeftPositionFeet());
		double rightLeadSensorPos = Math.abs(getRightPositionFeet());
		if (leftLeadSensorPos >= rightLeadSensorPos) {
			lowestAbsDist = rightLeadSensorPos;
		} else {
			lowestAbsDist = leftLeadSensorPos;
		}
		LOGGER.debug("The absolute distance moved: " + lowestAbsDist);
		return lowestAbsDist;
	}

	private double feetToTicks(double feet) {
		double ticks = (feet / (RobotMap.WHEEL_CIRCUMFERENCE / 12.0)) * RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION;
		LOGGER.trace(feet + " feet = " + ticks + " ticks.");
		return ticks;
	}

	private double ticksToFeet(double ticks) {
		double feet = (ticks / RobotMap.WHEEL_ENCODER_CODES_PER_REVOLUTION) * (RobotMap.WHEEL_CIRCUMFERENCE / 12);
		LOGGER.trace(ticks + " ticks = " + feet + " feet.");
		return feet;
	}

	public void setRamp(int elevatorHeight) {
		double heightPercent = (double) (RobotMap.ELEVATOR_BOTTOM_TICKS - elevatorHeight) / (RobotMap.ELEVATOR_BOTTOM_TICKS - RobotMap.ELEVATOR_TOP_TICKS);
		double ramp = MathUtils.weightedAverage(RobotMap.ELEVATOR_LOW_DRIVE_RAMP_TIME, RobotMap.ELEVATOR_HIGH_DRIVE_RAMP_TIME, heightPercent);

		left.setOpenLoopRamp(ramp);
		right.setOpenLoopRamp(ramp);
		LOGGER.trace("Ramp time: "+ ramp);
	}
}