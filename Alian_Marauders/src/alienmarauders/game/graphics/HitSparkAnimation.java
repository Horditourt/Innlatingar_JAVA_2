package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Short-lived hit spark animation that is played when a shot hits an enemy.
 * <p>
 * The animation uses an {@link ImageStride} over a horizontal sprite sheet
 * and plays once. After the last frame has been rendered, the animation
 * marks itself as inactive so that containers can remove it.
 */
public class HitSparkAnimation implements Animatable {

    private final ImageStride imageStride;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    private boolean active = true;

    /**
     * Creates a new hit spark animation at the given position and size.
     *
     * @param x                 the x coordinate in pixels where the spark
     *                          should be rendered
     * @param y                 the y coordinate in pixels where the spark
     *                          should be rendered
     * @param width             the width in pixels of the rendered spark
     * @param height            the height in pixels of the rendered spark
     * @param spriteSheet       the hit spark sprite sheet image containing
     *                          all frames in a single horizontal row
     * @param numFrames         number of animation frames contained in the
     *                          sprite sheet
     * @param timePerFrameMillis
     *                          how long each frame should be displayed, in
     *                          milliseconds
     */
    public HitSparkAnimation(double x,
                             double y,
                             double width,
                             double height,
                             Image spriteSheet,
                             int numFrames,
                             double timePerFrameMillis) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Non-looping animation: play once on hit
        this.imageStride = new ImageStride(
                timePerFrameMillis,
                numFrames,
                spriteSheet,
                false
        );
    }

    /**
     * Renders the current frame of the hit spark and advances its internal
     * animation state.
     *
     * @param gc the graphics context used to render the spark
     */
    @Override
    public void renderAnimation(GraphicsContext gc) {
        if (!active) {
            return;
        }

        imageStride.render(gc, x, y, width, height);

        if (imageStride.isFinished()) {
            active = false;
        }
    }

    /**
     * Indicates whether the hit spark is still playing.
     *
     * @return {@code true} while the animation is active, otherwise
     *         {@code false}
     */
    @Override
    public boolean isActive() {
        return active;
    }
}
