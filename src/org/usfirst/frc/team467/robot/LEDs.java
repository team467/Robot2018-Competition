package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Relay;

public class LEDs {
	boolean hasCube;
	double lightsOnTime = 75.0;
	double lightsOutTime = 75.0; 
	Relay rightRelay;
	Relay leftRelay;
	OpticalSensor os;
	
	public LEDs(int rChannel, int lChannel) {
		os = OpticalSensor.getInstance();
		rightRelay = new Relay(rChannel);
		leftRelay = new Relay(lChannel);
		hasCube = os.detectedTarget();
	}
	
	public void lightsUp() {
		rightRelay.set(Relay.Value.kForward);
		leftRelay.set(Relay.Value.kForward);
	}
	
	public void lightsOut() {
		rightRelay.set(Relay.Value.kOff);
		leftRelay.set(Relay.Value.kOff);
	}
	
	public void blink() {
		if(lightsOnTime == 0 && lightsOutTime == 0) {
			lightsOnTime = 75.0;
			lightsOutTime = 75.0;
		}
		if (lightsOnTime > 0) {
			lightsUp();
			lightsOnTime -=1;
		}
		else if (lightsOutTime > 0){
			lightsOut();
			lightsOutTime -= 1;
		}
	}
	
	public void act() {
		hasCube = os.detectedTarget();
		if (hasCube == false) {
			lightsUp();
		}
		else if(hasCube) {
			blink();
		}
		
	}
}
