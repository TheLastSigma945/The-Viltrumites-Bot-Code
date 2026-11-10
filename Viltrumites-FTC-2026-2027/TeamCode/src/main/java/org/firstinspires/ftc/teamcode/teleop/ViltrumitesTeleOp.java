package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/**
 * Main driver-controlled OpMode.
 *
 * <p>Gamepad 1 owns driving, the complete intake, and the collection arm. Gamepad 2 independently
 * owns the launcher and windmill. Every mechanism is manual; no subsystem starts another
 * subsystem.</p>
 */
@TeleOp(name = "Viltrumites TeleOp", group = "StarterBot")
public final class ViltrumitesTeleOp extends OpMode {
    private final Robot robot = new Robot();
    private final ElapsedTime runtime = new ElapsedTime();
    private boolean initialized;
    private boolean fastLauncherMode;
    private String initializationError = "";

    @Override
    public void init() {
        telemetry.addLine("Viltrumites StarterBot");
        try {
            robot.initialize(hardwareMap);
            initialized = true;
            telemetry.addData("Hardware Initialization", "OK");
            telemetry.addData("Drive", "Ready");
            telemetry.addData("Intake", "Ready");
            telemetry.addData("Arm", "Ready");
            telemetry.addData("Launcher", "Ready");
            telemetry.addData("Gamepad 1", "Drive + intake + arm");
            telemetry.addData("Gamepad 2", "Launcher + windmill");
        } catch (Robot.RobotInitializationException error) {
            initialized = false;
            initializationError = error.getMessage();
            robot.shutdown();
            telemetry.addData("Hardware Initialization", "FAILED");
            telemetry.addData("Fix configuration", initializationError);
        }
        telemetry.update();
    }

    @Override
    public void init_loop() {
        telemetry.addLine("Controller assignment check");
        addGamepadTelemetry(false);
        if (!initialized) {
            telemetry.addLine("ROBOT DISABLED - hardware initialization failed");
            telemetry.addData("Fix configuration", initializationError);
        } else {
            telemetry.addLine("Hardware ready; press PLAY after Gamepad 1 is assigned.");
        }
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
        fastLauncherMode = false;
    }

    @Override
    public void loop() {
        if (!initialized) {
            robot.shutdown();
            telemetry.addLine("ROBOT DISABLED - hardware initialization failed");
            telemetry.addData("Fix configuration", initializationError);
            telemetry.update();
            return;
        }

        boolean precisionMode = gamepad1.y;
        double maxDrivePower = precisionMode
                ? RobotConstants.Drive.PRECISION_MAX_POWER
                : RobotConstants.Drive.NORMAL_MAX_POWER;

        // FTC Y is negative when pushed forward, so invert it exactly once here.
        robot.drivetrain.mecanumDrive(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                maxDrivePower,
                RobotConstants.Drive.REAR_STRAFE_POWER_SCALE);

        // Gamepad 1 has complete, manual control of the intake assembly.
        robot.intake.update(gamepad1.right_trigger, gamepad1.left_trigger);

        // The arm CR servo runs only while one bumper is held and stops on release.
        robot.arm.update(
                gamepad1.left_bumper,
                gamepad1.right_bumper);

        // Gamepad 2 controls these mechanisms independently; neither can start automatically.
        // Holding one D-pad direction gives the windmill a continuous full-power command with no
        // software travel counter or timeout. The analog stick remains available for fine control.
        // PS4 Cross (X) maps to A; Circle (O) maps to B. Circle wins if both are held.
        if (gamepad2.b) {
            fastLauncherMode = false;
        } else if (gamepad2.a) {
            fastLauncherMode = true;
        }
        double selectedLauncherVelocity = fastLauncherMode
                ? RobotConstants.Launcher.FAST_VELOCITY_TICKS_PER_SECOND
                : RobotConstants.Launcher.TARGET_VELOCITY_TICKS_PER_SECOND;
        double windmillCommand = getWindmillCommand();
        robot.launcher.updateManual(
                gamepad2.right_trigger,
                gamepad2.left_trigger,
                windmillCommand,
                selectedLauncherVelocity);

        telemetry.addData("G1 drive mode", precisionMode ? "PRECISION (40%)" : "NORMAL (100%)");
        telemetry.addData(
                "G1 wheel power", "FL %.2f | FR %.2f | RL %.2f | RR %.2f",
                robot.drivetrain.getFrontLeftCommand(),
                robot.drivetrain.getFrontRightCommand(),
                robot.drivetrain.getRearLeftCommand(),
                robot.drivetrain.getRearRightCommand());
        telemetry.addData(
                "G1 intake", "%s (%.2f)", robot.intake.getState(), robot.intake.getCommand());
        telemetry.addData(
                "G1 arm", "%s | power %.2f",
                robot.arm.getState(), robot.arm.getCommand());
        telemetry.addData("G2 mechanisms", robot.launcher.getState());
        telemetry.addData("G2 launcher mode", "%s (%.0f ticks/s)",
                fastLauncherMode ? "FAST" : "NORMAL", selectedLauncherVelocity);
        telemetry.addData(
                "G2 launcher velocity", "target %.0f | current %.0f ticks/s",
                robot.launcher.getTargetVelocity(), robot.launcher.getCurrentVelocity());
        telemetry.addData("G2 windmill command", "%.2f", robot.launcher.getWindmillCommand());
        addGamepadTelemetry(true);
        telemetry.addData("Runtime", "%.1f s", runtime.seconds());
        telemetry.update();
    }

    private double getWindmillCommand() {
        if (gamepad2.dpad_left && gamepad2.dpad_right) {
            return 0.0;
        }
        if (gamepad2.dpad_left) {
            return 1.0;
        }
        if (gamepad2.dpad_right) {
            return -1.0;
        }
        return gamepad2.left_stick_x;
    }

    private void addGamepadTelemetry(boolean includeLiveInputs) {
        boolean gamepad1Assigned = gamepad1.getGamepadId() != Gamepad.ID_UNASSOCIATED;
        boolean gamepad2Assigned = gamepad2.getGamepadId() != Gamepad.ID_UNASSOCIATED;

        telemetry.addData(
                "Gamepad 1 status",
                gamepad1Assigned ? "ASSIGNED (%s, id %d)" : "NOT ASSIGNED",
                gamepad1.type(), gamepad1.getGamepadId());
        telemetry.addData(
                "Gamepad 2 status",
                gamepad2Assigned ? "ASSIGNED (%s, id %d)" : "NOT ASSIGNED",
                gamepad2.type(), gamepad2.getGamepadId());

        if (!gamepad1Assigned) {
            telemetry.addLine("DRIVER OFFLINE: press START+A (PS4: OPTIONS+Cross).");
        }
        if (!gamepad2Assigned) {
            telemetry.addLine("OPERATOR OFFLINE: press START+B (PS4: OPTIONS+Circle).");
        }

        if (includeLiveInputs) {
            telemetry.addData(
                    "G1 raw input",
                    "LY %.2f | LX %.2f | RX %.2f | RT %.2f | LT %.2f | LB %b | RB %b",
                    gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x,
                    gamepad1.right_trigger,
                    gamepad1.left_trigger,
                    gamepad1.left_bumper,
                    gamepad1.right_bumper);
            telemetry.addData(
                    "G2 raw input", "LX %.2f | RT %.2f | LT %.2f | Cross %b | Circle %b",
                    gamepad2.left_stick_x,
                    gamepad2.right_trigger,
                    gamepad2.left_trigger,
                    gamepad2.a,
                    gamepad2.b);
        }
    }

    @Override
    public void stop() {
        robot.shutdown();
    }
}
