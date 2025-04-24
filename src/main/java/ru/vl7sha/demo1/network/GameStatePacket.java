package ru.vl7sha.demo1.network;

import java.io.Serializable;
import java.util.List;

public class GameStatePacket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int currentWave;
    private List<NetworkEntity> players;
    private List<NetworkEntity> enemies;
    private List<NetworkEntity> experienceOrbs;
    
    // Getters and setters
    public int getCurrentWave() {
        return currentWave;
    }
    
    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }
    
    public List<NetworkEntity> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<NetworkEntity> players) {
        this.players = players;
    }
    
    public List<NetworkEntity> getEnemies() {
        return enemies;
    }
    
    public void setEnemies(List<NetworkEntity> enemies) {
        this.enemies = enemies;
    }
    
    public List<NetworkEntity> getExperienceOrbs() {
        return experienceOrbs;
    }
    
    public void setExperienceOrbs(List<NetworkEntity> experienceOrbs) {
        this.experienceOrbs = experienceOrbs;
    }
}