package alienmarauders.game.movement;

import alienmarauders.game.entities.Enemy;

public class MoveDownStrategy implements MovementStrategy {

    private double speedMultiplier = 1.0;

    @Override
    public void moveEnemy(Enemy enemy, double deltaTimeMillis) {
        double baseSpeed = enemy.getBaseSpeed();   // px/ms
        double s = baseSpeed * speedMultiplier;    // scaled speed
        double dy = s * deltaTimeMillis;

        double newX = enemy.getPositionX();
        double newY = enemy.getPositionY() + dy;

        enemy.setPosition(newX, newY);
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
