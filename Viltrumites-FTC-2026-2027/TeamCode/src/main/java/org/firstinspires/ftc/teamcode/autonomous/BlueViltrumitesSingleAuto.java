package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.config.RobotConstants;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;

/**
 * Blue timed autonomous route. INIT, then press START; no controller input is used.
 *
 * <p>All distances and turns are time-based, so test the route on a clear field.
 * A feed pulse is a timed windmill command, not confirmation that one ball fired.</p>
 */
@Autonomous(name = "Viltrumites Blue Auto", group = "BIOBUZZ")
public final class BlueViltrumitesSingleAuto extends LinearOpMode {

    // ======================= ROUTE SETTINGS =======================
    // Change these numbers after testing on your robot. Seconds are NOT inches or degrees.
    private static final double FIRST_BACKWARD_SECONDS = 1.0;
    private static final double DRIVE_POWER = 0.25;
    private static final double LAUNCHER_SPINUP_SECONDS = 2.0;
    private static final double FEED_SECONDS = 1.5;
    private static final double RECOVERY_SECONDS = 2.0;
    private static final int FEED_PULSES = 4;
    private static final double WINDMILL_POWER = 1.0;
    private static final double FIRST_RIGHT_TURN_SECONDS = 1.8;
    private static final double LAST_RIGHT_TURN_SECONDS = 1.5;
    // Four-motor turn power; tune after measuring the actual turn angle on the field.
    private static final double TURN_POWER = 0.30;
    private static final double FIRST_FORWARD_SECONDS = 3.5;
    private static final double FINAL_FORWARD_SECONDS = 7.0;
    private static final double AUTO_LIMIT_SECONDS = 30.0;
    private static final String BUILD_MARKER = "Two timed autos / turn power 0.30";

    // Keep this direction consistent with TeleOp. Reverse the sign of WINDMILL_POWER
    // only if a blocks-up test shows the feeder moving balls away from the launcher.
    private static final double LAUNCHER_VELOCITY =
            RobotConstants.Launcher.TARGET_VELOCITY_TICKS_PER_SECOND;
    // ===============================================================

    private Drivetrain drivetrain;
    private DcMotorEx launcher;
    private CRServo windmill;
    private Launcher launcherSubsystem;
    private final ElapsedTime autoClock = new ElapsedTime();
    private String step = "INIT";
    private String result = "not started";
    private double leftDriveCommand;
    private double rightDriveCommand;

    @Override
    public void runOpMode() {
        try {
            try {
                initializeHardware();
                result = "ready";
            } catch (RuntimeException error) {
                result = "Hardware setup failed: " + error.getMessage();
            }

            telemetry.addData("Profile", "BLUE");
            telemetry.addData("Code revision", BUILD_MARKER);
            telemetry.addData("Route", "Reverse > four feeds > right > forward > right > forward");
            telemetry.addData("Reverse seconds", FIRST_BACKWARD_SECONDS);
            telemetry.addData("First right turn seconds", FIRST_RIGHT_TURN_SECONDS);
            telemetry.addData("Last right turn seconds", LAST_RIGHT_TURN_SECONDS);
            telemetry.addData("Turn power", TURN_POWER);
            telemetry.addData("First forward seconds", FIRST_FORWARD_SECONDS);
            telemetry.addData("Final forward seconds", FINAL_FORWARD_SECONDS);
            telemetry.addData("Launcher", "2s spin-up; 2s recovery between feeds");
            telemetry.addData("Hardware", result);
            telemetry.addLine("Test directions and clearance on blocks before a field run.");
            telemetry.update();

            waitForStart();
            if (isStopRequested() || !"ready".equals(result)) return;
            autoClock.reset();

            if (!driveFor("Backward", FIRST_BACKWARD_SECONDS, -DRIVE_POWER, -DRIVE_POWER)) return;

            step = "Launcher spin-up";
            launcherSubsystem.updateManual(1.0, 0.0, 0.0, LAUNCHER_VELOCITY);
            if (!waitForSeconds(LAUNCHER_SPINUP_SECONDS)) return;

            for (int pulse = 1; pulse <= FEED_PULSES; pulse++) {
                step = "Feed pulse " + pulse + "/" + FEED_PULSES;
                windmill.setPower(WINDMILL_POWER);
                try {
                    if (!waitForSeconds(FEED_SECONDS)) return;
                } finally {
                    windmill.setPower(0);
                }

                if (pulse < FEED_PULSES) {
                    step = "Launcher recovery " + pulse + "/" + (FEED_PULSES - 1);
                    if (!waitForSeconds(RECOVERY_SECONDS)) return;
                }
            }

            stopLauncher();
            if (!driveFor("Turn right 1/2", FIRST_RIGHT_TURN_SECONDS, TURN_POWER, -TURN_POWER)) return;
            if (!driveFor("Forward 1/2", FIRST_FORWARD_SECONDS, DRIVE_POWER, DRIVE_POWER)) return;
            if (!driveFor("Turn right 2/2", LAST_RIGHT_TURN_SECONDS, TURN_POWER, -TURN_POWER)) return;
            if (!driveFor("Forward 2/2", FINAL_FORWARD_SECONDS, DRIVE_POWER, DRIVE_POWER)) return;
            step = "Complete";
            result = "timed commands completed; shots and position unverified";
        } catch (RuntimeException error) {
            result = step + ": " + error.getMessage();
        } finally {
            stopAll();
            telemetry.addData("Last step", step);
            telemetry.addData("Result", result);
            telemetry.update();
        }
    }

    private void initializeHardware() {
        // All four drive motors and both shooting devices are required.
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.Hardware.FRONT_LEFT_DRIVE);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, RobotConstants.Hardware.FRONT_RIGHT_DRIVE);
        DcMotor rearLeft = hardwareMap.get(DcMotor.class, RobotConstants.Hardware.LEFT_DRIVE);
        DcMotor rearRight = hardwareMap.get(DcMotor.class, RobotConstants.Hardware.RIGHT_DRIVE);
        launcher = hardwareMap.get(DcMotorEx.class, RobotConstants.Hardware.LAUNCHER);
        windmill = hardwareMap.get(CRServo.class, RobotConstants.Hardware.WINDMILL);

        drivetrain = new Drivetrain(frontLeft, frontRight, rearLeft, rearRight);
        // Use the same motor direction, encoder mode, PIDF and normal trigger target as TeleOp.
        launcherSubsystem = new Launcher(launcher, windmill);
    }

    private boolean driveFor(String name, double seconds, double leftPower, double rightPower) {
        step = name;
        leftDriveCommand = leftPower;
        rightDriveCommand = rightPower;
        drivetrain.setTankPower(leftPower, rightPower);
        try {
            return waitForSeconds(seconds);
        } finally {
            drivetrain.stop();
            leftDriveCommand = 0;
            rightDriveCommand = 0;
        }
    }

    private boolean waitForSeconds(double seconds) {
        ElapsedTime stepClock = new ElapsedTime();
        while (opModeIsActive()
                && !isStopRequested()
                && stepClock.seconds() < seconds
                && autoClock.seconds() < AUTO_LIMIT_SECONDS) {
            telemetry.addData("Step", step);
            telemetry.addData("Step elapsed", "%.1f / %.1f s", stepClock.seconds(), seconds);
            telemetry.addData("Auto elapsed", "%.1f / %.1f s", autoClock.seconds(), AUTO_LIMIT_SECONDS);
            telemetry.addData("Code revision", BUILD_MARKER);
            telemetry.addData("Drive command", "left %.2f | right %.2f",
                    leftDriveCommand, rightDriveCommand);
            telemetry.addData("Launcher velocity", "target %.0f | current %.0f ticks/s",
                    launcherSubsystem.getTargetVelocity(),
                    launcher.getVelocity());
            telemetry.update();
            idle();
        }
        if (autoClock.seconds() >= AUTO_LIMIT_SECONDS) {
            result = "30-second safety limit reached";
            return false;
        }
        if (!opModeIsActive() || isStopRequested()) {
            result = "stopped";
            return false;
        }
        return true;
    }

    private void stopLauncher() {
        if (launcherSubsystem != null) {
            launcherSubsystem.stop();
        } else {
            if (windmill != null) windmill.setPower(0);
            if (launcher != null) {
                launcher.setVelocity(0);
                launcher.setPower(0);
            }
        }
    }

    private void stopAll() {
        if (drivetrain != null) drivetrain.stop();
        stopLauncher();
    }
}
