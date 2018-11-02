package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Created by Mahim on 1/9/18.
 */

public class ArmSystem extends Mechanism {
    private Servo leftArmServo;
    private Servo rightArmServo;
    private DcMotor armMotor;
    private static final float OPEN_POSITION = .45F;

    public ArmSystem(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public ArmSystem() {}

    @Override
    public void init(HardwareMap hwMap) {
        this.leftArmServo = hwMap.servo.get(RobotMap.ARM_LEFT_SERVO);
        this.rightArmServo = hwMap.servo.get(RobotMap.ARM_RIGHT_SERVO);
        this.armMotor = hwMap.dcMotor.get(RobotMap.ARM_MOTOR);
        this.rightArmServo.setDirection(Servo.Direction.REVERSE);
        leftArmServo.scaleRange(0, OPEN_POSITION);
        rightArmServo.scaleRange(1 - OPEN_POSITION, 1);
    }

    public void goUp() {
        this.armMotor.setPower(1.0);
    }

    public void goDown() {
        this.armMotor.setPower(-1.0);
    }

    public void stopArmMotor() {
        this.armMotor.setPower(0.0);
    }

    public void triggerArmServo(double position) {
        this.leftArmServo.setPosition(position);
        this.rightArmServo.setPosition(position);
    }

    public double getLeftServoPosition() {
        return this.leftArmServo.getPosition();
    }

    public double getRightServoPosition() {
        return this.rightArmServo.getPosition();
    }
}