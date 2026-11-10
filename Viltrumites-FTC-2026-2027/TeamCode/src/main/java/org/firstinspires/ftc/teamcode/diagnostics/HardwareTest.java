package org.firstinspires.ftc.teamcode.diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Safely tests one selected component at a time while the robot is supported on blocks. */
@TeleOp(name = "StarterBot Hardware Test", group = "Diagnostics")
public final class HardwareTest extends OpMode {
    private enum Component {
        FRONT_LEFT_DRIVE,
        FRONT_RIGHT_DRIVE,
        LEFT_DRIVE,
        RIGHT_DRIVE,
        INTAKE_MOTOR,
        LEFT_INTAKE_SERVO,
        RIGHT_INTAKE_SERVO,
        ARM_SERVO,
        WINDMILL_FEEDER,
        LAUNCHER
    }

    private final Robot robot = new Robot();
    private boolean initialized;
    private String initializationError = "";
    private int selectedIndex;
    private boolean previousLeftBumper;
    private boolean previousRightBumper;

    @Override
    public void init() {
        try {
            robot.initialize(hardwareMap);
            initialized = true;
            telemetry.addData("Hardware Initialization", "OK");
        } catch (Robot.RobotInitializationException error) {
            initialized = false;
            initializationError = error.getMessage();
            robot.shutdown();
            telemetry.addData("Hardware Initialization", "FAILED");
            telemetry.addData("Fix configuration", initializationError);
        }
        telemetry.addLine("PUT THE ROBOT ON BLOCKS BEFORE STARTING");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (!initialized) {
            telemetry.addLine("TEST DISABLED - hardware initialization failed");
            telemetry.addData("Fix configuration", initializationError);
            telemetry.update();
            return;
        }

        Component[] components = Component.values();
        if (gamepad1.right_bumper && !previousRightBumper) {
            selectedIndex = (selectedIndex + 1) % components.length;
        }
        if (gamepad1.left_bumper && !previousLeftBumper) {
            selectedIndex = (selectedIndex - 1 + components.length) % components.length;
        }
        previousRightBumper = gamepad1.right_bumper;
        previousLeftBumper = gamepad1.left_bumper;

        robot.shutdown();
        String activeTest = runDriveValidation();
        if (activeTest == null) {
            activeTest = runSelectedComponent(components[selectedIndex]);
        }

        telemetry.addLine("ROBOT MUST BE ON BLOCKS");
        telemetry.addData("Selected", components[selectedIndex]);
        telemetry.addData("Change selection", "LB previous | RB next");
        telemetry.addData("Run selected", "Hold A forward | Hold X reverse");
        telemetry.addData("Drive validation", "D-pad: drive/turn | B/Y: strafe right/left");
        telemetry.addData("Currently testing", activeTest == null ? "NOTHING (safe)" : activeTest);
        telemetry.addData(
                "Drive commands", "FL %.2f | FR %.2f | RL %.2f | RR %.2f",
                robot.drivetrain.getFrontLeftCommand(),
                robot.drivetrain.getFrontRightCommand(),
                robot.drivetrain.getRearLeftCommand(),
                robot.drivetrain.getRearRightCommand());
        telemetry.addData(
                "Launcher velocity", "%.0f ticks/s", robot.launcher.getCurrentVelocity());
        telemetry.addData(
                "Arm", "%s | power %.2f", robot.arm.getState(), robot.arm.getCommand());
        if (components[selectedIndex] == Component.ARM_SERVO) {
            telemetry.addLine("ARM TEST: hold A for up | hold X for down");
        }
        if (components[selectedIndex] == Component.LAUNCHER) {
            telemetry.addLine("LAUNCHER WARNING: clear the mechanism and wear eye protection");
            telemetry.addLine("Launcher requires holding START + A; feeder stays off");
        }
        telemetry.update();
    }

    private String runDriveValidation() {
        double power = RobotConstants.Drive.DIAGNOSTIC_POWER;
        if (gamepad1.dpad_up) {
            robot.drivetrain.setTankPower(power, power);
            return "DRIVETRAIN FORWARD";
        }
        if (gamepad1.dpad_down) {
            robot.drivetrain.setTankPower(-power, -power);
            return "DRIVETRAIN BACKWARD";
        }
        if (gamepad1.dpad_left) {
            robot.drivetrain.setTankPower(-power, power);
            return "DRIVETRAIN TURN LEFT";
        }
        if (gamepad1.dpad_right) {
            robot.drivetrain.setTankPower(power, -power);
            return "DRIVETRAIN TURN RIGHT";
        }
        if (gamepad1.b) {
            robot.drivetrain.mecanumDrive(0, 1, 0, power);
            return "DRIVETRAIN STRAFE RIGHT";
        }
        if (gamepad1.y) {
            robot.drivetrain.mecanumDrive(0, -1, 0, power);
            return "DRIVETRAIN STRAFE LEFT";
        }
        return null;
    }

    private String runSelectedComponent(Component selected) {
        double requestedPower = gamepad1.a
                ? RobotConstants.Drive.DIAGNOSTIC_POWER
                : gamepad1.x ? -RobotConstants.Drive.DIAGNOSTIC_POWER : 0.0;

        switch (selected) {
            case FRONT_LEFT_DRIVE:
                robot.drivetrain.setWheelPower(requestedPower, 0, 0, 0);
                break;
            case FRONT_RIGHT_DRIVE:
                robot.drivetrain.setWheelPower(0, requestedPower, 0, 0);
                break;
            case LEFT_DRIVE:
                robot.drivetrain.setWheelPower(0, 0, requestedPower, 0);
                break;
            case RIGHT_DRIVE:
                robot.drivetrain.setWheelPower(0, 0, 0, requestedPower);
                break;
            case INTAKE_MOTOR:
                robot.intake.testMotor(requestedPower);
                break;
            case LEFT_INTAKE_SERVO:
                robot.intake.testLeftServo(requestedPower);
                break;
            case RIGHT_INTAKE_SERVO:
                robot.intake.testRightServo(requestedPower);
                break;
            case ARM_SERVO:
                robot.arm.update(gamepad1.a, gamepad1.x);
                return gamepad1.a == gamepad1.x ? null : "ARM " + robot.arm.getState();
            case WINDMILL_FEEDER:
                robot.launcher.runFeederTest(requestedPower);
                break;
            case LAUNCHER:
                if (gamepad1.start && gamepad1.a) {
                    robot.launcher.runLauncherTest();
                    return "LAUNCHER (REDUCED TEST VELOCITY; FEEDER OFF)";
                }
                return null;
            default:
                return null;
        }
        return requestedPower == 0.0 ? null : selected + (requestedPower > 0.0 ? " FORWARD" : " REVERSE");
    }

    @Override
    public void stop() {
        robot.shutdown();
    }
}
