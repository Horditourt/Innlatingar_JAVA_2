package alienmarauders.game.formation;

import alienmarauders.game.entities.Enemy;
import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creates enemies arranged along a circular arc.
 * <p>
 * The enemies are placed along an arc above the player,
 * centered horizontally in the play area.
 */
public class ArcFormation implements Formation {

    private final double playWidth;
    private final double playHeight;
    private final Image[] enemySheets;
    private final int[] enemyFrameCounts;
    private final MovementStrategy movement;
    private final double speedMultiplier;
    private final int enemyCount;
    private final Random rng;

    private final ArrayList<Enemy> enemies = new ArrayList<>();

    /**
     * Constructs a new arc-shaped enemy formation.
     *
     * @param playWidth        width of the play area in pixels
     * @param playHeight       height of the play area in pixels
     * @param enemySheets      array of enemy sprite sheets to choose from
     * @param enemyFrameCounts array of frame counts matching {@code enemySheets}
     * @param movement         movement strategy used by all enemies in this formation
     * @param speedMultiplier  speed multiplier for the enemies in this formation
     * @param enemyCount       number of enemies to place along the arc
     * @param rng              random number generator used to pick enemy sprites
     */
    public ArcFormation(double playWidth, double playHeight,
                        Image[] enemySheets,
                        int[] enemyFrameCounts,
                        MovementStrategy movement,
                        double speedMultiplier,
                        int enemyCount,
                        Random rng) {

        this.playWidth = playWidth;
        this.playHeight = playHeight;
        this.enemySheets = enemySheets;
        this.enemyFrameCounts = enemyFrameCounts;
        this.movement = movement;
        this.speedMultiplier = speedMultiplier;
        this.enemyCount = enemyCount;
        this.rng = rng;
    }

    /**
     * Creates the enemies along a circular arc and stores them internally.
     * Call {@link #getEnemies()} afterwards to retrieve the list.
     */
    @Override
    public void createEnemies() {
        enemies.clear();

        // Use the first sheet just to derive base width/height
        int framesInFirstSheet = enemyFrameCounts[0];
        double frameWidth = enemySheets[0].getWidth() / framesInFirstSheet;
        double frameHeight = enemySheets[0].getHeight();

        double enemyWidth = frameWidth;
        double enemyHeight = frameHeight;

        // Arc geometry
        double centerX = playWidth / 2.0;
        double centerY = 120;   // vertical center of the arc
        double radius = 150;    // radius of the arc

        // Place enemies from -60° to +60° along the arc
        double startDeg = -60.0;
        double endDeg = 60.0;

        double startRad = Math.toRadians(startDeg);
        double endRad = Math.toRadians(endDeg);
        double step = (enemyCount > 1)
                ? (endRad - startRad) / (enemyCount - 1)
                : 0.0;

        for (int i = 0; i < enemyCount; i++) {
            double angle = startRad + i * step;

            double ex = centerX + Math.cos(angle) * radius - enemyWidth / 2.0;
            double ey = centerY + Math.sin(angle) * radius - enemyHeight / 2.0;

            addEnemy(ex, ey, enemyWidth, enemyHeight);
        }
    }

    /**
     * Helper that creates a single enemy at (x, y) with a random sprite sheet.
     * It also picks the correct frame count for that sprite sheet.
     *
     * @param x      X coordinate for the enemy
     * @param y      Y coordinate for the enemy
     * @param width  drawing width in pixels
     * @param height drawing height in pixels
     */
    private void addEnemy(double x, double y, double width, double height) {
        int index = rng.nextInt(enemySheets.length);
        Image sprite = enemySheets[index];
        int framesInSheet = enemyFrameCounts[index];

        Enemy e = new Enemy(x, y, width, height, sprite, framesInSheet);
        e.setMovementStrategy(movement);
        e.setSpeedMultiplier(speedMultiplier);
        e.setBounds(playWidth, playHeight);

        enemies.add(e);
    }

    /**
     * Returns the list of enemies created by {@link #createEnemies()}.
     *
     * @return mutable list of enemies in this formation
     */
    @Override
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
