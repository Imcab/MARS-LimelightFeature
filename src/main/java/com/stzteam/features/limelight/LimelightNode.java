package com.stzteam.features.limelight;

import java.util.function.Consumer;

import com.stzteam.features.limelight.LimelightHelpers.LimelightResults;
import com.stzteam.features.limelight.services.LimelightQuery;
import com.stzteam.features.limelight.services.LimelightReply;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.services.Service;
import com.stzteam.mars.services.nodes.Node;
import com.stzteam.mars.services.nodes.NodeMessage;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class LimelightNode extends Node<LimelightNode.LimelightMsg> implements Service<LimelightQuery, LimelightReply> {

    public static class LimelightMsg extends NodeMessage<LimelightMsg> {
        public boolean hasTarget = false;
        public Pose2d botPose = new Pose2d();
        public double timestamp = 0.0;
        public boolean validPose = false;
        public Matrix<N3, N1> stdDevs;
        
        public boolean tv = false;
        public double tx = 0.0;
        public double ty = 0.0;
        public double ta = 0.0;
        public int pipeline = 0;
        public int targetCount = 0;

        public LimelightResults lastestResult = new LimelightResults();

        @Override
        public void telemeterize(String tableName) {

            NetworkIO.set(tableName, "has_target", hasTarget);
            NetworkIO.set(tableName, "robot_pose", botPose);
            NetworkIO.set(tableName, "timestamp", timestamp);
            NetworkIO.set(tableName, "valid_pose", validPose);

            com.stzteam.forgemini.io.NetworkIO.set(tableName, "tv", tv);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "tx", tx);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "ty", ty);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "ta", ta);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "pipeline", pipeline);

            NetworkIO.set(tableName, "targetcount", targetCount);

            if (lastestResult != null) {

                NetworkIO.set(tableName, "results/botpose", lastestResult.botpose);
                NetworkIO.set(tableName, "results/botpose_wpired", lastestResult.botpose_wpired);
                NetworkIO.set(tableName, "results/botpose_wpiblue", lastestResult.botpose_wpiblue);
                NetworkIO.set(tableName, "results/camerapose_robotspace", lastestResult.camerapose_robotspace);

                NetworkIO.set(tableName, "results/targets/retro_count", 
                    lastestResult.targets_Retro != null ? lastestResult.targets_Retro.length : 0);
                    
                NetworkIO.set(tableName, "results/targets/fiducials_count", 
                    lastestResult.targets_Fiducials != null ? lastestResult.targets_Fiducials.length : 0);
                    
                NetworkIO.set(tableName, "results/targets/classifier_count", 
                    lastestResult.targets_Classifier != null ? lastestResult.targets_Classifier.length : 0);
                    
                NetworkIO.set(tableName, "results/targets/detector_count", 
                    lastestResult.targets_Detector != null ? lastestResult.targets_Detector.length : 0);
                    
                NetworkIO.set(tableName, "results/targets/barcode_count", 
                    lastestResult.targets_Barcode != null ? lastestResult.targets_Barcode.length : 0);
            }
        }
    }

    public interface LimelightIO {
        void updateData(LimelightMsg data, LimelightConfig config);
    }

    private final LimelightIO hardwareDriver;
    private final LimelightConfig config;

    public LimelightNode(LimelightConfig config, LimelightIO hardwareDriver, Consumer<LimelightMsg> topicPublisher) {
        super(config.getLimelightKey(), new LimelightMsg(), topicPublisher);
        this.config = config;
        this.hardwareDriver = hardwareDriver;
        
        this.messagePayload.stdDevs = config.getDefaultStdDevs();
    }

    @Override
    public LimelightReply execute(LimelightQuery query) {
        if (isFallback() || !messagePayload.validPose) {
            return new LimelightReply(false, new Pose2d(), 0, config.getDefaultStdDevs());
        }
        return new LimelightReply(
            messagePayload.hasTarget, 
            messagePayload.botPose, 
            messagePayload.timestamp, 
            messagePayload.stdDevs
        );
    }

    @Override
    protected void processInformation() {
        hardwareDriver.updateData(messagePayload, config);
    }
}