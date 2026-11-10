package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Robot-centric four-wheel mecanum drive. Wheel powers are commanded independently. */
public final class Drivetrain {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor rearLeft;
    private final DcMotor rearRight;
    private double frontLeftCommand;
    private double frontRightCommand;
    private double rearLeftCommand;
    private double rearRightCommand;

    public Drivetrain(DcMotor frontLeft, DcMotor frontRight, DcMotor rearLeft, DcMotor rearRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;

        configure(frontLeft, DcMotor.Direction.FORWARD);
        configure(frontRight, DcMotor.Direction.REVERSE);
        configure(rearLeft, DcMotor.Direction.FORWARD);
        configure(rearRight, DcMotor.Direction.REVERSE);
        stop();
    }

    private static void configure(DcMotor motor, DcMotor.Direction direction) {
        motor.setPower(0);
        motor.setDirection(direction);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Left stick: forward and strafe; right stick: turn. Front-right strafe
     * remains reversed from the original mixer; front-left strafe is flipped
     * back per the latest wheel test. Rear, forward, and turn terms are unchanged.
     * Normalize all four wheel demands together before applying the front-left correction.
     */
    public void mecanumDrive(double forward, double strafe, double turn, double maxPower) {
        mecanumDrive(forward, strafe, turn, maxPower, 1.0);
    }

    /** TeleOp may scale only the rear wheels' strafe contribution. */
    public void mecanumDrive(double forward, double strafe, double turn,
                             double maxPower, double rearStrafeScale) {
        forward = deadband(forward);
        strafe = deadband(strafe);
        turn = deadband(turn);
        double rearStrafe = strafe * Range.clip(rearStrafeScale, 0.0, 1.0);

        double fl = forward + strafe + turn;
        double fr = forward + strafe - turn;
        double rl = forward - rearStrafe + turn;
        double rr = forward + rearStrafe - turn;
        double divisor = Math.max(1.0,
                Math.max(Math.max(Math.abs(fl), Math.abs(fr)),
                        Math.max(Math.abs(rl), Math.abs(rr))));
        double scale = Range.clip(maxPower, 0.0, 1.0) / divisor;
        setWheelPower(fl * scale, fr * scale, rl * scale, rr * scale);
    }

    /** Logical wheel powers before mixed-motor compensation; use for individual tests. */
    public void setWheelPower(double fl, double fr, double rl, double rr) {
        frontLeftCommand = Range.clip(fl, -1.0, 1.0)
                * RobotConstants.Drive.FRONT_LEFT_POWER_SCALE;
        frontRightCommand = Range.clip(fr, -1.0, 1.0);
        rearLeftCommand = Range.clip(rl, -1.0, 1.0);
        rearRightCommand = Range.clip(rr, -1.0, 1.0);
        frontLeft.setPower(frontLeftCommand);
        frontRight.setPower(frontRightCommand);
        rearLeft.setPower(rearLeftCommand);
        rearRight.setPower(rearRightCommand);
    }

    /** Keeps timed forward/turn autonomous commands compatible with all four wheels. */
    public void setTankPower(double leftPower, double rightPower) {
        setWheelPower(leftPower, rightPower, leftPower, rightPower);
    }

    public void stop() {
        setWheelPower(0, 0, 0, 0);
    }

    public double getFrontLeftCommand() { return frontLeftCommand; }
    public double getFrontRightCommand() { return frontRightCommand; }
    public double getRearLeftCommand() { return rearLeftCommand; }
    public double getRearRightCommand() { return rearRightCommand; }

    private static double deadband(double value) {
        return Math.abs(value) < RobotConstants.Drive.JOYSTICK_DEADBAND ? 0.0 : value;
    }
}
