package ru.vl7sha.demo1.core;



import java.util.Random;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.enemy.BasicEnemy;
import ru.vl7sha.demo1.entity.enemy.FastEnemy;
import ru.vl7sha.demo1.entity.enemy.LootGoblin;
import ru.vl7sha.demo1.entity.enemy.RangedEnemy;
import ru.vl7sha.demo1.entity.enemy.TankEnemy;


public class WaveManager {
    private Game game;
    private Random random;
    private double spawnTimer;
    private double spawnInterval;
    private int enemiesPerWave;
    private int enemiesSpawned;
    private boolean waveActive;
    private int currentWave;
    
    // Spawn chance for special enemies
    private static final double LOOT_GOBLIN_CHANCE = 0.05; // 5% chance
    
    public WaveManager(Game game) {
        this.game = game;
        this.random = new Random();
        this.spawnTimer = 0;
        this.spawnInterval = 1.0; // 1 second between spawns initially
        this.enemiesPerWave = 10; // Start with 10 enemies in wave 1
        this.enemiesSpawned = 0;
        this.waveActive = false;
        this.currentWave = 0;
    }
    
    public void startWave(int waveNumber) {
        this.currentWave = waveNumber;
        this.waveActive = true;
        this.enemiesSpawned = 0;
        
        // Calculate enemies for this wave (increases with wave number)
        this.enemiesPerWave = 10 + (waveNumber - 1) * 5;
        
        // Decrease spawn interval as waves progress (faster spawning)
        this.spawnInterval = Math.max(0.2, 1.0 - (waveNumber - 1) * 0.05);
        
        System.out.println("Wave " + waveNumber + " started! Enemies: " + enemiesPerWave);
    }
    
    public void update(double deltaTime) {
        if (!waveActive) {
            return;
        }
        
        // Update spawn timer
        spawnTimer += deltaTime;
        
        // Check if it's time to spawn an enemy
        if (spawnTimer >= spawnInterval && enemiesSpawned < enemiesPerWave) {
            spawnEnemy();
            spawnTimer = 0;
            enemiesSpawned++;
            
            // Check if wave is complete
            if (enemiesSpawned >= enemiesPerWave) {
                waveActive = false;
            }
        }
    }
    
    private void spawnEnemy() {
        // Determine spawn position (outside the visible area)
        int screenWidth = game.getWindowWidth();
        int screenHeight = game.getWindowHeight();
        
        double x, y;
        int side = random.nextInt(4); // 0: top, 1: right, 2: bottom, 3: left
        
        switch (side) {
            case 0: // Top
                x = random.nextInt(screenWidth);
                y = -50;
                break;
            case 1: // Right
                x = screenWidth + 50;
                y = random.nextInt(screenHeight);
                break;
            case 2: // Bottom
                x = random.nextInt(screenWidth);
                y = screenHeight + 50;
                break;
            case 3: // Left
                x = -50;
                y = random.nextInt(screenHeight);
                break;
            default:
                x = -50;
                y = -50;
        }
        
        // Check for special enemy spawn (loot goblin)
        if (random.nextDouble() < LOOT_GOBLIN_CHANCE) {
            spawnLootGoblin(x, y);
            return;
        }
        
        // Determine enemy type based on wave number and randomness
        Enemy enemy;
        double enemyTypeRoll = random.nextDouble();
        
        if (currentWave >= 3 && enemyTypeRoll < 0.1) {
            // 10% chance for tank enemy after wave 3
            enemy = new TankEnemy(x, y, currentWave);
        } else if (currentWave >= 2 && enemyTypeRoll < 0.3) {
            // 20% chance for ranged enemy after wave 2
            enemy = new RangedEnemy(x, y, currentWave);
        } else if (enemyTypeRoll < 0.5) {
            // 20% chance for fast enemy
            enemy = new FastEnemy(x, y, currentWave);
        } else {
            // 50% chance for basic enemy
            enemy = new BasicEnemy(x, y, currentWave);
        }
        
        // Add the enemy to the game
        game.addEnemy(enemy);
    }
    
    private void spawnLootGoblin(double x, double y) {
        LootGoblin lootGoblin = new LootGoblin(x, y, currentWave);
        game.addEnemy(lootGoblin);
        System.out.println("Loot Goblin spawned!");
    }
    
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getEnemiesRemaining() {
        return enemiesPerWave - enemiesSpawned;
    }
}