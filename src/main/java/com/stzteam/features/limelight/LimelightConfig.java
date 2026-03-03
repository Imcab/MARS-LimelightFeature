package com.stzteam.features.limelight;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class LimelightConfig {

    private final String limelightKey;
    
    private double maxValidDistanceMeters = 4.0;
    private double maxAngularVelocityDegPerSec = 720.0;
    private double rotationStdDev = 9999999.0;
    private double multiTagStdDev = 0.2;
    private double singleTagBaseStdDev = 0.5;
    private double singleTagDistanceMultiplier = 0.1;
    private Matrix<N3, N1> defaultStdDevs = VecBuilder.fill(0.3, 0.3, rotationStdDev);
    
    public LimelightConfig(String limelightName) {
        this.limelightKey = limelightName;
    }
    
    public LimelightConfig() {
        this("limelight");
    }

    public LimelightConfig withMaxValidDistanceMeters(double meters) {
        this.maxValidDistanceMeters = meters;
        return this;
    }

    public LimelightConfig withMaxAngularVelocity(double degPerSec) {
        this.maxAngularVelocityDegPerSec = degPerSec;
        return this;
    }

    public LimelightConfig withRotationStdDev(double stdDev) {
        this.rotationStdDev = stdDev;
 
        this.defaultStdDevs = VecBuilder.fill(
            this.defaultStdDevs.get(0, 0), 
            this.defaultStdDevs.get(1, 0), 
            stdDev
        );
        return this;
    }

    public LimelightConfig withMultiTagStdDev(double stdDev) {
        this.multiTagStdDev = stdDev;
        return this;
    }

    public LimelightConfig withSingleTagBaseStdDev(double stdDev) {
        this.singleTagBaseStdDev = stdDev;
        return this;
    }

    public LimelightConfig withSingleTagDistanceMultiplier(double multiplier) {
        this.singleTagDistanceMultiplier = multiplier;
        return this;
    }

    public LimelightConfig withDefaultStdDevs(Matrix<N3, N1> stdDevs) {
        this.defaultStdDevs = stdDevs;
        return this;
    }


    public String getLimelightKey() { return limelightKey; }
    public double getMaxValidDistanceMeters() { return maxValidDistanceMeters; }
    public double getMaxAngularVelocityDegPerSec() { return maxAngularVelocityDegPerSec; }
    public double getRotationStdDev() { return rotationStdDev; }
    public double getMultiTagStdDev() { return multiTagStdDev; }
    public double getSingleTagBaseStdDev() { return singleTagBaseStdDev; }
    public double getSingleTagDistanceMultiplier() { return singleTagDistanceMultiplier; }
    public Matrix<N3, N1> getDefaultStdDevs() { return defaultStdDevs; }
}