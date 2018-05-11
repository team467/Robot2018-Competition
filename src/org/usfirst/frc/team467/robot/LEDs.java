package org.usfirst.frc.team467.robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Relay;

public class LEDs {
	private static final Logger LOGGER = LogManager.getLogger(LEDs.class);
	
	boolean hasCube;
	double lightsOnTime = 25.0;
	double lightsOutTime = 25.0; 
	private Relay rightRelay;
	private Relay leftRelay;
	OpticalSensor os;
	
	private static LEDs leds;
	
	public static LEDs getInstance() {
		if (leds == null) {
			leds = new LEDs(RobotMap.LEFT_RELAY_CHANNEL, RobotMap.RIGHT_RELAY_CHANNEL);
		}
		return leds;
	}
	
	public LEDs(int lChannel, int rChannel) {
		if (!RobotMap.HAS_LEDS) {
			return;
		}
		os = OpticalSensor.getInstance();
		leftRelay = new Relay(lChannel);
		rightRelay = new Relay(rChannel);
		hasCube = os.detectedTarget();
		LOGGER.debug("Initializing LEDs");

	}
	
	public void resetTime() {
		lightsOnTime = 75.0;
		lightsOutTime = 75.0; 
	}
	
	public void lightsUp() {
		rightRelay.set(Relay.Value.kForward);
		leftRelay.set(Relay.Value.kForward);
		LOGGER.debug("LIGHTS UP");
	}
	
	private void lightsOut() {
		rightRelay.set(Relay.Value.kOff);
		leftRelay.set(Relay.Value.kOff);
		LOGGER.debug("LIGHTS OUT");
	}
	
	public void blink() {
		LOGGER.debug("BLINKING");
		if(lightsOnTime == 0 && lightsOutTime == 0) {
			resetTime();
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
	
	public void blinkWhenHasCube() {
		hasCube = os.detectedTarget();
		if (hasCube == false) {
			lightsUp();
		}
		else if(hasCube) {
			blink();
		}
	}
	
	public void lightUpWhenHasCube(){
		hasCube = os.detectedTarget();
		if(hasCube) {
			lightsUp();
		}else {
			lightsOut();
		}
	}
}