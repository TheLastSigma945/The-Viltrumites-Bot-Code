package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Runs the ball-collection arm only while a manual direction control is held. */
public final class Arm {
    public enum State {
        STOPPED,
        MOVING_UP,
        MOVING_DOWN
    }

    private final CRServo armServo;
    private double command;
    private State state = State.STOPPED;

    public Arm(CRServo armServo) {
        this.armServo = armServo;
        armServo.setDirection(RobotConstants.Arm.DIRECTION);
        stop();
    }

    /** Runs only while exactly one direction is held; both or neither immediately stops it. */
    public void update(boolean moveUp, boolean moveDown) {
        if (moveUp == moveDown) {
            stop();
            return;
        }

        command = moveUp
                ? -RobotConstants.Arm.MANUAL_POWER
                : RobotConstants.Arm.MANUAL_POWER;
        armServo.setPower(command);
        state = moveUp ? State.MOVING_UP : State.MOVING_DOWN;
    }

    /** Removes power so the continuous-rotation servo stops when controls are released. */
    public void stop() {
        command = 0.0;
        armServo.setPower(command);
        state = State.STOPPED;
    }

    public double getCommand() {
        return command;
    }

    public State getState() {
        return state;
    }
}
