package ru.vl7sha.demo1.util;



import java.io.*;
import java.util.Properties;

import ru.vl7sha.demo1.entity.Player;


public class SaveManager {
    private static final String SAVE_FILE = "player_save.properties";
    
    public void saveProgress(Player player) {
        Properties properties = new Properties();
        
        // Save player's persistent upgrades
        properties.setProperty("health_upgrade", String.valueOf(player.getHealthUpgrade()));
        properties.setProperty("speed_upgrade", String.valueOf(player.getSpeedUpgrade()));
        properties.setProperty("attack_range_upgrade", String.valueOf(player.getAttackRangeUpgrade()));
        
        // Save highest level reached
        properties.setProperty("highest_level", String.valueOf(player.getLevel()));
        
        try (OutputStream output = new FileOutputStream(SAVE_FILE)) {
            properties.store(output, "Player Save Data");
            System.out.println("Game progress saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving game progress: " + e.getMessage());
        }
    }
    
    public void loadProgress(Player player) {
        Properties properties = new Properties();
        
        try (InputStream input = new FileInputStream(SAVE_FILE)) {
            properties.load(input);
            
            // Load player's persistent upgrades
            int healthUpgrade = Integer.parseInt(properties.getProperty("health_upgrade", "0"));
            int speedUpgrade = Integer.parseInt(properties.getProperty("speed_upgrade", "0"));
            int attackRangeUpgrade = Integer.parseInt(properties.getProperty("attack_range_upgrade", "0"));
            
            // Apply the upgrades to the player
            player.setPersistentUpgrades(healthUpgrade, speedUpgrade, attackRangeUpgrade);
            
            System.out.println("Game progress loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("No save file found. Starting with default values.");
        } catch (IOException e) {
            System.err.println("Error loading game progress: " + e.getMessage());
        }
    }
    
    public boolean saveFileExists() {
        File saveFile = new File(SAVE_FILE);
        return saveFile.exists();
    }
}