package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Controls the intake motor and the two corner-pulling continuous-rotation servos. */
public final class Intake {
    public enum State {
        OFF,
        INTAKING,
        REVERSING,
        DIAGNOSTIC
    }

    private final DcMotor intakeMotor;
    private final CRServo leftIntakeServo;
    private final CRServo rightIntakeServo;
    private State state = State.OFF;
    private double command;

    public Intake(DcMotor intakeMotor, CRServo leftIntakeServo, CRServo rightIntakeServo) {
        this.intakeMotor = intakeMotor;
        this.leftIntakeServo = leftIntakeServo;
        this.rightIntakeServo = rightIntakeServo;

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Physical testing showed both side-intake servos were turning the wrong way.
        // These are intentionally opposite the original goBILDA example directions.
        leftIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        rightIntakeServo.setDirection(DcMotorSimple.Direction.FORWARD);
        stop();
    }

    /** Reverse has priority if the driver presses both intake triggers. */
    public void update(double forwardTrigger, double reverseTrigger) {
        double requestedPower;
        if (reverseTrigger > RobotConstants.Intake.TRIGGER_DEADBAND) {
            requestedPower = -reverseTrigger;
            state = State.REVERSING;
        } else if (forwardTrigger > RobotConstants.Intake.TRIGGER_DEADBAND) {
            requestedPower = forwardTrigger;
            state = State.INTAKING;
        } else {
            requestedPower = 0.0;
            state = State.OFF;
        }
        setAll(requestedPower);
    }

    public void testMotor(double power) {
        command = Range.clip(power, -1.0, 1.0);
        intakeMotor.setPower(command);
        state = State.DIAGNOSTIC;
    }

    public void testLeftServo(double power) {
        command = Range.clip(power, -1.0, 1.0);
        leftIntakeServo.setPower(command);
        state = State.DIAGNOSTIC;
    }

    public void testRightServo(double power) {
        command = Range.clip(power, -1.0, 1.0);
        rightIntakeServo.setPower(command);
        state = State.DIAGNOSTIC;
    }

    public void stop() {
        setAll(0.0);
        state = State.OFF;
    }

    public State getState() {
        return state;
    }

    public double getCommand() {
        return command;
    }

    private void setAll(double power) {
        command = Range.clip(power, -1.0, 1.0);
        intakeMotor.setPower(command);
        leftIntakeServo.setPower(command);
        rightIntakeServo.setPower(command);
    }
}
