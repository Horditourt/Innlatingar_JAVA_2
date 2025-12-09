package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Short-lived muzzle-flash animation that is played when the player
 * fires a shot.
 * <p>
 * The animation uses an {@link ImageStride} over a horizontal sprite sheet
 * and plays once. After the last frame has been rendered, the animation
 * marks itself as inactive so that containers can remove it.
 */
public class ShotFlashAnimation implements Animatable {

    private final ImageStride imageStride;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    private boolean active = true;

    /**
     * Creates a new muzzle-flash animation at the given position and size.
     *
     * @param x                 the x coordinate in pixels where the flash
     *                          should be rendered
     * @param y                 the y coordinate in pixels where the flash
     *                          should be rendered
     * @param width             the width in pixels of the rendered flash
     * @param height            the height in pixels of the rendered flash
     * @param spriteSheet       the muzzle-flash sprite sheet image containing
     *                          all frames in a single horizontal row
     * @param numFrames         number of animation frames contained in the
     *                          sprite sheet
     * @param timePerFrameMillis
     *                          how long each frame should be displayed, in
     *                          milliseconds
     */
    public ShotFlashAnimation(double x,
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

        // Non-looping animation: play once when a shot is fired
        this.imageStride = new ImageStride(
                timePerFrameMillis,
                numFrames,
                spriteSheet,
                false
        );
    }

    /**
     * Renders the current frame of the muzzle flash and advances its internal
     * animation state.
     *
     * @param gc the graphics context used to render the muzzle flash
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
     * Indicates whether the muzzle flash is still playing.
     *
     * @return {@code true} while the animation is active, otherwise
     *         {@code false}
     */
    @Override
    public boolean isActive() {
        return active;
    }
}
