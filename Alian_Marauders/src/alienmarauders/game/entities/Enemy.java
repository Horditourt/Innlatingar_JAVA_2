package alienmarauders.game.entities;

import alienmarauders.game.graphics.ImageStride;
import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


/**
 * Represents a single enemy in the game, with hit points, movement and
 * an animated sprite sheet.
 */
public class Enemy extends Entity {
    // Sprite animation for this enemy
    private final ImageStride imageStride;


    // HP
    private final int maxHitPoints = 3;
    private int hitPoints = maxHitPoints;

    // Base speed s (px/ms) from the assignment
    // Will be scaled by the MovementStrategy's speedMultiplier
    private final double baseSpeed = 0.035;

    // For zigzag: remember the spawn X as x0 in the sine formula
    private Double baseX = null;

    // Current movement strategy (NoMove, MoveDown, ZigZag, ...)
    private MovementStrategy movementStrategy;

    // Optional bounds if a strategy wants them
    private double maxX = 800;
    private double maxY = 600;

    /**
     * Creates a new enemy that uses a sprite sheet with several frames.
     *
     * @param posX0
     *         initial X position in pixels
     * @param posY0
     *         initial Y position in pixels
     * @param width
     *         drawing width in pixels for this enemy
     * @param height
     *         drawing height in pixels for this enemy
     * @param spriteSheet
     *         full sprite sheet image (frames in one horizontal row)
     * @param numImages
     *         number of animation frames in the sprite sheet
     */
    public Enemy(double posX0, double posY0,
                 double width, double height,
                 Image spriteSheet, int numImages) {

        super(posX0, posY0, width, height, spriteSheet);

        // 120 ms per frame to match your previous implementation
        this.imageStride = new ImageStride(180.0, numImages, spriteSheet);
    }


    // ----- Movement strategy wiring -----

    /**
     * Sets the movement strategy used to update this enemy's position.
     *
     * @param movementStrategy movement strategy implementation
     */
    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    /**
     * Called from GameModel when wave difficulty increases.
     * Just forwards to the current movement strategy.
     *
     * @param mult new speed multiplier value to set
     */
    public void setSpeedMultiplier(double mult) {
        if (movementStrategy != null) {
            movementStrategy.setSpeedMultiplier(mult);
        }
    }

    /**
     * Sets the maximum allowed X and Y positions in the play area that
     * some movement strategies might want to respect.
     *
     * @param maxX maximum X coordinate
     * @param maxY maximum Y coordinate
     */
    public void setBounds(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }

    /**
     * Base speed s from the assignment (scaled in MovementStrategy).
     *
     * @return base speed in pixels per millisecond
     */
    public double getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * x0 in the sine formula. First time it's asked, we "freeze" the spawn x.
     *
     * @return base X position used by zigzag logic
     */
    public double getBaseX() {
        if (baseX == null) {
            baseX = x;
        }
        return baseX;
    }

    // ----- HP / damage -----

    /**
     * Returns the current health as a ratio between 0 and 1.
     *
     * @return current hit points divided by maximum hit points
     */
    public double getHealthRatio() {
        return Math.max(0, (double) hitPoints / maxHitPoints);
    }

    /**
     * Reduces the enemy's hit points by the given amount and kills the enemy
     * if hit points reach zero or below.
     *
     * @param amount damage to apply
     */
    public void takeDamage(int amount) {
        this.hitPoints -= amount;
        if (hitPoints <= 0) {
            kill();
        }
    }

    // ----- Update / Render -----

    /**
     * Updates the enemy:
     * <ul>
     *   <li>Moves it according to the active movement strategy.</li>
     *   <li>Animation timing is handled inside {@link ImageStride}.</li>
     * </ul>
     *
     * @param deltaTimeMillis
     *         time elapsed since last update in milliseconds
     */
    @Override
    public void update(double deltaTimeMillis) {
        // Movement
        if (movementStrategy != null) {
            movementStrategy.moveEnemy(this, deltaTimeMillis);
        }
        // No explicit animation step needed; ImageStride advances on render.
    }

    /**
     * Renders the current enemy sprite and its health bar.
     *
     * @param gc
     *         graphics context of the canvas to draw onto
     */
    @Override
    public void render(GraphicsContext gc) {
        if (imageStride != null) {
            // ImageStride will advance the frame index based on wall-clock time.
            imageStride.render(gc, x, y, width, height);
        } else if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, width, height);
        }

        drawHealthBar(gc);
    }


    /**
     * Draws the health bar just below the enemy sprite, showing the current
     * health ratio in green over a red background.
     *
     * @param gc graphics context of the canvas to draw onto
     */
    private void drawHealthBar(GraphicsContext gc) {
        double ratio = getHealthRatio();

        double barWidth = width;
        double barHeight = 6;
        double barX = x;
        double barY = y + height + 2; // slightly below enemy

        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);

        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(barX, barY, barWidth * ratio, barHeight);

        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }
}
