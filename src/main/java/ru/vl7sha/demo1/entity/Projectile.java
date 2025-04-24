package ru.vl7sha.demo1.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile extends Entity {
    private double directionX;
    private double directionY;
    private double speed;
    private int damage;
    private double lifetime;
    private double currentLifetime;
    
    public Projectile(double x, double y, double directionX, double directionY, int damage, double speed, Color color) {
        super(x, y, 10, 10);
        this.directionX = directionX;
        this.directionY = directionY;
        this.damage = damage;
        this.speed = speed;
        this.color = color;
        this.lifetime = 2.0; // 2 seconds before disappearing
        this.currentLifetime = 0;
    }
    
    @Override
    public void update(double deltaTime) {
        // Update position
        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;
        
        // Update lifetime
        currentLifetime += deltaTime;
        if (currentLifetime >= lifetime) {
            alive = false;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
}