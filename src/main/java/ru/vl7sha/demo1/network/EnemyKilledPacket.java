package ru.vl7sha.demo1.network;

import java.io.Serializable;

public class EnemyKilledPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private NetworkEntity enemy;
    private NetworkEntity killer;
    
    public EnemyKilledPacket(NetworkEntity enemy, NetworkEntity killer) {
        this.enemy = enemy;
        this.killer = killer;
    }
    
    public NetworkEntity getEnemy() {
        return enemy;
    }
    
    public void setEnemy(NetworkEntity enemy) {
        this.enemy = enemy;
    }
    
    public NetworkEntity getKiller() {
        return killer;
    }
    
    public void setKiller(NetworkEntity killer) {
        this.killer = killer;
    }
}