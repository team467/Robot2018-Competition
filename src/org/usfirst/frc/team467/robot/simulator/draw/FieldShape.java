/**
 * 
 */
package org.usfirst.frc.team467.robot.simulator.draw;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 */
public class FieldShape {
	
	public static final double PIXELS_PER_MAP_INCH = 1;
	
	public final static double FIELD_OFFSET_X =  1.5 * 12.0;
	
	public final static double FIELD_OFFSET_Y = 10.0 * 12.0;
	
	GraphicsContext context;
	
	public void context(GraphicsContext context) {
		this.context = context;
	}
	
	/**
	 * Draws the field 
	 * Field sizes in width x height feet 
	 * 	Field: 29' x 74' (27' x 54" internal)
	 * 	Alliance Station: 22x10
	 * 	Auto Line: 27x2 (10 feet from alliance walls)
	 *  Exchange zone: 4x3 (2 inch tape)
	 *    Exchange hole is 1' 9" wide
	 *  Null Territory: 7ft 11.25in x 6 (2 inch tape)
	 *  Platform Zone: 11' 1.5" x 9' 11.75" [2' tape alliance color]
	 *  Portal: 4' x 12' 11"
	 *    10' on short width
	 *  Wall 1' 6" wide?
	 *  Power Cube Zone: 3' 9" x 3' 6"
	 *  Starting Line: 27' x 2" White tape, 2 ' 6" behind alliance wall
	 *  Player Station: 5' 9" x 1'
	 *  Scale 
	 *    15' from end to end
	 *    Plate is 3' x 4'
	 *    Platform top is 8' 8" x 3' 5.25"
	 *    Ramp is 1' 1"
	 *  Switch
	 *    14' from Alliance STation
	 *    Plates are 3 ' x 4'
	 *    Switch is 12' wide
	 *  Portal 
	 *    1' 2" square opening
	 *  Power cube is 1' 1" x 1' 1" x 11"
	 */
	public void draw() {
				
			context.setStroke(Color.YELLOW);
			context.setLineWidth(2.0 * PIXELS_PER_MAP_INCH);
			
			// Field
			context.setFill(Color.DARKGREY);	
			context.fillRect(
					 0.0, 
					 0.0, 
					74.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					30.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Red Alliance 
			context.setFill(Color.CRIMSON);
			
			// Red Alliance Station
			context.fillRect(
					 0.0,
					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					22.0 * 12.0 * PIXELS_PER_MAP_INCH);

//			// Red Exchange Zone
//			context.fillRect(
//					 0.0,
//					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
//					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
//					22.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Red Switch
			context.setFill(Color.LIGHTGRAY);
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			12.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.setFill(Color.DIMGRAY);
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.fillRect(
			24.0 * 12.0 * PIXELS_PER_MAP_INCH,
			18.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Blue Alliance
			context.setFill(Color.CORNFLOWERBLUE);

			// Blue Alliance Station
			context.fillRect(
					64.0 * 12.0 * PIXELS_PER_MAP_INCH,
					 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					10.0 * 12.0 * PIXELS_PER_MAP_INCH, 
					22.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Blue Switch
			context.setFill(Color.LIGHTGRAY);
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			12.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.setFill(Color.DIMGRAY);
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			 9.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			context.fillRect(
			46.0 * 12.0 * PIXELS_PER_MAP_INCH,
			18.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 4.0 * 12.0 * PIXELS_PER_MAP_INCH, 
			 3.0 * 12.0 * PIXELS_PER_MAP_INCH);
			
			// Center Line
			context.setFill(Color.DIMGREY);
			context.fillRect(
					(37.0 * 12.0 - 1.0) * PIXELS_PER_MAP_INCH,
					  1.5 * 12.0		* PIXELS_PER_MAP_INCH, 
					  2.0 				* PIXELS_PER_MAP_INCH, 
					 27.0 * 12.0        * PIXELS_PER_MAP_INCH);
			
			// Wall Line
			context.setFill(Color.LIGHTGREY);
			context.fillRect(
					(12.0 * 12.0 + 11) 							* PIXELS_PER_MAP_INCH,
					( 1.5 * 12.0 - 2.0)							* PIXELS_PER_MAP_INCH, 
					((74.0 * 12.0) - (12.0 * 12.0 + 11) * 2) 	* PIXELS_PER_MAP_INCH, 
					 2.0 			   							* PIXELS_PER_MAP_INCH);
			
			context.fillRect(
					(12.0 * 12.0 + 11) 							* PIXELS_PER_MAP_INCH,
					 28.5 * 12.0								* PIXELS_PER_MAP_INCH, 
					((74.0 * 12.0) - (12.0 * 12.0 + 11) * 2) 	* PIXELS_PER_MAP_INCH, 
					 2.0 			   							* PIXELS_PER_MAP_INCH);
			
	}

}