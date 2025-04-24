package ru.vl7sha.demo1.map;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Entity;
import ru.vl7sha.demo1.entity.Player;


public class GameMap {
    private int width;
    private int height;
    private List<MapArea> areas;
    private Random random;
    private double dynamicChangeTimer;
    private double dynamicChangeInterval;
    
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.areas = new ArrayList<>();
        this.random = new Random();
        this.dynamicChangeTimer = 0;
        this.dynamicChangeInterval = 30.0; // Change map every 30 seconds
        
        // Initialize map with some areas
        initializeMap();
    }
    
    private void initializeMap() {
        // Create some lava areas
        createArea(MapAreaType.LAVA, 100, 100, 150, 150);
        createArea(MapAreaType.LAVA, width - 250, height - 250, 150, 150);
        
        // Create some ice areas
        createArea(MapAreaType.ICE, width / 2 - 75, height / 2 - 75, 150, 150);
        createArea(MapAreaType.ICE, 100, height - 250, 150, 150);
        
        // Create some bush areas
        createArea(MapAreaType.BUSHES, width - 250, 100, 150, 150);
        createArea(MapAreaType.BUSHES, width / 2 - 200, height / 2 + 100, 150, 150);
    }
    
    private void createArea(MapAreaType type, double x, double y, double width, double height) {
        MapArea area = new MapArea(type, x, y, width, height);
        areas.add(area);
    }
    
    public void update(double deltaTime) {
        // Update dynamic change timer
        dynamicChangeTimer += deltaTime;
        
        // Check if it's time to change the map
        if (dynamicChangeTimer >= dynamicChangeInterval) {
            dynamicChangeTimer = 0;
            changeMapDynamically();
        }
    }
    
    private void changeMapDynamically() {
        // Randomly modify the map
        int changeType = random.nextInt(3);
        
        switch (changeType) {
            case 0:
                // Add a new area
                addRandomArea();
                break;
            case 1:
                // Remove a random area
                if (!areas.isEmpty()) {
                    areas.remove(random.nextInt(areas.size()));
                }
                break;
            case 2:
                // Move a random area
                if (!areas.isEmpty()) {
                    MapArea area = areas.get(random.nextInt(areas.size()));
                    area.setX(random.nextInt(width - 100));
                    area.setY(random.nextInt(height - 100));
                }
                break;
        }
        
        System.out.println("Map changed dynamically!");
    }
    
    private void addRandomArea() {
        MapAreaType type = MapAreaType.values()[random.nextInt(MapAreaType.values().length)];
        double x = random.nextInt(width - 150);
        double y = random.nextInt(height - 150);
        double areaWidth = 100 + random.nextInt(100);
        double areaHeight = 100 + random.nextInt(100);
        
        createArea(type, x, y, areaWidth, areaHeight);
    }
    
    public void render(GraphicsContext gc) {
        // Draw the base map
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, width, height);
        
        // Draw all areas
        for (MapArea area : areas) {
            area.render(gc);
        }
    }
    
    public void applyEffects(Entity entity) {
        // Check if the entity is in any special area
        for (MapArea area : areas) {
            if (area.contains(entity.getX(), entity.getY())) {
                // Apply the area effect to the entity
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    switch (area.getType()) {
                        case LAVA:
                            player.setInLava(true);
                            break;
                        case ICE:
                            player.setOnIce(true);
                            break;
                        case BUSHES:
                            player.setInBushes(true);
                            break;
                    }
                } else if (entity instanceof Enemy) {
                    Enemy enemy = (Enemy) entity;
                    switch (area.getType()) {
                        case LAVA:
                            enemy.setInLava(true);
                            break;
                        case ICE:
                            enemy.setOnIce(true);
                            break;
                        case BUSHES:
                            enemy.setInBushes(true);
                            break;
                    }
                }
            }
        }
    }
}