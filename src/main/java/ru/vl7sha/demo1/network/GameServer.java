package ru.vl7sha.demo1.network;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class GameServer {

    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private boolean running;
    private int currentWave;
    private long gameStartTime;
    private List<NetworkEntity> enemies;
    private List<NetworkEntity> experienceOrbs;


    public GameServer() {
        this.clients = new CopyOnWriteArrayList<>();
        this.running = false;
        this.currentWave = 1;
        this.enemies = new CopyOnWriteArrayList<>();
        this.experienceOrbs = new CopyOnWriteArrayList<>();
    }


    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            gameStartTime = System.currentTimeMillis();

            System.out.println("Game server started on port " + PORT);

            // Start a thread to accept client connections
            new Thread(this::acceptClients).start();

            // Start the game loop
            new Thread(this::gameLoop).start();

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }


    private void acceptClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create a new client handler
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);

                // Start the client handler thread
                new Thread(clientHandler).start();

            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }


    private void gameLoop() {
        long lastUpdateTime = System.nanoTime();

        while (running) {
            // Calculate delta time
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = currentTime;

            // Update game state
            updateGameState(deltaTime);

            // Send game state to all clients
            broadcastGameState();

            // Sleep to limit update rate
            try {
                Thread.sleep(16); // ~60 updates per second
            } catch (InterruptedException e) {
                Thread.currentThread()
                        .interrupt();
            }
        }
    }


    private void updateGameState(double deltaTime) {
        // Check if it's time for a new wave
        long currentTime = System.currentTimeMillis();
        if (currentTime - gameStartTime >= currentWave * 30000) { // 30 seconds per wave
            currentWave++;
            System.out.println("Starting wave " + currentWave);
        }

        // Update enemies
        // In a real implementation, this would update enemy positions, etc.
    }


    private void broadcastGameState() {
        GameStatePacket gameState = createGameStatePacket();

        for (ClientHandler client : clients) {
            client.sendGameState(gameState);
        }
    }


    private GameStatePacket createGameStatePacket() {
        GameStatePacket packet = new GameStatePacket();
        packet.setCurrentWave(currentWave);

        // Add player data
        List<NetworkEntity> players = new ArrayList<>();
        for (ClientHandler client : clients) {
            if (client.getPlayer() != null) {
                players.add(client.getPlayer());
            }
        }
        packet.setPlayers(players);

        // Add enemy data
        packet.setEnemies(enemies);

        // Add experience orb data
        packet.setExperienceOrbs(experienceOrbs);

        return packet;
    }


    public void handlePlayerUpdate(ClientHandler client, NetworkEntity playerData) {
        // Update the client's player data
        client.setPlayer(playerData);

        // Broadcast the updated player data to all clients
        broadcastGameState();
    }


    public void handleEnemyKilled(NetworkEntity enemy, NetworkEntity killer) {
        // Remove the enemy
        enemies.remove(enemy);

        // Create an experience orb
        NetworkEntity expOrb = new NetworkEntity();
        expOrb.setId("exp_" + System.currentTimeMillis());
        expOrb.setType("experience_orb");
        expOrb.setX(enemy.getX());
        expOrb.setY(enemy.getY());
        expOrb.setValue(enemy.getValue());

        experienceOrbs.add(expOrb);

        // Broadcast the updated game state
        broadcastGameState();
    }


    public void handleExperienceCollected(String orbId, String playerId) {
        // Find the orb
        NetworkEntity orbToRemove = null;
        for (NetworkEntity orb : experienceOrbs) {
            if (orb.getId()
                    .equals(orbId)) {
                orbToRemove = orb;
                break;
            }
        }

        if (orbToRemove != null) {
            experienceOrbs.remove(orbToRemove);

            // Find the player and update their experience
            for (ClientHandler client : clients) {
                NetworkEntity player = client.getPlayer();
                if (player != null && player.getId()
                        .equals(playerId)) {
                    player.setExperience(player.getExperience() + orbToRemove.getValue());
                    break;
                }
            }

            // Broadcast the updated game state
            broadcastGameState();
        }
    }


    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected. Remaining clients: " + clients.size());
    }


    public void stop() {
        running = false;

        // Close all client connections
        for (ClientHandler client : clients) {
            client.close();
        }

        // Close the server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }


    // Inner class to handle client connections
    private class ClientHandler implements Runnable {

        private Socket socket;
        private GameServer server;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private boolean running;
        private NetworkEntity player;


        public ClientHandler(Socket socket, GameServer server) {
            this.socket = socket;
            this.server = server;
            this.running = true;

            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.err.println("Error creating streams: " + e.getMessage());
                running = false;
            }
        }


        @Override
        public void run() {
            while (running) {
                try {
                    // Read packet from client
                    Object packet = input.readObject();

                    // Handle different packet types
                    if (packet instanceof PlayerUpdatePacket) {
                        PlayerUpdatePacket playerPacket = (PlayerUpdatePacket) packet;
                        server.handlePlayerUpdate(this, playerPacket.getPlayer());
                    } else if (packet instanceof EnemyKilledPacket) {
                        EnemyKilledPacket enemyPacket = (EnemyKilledPacket) packet;
                        server.handleEnemyKilled(enemyPacket.getEnemy(), enemyPacket.getKiller());
                    } else if (packet instanceof ExperienceCollectedPacket) {
                        ExperienceCollectedPacket expPacket = (ExperienceCollectedPacket) packet;
                        server.handleExperienceCollected(expPacket.getOrbId(), expPacket.getPlayerId());
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error reading from client: " + e.getMessage());
                    running = false;
                }
            }

            // Clean up
            close();
            server.removeClient(this);
        }


        public void sendGameState(GameStatePacket gameState) {
            try {
                output.writeObject(gameState);
                output.flush();
            } catch (IOException e) {
                System.err.println("Error sending game state: " + e.getMessage());
                running = false;
            }
        }


        public void close() {
            running = false;

            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client handler: " + e.getMessage());
            }
        }


        public NetworkEntity getPlayer() {
            return player;
        }


        public void setPlayer(NetworkEntity player) {
            this.player = player;
        }

    }

}