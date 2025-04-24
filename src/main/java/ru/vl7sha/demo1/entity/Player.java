package ru.vl7sha.demo1.entity;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import ru.vl7sha.demo1.entity.weapon.BasicWeapon;
import ru.vl7sha.demo1.entity.weapon.Weapon;


public class Player extends Entity {
    // Player stats
    private int maxHealth;
    private int currentHealth;
    private double moveSpeed;
    private int level;
    private int experience;
    private int experienceToNextLevel;
    
    // Weapons and abilities
    private List<Weapon> weapons;
    private double attackCooldown;
    
    // Persistent upgrades
    private int healthUpgrade;
    private int speedUpgrade;
    private int attackRangeUpgrade;
    
    // Status effects
    private boolean onIce;
    private boolean inLava;
    private boolean inBushes;
    private double iceSlideX;
    private double iceSlideY;
    private double lavaDamageTimer;
    
    public Player(double x, double y) {
        super(x, y, 30, 30); // Player size is 30x30
        this.color = Color.BLUE;
        
        // Initialize stats
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.moveSpeed = 200; // Pixels per second
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = 100; // 100 XP for level 2
        
        // Initialize weapons
        this.weapons = new ArrayList<>();
        this.weapons.add(new BasicWeapon(this));
        this.attackCooldown = 0;
        
        // Initialize upgrades
        this.healthUpgrade = 0;
        this.speedUpgrade = 0;
        this.attackRangeUpgrade = 0;
        
        // Initialize status effects
        this.onIce = false;
        this.inLava = false;
        this.inBushes = false;
        this.iceSlideX = 0;
        this.iceSlideY = 0;
        this.lavaDamageTimer = 0;
    }
    
    @Override
    public void update(double deltaTime) {
        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        // Update weapons
        for (Weapon weapon : weapons) {
            weapon.update(deltaTime);
        }
        
        // Handle status effects
        handleStatusEffects(deltaTime);
        
        // Apply ice sliding if on ice
        if (onIce && (iceSlideX != 0 || iceSlideY != 0)) {
            // Gradually reduce sliding
            double slideFriction = 0.95;
            iceSlideX *= Math.pow(slideFriction, deltaTime * 60);
            iceSlideY *= Math.pow(slideFriction, deltaTime * 60);
            
            // Apply slide movement
            x += iceSlideX * deltaTime;
            y += iceSlideY * deltaTime;
            
            // Stop sliding if very small
            if (Math.abs(iceSlideX) < 0.1 && Math.abs(iceSlideY) < 0.1) {
                iceSlideX = 0;
                iceSlideY = 0;
            }
        }
    }
    
    private void handleStatusEffects(double deltaTime) {
        // Handle lava damage
        if (inLava) {
            lavaDamageTimer += deltaTime;
            if (lavaDamageTimer >= 1.0) { // Damage every second
                takeDamage(5);
                lavaDamageTimer = 0;
            }
        } else {
            lavaDamageTimer = 0;
        }
        
        // Reset status effects (will be set again by map if still in effect area)
        onIce = false;
        inLava = false;
        inBushes = false;
    }
    
    public void move(double dx, double dy, double deltaTime) {
        double effectiveSpeed = moveSpeed;
        
        // Apply status effect modifiers
        if (inLava) {
            effectiveSpeed *= 0.5; // 50% speed in lava
        } else if (onIce) {
            effectiveSpeed *= 1.5; // 150% speed on ice
            
            // Store slide momentum
            iceSlideX = dx * effectiveSpeed;
            iceSlideY = dy * effectiveSpeed;
        }
        
        // Apply movement
        x += dx * effectiveSpeed * deltaTime;
        y += dy * effectiveSpeed * deltaTime;
    }
    
    public void attack(List<Enemy> enemies) {
        if (attackCooldown <= 0) {
            for (Weapon weapon : weapons) {
                weapon.use(enemies);
            }
            
            // Reset attack cooldown
            attackCooldown = getAttackSpeed();
        }
    }
    
    public void takeDamage(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
        }
    }
    
    public void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }
    
    public void addExperience(int amount) {
        experience += amount;
        
        // Check for level up
        while (experience >= experienceToNextLevel) {
            levelUp();
        }
    }
    
    private void levelUp() {
        level++;
        experience -= experienceToNextLevel;
        
        // Increase experience required for next level
        experienceToNextLevel = 100 + (level - 1) * 50;
        
        // Heal on level up
        heal(maxHealth / 4);
        
        System.out.println("Level up! Now level " + level);
        
        // This would trigger the upgrade selection UI
        // For now, just automatically upgrade a random stat
        int upgradeType = (int)(Math.random() * 3);
        switch (upgradeType) {
            case 0:
                maxHealth += 20;
                currentHealth += 20;
                System.out.println("Health increased!");
                break;
            case 1:
                moveSpeed += 10;
                System.out.println("Speed increased!");
                break;
            case 2:
                // Add a new weapon or upgrade existing one
                if (level % 3 == 0 && weapons.size() < 3) {
                    weapons.add(new BasicWeapon(this));
                    System.out.println("New weapon added!");
                } else {
                    for (Weapon weapon : weapons) {
                        weapon.upgrade();
                    }
                    System.out.println("Weapons upgraded!");
                }
                break;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw player
        gc.setFill(color);
        gc.fillOval(x - width / 2, y - height / 2, width, height);
        
        // Draw health bar
        double healthBarWidth = 40;
        double healthBarHeight = 5;
        double healthPercentage = (double) currentHealth / maxHealth;
        
        gc.setFill(Color.RED);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 10, healthBarWidth, healthBarHeight);
        
        gc.setFill(Color.GREEN);
        gc.fillRect(x - healthBarWidth / 2, y - height / 2 - 10, healthBarWidth * healthPercentage, healthBarHeight);
    }
    
    // Status effect setters
    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
    }
    
    public void setInLava(boolean inLava) {
        this.inLava = inLava;
    }
    
    public void setInBushes(boolean inBushes) {
        this.inBushes = inBushes;
    }
    
    // Getters and setters for stats
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public double getMoveSpeed() {
        return moveSpeed;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }
    
    public double getAttackSpeed() {
        double baseAttackSpeed = 0.5; // 2 attacks per second
        
        // Modify attack speed based on status effects
        if (inBushes) {
            baseAttackSpeed *= 1.5; // 50% slower in bushes
        }
        
        return baseAttackSpeed;
    }
    
    public double getAttackRange() {
        return 200 + (attackRangeUpgrade * 20);
    }
    
    // Persistent upgrade methods
    public void upgradeHealth() {
        healthUpgrade++;
        maxHealth += 10;
        currentHealth += 10;
    }
    
    public void upgradeSpeed() {
        speedUpgrade++;
        moveSpeed += 5;
    }
    
    public void upgradeAttackRange() {
        attackRangeUpgrade++;
    }
    
    // Getters for persistent upgrades
    public int getHealthUpgrade() {
        return healthUpgrade;
    }
    
    public int getSpeedUpgrade() {
        return speedUpgrade;
    }
    
    public int getAttackRangeUpgrade() {
        return attackRangeUpgrade;
    }
    
    // Method to set persistent upgrades (used when loading saved data)
    public void setPersistentUpgrades(int health, int speed, int attackRange) {
        this.healthUpgrade = health;
        this.speedUpgrade = speed;
        this.attackRangeUpgrade = attackRange;
        
        // Apply upgrades to stats
        this.maxHealth = 100 + (health * 10);
        this.currentHealth = this.maxHealth;
        this.moveSpeed = 200 + (speed * 5);
    }
}