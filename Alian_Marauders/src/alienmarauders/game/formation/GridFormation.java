package alienmarauders.game.formation;

import alienmarauders.game.entities.Enemy;
import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creates enemies arranged in a regular grid formation.
 * <p>
 * The enemies are placed in {@code rows} x {@code cols} layout,
 * centered horizontally in the play area and starting at a given Y offset.
 */
public class GridFormation implements Formation {

    private final double playWidth;
    private final double playHeight;
    private final Image[] enemySheets;
    private final int[] enemyFrameCounts;
    private final MovementStrategy movement;
    private final double speedMultiplier;
    private final int cols;
    private final int rows;
    private final double startY;
    private final Random rng;

    private final ArrayList<Enemy> enemies = new ArrayList<>();

    /**
     * Constructs a new grid-shaped enemy formation.
     *
     * @param playWidth        width of the play area in pixels
     * @param playHeight       height of the play area in pixels
     * @param enemySheets      array of enemy sprite sheets to choose from
     * @param enemyFrameCounts array of frame counts matching {@code enemySheets}
     * @param movement         movement strategy used by all enemies in this formation
     * @param speedMultiplier  speed multiplier for the enemies in this formation
     * @param cols             number of enemy columns
     * @param rows             number of enemy rows
     * @param startY           top Y position for the first row of enemies
     * @param rng              random number generator used to pick enemy sprites
     */
    public GridFormation(double playWidth, double playHeight,
                         Image[] enemySheets,
                         int[] enemyFrameCounts,
                         MovementStrategy movement,
                         double speedMultiplier,
                         int cols, int rows, double startY,
                         Random rng) {

        this.playWidth = playWidth;
        this.playHeight = playHeight;
        this.enemySheets = enemySheets;
        this.enemyFrameCounts = enemyFrameCounts;
        this.movement = movement;
        this.speedMultiplier = speedMultiplier;
        this.cols = cols;
        this.rows = rows;
        this.startY = startY;
        this.rng = rng;
    }

    /**
     * Creates the enemies in a grid and stores them internally.
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

        double spacingX = enemyWidth + 10;
        double spacingY = enemyHeight + 10;

        // Total width of the grid and starting X to center it
        double totalWidth = cols * spacingX - 10; // last column doesn’t need extra gap
        double startX = (playWidth - totalWidth) / 2.0;

        for (int row = 0; row < rows; row++) {
            double y = startY + row * spacingY;

            for (int col = 0; col < cols; col++) {
                double x = startX + col * spacingX;
                addEnemy(x, y, enemyWidth, enemyHeight);
            }
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
