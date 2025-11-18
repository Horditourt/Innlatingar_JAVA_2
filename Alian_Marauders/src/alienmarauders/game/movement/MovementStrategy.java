package alienmarauders.game.movement;

import java.util.ArrayList;

import alienmarauders.game.entities.Enemy;

public interface MovementStrategy {
    public void moveEnemy(Enemy enemy, double time);
    public void moveEnemies(ArrayList<Enemy> enemies, double time);
    public void setSpeedMultiplier(double speedMultiplier);
    public double getSpeedMultiplier();

}
