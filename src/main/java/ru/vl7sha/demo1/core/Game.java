package ru.vl7sha.demo1.core;


import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Entity;
import ru.vl7sha.demo1.entity.ExperienceOrb;
import ru.vl7sha.demo1.entity.Player;
import ru.vl7sha.demo1.map.GameMap;
import ru.vl7sha.demo1.util.CollisionDetector;
import ru.vl7sha.demo1.util.GameState;
import ru.vl7sha.demo1.util.SaveManager;


public class Game {
    // Constants
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    private static final long WAVE_DURATION_MS = 30000; // 30 seconds per wave
    
    // Game components
    private Stage stage;
    private Canvas canvas;
    private GraphicsContext gc;
    private GameMap gameMap;
    private WaveManager waveManager;
    private CollisionDetector collisionDetector;
    private SaveManager saveManager;
    
    // Game state
    private GameState gameState;
    private boolean isMultiplayer;
    private boolean isPaused;
    private long lastUpdateTime;
    private int currentWave = 1;
    private long waveStartTime;
    
    // Entity lists
    private List<Player> players;
    private List<Enemy> enemies;
    private List<ExperienceOrb> experienceOrbs;
    private List<Entity> entities;
    
    // Input handling
    private Set<KeyCode> pressedKeys;
    
    public Game(Stage stage) {
        this.stage = stage;
        this.isMultiplayer = false; // Default to single player
        this.isPaused = false;
        this.gameState = GameState.RUNNING;
        
        // Initialize entity lists
        this.players = new ArrayList<>();
        this.enemies = new CopyOnWriteArrayList<>();
        this.experienceOrbs = new CopyOnWriteArrayList<>();
        this.entities = new CopyOnWriteArrayList<>();
        
        // Initialize input handling
        this.pressedKeys = new HashSet<>();
        
        // Initialize game components
        this.collisionDetector = new CollisionDetector();
        this.saveManager = new SaveManager();
        
        // Set up the game window
        setupGameWindow();
        
        // Initialize game map
        this.gameMap = new GameMap(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Initialize wave manager
        this.waveManager = new WaveManager(this);
        
        // Create the player
        Player player = new Player(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        players.add(player);
        entities.add(player);
        
        // Set wave start time
        this.waveStartTime = System.currentTimeMillis();
    }
    
    private void setupGameWindow() {
        // Create the canvas
        canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // Create the scene
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Set up input handling
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
        
        // Configure the stage
        stage.setTitle("Vampire Survivors Clone");
        stage.setScene(scene);
        stage.setResizable(false);
    }
    
    public void start() {
        // Show the game window
        stage.show();
        
        // Start the game loop
        startGameLoop();
    }
    
    private void startGameLoop() {
        lastUpdateTime = System.nanoTime();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                // Calculate delta time
                double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = currentTime;
                
                if (!isPaused) {
                    // Update game state
                    update(deltaTime);
                    
                    // Render the game
                    render();
                }
            }
        }.start();
    }
    
    private void update(double deltaTime) {
        // Check if it's time for a new wave
        long currentTime = System.currentTimeMillis();
        if (currentTime - waveStartTime >= WAVE_DURATION_MS) {
            currentWave++;
            waveStartTime = currentTime;
            waveManager.startWave(currentWave);
        }
        
        // Update the map
        gameMap.update(deltaTime);
        
        // Update all entities
        for (Entity entity : entities) {
            entity.update(deltaTime);
            
            // Apply map effects to entities
            gameMap.applyEffects(entity);
        }
        
        // Handle player input
        handleInput(deltaTime);
        
        // Check collisions
        handleCollisions();
        
        // Remove dead entities
        removeDeadEntities();
        
        // Spawn enemies if needed
        waveManager.update(deltaTime);
    }
    
    private void handleInput(double deltaTime) {
        for (Player player : players) {
            // Movement
            double dx = 0, dy = 0;
            if (pressedKeys.contains(KeyCode.W)) dy -= 1;
            if (pressedKeys.contains(KeyCode.S)) dy += 1;
            if (pressedKeys.contains(KeyCode.A)) dx -= 1;
            if (pressedKeys.contains(KeyCode.D)) dx += 1;
            
            // Normalize diagonal movement
            if (dx != 0 && dy != 0) {
                double length = Math.sqrt(dx * dx + dy * dy);
                dx /= length;
                dy /= length;
            }
            
            player.move(dx, dy, deltaTime);
        }
    }
    
    private void handleCollisions() {
        // Check player-enemy collisions
        for (Player player : players) {
            for (Enemy enemy : enemies) {
                if (collisionDetector.checkCollision(player, enemy)) {
                    player.takeDamage(enemy.getDamage());
                }
            }
        }
        
        // Check player-experience collisions
        for (Player player : players) {
            for (ExperienceOrb orb : experienceOrbs) {
                if (collisionDetector.checkCollision(player, orb)) {
                    player.addExperience(orb.getValue());
                    experienceOrbs.remove(orb);
                    entities.remove(orb);
                }
            }
        }
        
        // Check player-player collisions (for multiplayer)
        if (players.size() > 1) {
            Player p1 = players.get(0);
            Player p2 = players.get(1);
            if (collisionDetector.checkCollision(p1, p2)) {
                // Handle player collision (e.g., push apart)
                resolvePlayerCollision(p1, p2);
            }
        }
    }
    
    private void resolvePlayerCollision(Player p1, Player p2) {
        // Simple collision resolution - push players apart
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double overlap = (p1.getRadius() + p2.getRadius()) - distance;
            double pushX = (dx / distance) * overlap * 0.5;
            double pushY = (dy / distance) * overlap * 0.5;
            
            p1.setX(p1.getX() - pushX);
            p1.setY(p1.getY() - pushY);
            p2.setX(p2.getX() + pushX);
            p2.setY(p2.getY() + pushY);
        }
    }
    
    private void removeDeadEntities() {
        // Remove dead enemies
        for (Enemy enemy : new ArrayList<>(enemies)) {
            if (!enemy.isAlive()) {
                // Spawn experience orb
                spawnExperienceOrb(enemy.getX(), enemy.getY(), enemy.getExperienceValue());
                
                // Remove the enemy
                enemies.remove(enemy);
                entities.remove(enemy);
            }
        }
        
        // Check if all players are dead
        boolean allPlayersDead = true;
        for (Player player : players) {
            if (player.isAlive()) {
                allPlayersDead = false;
                break;
            }
        }
        
        if (allPlayersDead) {
            gameState = GameState.GAME_OVER;
            // Handle game over
            handleGameOver();
        }
    }
    
    private void spawnExperienceOrb(double x, double y, int value) {
        ExperienceOrb orb = new ExperienceOrb(x, y, value);
        experienceOrbs.add(orb);
        entities.add(orb);
    }
    
    private void handleGameOver() {
        // Save progress
        saveManager.saveProgress(players.get(0));
        
        // Show game over screen
        // This would be implemented in a UI class
        System.out.println("Game Over! You reached wave " + currentWave);
    }
    
    private void render() {
        // Clear the canvas
        gc.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Render the map
        gameMap.render(gc);
        
        // Render all entities
        for (Entity entity : entities) {
            entity.render(gc);
        }
        
        // Render UI elements
        renderUI();
    }
    
    private void renderUI() {
        // Render player health, experience, wave info, etc.
        // This would be implemented in a UI class
    }
    
    // Methods for adding entities
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        entities.add(enemy);
    }
    
    public void addPlayer(Player player) {
        players.add(player);
        entities.add(player);
    }
    
    // Getters
    public List<Player> getPlayers() {
        return players;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getWindowWidth() {
        return WINDOW_WIDTH;
    }
    
    public int getWindowHeight() {
        return WINDOW_HEIGHT;
    }
}