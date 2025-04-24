package ru.vl7sha.demo1.entity.enemy;


import ru.vl7sha.demo1.entity.Enemy;

import javafx.scene.paint.Color;

public class FastEnemy extends Enemy {
    
    public FastEnemy(double x, double y, int waveLevel) {
        super(x, y, 15, 15, waveLevel);
        this.color = Color.ORANGE;
        
        // Fast enemies have less health but move faster
        this.maxHealth = 10 + (waveLevel * 3);
        this.health = maxHealth;
        this.damage = 3 + waveLevel;
        this.speed = 180 + (waveLevel * 5); // Much faster than basic enemies
        this.experienceValue = 8;
    }
}