package ru.vl7sha.demo1.network;

import java.io.Serializable;

public class ExperienceCollectedPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String orbId;
    private String playerId;
    
    public ExperienceCollectedPacket(String orbId, String playerId) {
        this.orbId = orbId;
        this.playerId = playerId;
    }
    
    public String getOrbId() {
        return orbId;
    }
    
    public void setOrbId(String orbId) {
        this.orbId = orbId;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}