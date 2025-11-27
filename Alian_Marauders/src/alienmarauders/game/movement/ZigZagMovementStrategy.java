package alienmarauders.game.movement;

import alienmarauders.game.entities.Enemy;

public class ZigZagMovementStrategy implements MovementStrategy {

    private double speedMultiplier = 1.0;

    // internal total elapsed time in ms
    private double elapsedMillis = 0;

    // constants from assignment
    private static final double A = 75;      // amplitude
    private static final double OMEGA = 0.0001; // frequency
    // base speed s is taken from Enemy, then scaled

    @Override
    public void moveEnemy(Enemy enemy, double deltaTimeMillis) {
        elapsedMillis += deltaTimeMillis;

        double T = elapsedMillis;                // total elapsed time
        double t = deltaTimeMillis;              // delta time
        double baseSpeed = enemy.getBaseSpeed(); // from enemy
        double s = baseSpeed * speedMultiplier;  // px/ms

        double x0 = enemy.getBaseX();  // initial/spawn x

        double newX = x0 + A * Math.sin(2 * Math.PI * OMEGA * T);
        double newY = enemy.getPositionY() + s * t;

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
