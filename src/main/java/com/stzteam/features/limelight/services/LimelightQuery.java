package com.stzteam.features.limelight.services;

import com.stzteam.mars.services.Query;

public class LimelightQuery implements Query {

    public final int targetId; 
    public LimelightQuery(int targetId) { this.targetId = targetId; }
    public LimelightQuery() { this(0); }
}
