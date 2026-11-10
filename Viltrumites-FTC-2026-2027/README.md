# The Viltrumites FTC Robot Code

Competition-oriented robot code for the 2026–2027 **BIOBUZZ** season, based on the official FTC SDK v12.0 workspace and the supplied goBILDA StarterBot example.

The robot now has four X-pattern mecanum wheels. Three drive motors are rated 312 RPM; the front-left motor is rated 1150 RPM. The code initially reduces front-left open-loop power by the RPM ratio, which must be calibrated on the real robot. The intake, arm, launcher, and windmill remain independently controlled.

For the two timed BIOBUZZ autonomous programs, see
[AUTONOMOUS_README.md](AUTONOMOUS_README.md).
For moving this project to another computer, see [PROJECT_HANDOFF.md](PROJECT_HANDOFF.md).

## Quick Start

1. Open this repository's root folder in Android Studio or VS Code.
2. Read [HARDWARE_CONFIGURATION.md](HARDWARE_CONFIGURATION.md) before configuring the robot.
3. Build the project with `gradlew.bat :TeamCode:assembleDebug`.
4. Put the robot on blocks and follow [FIRST_STARTUP_CHECKLIST.md](FIRST_STARTUP_CHECKLIST.md).
5. Run the diagnostics before enabling the competition TeleOp.

## Opening the Project in VS Code

1. Start Visual Studio Code.
2. Select **File > Open Folder...**.
3. Choose `C:\Projects\Codex\Viltrumites-FTC-2026-2027`.
4. If Workspace Trust appears, confirm that you trust this local project.
5. Open the Source Control view with **Ctrl+Shift+G**.

Open the repository root—not the ZIP file and not `TeamCode` by itself. From a terminal, the equivalent command is:

```powershell
code C:\Projects\Codex\Viltrumites-FTC-2026-2027
```

The repository recommends Microsoft's Java and Gradle extensions. To compile inside VS Code, press **Ctrl+Shift+B** and select **Build FTC TeamCode**, or run the Gradle command in the integrated terminal. Android Studio remains the recommended tool for installing the Robot Controller app on a Control Hub.

## Publishing Your Own GitHub Repository

This checkout uses a local `main` branch. The original FIRST FTC SDK repository is retained as the read-only-style `upstream` remote so it is not confused with your team's future `origin` remote.

1. Open the Source Control view with **Ctrl+Shift+G**.
2. Review the changed files. Use the **+** button to stage the files you want in the repository.
3. Enter a commit message such as `Initial Viltrumites BIOBUZZ robot code` and select **Commit**.
4. Open the Command Palette with **Ctrl+Shift+P**.
5. Run **Publish to GitHub** and sign in when prompted.
6. Choose a unique repository name and select **Private** or **Public**.
7. Confirm that VS Code created an `origin` remote pointing to your GitHub account before pushing future work.

If **Publish to GitHub** is unavailable, create an empty repository on GitHub, then in VS Code choose **Source Control > ... > Remotes > Add Remote**, name it `origin`, paste the repository URL, and publish the `main` branch. Never set `origin` to the official FIRST repository because your team does not own that repository.

## How the Code Is Organized

The runtime path is intentionally short:

`Driver Station input → OpMode → Robot → subsystem → configured hardware`

| Location | Responsibility |
|---|---|
| `config/RobotConstants.java` | Hardware-map names and values the team may tune |
| `Robot.java` | Maps hardware once, creates subsystems, and safely stops everything |
| `subsystems/Drivetrain.java` | Four-wheel mecanum mixing, front-left RPM compensation, and braking |
| `subsystems/Intake.java` | Intake motor and paired continuous-rotation servos |
| `subsystems/Arm.java` | Direct, fully manual continuous-rotation control for the collection arm |
| `subsystems/Launcher.java` | Independent manual launcher and bidirectional windmill control |
| `teleop/ViltrumitesTeleOp.java` | Driver controls and concise match telemetry |
| `autonomous/` | Red and Blue timed routes; turn power and durations need physical calibration |
| `diagnostics/` | Low-power, operator-controlled tests for first startup |

Start with `ViltrumitesTeleOp.java` to see how controls flow through the robot. Open a subsystem when you need the hardware behavior, and edit `RobotConstants.java` when tuning a named value. Hardware-map strings should never be scattered through OpModes.

## Requirements

- Android Studio Narwhal 3 Feature Drop or newer.
- FTC SDK 12.0 dependencies (declared by this workspace and downloaded by Gradle).
- A REV Control Hub running a compatible Robot Controller version.
- A Driver Station running the matching FTC season software.
- Java/Gradle supplied by the supported Android Studio installation.
- Internet access for the first Gradle sync.

## Opening the Project in Android Studio

1. Extract `Viltrumites-FTC-2026-2027.zip` to a normal writable folder.
2. In Android Studio, choose **Open**.
3. Select the extracted `Viltrumites-FTC-2026-2027` root—the folder containing `settings.gradle`, not the `TeamCode` folder by itself.
4. Trust the project when Android Studio asks.
5. Let indexing and Gradle sync complete.

## Gradle Sync

Use **File > Sync Project with Gradle Files**. The project uses the official v12.0 Gradle wrapper. If sync cannot download dependencies, check the network connection and make sure Gradle Offline Mode is disabled.

Command-line validation from the project root:

```powershell
.\gradlew.bat :TeamCode:assembleDebug
```

## Connecting to the REV Control Hub

1. Power the Control Hub with the robot switch.
2. Connect the programming computer to the Control Hub Wi-Fi network, or connect by USB-C.
3. Confirm Android Studio sees the Control Hub as a deployment target.
4. Keep the Driver Station paired with the Control Hub and use matching FTC app versions.

Never upload or test mechanisms while someone is touching the robot.

## Uploading/Running the Robot Controller

1. Select the `TeamCode` run configuration in Android Studio.
2. Select the connected Control Hub.
3. Press **Run** and wait for installation to finish.
4. On the Driver Station, open the OpMode selector.
5. Begin with **StarterBot Hardware Test**, not the competition TeleOp.

## Creating the REV Hardware Configuration

Create or edit a configuration from the Driver Station/Robot Controller configuration screen. Add every device and use the names in [HARDWARE_CONFIGURATION.md](HARDWARE_CONFIGURATION.md) exactly, including underscores and capitalization. The supplied goBILDA source gives no hub ports, so use available compatible ports and document the physical wiring for your robot.

## Hardware Names

| Name | Code type | Function |
|---|---|---|
| `front_left_drive` | DC motor | Front-left, 1150 RPM, Expansion Hub motor port 2 |
| `front_right_drive` | DC motor | Front-right, 312 RPM, Control Hub motor port 3 |
| `left_drive` | DC motor | Existing rear-left, 312 RPM |
| `right_drive` | DC motor | Existing rear-right, 312 RPM |
| `intake` | DC motor | Intake roller |
| `launcher` | DC motor with encoder | High-speed launcher |
| `windmill` | Continuous-rotation servo | Launcher feeder |
| `left_intake_servo` | Continuous-rotation servo | Left corner intake |
| `right_intake_servo` | Continuous-rotation servo | Right corner intake |
| `arm_servo` | Continuous-rotation servo | Raises and lowers the collection arm while a bumper is held |
| `Webcam 1` | USB webcam | Optional Driver Station camera stream |

## Gamepad Controls

Before pressing PLAY, assign the physical controllers in the Driver Station:

- Logitech/Xbox-style controller: press **START + A** for Gamepad 1 and **START + B** for Gamepad 2.
- PS4-style controller: press **OPTIONS + Cross** for Gamepad 1 and **OPTIONS + Circle** for Gamepad 2.
- Watch the controller icons at the top-right of the Driver Station. The correct icon must appear over the intended user and highlight green when you move a control. Reassign a controller after unplugging or reconnecting it.

While `Viltrumites TeleOp` is initialized, telemetry reports `Gamepad 1 status` and `Gamepad 2 status`. After PLAY, `G1 raw input` shows the actual driver stick and trigger values received by the robot. If the status is `ASSIGNED` but those values never change, run **Test Gamepad (Built-in)** from the OpMode list to isolate a controller, cable, adapter, or Driver Station problem.

### Viltrumites TeleOp

#### Gamepad 1 — Driver, Intake, and Arm

| Control | Action |
|---|---|
| Left stick up/down | Drive forward/backward |
| Left stick left/right | Strafe left/right |
| Right stick left/right | Turn left/right |
| Hold Y | Precision drive, 40% maximum |
| Right trigger | Complete intake forward, proportional |
| Left trigger | Complete intake reverse/eject, proportional; reverse wins if both triggers are pressed |
| Hold left bumper | Move the arm up gradually |
| Hold right bumper | Move the arm down gradually |

The arm uses a continuous-rotation servo. It moves only while exactly one bumper is held; releasing both—or holding both—commands zero power and stops it. Test direction and safe linkage travel with the servo horn disconnected before installing it.

#### Gamepad 2 — Launcher and Windmill

| Control | Action |
|---|---|
| PS4 Cross/X on Gamepad 2 (Xbox A) | Select FAST launcher mode: 2000 ticks/second full-trigger target |
| PS4 Circle/O on Gamepad 2 (Xbox B) | Select NORMAL launcher mode: original 1250 ticks/second full-trigger target |
| Right trigger | Launcher forward, proportional up to the selected target |
| Left trigger | Launcher reverse, proportional down to the negative selected target |
| Left stick left/right | Windmill in either direction, proportional |
| Hold D-pad left/right | Windmill continuously at full power in that direction; there is no software angle, rotation-count, or time limit |

Launcher mode starts at NORMAL each TeleOp run. Cross and Circle only select a speed; neither starts the launcher. Circle wins if both are held. If both launcher triggers are pressed, their values cancel; the stronger trigger determines the direction. The launcher, windmill, and intake are independent. Nothing starts automatically, and no mechanism starts another mechanism.

The windmill is configured as a continuous-rotation servo. Its stick and D-pad commands are applied every loop and never stop because of a software travel limit. If it repeatedly stops at the same physical angle while a D-pad direction is still held and `G2 windmill command` remains `-1.00` or `1.00`, stop testing and inspect the mechanism: it is binding, hitting a hard stop, losing power, or the installed servo is not actually continuous-rotation.

### StarterBot Hardware Test

Put the robot on blocks. Use left/right bumper presses to select one component. Hold **A** for its low-power forward test or **X** for reverse. For `ARM_SERVO`, A runs the arm up and X runs it down; releasing the button stops it. The launcher is intentionally different: select `LAUNCHER`, clear the mechanism, then hold **START + A** for a reduced-speed launcher-only test. The feeder stays off. The D-pad performs low-power whole-drivetrain direction checks and takes priority over the selected component.

## Arm Servo Setup

Connect a **continuous-rotation servo** to **Expansion Hub 2, Servo Port 0** and configure that port in the Driver Station as **Continuous Rotation Servo** with the exact name `arm_servo`. Before attaching the servo horn to the arm linkage, select `ARM_SERVO` in **StarterBot Hardware Test**, then briefly hold A and X to confirm both directions and verify that release stops the servo. If the directions are reversed, change `RobotConstants.Arm.DIRECTION` from `FORWARD` to `REVERSE`; do not cross or reverse the servo's power wires.

### StarterBot Camera Test

The connected Logitech C270 is supported through FTC `VisionPortal` at 640×480. Before running the test, use **Driver Station > menu (three dots) > Configure Robot > Edit `GojoBot` > Scan**, add the detected webcam with the exact name `Webcam 1`, then save and activate the configuration. Prefer the Control Hub's USB 3.0 port.

Initialize **StarterBot Camera Test** and wait for `State: STREAMING`. While still in INIT, open **Driver Station > menu (three dots) > Camera Stream** and tap the image to refresh it. After PLAY, Gamepad 1 **A** resumes streaming, **B** pauses it, and **X** saves one raw frame. This diagnostic is separate from match TeleOp so camera setup cannot disable driving.

## First Startup

Follow [FIRST_STARTUP_CHECKLIST.md](FIRST_STARTUP_CHECKLIST.md) in order. Do not begin with full-power TeleOp. The first test must be on blocks with wheels clear of the floor.

## Drivetrain Direction Test

Run **StarterBot Drivetrain Direction Test** with the robot on blocks. Hold D-pad for forward/back/turn, B for right strafe, or X for left strafe. Releasing controls stops all four motors. The latest correction flips only the front-left strafe command; the other wheel commands are unchanged. This is not standard X-mecanum mixing. If the robot rotates instead of strafing, inspect wheel mounting and motor directions before driving on the floor.

TeleOp left-stick sideways input gives the two front wheels the 312-RPM motors' theoretical full-speed power level and the rear wheels half that sideways power. The front-left 1150-RPM motor still uses its existing `312/1150` power limit. Forward/backward, turning, diagnostics, and autonomous retain their previous power ratios. These are open-loop power estimates, not measured wheel RPM.

If the physical result is wrong, first check motor placement, wiring, configuration names, and the single joystick sign inversion. Do not casually reverse motors in software—the supplied directions are the physical baseline.

## Intake Test

In **StarterBot Hardware Test**, select and test `INTAKE_MOTOR`, `LEFT_INTAKE_SERVO`, and `RIGHT_INTAKE_SERVO` individually. Physical testing showed that both side-intake directions in the original source needed to be flipped, so the left servo is now `REVERSE` and the right servo is `FORWARD`. Confirm that a positive command pulls game pieces inward before using full TeleOp.

## Launcher Test

Wear eye protection, clear all game pieces and people from the launch path, and keep the robot on blocks. Select `LAUNCHER` in **StarterBot Hardware Test**, then hold **START + A**. The diagnostic target remains 600 encoder ticks/second and the windmill stays off. In TeleOp, first test the NORMAL 1250-ticks/s target gradually with Gamepad 2's right trigger; then press Cross to select the previously tested FAST 2000-ticks/s target and test it gradually. Press Circle to return to NORMAL. Release the trigger before testing reverse. The windmill remains independent on Gamepad 2's left stick or D-pad. The [launcher configuration backup](LAUNCHER_CONFIGURATION_BACKUP.md) records the original values.

## Troubleshooting

| Symptom | Check |
|---|---|
| Gamepad 1 does nothing | Before PLAY, assign it to User 1 with START+A (PS4: OPTIONS+Cross). Confirm `Gamepad 1 status: ASSIGNED`, watch `G1 raw input`, and run `Test Gamepad (Built-in)` if the values remain zero. |
| Controller icon is orange or inputs are mapped incorrectly | Use an FTC-supported controller and verify its mode. Also confirm the Robot Controller and Driver Station use the same FTC SDK major/minor version. |
| Robot drives backward when the stick is pushed forward | Confirm the Y axis is inverted exactly once in `ViltrumitesTeleOp`; check wiring before changing motor directions. |
| Robot spins when commanded forward | Check each of the four wheels individually; a motor direction or hardware name may be wrong. |
| Forward works but strafing does not | Check X-pattern mecanum wheel placement and the four-wheel sign pattern in the direction test. |
| Robot turns the wrong direction | Check the sign of the right-stick turn input and physical motor installation. |
| One wheel runs backward | Verify that motor's wiring/installation and its documented software direction. |
| Robot moves with sticks centered | Verify the gamepad; increase `JOYSTICK_DEADBAND` slightly above 0.05 if necessary. |
| Front-left is faster or slower | Verify the 1150 RPM motor model and `FRONT_LEFT_POWER_SCALE`; the RPM ratio is only an unloaded starting estimate. Check drag, gearing, battery, and wheel size before tuning. |
| Launcher does not spin | Verify Gamepad 2, the requested trigger, encoder cable, launcher direction, and `RUN_USING_ENCODER`. If both triggers are pressed equally, the correct command is zero. |
| Windmill does not move both ways | Verify Gamepad 2's left-stick X input, CR-servo configuration, wiring, and the `windmill` hardware name. |
| Windmill stops at the same physical point | Hold Gamepad 2 D-pad left/right and watch `G2 windmill command`. If it remains at full power, there is no software travel limit; inspect for a hard stop, binding, voltage loss, or a positional servo installed as a CR servo. |
| Arm moves opposite the bumper labels | Change `RobotConstants.Arm.DIRECTION` between `FORWARD` and `REVERSE`; do not reverse servo power wiring. |
| TeleOp reports missing `arm_servo` | Configure Expansion Hub 2 Servo Port 0 as a Continuous Rotation Servo named exactly `arm_servo`, then save and activate the configuration. |
| Camera test says `Webcam 1` is missing | In the active REV configuration, run Scan, add the USB webcam, name it exactly `Webcam 1`, then save and activate the configuration. |
| Launcher speed immediately overshoots | Verify the encoder is connected beside the matching motor port and polarity is consistent through the wiring. |
| Initialization fails | Read the full Driver Station message and compare every name/type with `HARDWARE_CONFIGURATION.md`. |
| Gradle cannot find the Android SDK | Open through Android Studio and install the requested SDK components, or create a valid local Android SDK configuration. |

## How to Change Constants

Tune shared values in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/config/RobotConstants.java` and autonomous route settings in the Red or Blue autonomous Java file. Change one value at a time, note the result, and keep the robot in a safe test setup. Launcher targets, PIDF coefficients, motor directions, and autonomous timing should not be changed without a measured reason.

The autonomous routines are deliberately simple timed movements. Their distances and angles depend on battery, traction, gearing, and field surface, so drive and turn settings require physical calibration. The timed routes do not use drivetrain encoders.

## How to Add Future Subsystems

1. Add hardware names to `RobotConstants.Hardware`.
2. Create a focused subsystem class under `subsystems` that owns its devices and has a safe `stop()` method.
3. Map the devices once in `Robot.initialize()`.
4. Add low-power checks to the diagnostics before using the subsystem in competition code.
5. Call the subsystem stop method from `Robot.shutdown()`.
6. Update the hardware configuration and startup checklist.

Avoid adding odometry, vision, or a command framework until the stock robot is validated and the new hardware genuinely requires it.

## Project Layout

- `FtcRobotController/` — official FTC Robot Controller module.
- `TeamCode/` — Viltrumites robot code and OpModes.
- `reference/gobilda-original/` — unmodified supplied goBILDA example, excluded from compilation.
- `HARDWARE_CONFIGURATION.md` — exact REV names and direction baseline.
- `FIRST_STARTUP_CHECKLIST.md` — safe commissioning sequence.

Compilation verifies API use and project structure; it cannot verify physical wiring, wheel direction, launcher safety, or autonomous distances. Complete the startup checklist on the real robot before competition use.
