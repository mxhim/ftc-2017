package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.systems.tools.Direction;
import org.firstinspires.ftc.teamcode.systems.tools.DriveSystem;
import org.firstinspires.ftc.teamcode.systems.tools.PIDController;


/**
 * Created by Mahim on 12/4/2017.
 */

public class MecanumDriveSystem {
    private DcMotor                     frontLeftMotor;
    private DcMotor                     rearLeftMotor;
    private DcMotor                     frontRightMotor;
    private DcMotor                     rearRightMotor;
    private BNO055IMU                   imu;
    private double                      p = 0.1,
                                        i = 0.0,
                                        d = 0.0,
                                        setpoint,
                                        range = 2.0;
    private PIDController               pidController;

    public MecanumDriveSystem(HardwareMap hardwareMap) {
        /* MOTORS */
        frontLeftMotor             = hardwareMap.get(DcMotor.class,"front left motor");
        rearLeftMotor              = hardwareMap.get(DcMotor.class,"rear left motor");
        frontRightMotor            = hardwareMap.get(DcMotor.class,"front right motor");
        rearRightMotor             = hardwareMap.get(DcMotor.class,"rear right motor");
        rearRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        /* GYRO */
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit            = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit            = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile  = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled       = true;
        parameters.loggingTag           = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        pidController = new PIDController(p, i, d);
        pidController.setSetpointRange(range);
        pidController.setOutputLimits(0.2);
        pidController.setOutputFilter(0.2);

    }

    public void mecanumDrive(double x, double y, double turn) {
        this.frontLeftMotor.setPower(y - x - turn);
        this.rearLeftMotor.setPower(y + x - turn);
        this.frontRightMotor.setPower(y + x + turn);
        this.rearRightMotor.setPower(y - x + turn);
    }

    public void mecanumDrive(double left_x, double right_x, double left_y, double right_y) {
        this.frontLeftMotor.setPower(left_y + left_x - right_x);
        this.rearLeftMotor.setPower(left_y - left_x + right_x);
        this.frontRightMotor.setPower(right_y - left_x + right_x);
        this.rearRightMotor.setPower(right_y + left_x - right_x);
    }

    public void drive(double frontLeftSpeed,  double rearLeftSpeed,
                      double frontRightSpeed, double rearRightSpeed) {
        this.frontLeftMotor.setPower(frontLeftSpeed);
        this.rearLeftMotor.setPower(rearLeftSpeed);
        this.frontRightMotor.setPower(frontRightSpeed);
        this.rearRightMotor.setPower(rearRightSpeed);
    }

    public void drive(double left, double right) {
        this.frontLeftMotor.setPower(left);
        this.rearLeftMotor.setPower(left);
        this.frontRightMotor.setPower(right);
        this.rearRightMotor.setPower(right);
    }

    public void stop() {
        this.frontLeftMotor.setPower(0.0);
        this.rearLeftMotor.setPower(0.0);
        this.frontRightMotor.setPower(0.0);
        this.rearRightMotor.setPower(0.0);
        this.pidController.reset();
    }

    public double getFrontRightSpeed() {
        return this.frontRightMotor.getPower();
    }

    public double getRearRightSpeed() {
        return this.rearRightMotor.getPower();
    }

    public double getFrontLeftSpeed() {
        return this.frontLeftMotor.getPower();
    }

    public double getRearLeftSpeed() {
        return  this.rearLeftMotor.getPower();
    }

    private void driveForward(double leftSpeed, double rightSpeed) {
        double left  = Math.abs(leftSpeed);
        double right = Math.abs(rightSpeed);
        drive(-left, -right);
    }

    private void driveBackwards(double leftSpeed, double rightSpeed) {
        double left = Math.abs(leftSpeed);
        double right = Math.abs(rightSpeed);
        drive(left, right);
    }

    public void drive(double leftSpeed,  double rightSpeed, Direction direction) {
        if(direction == Direction.FORWARD) {
            driveForward(leftSpeed, rightSpeed);
        } else if(direction == Direction.REVERSE) {
            driveBackwards(leftSpeed, rightSpeed);
        } else {
            stop();
        }
    }

    public double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angles.firstAngle;
    }

    public boolean isFinished() {
        if (((setpoint - range) < getAngle()) && ((setpoint + range) > getAngle())) {
            return true;
        } else {
            return false;
        }
    }

    public void setAngle(double angle) {
        this.setpoint = angle;
        pidController.setSetpoint(setpoint);
        if (isFinished()) {
            stop();
        } else {
            drive(pidController.getOutput(getAngle()), -pidController.getOutput(getAngle()));
        }
    }
}
