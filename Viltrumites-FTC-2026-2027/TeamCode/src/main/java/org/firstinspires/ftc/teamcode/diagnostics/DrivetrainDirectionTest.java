package org.firstinspires.ftc.teamcode.diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/** Provides low-power, hold-to-run commands for confirming physical drivetrain directions. */
@TeleOp(name = "StarterBot Drivetrain Direction Test", group = "Diagnostics")
public final class DrivetrainDirectionTest extends OpMode {
    private final Robot robot = new Robot();
    private boolean initialized;
    private String initializationError = "";

    @Override
    public void init() {
        try {
            robot.initialize(hardwareMap);
            initialized = true;
            telemetry.addLine("READY - put the robot on blocks");
        } catch (Robot.RobotInitializationException error) {
            initializationError = error.getMessage();
            telemetry.addData("Initialization failed", initializationError);
        }
        telemetry.update();
    }

    @Override
    public void loop() {
        if (!initialized) {
            robot.shutdown();
            telemetry.addData("TEST DISABLED", initializationError);
            telemetry.update();
            return;
        }

        double power = RobotConstants.Drive.DIAGNOSTIC_POWER;
        String command = "STOP";
        if (gamepad1.dpad_up) {
            robot.drivetrain.setTankPower(power, power);
            command = "FORWARD";
        } else if (gamepad1.dpad_down) {
            robot.drivetrain.setTankPower(-power, -power);
            command = "BACKWARD";
        } else if (gamepad1.dpad_left) {
            robot.drivetrain.setTankPower(-power, power);
            command = "TURN LEFT";
        } else if (gamepad1.dpad_right) {
            robot.drivetrain.setTankPower(power, -power);
            command = "TURN RIGHT";
        } else if (gamepad1.b) {
            robot.drivetrain.mecanumDrive(0, 1, 0, power);
            command = "STRAFE RIGHT";
        } else if (gamepad1.x) {
            robot.drivetrain.mecanumDrive(0, -1, 0, power);
            command = "STRAFE LEFT";
        } else {
            robot.drivetrain.stop();
        }

        telemetry.addLine("ROBOT MUST BE ON BLOCKS");
        telemetry.addData("Hold", "D-pad drive/turn | B strafe right | X strafe left");
        telemetry.addData("Command", command);
        telemetry.addData(
                "Motor commands", "FL %.2f | FR %.2f | RL %.2f | RR %.2f",
                robot.drivetrain.getFrontLeftCommand(),
                robot.drivetrain.getFrontRightCommand(),
                robot.drivetrain.getRearLeftCommand(),
                robot.drivetrain.getRearRightCommand());
        telemetry.update();
    }

    @Override
    public void stop() {
        robot.shutdown();
    }
}
