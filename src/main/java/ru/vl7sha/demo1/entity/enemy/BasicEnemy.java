package ru.vl7sha.demo1.entity.enemy;


import ru.vl7sha.demo1.entity.Enemy;

import javafx.scene.paint.Color;

public class BasicEnemy extends Enemy {
    
    public BasicEnemy(double x, double y, int waveLevel) {
        super(x, y, 20, 20, waveLevel);
        this.color = Color.RED;
        
        // Base stats
        this.maxHealth = 20 + (waveLevel * 5);
        this.health = maxHealth;
        this.damage = 5 + waveLevel;
        this.speed = 100 + (waveLevel * 2); // Pixels per second
        this.experienceValue = 10;
    }
}