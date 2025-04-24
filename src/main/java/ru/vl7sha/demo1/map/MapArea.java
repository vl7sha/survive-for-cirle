package ru.vl7sha.demo1.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapArea {
    private MapAreaType type;
    private double x;
    private double y;
    private double width;
    private double height;
    
    public MapArea(MapAreaType type, double x, double y, double width, double height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }
    
    public void render(GraphicsContext gc) {
        // Set color based on area type
        switch (type) {
            case LAVA:
                gc.setFill(Color.rgb(200, 50, 0, 0.7)); // Semi-transparent red/orange
                break;
            case ICE:
                gc.setFill(Color.rgb(100, 200, 255, 0.7)); // Semi-transparent light blue
                break;
            case BUSHES:
                gc.setFill(Color.rgb(0, 100, 0, 0.7)); // Semi-transparent dark green
                break;
        }
        
        // Draw the area
        gc.fillRect(x, y, width, height);
        
        // Draw a border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        
        // Add a label
        gc.setFill(Color.WHITE);
        String label = type.toString();
        gc.fillText(label, x + width / 2 - 15, y + height / 2);
    }
    
    // Getters and setters
    public MapAreaType getType() {
        return type;
    }
    
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
}