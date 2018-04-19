package org.usfirst.frc.team467.robot.turnpid;

import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Gyrometer;
import org.usfirst.frc.team467.robot.Autonomous.MatchConfiguration;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class GyroPIDController extends PIDController {
	
	private static GyroPIDController instance;
	
	private static double Kp = 0.0;
	private static double Ki = 0.0;
	private static double Kd = 0.0;
	private static double Kf = 0.0;
	
	public static GyroPIDController getInstance() {
		
		if (instance == null) {
			instance = new GyroPIDController(Kp, Ki, Kd, Kf);
		}
		return instance;
	}
	
	public GyroPIDController(double Kp, double Ki, double Kd, double Kf) {
		super(Kp, Ki, Kd, Kf, Gyrometer.getInstance(), Drive.getInstance(), 20);
		}
}
