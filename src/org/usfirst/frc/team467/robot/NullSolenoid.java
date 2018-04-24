package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class NullSolenoid extends GrabberSolenoid {
	public NullSolenoid(String name, int forwardChannel, int reverseChannel, boolean exists) {
		super(name, forwardChannel, reverseChannel, exists);
		// TODO Auto-generated constructor stub
	}

	public void open() {
		
	}
	
	public void close() {
		
	}
	
	public boolean exists() {
		return false;
	}
}
