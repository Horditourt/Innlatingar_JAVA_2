package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * Helper class for animating a horizontal sprite sheet.
 * <p>
 * The sprite sheet is assumed to contain {@code numImages} frames laid out
 * in a single row. Each call to
 * {@link #render(GraphicsContext, double, double, double, double)}
 * advances the internal frame pointer based on the configured time per image.
 */
public class ImageStride {

    private final Image[] frames;
    private final double timePerImageMillis;
    private final boolean loop;

    private int currentFrame = 0;
    private long lastUpdateNanos;

    /** Accumulates elapsed time (in ms) between frames. */
    private double frameAccumulatorMillis = 0.0;

    /**
     * Creates a looping {@code ImageStride} from a horizontal sprite sheet.
     *
     * @param timePerImageMillis
     *         how long (in milliseconds) each frame should be shown
     * @param numImages
     *         number of frames contained in the sprite sheet
     * @param spriteSheet
     *         the full sprite sheet image (frames in one horizontal row)
     */
    public ImageStride(double timePerImageMillis, int numImages, Image spriteSheet) {
        this(timePerImageMillis, numImages, spriteSheet, true);
    }

    /**
     * Creates an {@code ImageStride} from a horizontal sprite sheet.
     *
     * @param timePerImageMillis
     *         how long (in milliseconds) each frame should be shown
     * @param numImages
     *         number of frames contained in the sprite sheet
     * @param spriteSheet
     *         the full sprite sheet image (frames in one horizontal row)
     * @param loop
     *         whether the animation should loop when it reaches the last frame
     */
    public ImageStride(double timePerImageMillis,
                       int numImages,
                       Image spriteSheet,
                       boolean loop) {
        this.timePerImageMillis = timePerImageMillis;
        this.loop = loop;
        this.frames = sliceSpriteSheet(spriteSheet, numImages);
        this.lastUpdateNanos = System.nanoTime();
    }

    /**
     * Renders the current frame at the given position and size.
     * <p>
     * This method also advances the internal frame index, based on the elapsed
     * real time since it was last called.
     *
     * @param gc
     *         the {@link GraphicsContext} to draw on
     * @param x
     *         destination x coordinate in pixels
     * @param y
     *         destination y coordinate in pixels
     * @param width
     *         destination width in pixels
     * @param height
     *         destination height in pixels
     */
    public void render(GraphicsContext gc,
                       double x,
                       double y,
                       double width,
                       double height) {
        updateFrameIndexFromTime();
        if (frames.length == 0) {
            return;
        }
        Image frame = frames[currentFrame];
        gc.drawImage(frame, x, y, width, height);
    }

    /**
     * Returns {@code true} if this animation has reached its final frame
     * and is not configured to loop.
     *
     * @return {@code true} if a non-looping animation has completed,
     *         otherwise {@code false}
     */
    public boolean isFinished() {
        return !loop && currentFrame == frames.length - 1;
    }

    /**
     * Resets the animation back to the first frame and restarts its timing.
     */
    public void reset() {
        currentFrame = 0;
        frameAccumulatorMillis = 0.0;
        lastUpdateNanos = System.nanoTime();
    }

    /**
     * Slices a horizontal sprite sheet into individual frame images.
     *
     * @param spriteSheet
     *         the full sprite sheet image
     * @param numImages
     *         number of frames contained in the sprite sheet
     * @return an array of per-frame {@link Image} objects, one for each frame
     */
    private Image[] sliceSpriteSheet(Image spriteSheet, int numImages) {
        Image[] result = new Image[numImages];
        PixelReader reader = spriteSheet.getPixelReader();
        int frameWidth = (int) (spriteSheet.getWidth() / numImages);
        int frameHeight = (int) spriteSheet.getHeight();

        for (int i = 0; i < numImages; i++) {
            WritableImage frame = new WritableImage(reader,
                                                    i * frameWidth,
                                                    0,
                                                    frameWidth,
                                                    frameHeight);
            result[i] = frame;
        }

        return result;
    }

    /**
     * Advances {@link #currentFrame} according to the time elapsed since the
     * last call, honoring {@link #timePerImageMillis} and {@link #loop}.
     * <p>
     * Small time steps are accumulated until at least one full frame duration
     * has passed, ensuring that animations advance even at high frame rates.
     */
    private void updateFrameIndexFromTime() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastUpdateNanos;
        lastUpdateNanos = now;

        double elapsedMillis = elapsedNanos / 1_000_000.0;
        if (elapsedMillis <= 0 || frames.length == 0) {
            return;
        }

        // Accumulate elapsed time like the old frameTimer.
        frameAccumulatorMillis += elapsedMillis;

        while (frameAccumulatorMillis >= timePerImageMillis) {
            frameAccumulatorMillis -= timePerImageMillis;

            if (loop) {
                currentFrame = (currentFrame + 1) % frames.length;
            } else {
                if (currentFrame < frames.length - 1) {
                    currentFrame++;
                } else {
                    // Stay on last frame if non-looping.
                    frameAccumulatorMillis = 0.0;
                    break;
                }
            }
        }
    }
}
