package ru.vl7sha.demo1.entity.enemy;


import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Player;
import ru.vl7sha.demo1.entity.Projectile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class RangedEnemy extends Enemy {
    private double attackCooldown;
    private double attackRange;
    private double currentCooldown;
    
    public RangedEnemy(double x, double y, int waveLevel) {
        super(x, y, 20, 20, waveLevel);
        this.color = Color.PURPLE;
        
        // Ranged enemies have medium health and speed
        this.maxHealth = 15 + (waveLevel * 4);
        this.health = maxHealth;
        this.damage = 4 + waveLevel;
        this.speed = 80 + (waveLevel * 3);
        this.experienceValue = 15;
        
        // Ranged attack properties
        this.attackRange = 300;
        this.attackCooldown = 2.0; // Seconds between attacks
        this.currentCooldown = 0;
    }
    
    @Override
    public void update(double deltaTime) {
        if (!alive) return;
        
        // Update attack cooldown
        if (currentCooldown > 0) {
            currentCooldown -= deltaTime;
        }
        
        // Find the nearest player
        Player target = findNearestPlayer();
        if (target != null) {
            double distanceToTarget = calculateDistance(target);
            
            if (distanceToTarget <= attackRange) {
                // If within attack range, stop moving and attack
                if (currentCooldown <= 0) {
                    attack(target);
                    currentCooldown = attackCooldown;
                }
                
                // If too close, move away from player
                if (distanceToTarget < attackRange * 0.5) {
                    moveAwayFromTarget(target, deltaTime);
                } else {
                    // Otherwise, stay still
                }
            } else {
                // If outside attack range, move towards player
                moveTowardsTarget(target, deltaTime);
            }
        }
    }
    
    private double calculateDistance(Player target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private void moveAwayFromTarget(Player target, double deltaTime) {
        double dx = x - target.getX();
        double dy = y - target.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double effectiveSpeed = speed * 0.7; // Move away slower
            
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
    
    private void attack(Player target) {
        // Calculate direction to player
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            // Normalize direction
            dx = dx / distance;
            dy = dy / distance;
            
            // Create a projectile
            Projectile projectile = new Projectile(x, y, dx, dy, damage, 300, Color.PURPLE);
            
            // In a real implementation, we would add this projectile to a list in the game
            // For now, we'll just print a message
            System.out.println("Ranged enemy fired a projectile!");
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw the enemy
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
        
        // Draw a ring to indicate it's a ranged enemy
        gc.setStroke(Color.LIGHTBLUE);
        gc.setLineWidth(2);
        gc.strokeOval(x - width / 2 - 3, y - height / 2 - 3, width + 6, height + 6);
        
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