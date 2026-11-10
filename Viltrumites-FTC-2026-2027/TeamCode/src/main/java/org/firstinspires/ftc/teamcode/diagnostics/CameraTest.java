package org.firstinspires.ftc.teamcode.diagnostics;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.config.RobotConstants;
import org.firstinspires.ftc.vision.VisionPortal;

/** Safely verifies a configured USB webcam and exposes its stream to the Driver Station. */
@TeleOp(name = "StarterBot Camera Test", group = "Diagnostics")
public final class CameraTest extends OpMode {
    private VisionPortal visionPortal;
    private String cameraError = "";
    private boolean lastA;
    private boolean lastB;
    private boolean lastX;
    private int frameNumber;

    @Override
    public void init() {
        try {
            WebcamName webcam = hardwareMap.get(
                    WebcamName.class, RobotConstants.Hardware.WEBCAM);
            if (!webcam.isAttached()) {
                throw new IllegalStateException("Configured webcam is not attached");
            }

            visionPortal = new VisionPortal.Builder()
                    .setCamera(webcam)
                    .setCameraResolution(new Size(
                            RobotConstants.Vision.STREAM_WIDTH,
                            RobotConstants.Vision.STREAM_HEIGHT))
                    .enableLiveView(true)
                    .setAutoStopLiveView(false)
                    .setShowStatsOverlay(true)
                    .build();
        } catch (RuntimeException error) {
            cameraError = error.getMessage() == null
                    ? error.getClass().getSimpleName()
                    : error.getMessage();
            closeCamera();
        }

        addCameraTelemetry();
        telemetry.update();
    }

    @Override
    public void init_loop() {
        addCameraTelemetry();
        telemetry.update();
    }

    @Override
    public void loop() {
        if (visionPortal != null) {
            if (gamepad1.a && !lastA) {
                visionPortal.resumeStreaming();
            }
            if (gamepad1.b && !lastB) {
                visionPortal.stopStreaming();
            }
            if (gamepad1.x && !lastX) {
                visionPortal.saveNextFrameRaw("StarterBot-Camera-" + frameNumber++);
            }
        }

        lastA = gamepad1.a;
        lastB = gamepad1.b;
        lastX = gamepad1.x;
        addCameraTelemetry();
        telemetry.update();
    }

    @Override
    public void stop() {
        closeCamera();
    }

    private void addCameraTelemetry() {
        telemetry.addLine("StarterBot USB Camera Test");
        if (visionPortal == null) {
            telemetry.addData("Camera", "NOT READY");
            telemetry.addData("Reason", cameraError);
            telemetry.addLine("Configure the USB webcam as 'Webcam 1', then restart this OpMode.");
            return;
        }

        telemetry.addData("Camera", RobotConstants.Hardware.WEBCAM);
        telemetry.addData("State", visionPortal.getCameraState());
        telemetry.addData("Frame rate", "%.1f FPS", visionPortal.getFps());
        telemetry.addData(
                "Resolution", "%d x %d",
                RobotConstants.Vision.STREAM_WIDTH,
                RobotConstants.Vision.STREAM_HEIGHT);
        telemetry.addLine("During INIT: DS menu (3 dots) > Camera Stream.");
        telemetry.addLine("After PLAY: A resume | B pause | X save one frame.");
    }

    private void closeCamera() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }
}
