package org.usfirst.frc.team467.robot.turnpid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Gyrometer;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.TalonSpeedControllerGroup;

import edu.wpi.first.wpilibj.PIDController;

public class GyroPIDController extends PIDController {
	
	private static final Logger LOGGER = LogManager.getLogger(GyroPIDController.class);
	
	private static GyroPIDController instance;
	
	public static GyroPIDController getInstance() {
		
		if (instance == null) {
			instance = new GyroPIDController();
		}
		return instance;
	}
	
	public GyroPIDController() {
		super(RobotMap.GYRO_TURN_PID_P, RobotMap.GYRO_TURN_PID_I, RobotMap.GYRO_TURN_PID_D, RobotMap.GYRO_TURN_PID_F, 
				Gyrometer.getInstance(), Drive.getInstance(), 20);
		setContinuous(false);
		super.setInputRange(-180,180);
		super.setOutputRange(-180,180);
		setPercentTolerance(5.0);
		}
	
	@Override
	public void setSetpoint(double setpoint) {
		// Put in some logging
		super.setSetpoint(setpoint);
		LOGGER.info("setSetPoint = {}", setpoint);
		}
		
	public void setPercentTolerance(double percentage) {
		//logging
		super.setPercentTolerance(percentage);
		LOGGER.info("setPercentTolerance = {}", percentage);
	}
	
	

}
