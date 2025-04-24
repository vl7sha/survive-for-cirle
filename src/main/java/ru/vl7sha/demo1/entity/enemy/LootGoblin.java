package ru.vl7sha.demo1.entity.enemy;


import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LootGoblin extends Enemy {
    private double fleeTimer;
    private double fleeThreshold;
    private boolean fleeing;
    
    public LootGoblin(double x, double y, int waveLevel) {
        super(x, y, 25, 25, waveLevel);
        this.color = Color.GOLD;
        
        // Loot goblins have low health but drop a lot of experience
        this.maxHealth = 15 + (waveLevel * 2);
        this.health = maxHealth;
        this.damage = 0; // Doesn't deal damage
        this.speed = 150 + (waveLevel * 4); // Fast
        this.experienceValue = 50 + (waveLevel * 10); // Drops a lot of experience
        
        // Fleeing behavior
        this.fleeTimer = 0;
        this.fleeThreshold = 10.0; // Flees after 10 seconds
        this.fleeing = false;
    }
    
    @Override
    public void update(double deltaTime) {
        if (!alive) return;
        
        // Update flee timer
        fleeTimer += deltaTime;
        if (fleeTimer >= fleeThreshold) {
            fleeing = true;
        }
        
        // Find the nearest player
        Player target = findNearestPlayer();
        if (target != null) {
            if (fleeing) {
                // Run away from player
                fleeFromTarget(target, deltaTime);
            } else {
                // Run around randomly
                moveRandomly(deltaTime);
            }
        }
        
        // If the loot goblin has been fleeing for too long, remove it
        if (fleeing && fleeTimer >= fleeThreshold + 5.0) {
            alive = false;
        }
    }
    
    private void fleeFromTarget(Player target, double deltaTime) {
        double dx = x - target.getX();
        double dy = y - target.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double effectiveSpeed = speed * 1.5; // Flee faster
            
            // Apply status effect modifiers
            if (inLava) {
                effectiveSpeed *= 0.5;
            } else if (onIce) {
                effectiveSpeed *= 1.5;
            }
            
            // Normalize and apply movement
            dx = dx / distance * effectiveSpeed * deltaTime;
            dy = dy / distance * effectiveSpeed * deltaTime;
            
            x += dx;
            y += dy;
        }
    }
    
    private void moveRandomly(double deltaTime) {
        // Change direction every second
        if (Math.random() < deltaTime) {
            double angle = Math.random() * 2 * Math.PI;
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            
            double effectiveSpeed = speed;
            
            // Apply status effect modifiers
            if (inLava) {
                effectiveSpeed *= 0.5;
            } else if (onIce) {
                effectiveSpeed *= 1.5;
            }
            
            x += dx * effectiveSpeed * deltaTime;
            y += dy * effectiveSpeed * deltaTime;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw the loot goblin
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
        
        // Draw a dollar sign to indicate it's a loot goblin
        gc.setFill(Color.BLACK);
        gc.fillText("$", x - 4, y + 4);
        
        // Draw health bar
        double healthBarWidth = width;
        double healthBarHeight = 3;
        double healthPercentage = (double) health / maxHealth;
        
        gc.setFill(Color.RED);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 8, healthBarWidth, healthBarHeight);
        
        gc.setFill(Color.GREEN);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 8, healthBarWidth * healthPercentage, healthBarHeight);
    }
}