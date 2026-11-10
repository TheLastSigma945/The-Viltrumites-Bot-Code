# Viltrumites autonomous

The Driver Station has exactly two BIOBUZZ autonomous choices:
**Viltrumites Red Auto** and **Viltrumites Blue Auto**. Select one, press
INIT, then START. Neither routine reads a controller. The older calibration,
park, and alternate alliance OpModes have been removed.

Both choices follow the same sequence. The Blue first forward leg lasts
3.5 seconds; the Red leg lasts 3.0 seconds. All other timings match.

| Step | Blue | Red |
|---|---:|---:|
| Drive backward at 0.25 power | 1.0 s | 1.0 s |
| Launcher spin-up at normal 1250 encoder ticks/s | 2.0 s | 2.0 s |
| Windmill feed at full power | Four 1.5 s pulses | Four 1.5 s pulses |
| Launcher recovery between feeds | 2.0 s, three times | 2.0 s, three times |
| First right turn at 0.30 power | 1.8 s | 1.8 s |
| First forward leg at 0.25 power | 3.5 s | 3.0 s |
| Second right turn at 0.30 power | 1.5 s | 1.5 s |
| Final forward leg at 0.25 power | 7.0 s | 7.0 s |

The launcher and windmill stop before the first turn. The routine stops all
motors and servos on STOP, error, or the 30-second software limit. Blue's
nominal step times total 28.8 seconds and Red's total 28.3 seconds, leaving
little time for overhead. A feed pulse does not confirm that a ball fired.

## Changing the path

Edit the `ROUTE SETTINGS` section in
[`BlueViltrumitesSingleAuto.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/autonomous/BlueViltrumitesSingleAuto.java)
or
[`RedViltrumitesSingleAuto.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/autonomous/RedViltrumitesSingleAuto.java).
The route steps appear in `runOpMode()` in execution order. Keep both files
in sync if you want identical paths. Do not add steps that exceed the
30-second limit.

The turn durations remain at their previous 1.8 and 1.5 seconds. Turn power
was raised from 0.22 to 0.30 because both turns were reported short after
the four-motor drivetrain update. This is an initial adjustment, not a
measured angle. Test each turn on a clear practice field; reduce power if it
overshoots or increase it cautiously if it still falls short. The drive is
timed, so battery voltage and floor grip also affect the path.

The Driver Station shows a `Code revision` marker during INIT and running,
plus the requested left/right drive power and launcher target/current
velocity. Check the marker after installing an updated Robot Controller app
so an older build is not mistaken for this route.
