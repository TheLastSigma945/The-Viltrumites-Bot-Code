# BIOBUZZ field coordinates and route worksheet

Sources: [FIRST Competition Manual TU06, sections 9–11](https://ftc-resources.firstinspires.org/ftc/game/cm-html),
[FIRST Event Field Setup Guide and CAD links](https://ftc-resources.firstinspires.org/ftc/field).
Manual dimensions are nominal; CAD has about ±1 in general tolerance.

The software origin is field center. X is positive toward blue columns,
Y positive toward software row 1, inches. Heading 0 faces +Y; positive
heading turns left toward -X. These axes are **software conventions**.
The nominal tile-center transform is:

`x = -72 + (columnIndex + 0.5) × 24` (A=0 through F=5)

`y = 72 - (rowIndex - 0.5) × 24` (row 1 through 6)

Verify row orientation against official Figure 9-5 when placing the field;
the row index here is the software drawing convention. No automatic path
uses an unverified tile coordinate. For a mirrored alliance pose, negate X
and heading. The robot pose is at its **center**, not the front bumper.

```text
                 +Y (software row 1)
             A     B     C  |  D     E     F
 row 1      -60   -36   -12 | +12   +36   +60
 row 2      -60   -36   -12 | +12   +36   +60
 row 3      -60   -36   -12 | +12   +36   +60
                         [HIVE near field center]
 row 4      -60   -36   -12 | +12   +36   +60
 row 5      -60   -36   -12 | +12   +36   +60
 row 6      -60   -36   -12 | +12   +36   +60
              red half      |       blue half
                 -Y (software row 6)
```

The HIVE is centered in the field, with a 49.46 × 38.95 in frame base.
Four FLOWERS attach to the perimeter. Each alliance has a nominal 23 ×
11 in LOADING ZONE next to its ALLIANCE AREA. Exact FLOWER positions,
LOADING ZONE boundaries, HIVE collision polygon and safe shooting pose
must be taken from the official CAD/guide and then checked on the actual
field. None is hard-coded as an invented coordinate.

| Route | Robot-center waypoints / action | Heading | Clearance and status |
|---|---|---|---|
| Red SAFE_LEAVE | legal red wall start → local forward timed 0.80 s | inward from selected wall | Requires measured robot length and proof that entire robot clears wall/HIVE/FLOWERS. Timed trial, not a known coordinate. |
| Blue SAFE_LEAVE | legal blue wall start → local forward timed 0.80 s | inward from selected wall | Same physical checks. |
| Red LEAVE_PARK | measured legal AUDIENCE start → red LOADING ZONE via straight measured distance | inward | Disabled until footprint, zone, route and encoder calibration are verified. |
| Blue LEAVE_PARK | measured legal AUDIENCE start → blue LOADING ZONE via straight measured distance | inward | Same validation. |
| HIVE_PARK | measured start → shooting pose → velocity-confirmed feed → measured LOADING ZONE | measured | Disabled; requires projectile and path testing. |

Worksheet before enabling PARK: actual length ___ in; width ___ in;
intake extension ___ in; launcher extension ___ in; turning sweep ___ in;
red start center/heading ___; blue start center/heading ___;
red LOADING ZONE CAD bounds ___; blue bounds ___;
minimum full-envelope obstacle clearance ___ (include at least field
tolerance plus robot-specific margin). Keep robot inside the legal
alliance half at setup; do not start in a LOADING ZONE or contact a FLOWER.
