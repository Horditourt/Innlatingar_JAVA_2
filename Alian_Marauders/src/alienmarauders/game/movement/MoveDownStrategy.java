package alienmarauders.game.movement;

import alienmarauders.game.entities.Enemy;

/**
 * Movement strategy that moves an enemy straight down the screen
 * at a constant speed.
 *
 * <p>The vertical speed is calculated as:
 * <pre>
 * s = enemy.getBaseSpeed() * speedMultiplier
 * dy = s * deltaTimeMillis
 * </pre>
 * where {@code deltaTimeMillis} is the time since the last update
 * in milliseconds.
 */
public class MoveDownStrategy implements MovementStrategy {

    /**
     * Multiplier applied to the enemy's base speed.
     */
    private double speedMultiplier = 1.0;

    /**
     * Moves the enemy straight down according to its base speed and
     * the current speed multiplier.
     *
     * @param enemy           the enemy to move
     * @param deltaTimeMillis elapsed time in milliseconds since the last update
     */
    @Override
    public void moveEnemy(Enemy enemy, double deltaTimeMillis) {
        // Base speed (px/ms) from the Enemy
        double baseSpeed = enemy.getBaseSpeed();
        double s = baseSpeed * speedMultiplier;    // scaled speed
        double dy = s * deltaTimeMillis;

        double newX = enemy.getPositionX();
        double newY = enemy.getPositionY() + dy;

        enemy.setPosition(newX, newY);
    }

    /**
     * Sets the speed multiplier used for this movement strategy.
     *
     * @param speedMultiplier the multiplier to apply to the base speed
     */
    @Override
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Returns the current speed multiplier.
     *
     * @return the speed multiplier
     */
    @Override
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
