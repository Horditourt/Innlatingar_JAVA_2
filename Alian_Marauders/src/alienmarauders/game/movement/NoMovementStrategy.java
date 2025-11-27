package alienmarauders.game.movement;

import alienmarauders.game.entities.Enemy;

public class NoMovementStrategy implements MovementStrategy {

    private double speedMultiplier = 1.0;

    @Override
    public void moveEnemy(Enemy enemy, double deltaTimeMillis) {
        // do nothing
    }

    @Override
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
