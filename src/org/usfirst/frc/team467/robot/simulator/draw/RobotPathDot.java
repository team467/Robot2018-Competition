package org.usfirst.frc.team467.robot.simulator.draw;

import javafx.scene.Group;
import javafx.scene.shape.Circle;

public class RobotPathDot {
	private Group robotPath = new Group();
	private Circle dot = null;
	double y_pos;
	double x_pos;

	public RobotPathDot (double x, double y) {
		x_pos = x;
		y_pos = y;
	}

	public Group createPathElement() {
		dot = new Circle(x_pos, y_pos, 1);

		robotPath.getChildren().add(dot);
		robotPath.setVisible(false);

		return robotPath;
	}

	public void draw() {
		robotPath.relocate(FieldShape.FIELD_OFFSET_Y + y_pos,
				(FieldShape.FIELD_OFFSET_X + x_pos));
		robotPath.setVisible(true);
	}
}
