package com.stzteam.features.limelight;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation2d;

public class LimelightDriver implements LimelightNode.LimelightIO {

    private final Supplier<Rotation2d> yawSupplier;
    private final DoubleSupplier yawRateSupplier;

    public LimelightDriver(Supplier<Rotation2d> yawSupplier, DoubleSupplier yawRateSupplier) {
        this.yawSupplier = yawSupplier;
        this.yawRateSupplier = yawRateSupplier;
    }

    @Override
    public void updateData(LimelightNode.LimelightMsg data, LimelightConfig config) {
        String cameraName = config.getLimelightKey();
        
        data.hasTarget = LimelightHelpers.getTV(cameraName);

        data.tv = data.hasTarget;
        data.tx = LimelightHelpers.getTX(cameraName);
        data.ty = LimelightHelpers.getTY(cameraName);
        data.ta = LimelightHelpers.getTA(cameraName);
        data.pipeline = (int) LimelightHelpers.getCurrentPipelineIndex(cameraName);
        data.targetCount = LimelightHelpers.getTargetCount(cameraName);

        data.lastestResult = LimelightHelpers.getLatestResults(cameraName);
        
        double currentYaw = yawSupplier.get().getDegrees();
        double currentYawRate = yawRateSupplier.getAsDouble();

        LimelightHelpers.SetRobotOrientation(cameraName, currentYaw, 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(cameraName);
        
        if (mt2 == null || mt2.tagCount == 0 || Math.abs(currentYawRate) > config.getMaxAngularVelocityDegPerSec()) {
            data.validPose = false;
            return;
        }

        double xyStdDev = (mt2.tagCount >= 2) ? config.getMultiTagStdDev() : calculateSingleTagStdDev(mt2.avgTagDist, config);

        if (mt2.avgTagDist > config.getMaxValidDistanceMeters() && mt2.tagCount < 2) {
            data.validPose = false;
            return;
        }

        data.botPose = mt2.pose;
        data.timestamp = mt2.timestampSeconds;
        data.validPose = true;
        data.stdDevs = VecBuilder.fill(xyStdDev, xyStdDev, config.getRotationStdDev());
    }

    private double calculateSingleTagStdDev(double distance, LimelightConfig config) {
        return config.getSingleTagBaseStdDev() + 
               (Math.pow(distance, 2) * config.getSingleTagDistanceMultiplier());
    }
}