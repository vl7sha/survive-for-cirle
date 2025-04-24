package ru.vl7sha.demo1.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Enemy extends Entity {
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected double speed;
    protected int experienceValue;
    protected int waveLevel;
    
    // Status effects
    protected boolean onIce;
    protected boolean inLava;
    protected boolean inBushes;
    
    public Enemy(double x, double y, double width, double height, int waveLevel) {
        super(x, y, width, height);
        this.waveLevel = waveLevel;
        
        // Initialize status effects
        this.onIce = false;
        this.inLava = false;
        this.inBushes = false;
    }
    
    @Override
    public void update(double deltaTime) {
        if (!alive) return;
        
        // Find the nearest player and move towards them
        Player target = findNearestPlayer();
        if (target != null) {
            moveTowardsTarget(target, deltaTime);
        }
    }
    
    protected Player findNearestPlayer() {
        // This would be implemented to find the nearest player
        // For now, we'll assume there's only one player at position (0,0)
        return new Player(0, 0);
    }
    
    protected void moveTowardsTarget(Player target, double deltaTime) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double effectiveSpeed = speed;
            
            // Apply status effect modifiers
            if (inLava) {
                effectiveSpeed *= 0.5; // 50% speed in lava
            } else if (onIce) {
                effectiveSpeed *= 1.5; // 150% speed on ice
            }
            
            // Normalize and apply movement
            dx = dx / distance * effectiveSpeed * deltaTime;
            dy = dy / distance * effectiveSpeed * deltaTime;
            
            x += dx;
            y += dy;
        }
    }
    
    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw enemy
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
        
        // Draw health bar
        double healthBarWidth = width;
        double healthBarHeight = 3;
        double healthPercentage = (double) health / maxHealth;
        
        gc.setFill(Color.RED);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 5, healthBarWidth, healthBarHeight);
        
        gc.setFill(Color.GREEN);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 5, healthBarWidth * healthPercentage, healthBarHeight);
    }
    
    // Status effect setters
    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
    }
    
    public void setInLava(boolean inLava) {
        this.inLava = inLava;
    }
    
    public void setInBushes(boolean inBushes) {
        this.inBushes = inBushes;
    }
    
    // Getters
    public int getHealth() {
        return health;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public int getExperienceValue() {
        return experienceValue;
    }
}