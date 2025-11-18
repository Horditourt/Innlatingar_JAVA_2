package alienmarauders.game;

import alienmarauders.game.entities.Entity;

public class CollisionDetection {
    /* 
     * @param a an entity
     * @param b another entity
     * @return true if the two entities are colliding using aabb colision detection, false otherwise
     */
    public static boolean Aabb(Entity a, Entity b) {
        return a.getPositionX() + a.getWidth() > b.getPositionX() &&
               b.getPositionX() + b.getWidth() > a.getPositionX() &&
               a.getPositionY() + a.getHeight() > b.getPositionY() &&
               b.getPositionY() + b.getHeight() > a.getPositionY();

    }
    
}
