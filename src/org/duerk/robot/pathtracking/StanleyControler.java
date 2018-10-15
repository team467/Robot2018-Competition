package org.duerk.robot.pathtracking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import org.duerk.robot.pathplanning.Spline2D;
import org.duerk.robot.pathplanning.SplineCourseData;
import org.duerk.robot.utils.Utils;

/**
 */
public class StanleyControler {

    public static final boolean SHOW_ANIMATION = true;

    /**
     * X-Coordinate
     */
    private double x = 0.0;
    public double x() {
        return x;
    }

    /**
     * Y-Coordinate
     */
    private double y = 0.0;
    public double y() {
        return y;
    }

    /**
     * Yaw (right or left twist) angle
     */
    private double yaw = 0.0;
    public double yaw() {
        return yaw;
    }

    /**
     * Velocity
     */
    private double velocity = 0.0;
    public double velocity() {
        return velocity;
    }


    /**
     * Control gain
     */
    private double k = 0.5;

    /**
     * Speed proportional gain
     */
    private double Kp = 1.0;

    /**
     * Time difference in seconds
     */
    private double timeIncrement = 0.1;
    public double timeIncrement() {
        return timeIncrement;
    }

    /**
     * Wheel base of the vehicle in meters
     */
    private double wheelBase = 2.9;

    /**
     * Maximum steering angle of the vehicle in radians
     */
    private double maxSteer = Math.toRadians(30.0);

    /**
     *
     */
    private int currentTargetIndex = 0;
    public int currentTargetIndex() {
        return currentTargetIndex;
    }

    /**
     *
     */
    private double frontAxleError = 0.0;
    public double frontAxleError() {
        return frontAxleError;
    }

    /**
     *
     * @param x
     * @param y
     * @param yaw
     * @param velocity
     */
    public StanleyControler(
        double x,
        double y,
        double yaw,
        double velocity) {
            this.x = x;
            this.y = y;
            this.yaw = yaw;
            this.velocity = velocity;
    }

    /**
     * Update the state of the vehicle. The Stanley Controler uses a biplanYcle model
     * with acceleration and change in steering angle.
     *
     * @param acceleration  change in velocity
     * @param delta the change in the steering angle
     */
    public void update(double acceleration, double delta) {
        delta = Utils.clip(delta, -maxSteer, maxSteer);
        x += velocity * Math.cos(yaw) * timeIncrement;
        y += velocity * Math.sin(yaw)  * timeIncrement;
        yaw += velocity / wheelBase * Math.tan(delta) * timeIncrement;
        yaw = Utils.normalizeAngle(yaw);
        velocity += acceleration * timeIncrement;
    }

    /**
     * Proportional control for the speed.
     *
     * @param targetVelocity the target speed
     * @param currentVelocity the current speed
     */
    public double pidControl(double targetVelocity, double currentVelocity) {
        return Kp * (targetVelocity - currentVelocity);
    }

    /**
     * Stanley steering control
     *
     * @param planX
     * @param planY
     * @param planYaw
     * @param indexOfLastTarget
     * @return
     */
    public double stanleyControl(double[] planX, double[] planY, double[] planYaw, int indexOfLastTarget) {

        double frontAxleError = calculateTargetIndex(planX, planY);

        if (indexOfLastTarget >= currentTargetIndex) {
            currentTargetIndex = indexOfLastTarget;
        }

        // thetaE corrects the heading error
        double thetaE = Utils.normalizeAngle(planYaw[currentTargetIndex] - yaw);
        // thetaD corrects the cross track error
        double thetaD = Math.atan2(k * frontAxleError, velocity);
        // Change in steering control
        double delta = thetaE + thetaD;

        return delta;
    }

    /**
     * Compute index in the trajectory list of the target.
     *
     * @param planX
     * @param planY
     * @return float the front axle error
     */
    public double calculateTargetIndex(double[] planX, double[] planY) {
        // Calculate the position of the  front axle
        double fX = x + wheelBase * Math.cos(yaw);
        double fY = y + wheelBase * Math.sin(yaw);

        // Search nearest point index
        double[] dX = new double[planX.length];
        for (int i=0; i < planX.length; i++) {
            dX[i] = fX - planX[i];
        }

        double[] dY = new double[planY.length];
        for (int i=0; i < planY.length; i++) {
            dY[i] = fY - planY[i];
        }

        ArrayList<Double> d = new ArrayList<>();
        for (int i = 0; i < dX.length; i++) {
            d.add(Math.sqrt(Math.pow(dX[i], 2) + Math.pow(dY[i], 2)));
        }

        frontAxleError = Collections.min(d);

        currentTargetIndex = d.indexOf(frontAxleError);

        double targetYaw = Utils.normalizeAngle(Math.atan2(fY - planY[currentTargetIndex], fX - planX[currentTargetIndex]) - yaw);
        if (targetYaw > 0.0) {
            frontAxleError = -frontAxleError;
        }

        return frontAxleError;
    }

    /**
     * Plot an example of Stanley steering control on a cubic spline. Used for testing.
     * @param args Input args, none used
     */
    public static void main(String args[]) {

        // Target course
        double[] ax = {0.0, 100.0, 100.0, 50.0, 60.0};
        double[] ay = {0.0, 0.0, -30.0, -20.0, 0.0};

        //splineFunction
        SplineCourseData[] course = Spline2D.calculateSplineCourse(ax, ay, 0.1);
        double[] courseX = SplineCourseData.vectorize(course, "x");
        double[] courseY = SplineCourseData.vectorize(course, "y");
        double[] courseYaw = SplineCourseData.vectorize(course, "yaw");
        // cx, cy, cyaw, ck, s = cubic_spline_planner.calc_spline_course(ax, ay, ds=0.1)

        double targetSpeed = 30.0 / 3.6;  // [m/s]

        double maxSimulationTime = 100.0;

        // Initial state (x & y position, yaw, velocity)
        StanleyControler state = new StanleyControler(-0.0, 5.0, Math.toRadians(20.0), 0.0);

        int lastIndex = course.length - 1;
        double time = 0.0;
        ArrayList<Double> x = new ArrayList<Double>();
        x.add(state.x);
        ArrayList<Double> y = new ArrayList<Double>();
        y.add(state.y);
        ArrayList<Double> yaw = new ArrayList<Double>();
        yaw.add(state.yaw);
        ArrayList<Double> velocity = new ArrayList<Double>();
        velocity.add(state.velocity);
        ArrayList<Double> t = new ArrayList<Double>();
        t.add(0.0);
        state.calculateTargetIndex(ax, ay);
        int targetIndex = state.currentTargetIndex();

        // last_idx = len(cx) - 1
        // time = 0.0
        // x = [state.x]
        // y = [state.y]
        // yaw = [state.yaw]
        // v = [state.v]
        // t = [0.0]
        // target_idx, _ = calc_target_index(state, cx, cy)

        while (maxSimulationTime >= time && lastIndex > targetIndex) {
            double acceleration = state.pidControl(targetSpeed, state.velocity());
            double delta = state.stanleyControl(courseX, courseY, courseYaw, targetIndex);
            targetIndex = state.currentTargetIndex();
            state.update(acceleration, delta);
            time += state.timeIncrement();
            x.add(state.x());
            y.add(state.y());
            yaw.add(state.yaw());
            velocity.add(state.velocity());
            t.add(time);
            }
            if (SHOW_ANIMATION) {
                Plot plt = Plot.create();
                plt.plot()
                    .add(Utils.arrayToList(courseX), Utils.arrayToList(courseY))
                    .label("course")
                    .linestyle("-.")
                ;
                plt.plot()
                    .add(x, y)
                    .label("trajectory")
                    .linestyle("-")
                ;
                // plt.plot()
                //     .add(courseX[targetIndex], courseY[targetIndex])
                //     .label("target")
                //     .linestyle(".")
                // ;
                //DecimalFormat numberFormat = new DecimalFormat("#.0000");
                //plt.title("Speed[km/h]:" + numberFormat.format(state.velocity() * 3.6));
                plt.legend();

                try{
                    // plt1.show();
                    plt.show();
                    // plt3.show();
                } catch (IOException ex) {
                    System.err.println(ex);
                } catch (PythonExecutionException ex) {
                    System.err.println(ex);
                }

        }
    }

}
