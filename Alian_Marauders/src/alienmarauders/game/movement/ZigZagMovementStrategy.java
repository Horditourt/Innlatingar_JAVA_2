package alienmarauders.game.movement;

import alienmarauders.game.entities.Enemy;

/**
 * Movement strategy that moves enemies downward while oscillating horizontally
 * using a sine wave.
 *
 * <p>The motion follows the assignment formula:
 * <pre>
 * xi = x0 + a * sin(2π * ω * T)
 * yi = yi-1 + s * t
 *
 * a = 75
 * ω = 0.0001
 * s = baseSpeed * speedMultiplier
 * T = total elapsed time in ms since spawn (for this strategy)
 * t = deltaTimeMillis
 * x0 = enemy.getBaseX()
 * </pre>
 *
 * <p>In practice, {@code T} is tracked inside the strategy as {@code elapsedMillis}.
 * As long as the enemies in a wave are created at the same time with this
 * strategy, the behaviour matches "time since spawn" for the wave.
 */
public class ZigZagMovementStrategy implements MovementStrategy {

    /**
     * Horizontal oscillation amplitude in pixels.
     */
    private static final double A = 75.0;

    /**
     * Angular frequency in 1/ms.
     */
    private static final double OMEGA = 0.0001;

    /**
     * Multiplier applied to the enemy's base speed.
     */
    private double speedMultiplier = 1.0;

    /**
     * Internal time accumulator in milliseconds.
     * Represents {@code T} in the formula.
     */
    private double elapsedMillis = 0.0;

    /**
     * Moves the enemy in a zig-zag pattern according to the sine formula.
     *
     * @param enemy           the enemy to move
     * @param deltaTimeMillis elapsed time in milliseconds since the last update
     */
    @Override
    public void moveEnemy(Enemy enemy, double deltaTimeMillis) {
        // Update total elapsed time T for this strategy instance.
        elapsedMillis += deltaTimeMillis;

        // x0 – "frozen" spawn X-position from the enemy.
        double x0 = enemy.getBaseX();

        // Horizontal oscillation: xi = x0 + a * sin(2π * ω * T)
        double T = elapsedMillis;
        double phase = 2.0 * Math.PI * OMEGA * T;
        double newX = x0 + A * Math.sin(phase);

        // Vertical movement: yi = yi-1 + s * t
        double baseSpeed = enemy.getBaseSpeed();   // px/ms
        double s = baseSpeed * speedMultiplier;    // scaled speed
        double t = deltaTimeMillis;
        double dy = s * t;
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
