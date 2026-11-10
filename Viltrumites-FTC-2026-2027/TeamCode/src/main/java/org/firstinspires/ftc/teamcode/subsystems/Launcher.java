package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Encoder-controlled launcher and its continuous-rotation windmill feeder. */
public final class Launcher {
    public enum State {
        IDLE,
        MANUAL,
        DIAGNOSTIC
    }

    private final DcMotorEx launcherMotor;
    private final CRServo windmillServo;
    private State state = State.IDLE;
    private double targetVelocity;
    private double windmillCommand;

    public Launcher(DcMotorEx launcherMotor, CRServo windmillServo) {
        this.launcherMotor = launcherMotor;
        this.windmillServo = windmillServo;

        // FORWARD is the default direction used by the supplied goBILDA example.
        launcherMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        launcherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherMotor.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(
                        RobotConstants.Launcher.PIDF_P,
                        RobotConstants.Launcher.PIDF_I,
                        RobotConstants.Launcher.PIDF_D,
                        RobotConstants.Launcher.PIDF_F));

        // Copied from the supplied source to match the physical feeder installation.
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);
        stop();
    }

    /**
     * Directly applies Gamepad 2 commands. Nothing starts the windmill or launcher automatically.
     *
     * <p>The right trigger commands the launching direction and the left trigger commands the
     * opposite direction. Subtracting the trigger values makes the stronger trigger win and makes
     * equal trigger presses cancel safely to zero. Selecting a speed mode alone does not
     * start the motor. The windmill command remains signed.</p>
     */
    public void updateManual(
            double launcherForwardTrigger,
            double launcherReverseTrigger,
            double windmillStick,
            double selectedMaxVelocityTicksPerSecond) {
        double launcherCommand =
                Range.clip(launcherForwardTrigger, 0.0, 1.0)
                        - Range.clip(launcherReverseTrigger, 0.0, 1.0);
        if (Math.abs(launcherCommand) < RobotConstants.Launcher.MANUAL_TRIGGER_DEADBAND) {
            launcherCommand = 0.0;
        }

        targetVelocity = launcherCommand * selectedMaxVelocityTicksPerSecond;
        if (targetVelocity != 0.0) {
            launcherMotor.setVelocity(targetVelocity);
        } else {
            launcherMotor.setVelocity(0.0);
            launcherMotor.setPower(0.0);
        }

        windmillCommand = Math.abs(windmillStick)
                < RobotConstants.Launcher.WINDMILL_STICK_DEADBAND
                ? 0.0
                : Range.clip(windmillStick, -1.0, 1.0);
        windmillServo.setPower(windmillCommand);
        state = targetVelocity != 0.0 || windmillCommand != 0.0 ? State.MANUAL : State.IDLE;
    }

    /** Safe reduced-speed launcher check; the feeder remains off. */
    public void runLauncherTest() {
        windmillCommand = 0.0;
        windmillServo.setPower(windmillCommand);
        targetVelocity = RobotConstants.Launcher.TEST_VELOCITY_TICKS_PER_SECOND;
        launcherMotor.setVelocity(targetVelocity);
        state = State.DIAGNOSTIC;
    }

    public void runFeederTest(double power) {
        targetVelocity = 0.0;
        launcherMotor.setVelocity(0.0);
        launcherMotor.setPower(0.0);
        windmillCommand = Range.clip(power, -1.0, 1.0);
        windmillServo.setPower(windmillCommand);
        state = State.DIAGNOSTIC;
    }

    public void stop() {
        windmillCommand = 0.0;
        windmillServo.setPower(windmillCommand);
        targetVelocity = 0.0;
        launcherMotor.setVelocity(0.0);
        launcherMotor.setPower(0.0);
        state = State.IDLE;
    }

    public State getState() {
        return state;
    }

    public double getCurrentVelocity() {
        return launcherMotor.getVelocity();
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    public double getWindmillCommand() {
        return windmillCommand;
    }
}
