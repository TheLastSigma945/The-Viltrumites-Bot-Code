# REV Hardware Configuration

Hardware names are centralized in `RobotConstants.java` and are case-sensitive. This robot now has four X-pattern mecanum wheels. The existing `left_drive` and `right_drive` names are the rear motors; the two new front motors need the exact names below. Confirm other devices are not already assigned to the stated motor ports.

| Configuration name | Device type in code | Hub/connection | Expected physical function | Team wiring record |
|---|---|---|---|---|
| `front_left_drive` | DC motor (`DcMotor`), 1150 RPM | **Expansion Hub motor port 2** | Front-left mecanum wheel | Expansion Hub motor port 2 |
| `front_right_drive` | DC motor (`DcMotor`), 312 RPM | **Control Hub motor port 3** | Front-right mecanum wheel | Control Hub motor port 3 |
| `left_drive` | DC motor (`DcMotor`), 312 RPM | Existing port; verify | Rear-left mecanum wheel | __________ |
| `right_drive` | DC motor (`DcMotor`), 312 RPM | Existing port; verify | Rear-right mecanum wheel | __________ |
| `intake` | DC motor (`DcMotor`) | Available Control Hub or Expansion Hub motor port | Turns the intake roller | __________ |
| `launcher` | Encoder-equipped DC motor (`DcMotorEx`) | Available motor port plus its matching encoder port | Spins the high-speed launcher wheel | __________ |
| `windmill` | Continuous-rotation servo (`CRServo`) | Available servo port | Feeds game pieces into the launcher | __________ |
| `left_intake_servo` | Continuous-rotation servo (`CRServo`) | Available servo port | Pulls elements from the left corner into the intake | __________ |
| `right_intake_servo` | Continuous-rotation servo (`CRServo`) | Available servo port | Pulls elements from the right corner into the intake | __________ |
| `arm_servo` | Continuous-rotation servo (`CRServo`) | **Expansion Hub 2, Servo Port 0** | Raises and lowers the ball-collection arm while a bumper is held | Expansion Hub 2, Servo Port 0 |
| `Webcam 1` | UVC USB webcam (`WebcamName`) | Control Hub USB 3.0 port | Driver Station camera stream and future vision processing | Logitech C270 detected; serial `6772BD90` |

Choose the actual motor model for each port in the REV configuration. The front-left motor's 1150 RPM rating is different from the three 312 RPM motors; the code scales its open-loop power by 312/1150 as an initial estimate. This does not guarantee equal loaded wheel speed.

The webcam must be added as a top-level USB device in the active REV configuration; merely plugging it in does not create the `Webcam 1` hardware-map entry. On the Driver Station, edit `GojoBot`, select **Scan**, add the detected webcam as `Webcam 1`, then save and activate the configuration.

## Encoder Configuration

Only `launcher` uses an encoder in the normal TeleOp implementation. In the active Driver Station hardware configuration, select the exact installed goBILDA 6000-RPM motor model rather than leaving the old motor type, and connect its encoder cable to the encoder socket corresponding to its motor port. It runs in `RUN_USING_ENCODER`. Gamepad 2's PS4 Cross/X selects the previously tested FAST target of ±2000 ticks/second at full trigger; Circle/O restores the original NORMAL ±1250-ticks/second target. NORMAL is selected at the start of TeleOp and in autonomous. Buttons select speed but do not spin the motor; the triggers remain proportional. FAST is not full 6000-RPM operation. Confirm that the installed motor is a 1:1, 28-events-per-output-revolution model before converting ticks/s to RPM. See [LAUNCHER_CONFIGURATION_BACKUP.md](LAUNCHER_CONFIGURATION_BACKUP.md) for the saved original settings.

All four drive motors and the intake motor run in `RUN_WITHOUT_ENCODER`. The RPM ratio is not an encoder calibration; autonomous distances and turn angles still need field measurement.

## Motor and Servo Direction Baseline

Do not interpret `FORWARD` as a universal clockwise shaft direction: physical clockwise/counterclockwise depends on which end of a motor is viewed and how it is installed. The drive and launcher baselines come from the supplied source. The two side-intake directions include the explicit corrections found during physical testing.

| Device | Configuration name | Direction in software | Expected physical rotation/effect for positive command | Robot effect |
|---|---|---|---|---|
| Front-left drive | `front_left_drive` | `FORWARD`, initially scaled to 27.1% of requested power | Wheel rolls robot forward on positive command | Front-left |
| Front-right drive | `front_right_drive` | `REVERSE` | Wheel rolls robot forward on positive command | Front-right |
| Rear-left drive | `left_drive` | `FORWARD` | Wheel rolls robot forward on positive command | Rear-left |
| Rear-right drive | `right_drive` | `REVERSE` | Wheel rolls robot forward on positive command | Rear-right |
| Intake motor | `intake` | `FORWARD` (SDK baseline) | Turns the roller toward the hopper | Pulls game pieces inward |
| Launcher motor | `launcher` | `FORWARD` (SDK baseline) | Spins in the launcher's intended firing direction | Accelerates game pieces toward the exit |
| Left intake CR servo | `left_intake_servo` | `REVERSE` (physical-test correction) | Turns inward | Pulls pieces from the left corner |
| Right intake CR servo | `right_intake_servo` | `FORWARD` (physical-test correction) | Mirrored servo turns inward | Pulls pieces from the right corner |
| Collection arm CR servo | `arm_servo` | `FORWARD` starting value; reverse if physical testing requires | Negative power is labeled up; positive power is labeled down | Gamepad 1 bumpers run the arm; releasing them sends zero power |
| Windmill CR servo | `windmill` | `REVERSE` | Signed power follows Gamepad 2's left-stick X; D-pad left/right provides continuous full power | Manually feeds or reverses with no software travel limit |

## Arm Servo Configuration

In the Driver Station, edit the active `GojoBot` configuration, open **Expansion Hub 2**, and set **Servo Port 0** to device type **Continuous Rotation Servo** with the exact case-sensitive name `arm_servo`. The left bumper commands full power in the arm-up direction, the right bumper commands full power in the arm-down direction, and releasing both—or holding both—immediately commands `0.0` power.

## Drivetrain Command Expectations

| Command | Front-left | Front-right | Rear-left | Rear-right |
|---|---:|---:|---:|---:|
| Forward | + | + | + | + |
| Backward | − | − | − | − |
| Strafe right (tested correction) | + | + | − | + |
| Strafe left (tested correction) | − | − | + | − |
| Turn right | + | − | + | − |
| Turn left | − | + | − | + |
| Stop | 0 | 0 | 0 | 0 |

These are logical wheel command signs. In TeleOp at full left-stick sideways deflection, front-right receives full requested power, front-left receives `312/1150 ≈ 0.271` requested power, and each rear motor receives half requested power. At smaller deflections, all are proportional. This is an open-loop approximation of 312 RPM front and 156 RPM rear, **not measured RPM**. The diagnostic B/X strafe tests and autonomous commands retain their previous power ratios; only TeleOp left-stick sideways power changed.

The current signs are **not the standard X-mecanum sign pattern** and may make the robot rotate rather than translate. Validate on blocks with **StarterBot Drivetrain Direction Test**, then test TeleOp at low speed on a clear floor. If it rotates or fights itself, stop and inspect wheel mounting, motor directions, and wiring before using full-power strafe.
