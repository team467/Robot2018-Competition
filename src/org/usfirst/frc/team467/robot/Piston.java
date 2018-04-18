package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Piston {
	DoubleSolenoid solenoid;
	State state;
	
	public enum State {
		OPEN, 
		CLOSE, 
		NONEXISTENT;
	}
	
	public Piston(int openChannel, int closeChannel, boolean exists) {
		if(!exists) {
			state = State.NONEXISTENT;
			return;
		}
		solenoid = new DoubleSolenoid(openChannel, closeChannel);
		state = State.CLOSE;
		
	}
	
	public State getState() {
		return state;
	}
	
	public void open() {
		if(state == State.CLOSE) {
			solenoid.set(DoubleSolenoid.Value.kForward);
			state = State.OPEN;
		}
	}
	
	public void close() {
		if(state == State.OPEN) {
			solenoid.set(DoubleSolenoid.Value.kReverse);
			state = State.CLOSE;
		}
	}
	
	public void reset() {
		close();
		state = State.CLOSE;
	}

}
