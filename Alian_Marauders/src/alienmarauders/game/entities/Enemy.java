package alienmarauders.game.entities;

import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Enemy extends Entity {
    // Sprite sheet frames
    private Image[] images;
    private int numImages;

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

    public Enemy(double posX0, double posY0,
                 double width, double height,
                 Image image, int numImages) {

        super(posX0, posY0, width, height, image);

        this.numImages = numImages;
        this.images = new Image[numImages];
        getImageStrides(image);

        for (int i = 0; i < numImages; i++) {
            this.images[i] = new Image(
                    image.getUrl(),
                    width,
                    height,
                    false,
                    false,
                    false
            );
        }
    }

    // ----- Movement strategy wiring -----

    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    /**
     * Called from GameModel when wave difficulty increases.
     * Just forwards to the current movement strategy.
     */
    public void setSpeedMultiplier(double mult) {
        if (movementStrategy != null) {
            movementStrategy.setSpeedMultiplier(mult);
        }
    }

    public void setBounds(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }

    /**
     * Base speed s from the assignment (scaled in MovementStrategy).
     */
    public double getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * x0 in the sine formula. First time it's asked, we "freeze" the spawn x.
     */
    public double getBaseX() {
        if (baseX == null) {
            baseX = x;
        }
        return baseX;
    }

    // ----- HP / damage -----

    public double getHealthRatio() {
        return Math.max(0, (double) hitPoints / maxHitPoints);
    }

    public void takeDamage(int amount) {
        this.hitPoints -= amount;
        if (hitPoints <= 0) {
            kill();
        }
    }

    // ----- Sprite slicing -----

    private void getImageStrides(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = null;
        int w = (int) image.getWidth() / numImages;
        int h = (int) image.getHeight();

        for (int i = 0; i < numImages; i++) {
            WritableImage imageSection = new WritableImage(w, h);
            pixelWriter = imageSection.getPixelWriter();

            for (int y = 0; y < h; y++) {
                int offset = i * w;
                for (int x = offset; x < offset + w; x++) {
                    pixelWriter.setColor(
                            x - offset, y,
                            pixelReader.getColor(x, y));
                }
            }
            images[i] = imageSection;
        }
    }

    // ----- Update / Render -----

    @Override
    public void update(double deltaTimeMillis) {
        if (movementStrategy != null) {
            movementStrategy.moveEnemy(this, deltaTimeMillis);
        }
        // else: no movement by default
    }

    @Override
    public void render(GraphicsContext gc) {
        if (images != null && images.length > 0) {
            gc.drawImage(images[0], x, y, width, height);
        } else if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, width, height);
        }

        drawHealthBar(gc);
    }

    private void drawHealthBar(GraphicsContext gc) {
        double ratio = getHealthRatio();

        double barWidth = width;
        double barHeight = 6;
        double barX = x;
        double barY = y - barHeight - 2; // slightly above enemy

        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);

        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(barX, barY, barWidth * ratio, barHeight);

        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }
}
