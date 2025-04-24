package ru.vl7sha.demo1.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.ExperienceOrb;
import ru.vl7sha.demo1.entity.Player;


public class GameClient {
    private static final int PORT = 8888;
    
    private String serverIp;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connected;
    private String playerId;
    
    // Callbacks for game events
    private Consumer<GameStatePacket> onGameStateReceived;
    private Runnable onConnectionLost;
    
    public GameClient(String serverIp) {
        this.serverIp = serverIp;
        this.connected = false;
        this.playerId = UUID.randomUUID().toString();
    }
    
    public void connect() throws IOException {
        try {
            socket = new Socket(serverIp, PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
            System.out.println("Connected to server: " + serverIp);
            
            // Start a thread to receive game state updates
            new Thread(this::receiveUpdates).start();
            
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            throw e;
        }
    }
    
    private void receiveUpdates() {
        while (connected) {
            try {
                Object packet = input.readObject();
                
                if (packet instanceof GameStatePacket) {
                    GameStatePacket gameState = (GameStatePacket) packet;
                    
                    // Notify the game about the new state
                    if (onGameStateReceived != null) {
                        onGameStateReceived.accept(gameState);
                    }
                }
                
            } catch (SocketException e) {
                // Connection lost
                connected = false;
                if (onConnectionLost != null) {
                    onConnectionLost.run();
                }
                System.err.println("Connection to server lost: " + e.getMessage());
                break;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error receiving update: " + e.getMessage());
            }
        }
    }
    
    public void sendPlayerUpdate(Player player) {
        if (!connected) return;
        
        try {
            // Convert Player to NetworkEntity
            NetworkEntity playerEntity = new NetworkEntity();
            playerEntity.setId(playerId);
            playerEntity.setType("player");
            playerEntity.setX(player.getX());
            playerEntity.setY(player.getY());
            playerEntity.setHealth(player.getCurrentHealth());
            playerEntity.setMaxHealth(player.getMaxHealth());
            playerEntity.setLevel(player.getLevel());
            playerEntity.setExperience(player.getExperience());
            playerEntity.setExperienceToNextLevel(player.getExperienceToNextLevel());
            playerEntity.setMoveSpeed(player.getMoveSpeed());
            
            // Create and send packet
            PlayerUpdatePacket packet = new PlayerUpdatePacket(playerEntity);
            output.writeObject(packet);
            output.flush();
            
        } catch (IOException e) {
            System.err.println("Error sending player update: " + e.getMessage());
            connected = false;
        }
    }
    
    public void sendEnemyKilled(Enemy enemy, Player killer) {
        if (!connected) return;
        
        try {
            // Convert Enemy to NetworkEntity
            NetworkEntity enemyEntity = new NetworkEntity();
            enemyEntity.setId("enemy_" + System.identityHashCode(enemy));
            enemyEntity.setType("enemy");
            enemyEntity.setX(enemy.getX());
            enemyEntity.setY(enemy.getY());
            enemyEntity.setValue(enemy.getExperienceValue());
            
            // Convert Player to NetworkEntity
            NetworkEntity killerEntity = new NetworkEntity();
            killerEntity.setId(playerId);
            killerEntity.setType("player");
            
            // Create and send packet
            EnemyKilledPacket packet = new EnemyKilledPacket(enemyEntity, killerEntity);
            output.writeObject(packet);
            output.flush();
            
        } catch (IOException e) {
            System.err.println("Error sending enemy killed: " + e.getMessage());
            connected = false;
        }
    }
    
    public void sendExperienceCollected(ExperienceOrb orb) {
        if (!connected) return;
        
        try {
            // Create and send packet
            ExperienceCollectedPacket packet = new ExperienceCollectedPacket(
                "exp_" + System.identityHashCode(orb),
                playerId
            );
            output.writeObject(packet);
            output.flush();
            
        } catch (IOException e) {
            System.err.println("Error sending experience collected: " + e.getMessage());
            connected = false;
        }
    }
    
    public void disconnect() {
        connected = false;
        
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    // Setters for callbacks
    public void setOnGameStateReceived(Consumer<GameStatePacket> callback) {
        this.onGameStateReceived = callback;
    }
    
    public void setOnConnectionLost(Runnable callback) {
        this.onConnectionLost = callback;
    }
    
    // Method to update the game state based on network data
    public void updateGameState(GameStatePacket gameState, List<Player> players, List<Enemy> enemies, List<ExperienceOrb> experienceOrbs) {
        // Clear existing entities (except local player)
        Player localPlayer = players.get(0);
        players.clear();
        players.add(localPlayer);
        
        enemies.clear();
        experienceOrbs.clear();
        
        // Add network players
        for (NetworkEntity playerEntity : gameState.getPlayers()) {
            if (!playerEntity.getId().equals(playerId)) {
                // Create a new player for the remote player
                Player remotePlayer = new Player(playerEntity.getX(), playerEntity.getY());
                // Update player properties from network data
                // ...
                players.add(remotePlayer);
            }
        }
        
        // Add network enemies
        for (NetworkEntity enemyEntity : gameState.getEnemies()) {
            // Create appropriate enemy type based on network data
            // ...
        }
        
        // Add network experience orbs
        for (NetworkEntity orbEntity : gameState.getExperienceOrbs()) {
            ExperienceOrb orb = new ExperienceOrb(orbEntity.getX(), orbEntity.getY(), orbEntity.getValue());
            experienceOrbs.add(orb);
        }
    }
}