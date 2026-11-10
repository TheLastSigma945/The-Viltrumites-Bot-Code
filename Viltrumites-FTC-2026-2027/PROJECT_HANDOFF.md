# Project handoff for a new computer

## Get the project

- GitHub repository: https://github.com/Shreshtgaming606/Viltrumites-FTC-2026-2027
- Active branch: `main`. The GitHub ZIP contains the complete tracked source at that branch. Clone the repository when you want to make changes and push them.
- The old `autonomous-testing` branch history is preserved under tag `archive-autonomous-testing-2026-11-07`. Its branch was removed so `main` is the single active development branch. The older `oct-4-2026` branch was already contained in `main`.
- The old computer kept two synchronized copies at `C:\Projects\Codex\Viltrumites-FTC-2026-2027` and `C:\Projects\Github\Viltrumites-FTC-2026-2027`. On a new computer, one clone is sufficient.
- Open the repository root in Android Studio. It is an FTC SDK v12.0 Android/Gradle project. Let Android Studio install or locate the Android SDK and sync Gradle. `local.properties` is ignored because its SDK path is specific to each computer.
- On Windows, verify code with `./gradlew.bat :TeamCode:testDebugUnitTest :TeamCode:assembleDebug` from the repository root. Build success does not install the Robot Controller app on the hub.

## Robot and code layout

- Team: The Viltrumites, 2026–2027 BIOBUZZ FTC season. Java code is in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.
- `teleop/ViltrumitesTeleOp.java` is the manual match OpMode. `Robot.java` maps the mechanisms. `config/RobotConstants.java` has hardware names and shared tuning values. `subsystems/` owns drive, intake, arm, and launcher behavior.
- There are exactly two active autonomous OpModes: `autonomous/RedViltrumitesSingleAuto.java` and `autonomous/BlueViltrumitesSingleAuto.java`. They are independent of controller input and use timed motion. See `AUTONOMOUS_README.md` for the step sequence.
- Do not infer that a successful compile proves wheel direction, autonomous angles, launcher speed, or scoring. Timed paths still need physical calibration.

## Hardware configuration

- Four X-pattern mecanum wheels. Front-left `front_left_drive` is the **1150 RPM** motor on Expansion Hub motor port 2. Front-right `front_right_drive` is a 312 RPM motor on Control Hub motor port 3. Rear-left `left_drive` and rear-right `right_drive` are 312 RPM motors on their existing ports. Their exact rear port numbers need physical verification.
- The front-left motor has an open-loop power multiplier of `312.0 / 1150.0`. In TeleOp, the two rear wheels receive 50% of the sideways strafe contribution. These settings were restored after a full-power experiment. The full-power experiment is preserved in Git history; the pre-experiment snapshot is tagged `backup-before-full-drive-power-2026-10-31`.
- The launcher hardware name is `launcher`; it is a goBILDA 6000 RPM encoder-equipped motor. Its encoder cable must match its motor port. Normal target is 1250 encoder ticks/second, tested fast target is 2000, diagnostic target is 600. Current PIDF is 40 / 0 / 0 / 12.5.
- `windmill`, `left_intake_servo`, and `right_intake_servo` are continuous-rotation servos. The intake motor is `intake`. The collection arm is a continuous-rotation servo named `arm_servo` on Expansion Hub 2, Servo Port 0; releasing its control stops it.
- The optional USB camera is a Logitech C270 and must be configured in the Driver Station as `Webcam 1`. Hardware names are case-sensitive. See `HARDWARE_CONFIGURATION.md` for the full mapping and directions. The active Driver Station hardware configuration is stored on the robot setup, not transferred by cloning this repository.

## Controls and autonomous route

- Gamepad 1 drives: left stick forward/back and sideways strafe, right stick turns, held Y selects 40% precision. Right trigger intakes; left trigger ejects. Left bumper runs the arm up; right bumper runs it down; release stops it.
- Gamepad 2 controls the launcher and windmill. Right trigger launches, left trigger reverses the launcher. PS4 Cross/X selects the tested 2000-ticks/second fast mode; Circle/O selects normal 1250. Left stick X or held D-pad left/right turns the windmill. Nothing starts automatically in TeleOp. See `README.md` for controller assignment on the Driver Station.
- Both autonomous profiles begin with a 1.0-second backward drive at 0.25 power, spin the normal launcher for 2.0 seconds, perform four 1.5-second windmill feeds separated by 2.0-second recovery intervals, turn right for 1.8 seconds at 0.30 power, drive forward (Blue 3.5 seconds; Red 3.0), turn right for 1.5 seconds at 0.30 power, then drive forward 7.0 seconds. The launcher stops before the first turn. There is a 30-second software limit. Autonomous uses the same launcher setup class as TeleOp.

## Physical checks still needed

- A field test reported that the first autonomous step moved forward even though source code commands negative power; later positive-power forward steps reportedly moved forward. The contradiction has not been resolved with a verified run of the latest build. Both autonomous OpModes display a code marker and requested drive powers on Driver Station telemetry to confirm what is installed.
- Both right turns were reported short at power 0.22. The code now requests 0.30, but the resulting angles have not been measured on the real robot. The front-left speed mismatch and the restored power limiter may affect motion.
- Autonomous launcher firing and ball count have not been physically confirmed. During the spin-up step, read launcher target and current velocity from telemetry. A timed windmill pulse is not proof that a ball fired.
- Test wheel direction on blocks, then move cautiously on a clear floor. Install the newly built Robot Controller app before comparing physical behavior with source code.

## Ready-to-paste prompt for Codex on the new computer

> Continue my FTC robot project for The Viltrumites. Clone `https://github.com/Shreshtgaming606/Viltrumites-FTC-2026-2027.git` on the `main` branch, then read `PROJECT_HANDOFF.md`, `README.md`, `HARDWARE_CONFIGURATION.md`, and `AUTONOMOUS_README.md`. Check the actual code and Git status before changing anything. This is an FTC SDK v12.0 Java Android Studio project. Preserve the existing manual TeleOp behavior and the separate Red and Blue timed autonomous routes unless I request a change. The four mecanum drive motors include a 1150 RPM front-left motor with a `312/1150` power limiter; the rear wheels have a 50% TeleOp strafe contribution. The launcher normal target is 1250 encoder ticks/s and fast mode is 2000 in TeleOp. The arm and windmill are continuous-rotation servos. The autonomous route and its unresolved physical calibration issues are described in the handoff file. Build with `./gradlew.bat :TeamCode:testDebugUnitTest :TeamCode:assembleDebug`, keep my GitHub `main` branch and local clone synchronized after authorized changes, and tell me what still needs a real-robot test. Ask me for new hardware measurements when code and physical observations conflict.
