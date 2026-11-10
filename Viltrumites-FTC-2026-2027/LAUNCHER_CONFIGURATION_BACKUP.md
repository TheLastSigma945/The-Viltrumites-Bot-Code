# Launcher configuration backup and rollback

This file records the working code settings **before** the 6000-RPM launcher
motor update. It is a software backup, not proof that the old motor was
physically calibrated or that it can be reinstalled without checking wiring.

| Setting | Original / NORMAL | Tested FAST |
|---|---:|---:|
| TeleOp full-trigger velocity target | 1250 encoder ticks/s | 2000 encoder ticks/s |
| Autonomous velocity target | 1250 encoder ticks/s | Not selected in autonomous |
| Reduced-speed hardware test | 600 encoder ticks/s | 600 encoder ticks/s |
| PIDF P / I / D / F | 40 / 0 / 0 / 12.5 | 40 / 0 / 0 / 12.5 |
| Launcher motor direction | FORWARD | FORWARD |
| Launcher run mode | RUN_USING_ENCODER | RUN_USING_ENCODER |
| Windmill direction | REVERSE | REVERSE |

In TeleOp, the launcher starts in NORMAL. On Gamepad 2, press PS4 **Cross
(X)** to select the tested FAST target or **Circle (O)** to return to the
original NORMAL target. These buttons select a speed only; the launcher
runs only while a trigger is held. Circle wins if both are pressed.
The old PIDF, test speed, motor direction, and run mode remain unchanged.
The targets are in
[RobotConstants.java](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/config/RobotConstants.java).

For a goBILDA 6000-RPM 1:1 motor with 28 countable encoder events per output
revolution, 2000 ticks/s corresponds to about 4286 output RPM *in theory*.
Both the [5203 1:1 motor](https://www.gobilda.com/5203-series-yellow-jacket-motor-1-1-ratio-24mm-length-8mm-rex-shaft-6000-rpm-3-3-5v-encoder/)
and [5202 1:1 motor](https://www.gobilda.com/5202-series-yellow-jacket-motor-1-1-ratio-24mm-length-6mm-d-shaft-6000-rpm-3-3-5v-encoder/)
list 28 encoder events per output revolution.
The 6000-RPM rating corresponds to 2800 ticks/s at no load. The actual
launcher speed depends on battery, load, wheel gearing, and PIDF tuning.
Confirm the installed motor's SKU and encoder resolution before relying on
the RPM conversion. The software does not guarantee a shot speed.

Test on blocks with eye protection and no balls first. Check that measured
launcher velocity follows the diagnostic 600-ticks/s target, then gradually
increase Gamepad 2's right trigger while watching measured velocity. Keep
people clear of the launch path. Do not run autonomous shooting until the
new motor's direction, feeder behavior, and shot trajectory have been
validated.
