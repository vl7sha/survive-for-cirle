package ru.vl7sha.demo1.entity.enemy;


import ru.vl7sha.demo1.entity.Enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TankEnemy extends Enemy {
    
    public TankEnemy(double x, double y, int waveLevel) {
        super(x, y, 35, 35, waveLevel);
        this.color = Color.DARKRED;
        
        // Tank enemies have more health but move slower
        this.maxHealth = 50 + (waveLevel * 10);
        this.health = maxHealth;
        this.damage = 8 + (waveLevel * 2);
        this.speed = 60 + waveLevel; // Slower than basic enemies
        this.experienceValue = 20;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw a larger, more intimidating enemy
        gc.setFill(color);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
        
        // Draw health bar
        double healthBarWidth = width;
        double healthBarHeight = 5;
        double healthPercentage = (double) health / maxHealth;
        
        gc.setFill(Color.RED);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 10, healthBarWidth, healthBarHeight);
        
        gc.setFill(Color.GREEN);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 10, healthBarWidth * healthPercentage, healthBarHeight);
    }
}