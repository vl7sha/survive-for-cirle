package ru.vl7sha.demo1.entity.weapon;



import java.util.List;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Player;


public abstract class Weapon {
    protected Player owner;
    protected int level;
    protected int damage;
    protected double cooldown;
    protected double currentCooldown;
    protected double range;
    
    public Weapon(Player owner) {
        this.owner = owner;
        this.level = 1;
        this.currentCooldown = 0;
    }
    
    public void update(double deltaTime) {
        if (currentCooldown > 0) {
            currentCooldown -= deltaTime;
        }
    }
    
    public abstract void use(List<Enemy> enemies);
    
    public void upgrade() {
        level++;
        onUpgrade();
    }
    
    protected abstract void onUpgrade();
    
    // Getters and setters
    public int getLevel() {
        return level;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public double getCooldown() {
        return cooldown;
    }
    
    public double getRange() {
        return range;
    }
}