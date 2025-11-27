package alienmarauders.game.movement;

import java.util.ArrayList;

import alienmarauders.game.entities.Enemy;

public interface MovementStrategy {
    void moveEnemy(Enemy enemy, double deltaTimeMillis);
    default void moveEnemies(ArrayList<Enemy> enemies, double deltaTimeMillis) {
        for (Enemy e : enemies) {
            moveEnemy(e, deltaTimeMillis);
        }
    }
    void setSpeedMultiplier(double speedMultiplier);
    double getSpeedMultiplier();
}
