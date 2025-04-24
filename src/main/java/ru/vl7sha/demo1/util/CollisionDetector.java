package ru.vl7sha.demo1.util;


import ru.vl7sha.demo1.entity.Entity;


public class CollisionDetector {
    
    public boolean checkCollision(Entity entity1, Entity entity2) {
        // Simple circle-based collision detection
        double dx = entity1.getX() - entity2.getX();
        double dy = entity1.getY() - entity2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance < (entity1.getRadius() + entity2.getRadius());
    }
    
    public boolean isPointInCircle(double pointX, double pointY, double circleX, double circleY, double radius) {
        double dx = pointX - circleX;
        double dy = pointY - circleY;
        double distanceSquared = dx * dx + dy * dy;
        
        return distanceSquared <= radius * radius;
    }
    
    public boolean isPointInRectangle(double pointX, double pointY, double rectX, double rectY, double rectWidth, double rectHeight) {
        return pointX >= rectX && pointX <= rectX + rectWidth && 
               pointY >= rectY && pointY <= rectY + rectHeight;
    }
}