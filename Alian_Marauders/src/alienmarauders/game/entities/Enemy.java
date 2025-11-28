package alienmarauders.game.entities;

import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Represents a single enemy in the game, with hit points, movement and
 * an animated sprite sheet.
 */
public class Enemy extends Entity {
    // Sprite sheet frames
    private Image[] images;
    private int numImages;

    // Animation state
    private int currentFrame = 0;
    private double frameTimeMillis = 120;  // how long one frame is shown
    private double frameTimer = 0;

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
     * @param posX0     initial X position in pixels
     * @param posY0     initial Y position in pixels
     * @param width     drawing width in pixels for this enemy
     * @param height    drawing height in pixels for this enemy
     * @param image     full sprite sheet image (frames in one horizontal row)
     * @param numImages number of animation frames in the sprite sheet
     */
    public Enemy(double posX0, double posY0,
                 double width, double height,
                 Image image, int numImages) {

        super(posX0, posY0, width, height, image);

        this.numImages = numImages;
        this.images = new Image[numImages];

        // Slice the sprite sheet into individual frames
        sliceSpriteSheet(image);
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

    // ----- Sprite slicing & animation -----

    /**
     * Cuts the given sprite sheet into individual animation frames and stores
     * them in the {@code images} array. Assumes that all frames are laid out
     * in a single horizontal row.
     *
     * @param sprite full sprite sheet image
     */
    private void sliceSpriteSheet(Image sprite) {
        PixelReader pixelReader = sprite.getPixelReader();
        int frameWidth = (int) sprite.getWidth() / numImages;
        int frameHeight = (int) sprite.getHeight();

        for (int i = 0; i < numImages; i++) {
            WritableImage frame = new WritableImage(frameWidth, frameHeight);
            PixelWriter pixelWriter = frame.getPixelWriter();

            int offsetX = i * frameWidth;

            for (int y = 0; y < frameHeight; y++) {
                for (int x = 0; x < frameWidth; x++) {
                    pixelWriter.setColor(
                            x, y,
                            pixelReader.getColor(offsetX + x, y)
                    );
                }
            }
            images[i] = frame;
        }
    }

    // ----- Update / Render -----

    /**
     * Updates the enemy:
     * <ul>
     *   <li>Moves it according to the active movement strategy.</li>
     *   <li>Advances the animation frame based on elapsed time.</li>
     * </ul>
     *
     * @param deltaTimeMillis time elapsed since last update in milliseconds
     */
    @Override
    public void update(double deltaTimeMillis) {
        // Movement
        if (movementStrategy != null) {
            movementStrategy.moveEnemy(this, deltaTimeMillis);
        }

        // Animation: advance frame based on time
        if (images != null && numImages > 1) {
            frameTimer += deltaTimeMillis;
            while (frameTimer >= frameTimeMillis) {
                frameTimer -= frameTimeMillis;
                currentFrame = (currentFrame + 1) % numImages;
            }
        }
    }

    /**
     * Renders the current animation frame and the enemy's health bar.
     *
     * @param gc graphics context of the canvas to draw onto
     */
    @Override
    public void render(GraphicsContext gc) {
        Image frameToDraw = null;

        if (images != null && images.length > 0) {
            frameToDraw = images[currentFrame];
        } else if (image != null) {
            frameToDraw = image;
        }

        if (frameToDraw != null) {
            gc.drawImage(frameToDraw, x, y, width, height);
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
