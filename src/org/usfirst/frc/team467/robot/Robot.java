/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
// import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.Actions;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.log4j.Logger;
import edu.wpi.first.wpilibj.DigitalInput;

import org.usfirst.frc.team467.robot.XBoxJoystick467.Button;
import org.usfirst.frc.team467.robot.RobotMap.RobotID;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also
 * update the manifest file in the resource directory.
 */

public class Robot extends TimedRobot {
	private static final Logger LOGGER = Logger.getLogger(Robot.class);
	
	// Robot objects
	private DriverStation driverstation;
	private Drive drive;
//	private ActionGroup autonomous;
	
//	private VisionProcessing vision;
	private Gyrometer gyro;
   private Grabber grabber; //not used yet
  // private OpticalSensor opticalsensor;

	int session;
	
	String axis = "No turning";
	
	double rumbleTime = 0.0;
	
	//double count = 0.0;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework
		Logging.init();
		
		// Initialize RobotMap
		RobotMap.init(RobotID.Board);
		
		// Make robot objects
		driverstation = DriverStation.getInstance();
		LOGGER.info("Initialized Driverstation");
		
		drive = Drive.getInstance();
		
		//gyro = Gyrometer.getInstance();
		grabber = Grabber.getInstance();
		
		//gyro.calibrate();
		//gyro.reset();
		
		// Initialize math lookup table
		LookUpTable.init();
		
//		vision = VisionProcessing.getInstance();
		
		// TODO: Implement actions.doNothing
//		autonomous = Actions.doNothing();
		
		//made usb camera and captures video
		//UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		//set resolution and frames per second to match driverstation
		//cam.setResolution(320, 240);
		//cam.setFPS(15);
		
		//TODO: Create list of autonomous modes for selector
		// Setup autonomous mode selectors
	}
	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		drive.logClosedLoopErrors();
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void disabledPeriodic() {
		LOGGER.trace("Disabled Periodic");
		
//		driverstation.navJoy.leftRumble(0.0);
//		driverstation.navJoy.rightRumble(0.0); 
	}
//TODO: Figure out the NetworkTables later.
//	String[] autoList = {"none", "go"};
//			 
//	NetworkTable table = NetworkTable.getTable("SmartDashboard");
//	table.putStringArray("Auto List", autoList);
//	LOGGER.debug("Robot Initialized");
//	
	public void autonomousInit() {
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");
		drive.initMotionMagicMode();
		
//	drive.initPositionMode();
		LOGGER.info(drive);
		// TODO: call appropriate auto modes based on list
		LOGGER.debug("Autonomous init: " + autoMode);
//		switch (autoMode) {
//		case "none":
//			autonomous = Actions.doNothing();
//			break;
//		default:
//			autonomous = Actions.doNothing();
//			break;
//		}
//		LOGGER.info("Init Autonomous:" + autonomous.getName());
//		autonomous.enable();
	}

	public void teleopInit() {
		
		driverstation.readInputs();
		
//		autonomous.terminate();
//		autonomous = Actions.doNothing();
	}

	public void testInit() {
	}

	public void testPeriodic() {
		driverstation.readInputs();

		if (driverstation.getNavJoystick().pressed(Button.b)){ 
			driverstation.getNavRumbler().rumble(150, 0.3);
			LOGGER.info("You pressed b");
		}
		if (driverstation.getDriveJoystick().pressed(Button.b)){ 
			driverstation.getNavRumbler().rumble(150, 1.0);
			LOGGER.info("You pressed b");
		}

		driverstation.periodic();
	}


	public void autonomousPeriodic() {
		
		drive.logClosedLoopErrors();
		drive.publishRawSensorValues();
//		drive.PositionModeMove(drive.feetToTicks(amountToGoLeft), drive.feetToTicks(amountToGoRight));
//		drive.motionMagicMove(amountToGoLeft, amountToGoRight);
		
//		autonomous.run();
		
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		driverstation.readInputs();
		//TODO: Set Min_DRIVE_SPEED in Robot Map.
		double MIN_DRIVE_SPEED = 0.1;
		driverstation.readInputs();
		
		double left = driverstation.getArcadeSpeed();
		double right = driverstation.getArcadeTurn();
		// -1* driverstation.getDriveJoystick().getJoystick()
		LOGGER.info("left " + left + " right " + right);
		LOGGER.info(grabber.hasCube());
		
		if (Math.abs(left) < MIN_DRIVE_SPEED) {
			left = 0.0;
		}
		if (Math.abs(right) < MIN_DRIVE_SPEED) {
			right = 0.0;
		}
		
		driverstation.getNavJoystick().read();
		
		if (grabber.hasCube()) {
			driverstation.getNavRumbler().rumble(100, 1.0);
		}
		
		if (driverstation.getNavJoystick().down(XBoxJoystick467.Button.x)) {
			grabber.grab();
		} else if (driverstation.getNavJoystick().down(XBoxJoystick467.Button.y)) {
			grabber.release();
		} else {
			grabber.pause();
		}
	
		//changed to arcade drive
		drive.arcadeDrive(left, right, true);
	}
}