package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * One place for hardware-map names and values that the team may tune.
 *
 * <p>Hardware-map strings are case-sensitive and must match the REV configuration exactly.</p>
 */
public final class RobotConstants {
    private RobotConstants() {
        // Constants only.
    }

    public static final class Hardware {
        public static final String FRONT_LEFT_DRIVE = "front_left_drive";
        public static final String FRONT_RIGHT_DRIVE = "front_right_drive";
        // Existing names are retained for the rear motors.
        public static final String LEFT_DRIVE = "left_drive";
        public static final String RIGHT_DRIVE = "right_drive";
        public static final String INTAKE = "intake";
        public static final String LAUNCHER = "launcher";
        public static final String WINDMILL = "windmill";
        public static final String LEFT_INTAKE_SERVO = "left_intake_servo";
        public static final String RIGHT_INTAKE_SERVO = "right_intake_servo";
        public static final String ARM_SERVO = "arm_servo";
        public static final String WEBCAM = "Webcam 1";

        private Hardware() {
        }
    }

    public static final class Drive {
        // The front-left motor is 1150 RPM; the other three are 312 RPM.
        // Theoretical no-load open-loop correction only; tune on the actual robot.
        public static final double FRONT_LEFT_POWER_SCALE = 312.0 / 1150.0;
        // TeleOp left-stick strafe only: rear 312 RPM motors get half the
        // sideways power of the front-right 312 RPM motor.
        public static final double REAR_STRAFE_POWER_SCALE = 0.50;
        public static final double JOYSTICK_DEADBAND = 0.05;
        public static final double NORMAL_MAX_POWER = 1.00;
        public static final double PRECISION_MAX_POWER = 0.40;
        public static final double DIAGNOSTIC_POWER = 0.22;

        private Drive() {
        }
    }

    public static final class Intake {
        public static final double TRIGGER_DEADBAND = 0.05;

        private Intake() {
        }
    }

    public static final class Arm {
        public static final DcMotorSimple.Direction DIRECTION = DcMotorSimple.Direction.FORWARD;
        public static final double MANUAL_POWER = 1.00;

        private Arm() {
        }
    }

    public static final class Launcher {
        // Normal is the original setting and the autonomous default.
        public static final double TARGET_VELOCITY_TICKS_PER_SECOND = 1250.0;
        // Previously tested faster target for the 6000-RPM motor (not 6000 RPM output).
        public static final double FAST_VELOCITY_TICKS_PER_SECOND = 2000.0;
        // Original low-speed diagnostic and PIDF values remain unchanged.
        public static final double TEST_VELOCITY_TICKS_PER_SECOND = 600.0;
        public static final double MANUAL_TRIGGER_DEADBAND = 0.05;
        public static final double WINDMILL_STICK_DEADBAND = 0.05;
        public static final double PIDF_P = 40.0;
        public static final double PIDF_I = 0.0;
        public static final double PIDF_D = 0.0;
        public static final double PIDF_F = 12.5;

        private Launcher() {
        }
    }

    public static final class Vision {
        public static final int STREAM_WIDTH = 640;
        public static final int STREAM_HEIGHT = 480;

        private Vision() {
        }
    }

    public static final class Autonomous {
        // Conservative starting points only. Tune on the real robot and field surface.
        public static final double DRIVE_POWER = 0.25;
        public static final double DRIVE_SECONDS = 0.80;
        public static final double TURN_POWER = 0.22;
        public static final double TURN_SECONDS = 0.35;

        private Autonomous() {
        }
    }
}
