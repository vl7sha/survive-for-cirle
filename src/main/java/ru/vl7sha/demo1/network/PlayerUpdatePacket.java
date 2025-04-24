package ru.vl7sha.demo1.network;

import java.io.Serializable;

public class PlayerUpdatePacket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private NetworkEntity player;
    
    public PlayerUpdatePacket(NetworkEntity player) {
        this.player = player;
    }
    
    public NetworkEntity getPlayer() {
        return player;
    }
    
    public void setPlayer(NetworkEntity player) {
        this.player = player;
    }
}