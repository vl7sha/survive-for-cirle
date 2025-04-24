package ru.vl7sha.demo1.entity.weapon;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.vl7sha.demo1.entity.Enemy;
import ru.vl7sha.demo1.entity.Player;


public class BasicWeapon extends Weapon {
    private int projectileCount;
    
    public BasicWeapon(Player owner) {
        super(owner);
        this.damage = 10;
        this.cooldown = 1.0; // 1 second between attacks
        this.range = owner.getAttackRange();
        this.projectileCount = 1;
    }
    
    @Override
    public void use(List<Enemy> enemies) {
        if (currentCooldown <= 0) {
            // Find the nearest enemies within range
            List<Enemy> targetsInRange = findTargetsInRange(enemies);
            
            // Attack up to projectileCount enemies
            int targetsHit = 0;
            for (Enemy enemy : targetsInRange) {
                if (targetsHit >= projectileCount) break;
                
                // Deal damage to the enemy
                enemy.takeDamage(damage);
                targetsHit++;
                
                // In a real implementation, we would create visual effects here
                System.out.println("Basic weapon hit enemy for " + damage + " damage!");
            }
            
            // Reset cooldown
            currentCooldown = cooldown;
        }
    }
    
    private List<Enemy> findTargetsInRange(List<Enemy> allEnemies) {
        List<Enemy> inRange = new ArrayList<>();
        
        for (Enemy enemy : allEnemies) {
            double dx = enemy.getX() - owner.getX();
            double dy = enemy.getY() - owner.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= range && enemy.isAlive()) {
                inRange.add(enemy);
            }
        }
        
        // Sort by distance (nearest first)
        inRange.sort(Comparator.comparingDouble(e -> {
            double dx = e.getX() - owner.getX();
            double dy = e.getY() - owner.getY();
            return dx * dx + dy * dy;
        }));
        
        return inRange;
    }
    
    @Override
    protected void onUpgrade() {
        // Increase damage and range with each level
        damage += 5;
        range += 20;
        
        // Every 3 levels, increase projectile count
        if (level % 3 == 0) {
            projectileCount++;
        }
    }
}