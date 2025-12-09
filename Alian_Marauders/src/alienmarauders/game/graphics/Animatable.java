package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a short-lived animation that can render itself onto a
 * {@link GraphicsContext}.
 * <p>
 * Implementations are expected to have a limited lifetime; once the
 * animation has finished playing, {@link #isActive()} should return
 * {@code false} so that containers can discard it.
 */
public interface Animatable {

    /**
     * Renders the animation on the given {@link GraphicsContext}.
     * <p>
     * Implementations may also advance their internal state when this
     * method is called (e.g. move to the next frame of a sprite sheet).
     *
     * @param gc the graphics context to render the animation on
     */
    void renderAnimation(GraphicsContext gc);

    /**
     * Indicates whether this animation is still active.
     * <p>
     * When this method returns {@code false}, the animation is considered
     * finished and can be removed from any container that manages it.
     *
     * @return {@code true} if the animation is still active, otherwise
     *         {@code false}
     */
    boolean isActive();
}
