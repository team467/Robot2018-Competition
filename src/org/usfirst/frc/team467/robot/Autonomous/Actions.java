package org.usfirst.frc.team467.robot.Autonomous;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.Elevator;
import org.usfirst.frc.team467.robot.Elevator.Stops;
import org.usfirst.frc.team467.robot.Grabber;
import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup.ConcurrentActions;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup.MultiCondition;
import org.usfirst.frc.team467.robot.simulator.DriveSimulator;

public class Actions {

	private static final Logger LOGGER = Logger.getLogger(Actions.class);

	private static AutoDrive drive = (RobotMap.useSimulator) ? DriveSimulator.getInstance() : Drive.getInstance();
	
	private static double mirrorTurns = 1.0;
	
	public static void startOnLeft() {
		mirrorTurns = -1.0;
	}
	
	public static final Action nothing(){
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.moveLinearFeet(0));
	}

	public static Action wait(double duration) {
		String actionText = "Do Nothing";
		return new Action(actionText,
				new ActionGroup.Duration(duration),
				() -> drive.moveLinearFeet(0));
	}

	public static final Action nothingForever(){
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> false,
				() -> drive.moveLinearFeet(0));
	}

	public static ActionGroup doNothing(){
		ActionGroup mode = new ActionGroup("none");
		mode.addAction(nothing());
		return mode;
	}

	public static Action print(String message) {
		return new Action(
				"Print custom message",
				new ActionGroup.RunOnce(() -> LOGGER.info(message)));
	}

	public static Action grabCube() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Grabbing cube",
				new ActionGroup.Duration(1.0),
				//new ActionGroup.RunOnce(
				() -> grabber.grab());
	}
	
	public static Action lockCube() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Starting: Locking cube",
				new ActionGroup.Duration(0.5),
				() -> grabber.startGrab());
	}

	public static Action grabCubeWhileDriving(double distance) {
		Grabber grabber = Grabber.getInstance();
		drive.zero();
		MultiCondition multicondition = new MultiCondition(
				new ActionGroup.ReachDistance(distance), 
				() -> grabber.hasCube());
		
		ConcurrentActions concurrentaction = new ConcurrentActions(
				() -> grabber.grab(RobotMap.MAX_GRAB_SPEED),
				() -> drive.moveLinearFeet(distance));

		return new Action(
				"Grabbing cube and driving forward",
				multicondition,
				concurrentaction);
	}

	public static Action releaseCube() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Releasing cube",
				//new ActionGroup.RunOnce(
				new ActionGroup.Duration(1.0),
				() -> grabber.release());
	}

	public static Action pauseGrabber() {
		Grabber grabber = Grabber.getInstance();
		return new Action(
				"Pausing grabber",
				new ActionGroup.RunOnce(() -> grabber.pause()));
	}

	public static Action elevatorToFloor() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to lowest level", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.basement)));
	}
	
	public static Action arcTurn(double rotation, double distance) {
		String actionText = "Move " + distance + " feet and turn ";
		return new Action(actionText,
				new ActionGroup.ReachDistance(drive.calculateArc(rotation, distance)), () -> drive.arcTurn(
						drive.calculateArc(rotation, distance), drive.calculateOuterArc(rotation, distance), rotation));
	}
	
	public static ActionGroup arc(double rotation, double distance) {
		String actionGroupText = "Move " + distance + " feet and turn ";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(arcTurn(rotation, distance));
		return mode;
	}
	
	
	
	public static Action archTurn(double rotation, double displacement) {
		String actionText = "Move " + displacement + " feet and turn ";
		return new Action(actionText,
				new ActionGroup.ReachDistance(drive.calculateArch(rotation, displacement)), () -> drive.archTurn(
						drive.calculateArch(rotation, displacement), drive.calculateOuterArch(rotation, displacement), rotation));
	}
	
	public static ActionGroup arch(double rotation, double displacement) {
		String actionGroupText = "Move " + displacement + " feet and turn ";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(archTurn(rotation, displacement));
		return mode;
	}

	public static Action elevatorToSwitch() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going up to switch height", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.fieldSwitch)));
	}

	public static Action elevatorToLowScale() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to lower level on scale",
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.lowScale)));
	}

	public static Action elevatorToHighScale() {
		Elevator elevator = Elevator.getInstance();
		return new Action(
				"Elevator going to higher level on scale", 
				new ActionGroup.RunOnce(() -> elevator.moveToHeight(Stops.highScale)));		
	}

	public static Action zeroDistance() {
		return new Action(
				"Zeroing the distance",
				new ActionGroup.RunOnce(() -> drive.zero()));
	}

	/**
	 * 
	 * @param Distance moves robot in feet.
	 * @return
	 */
	public static Action moveDistanceForward(double distance) {
		String actionText = "Move forward " + distance + " feet";
		return new Action(actionText,
				new ActionGroup.ReachDistance(distance),
				() -> drive.moveLinearFeet(distance));
	}

	/**
	 * 
	 * @param rotationInDegrees Rotates robot in radians. Enter rotation amount in Degrees.
	 * 
	 */
	public static Action moveturn(double rotationInDegrees) {
		String actionText = "Rotate " + rotationInDegrees + " degrees.";
		return new Action(actionText,
				new ActionGroup.ReachDistance(rotationInDegrees),
				() -> drive.rotateByAngle(rotationInDegrees));
	}

	public static boolean moveDistanceComplete(double distance) {
		double distanceMoved = drive.absoluteDistanceMoved();

		LOGGER.debug("Distances - Target: " + Math.abs(distance) + " Moved: " + distanceMoved);
		if (distanceMoved >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
			LOGGER.info("Finished moving " + distanceMoved + " feet");
			return true;
		} else {
			LOGGER.info("Still moving " + distanceMoved + " feet");
			return false;
		}
	}

	public static ActionGroup move(double distance) {
		String actionGroupText = "Move forward " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveDistanceForward(distance));
		return mode;
	}

	public static ActionGroup turn(double degrees) {
		String actionGroupText = "Turn " + degrees + " degrees";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(zeroDistance());
		mode.addAction(moveturn(mirrorTurns * degrees));
		return mode;
	}

	public static ActionGroup start() {
		String actionGroupText = "Lower grabber down and move elevator to safe height";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(lockCube());
		mode.addAction(elevatorToSwitch());
		return mode;
	}

	public static ActionGroup crossAutoLine(){
		String actionGroupText = "Go straight to cross the auto line.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(10.0));
		return mode;
	}
	
	// TEST ACTIONS

	public static ActionGroup fourFootSquare() {
		String actionGroupText = "Move in 4 foot square.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		return mode;
	}

	public static ActionGroup testGrab() {
		String actionGroupText = "Testing grab with a 2 foot move.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(elevatorToFloor());
		mode.addAction(grabCubeWhileDriving(2));
		return mode;
	}

	// BASIC ACTIONS

	public static ActionGroup centerBasicSwitchLeft() {
		String actionGroupText = "Start in center, put cube on left switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0)); 
		mode.addActions(turn(-90));
		mode.addActions(move(5.27)); 
		mode.addActions(turn(90));
		mode.addActions(move(4.34)); 
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		return mode;
	}

	public static ActionGroup centerBasicSwitchRight() {
		String actionGroupText = "Start in center, put cube on right switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(4.0));
		mode.addActions(turn(90));
		mode.addActions(move(4.27)); 
		mode.addActions(turn(-90));
		mode.addActions(move(4.34));
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		return mode;
	}

	public static ActionGroup basicSwitchOurSide() {
		String actionGroupText = "Put cube on our side switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(arc(32, 8.4)); 
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		
		return mode;
	}

	public static ActionGroup basicScaleOurSide(){
		String actionGroupText = "Put cube on our side scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(10));
		mode.addActions(arc(18, 11.5));
		mode.addAction(elevatorToHighScale());
		//mode.addActions(move(0.375));
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		return mode;
	}

	public static ActionGroup basicScaleOppositeSide(){
		String actionGroupText = "Put cube on opposite side scale";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(start());
		mode.addActions(move(15));
		mode.addActions(arc(90, 3));
		mode.addActions(move(12));
		mode.addActions(arc(-110, 2.5));
		mode.addAction(elevatorToHighScale());
		//mode.addActions(move(3.4));
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		return mode;
	}

	// Advanced
	
	public static ActionGroup advancedSwitchOurSideScaleOurSide() {
		String actionGroupText = "Put cube on our side scale and second on our side switch.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(basicScaleOurSide());

		// pick up cube
		mode.addActions(move(-1.0)); 
		mode.addAction(elevatorToFloor());
		mode.addActions(turn(-68)); 
		mode.addAction(grabCubeWhileDriving(10.42));
	
		// release cube into switch
//		mode.addActions(move(0.1));
		mode.addAction(elevatorToSwitch());
		mode.addAction(releaseCube());
		mode.addAction(pauseGrabber());
		
		
//		// pick up cube
//				mode.addActions(move(-1.0)); 
//				mode.addAction(elevatorToFloor());
//				mode.addActions(turn(-90)); 
//				mode.addActions(move(5.81)); 
//				mode.addActions(turn(50));
//				mode.addAction(grabCubeWhileDriving(5.8));
//			
//				// release cube into switch
////				mode.addActions(move(0.1));
//				mode.addAction(elevatorToSwitch());
//				mode.addAction(releaseCube());
//				mode.addAction(pauseGrabber());
		return mode;
	}

	public static ActionGroup advancedSwitchOurSideScaleOpposite() {
		String actionGroupText = "Put cube on our side switch then opposite side scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(basicSwitchOurSide());

		//pick up cube
		mode.addActions(move(-2.0));
		mode.addActions(turn(90));
		mode.addActions(move(5.81));
		mode.addActions(turn(-90 - 35));
		mode.addActions(move(3.5));
		mode.addActions(move(-3.5));
		mode.addActions(turn(125-90));
//		mode.addActions(move(2.0 + 12.0));
//		mode.addActions(turn(-50));
//		mode.addActions(move(1.5));
//		mode.addAction(grabCube());
//		mode.addActions(move(-1.5));
		
		// place cube in scale
//		mode.addActions(turn(50));
//		mode.addActions(move(4.0));
//		mode.addActions(turn(100));
//		mode.addAction(elevatorToHighScale());
//		mode.addActions(move(3.4));
//		mode.addAction(releaseCube());
//		mode.addAction(pauseGrabber());
		return mode;
	}

	public static ActionGroup advancedSwitchOppositeScaleOurSide() {
		String actionGroupText = "Start on our side scale then get another.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(basicScaleOurSide());

		// pick up cube
		mode.addActions(move(-1.25)); 
		mode.addAction(elevatorToFloor());
		mode.addActions(turn(-68)); 
		mode.addActions(move(9.8));
		mode.addAction(elevatorToSwitch());
		mode.addActions(move(-9.8));
		mode.addActions(turn(68)); 
		mode.addActions(move(1.0)); 
//		mode.addActions(move(1.0)); 
		//mode.addAction(grabCube());

		return mode;
	}

	public static ActionGroup advancedSwitchOppositeScaleOpposite() {
		String actionGroupText = "Start on Right side, put cube on left switch and second on left scale.";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(basicScaleOppositeSide());
		
		return mode;
	}

}
