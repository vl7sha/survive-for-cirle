package ru.vl7sha.demo1.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ExperienceOrb extends Entity {
    private int value;
    private double lifetime;
    private double pulseTimer;
    private double pulseDirection;
    private double baseSize;
    
    public ExperienceOrb(double x, double y, int value) {
        super(x, y, 15, 15);
        this.value = value;
        this.color = Color.LIGHTBLUE;
        this.lifetime = 20.0; // 20 seconds before disappearing
        this.pulseTimer = 0;
        this.pulseDirection = 1;
        this.baseSize = 15;
    }
    
    @Override
    public void update(double deltaTime) {
        // Update lifetime
        lifetime -= deltaTime;
        if (lifetime <= 0) {
            alive = false;
        }
        
        // Pulse effect
        pulseTimer += deltaTime * 2;
        if (pulseTimer >= 1.0) {
            pulseDirection *= -1;
            pulseTimer = 0;
        }
        
        // Adjust size based on pulse
        double pulseFactor = 1.0 + (pulseTimer * 0.2 * pulseDirection);
        width = baseSize * pulseFactor;
        height = baseSize * pulseFactor;
        radius = baseSize * pulseFactor / 2;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw a glowing orb
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
        
        // Add a highlight
        gc.setFill(Color.WHITE);
        gc.fillOval(x - width / 4, y - height / 4, width / 4, height / 4);
    }
    
    public int getValue() {
        return value;
    }
}