package ru.vl7sha.demo1.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double radius; // For circular collision detection
    protected boolean alive;
    protected Color color;
    
    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.radius = Math.max(width, height) / 2;
        this.alive = true;
        this.color = Color.WHITE; // Default color
    }
    
    public abstract void update(double deltaTime);
    
    public void render(GraphicsContext gc) {
        // Default rendering as a colored rectangle
        gc.setFill(color);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
    }
    
    // Getters and setters
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    // Collision detection helper methods
    public boolean intersects(Entity other) {
        // Simple circle-based collision detection
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.radius + other.radius);
    }
}