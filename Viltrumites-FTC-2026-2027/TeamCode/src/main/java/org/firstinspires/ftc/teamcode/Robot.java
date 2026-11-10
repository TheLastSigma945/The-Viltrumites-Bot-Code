package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.RobotConstants;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;

import java.util.ArrayList;
import java.util.List;

/** Owns all hardware mapping so every OpMode initializes the robot consistently. */
public final class Robot {
    public Drivetrain drivetrain;
    public Intake intake;
    public Launcher launcher;
    public Arm arm;

    /**
     * Maps every required device before configuring any of them. This produces one useful error
     * listing all missing/mistyped names instead of a later NullPointerException.
     */
    public void initialize(HardwareMap hardwareMap) throws RobotInitializationException {
        List<String> errors = new ArrayList<>();

        DcMotor frontLeftDrive = getRequired(
                hardwareMap, DcMotor.class, RobotConstants.Hardware.FRONT_LEFT_DRIVE, errors);
        DcMotor frontRightDrive = getRequired(
                hardwareMap, DcMotor.class, RobotConstants.Hardware.FRONT_RIGHT_DRIVE, errors);
        DcMotor leftDrive = getRequired(
                hardwareMap, DcMotor.class, RobotConstants.Hardware.LEFT_DRIVE, errors);
        DcMotor rightDrive = getRequired(
                hardwareMap, DcMotor.class, RobotConstants.Hardware.RIGHT_DRIVE, errors);
        DcMotor intakeMotor = getRequired(
                hardwareMap, DcMotor.class, RobotConstants.Hardware.INTAKE, errors);
        DcMotorEx launcherMotor = getRequired(
                hardwareMap, DcMotorEx.class, RobotConstants.Hardware.LAUNCHER, errors);
        CRServo windmill = getRequired(
                hardwareMap, CRServo.class, RobotConstants.Hardware.WINDMILL, errors);
        CRServo leftIntakeServo = getRequired(
                hardwareMap, CRServo.class, RobotConstants.Hardware.LEFT_INTAKE_SERVO, errors);
        CRServo rightIntakeServo = getRequired(
                hardwareMap, CRServo.class, RobotConstants.Hardware.RIGHT_INTAKE_SERVO, errors);
        CRServo armServo = getRequired(
                hardwareMap, CRServo.class, RobotConstants.Hardware.ARM_SERVO, errors);

        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String error : errors) {
                if (message.length() > 0) {
                    message.append("; ");
                }
                message.append(error);
            }
            throw new RobotInitializationException(message.toString());
        }

        try {
            drivetrain = new Drivetrain(frontLeftDrive, frontRightDrive, leftDrive, rightDrive);
            intake = new Intake(intakeMotor, leftIntakeServo, rightIntakeServo);
            launcher = new Launcher(launcherMotor, windmill);
            arm = new Arm(armServo);
            shutdown();
        } catch (RuntimeException error) {
            shutdown();
            String detail = error.getMessage() == null
                    ? error.getClass().getSimpleName()
                    : error.getMessage();
            throw new RobotInitializationException("Hardware setup failed: " + detail, error);
        }
    }

    /** Stops every powered mechanism. Safe to call repeatedly or after partial initialization. */
    public void shutdown() {
        if (drivetrain != null) {
            drivetrain.stop();
        }
        if (intake != null) {
            intake.stop();
        }
        if (launcher != null) {
            launcher.stop();
        }
        if (arm != null) {
            arm.stop();
        }
    }

    private static <T> T getRequired(
            HardwareMap hardwareMap,
            Class<? extends T> deviceType,
            String name,
            List<String> errors) {
        try {
            return hardwareMap.get(deviceType, name);
        } catch (RuntimeException error) {
            errors.add("Missing or wrong type: '" + name + "' (" + deviceType.getSimpleName() + ")");
            return null;
        }
    }

    public static final class RobotInitializationException extends Exception {
        public RobotInitializationException(String message) {
            super(message);
        }

        public RobotInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
