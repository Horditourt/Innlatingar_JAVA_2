package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Short-lived explosion animation that plays once and then deactivates.
 * <p>
 * The animation uses an {@link ImageStride} over a sprite sheet containing
 * multiple frames of an explosion. Once the last frame has been rendered,
 * the animation marks itself as inactive.
 */
public class ExplosionAnimation implements Animatable {

    private final ImageStride imageStride;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    private boolean active = true;

    /**
     * Creates a new explosion animation at the given position and size.
     *
     * @param x                 the x coordinate in pixels where the explosion
     *                          should be rendered
     * @param y                 the y coordinate in pixels where the explosion
     *                          should be rendered
     * @param width             the width in pixels of the rendered explosion
     * @param height            the height in pixels of the rendered explosion
     * @param spriteSheet       the explosion sprite sheet image containing
     *                          all frames in a single horizontal row
     * @param numFrames         number of animation frames contained in the
     *                          sprite sheet
     * @param timePerFrameMillis
     *                          how long each frame should be displayed, in
     *                          milliseconds
     */
    public ExplosionAnimation(double x,
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

        // Non-looping explosion: play once
        this.imageStride = new ImageStride(timePerFrameMillis,
                numFrames,
                spriteSheet,
                false);
    }

    /**
     * Renders the current frame of the explosion and advances its internal
     * animation state.
     *
     * @param gc the graphics context used to render the explosion
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
     * Indicates whether the explosion is still playing.
     *
     * @return {@code true} while the explosion animation is still active,
     *         otherwise {@code false}
     */
    @Override
    public boolean isActive() {
        return active;
    }
}
