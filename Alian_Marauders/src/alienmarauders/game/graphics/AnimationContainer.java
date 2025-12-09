package alienmarauders.game.graphics;

import javafx.scene.canvas.GraphicsContext;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for short-lived animations.
 * <p>
 * The container holds a collection of {@link Animatable} instances and is
 * responsible for rendering them as well as removing any animations that
 * have finished playing (i.e. {@link Animatable#isActive()} returns
 * {@code false}).
 *
 * @param <T> the type of animations contained in this container;
 *            must implement {@link Animatable}
 */
public class AnimationContainer<T extends Animatable> {

    private final List<T> animations = new LinkedList<>();

    /**
     * Adds a new animation to this container.
     *
     * @param animation the animation to add; if {@code null}, the call is ignored
     */
    public void addAnimation(T animation) {
        if (animation != null) {
            animations.add(animation);
        }
    }

    /**
     * Removes all animations from this container.
     */
    public void clear() {
        animations.clear();
    }

    /**
     * Returns whether this container currently holds any animations.
     *
     * @return {@code true} if the container is empty, otherwise {@code false}
     */
    public boolean isEmpty() {
        return animations.isEmpty();
    }

    /**
     * Renders all animations in this container and removes those that have
     * finished playing.
     * <p>
     * For each animation, {@link Animatable#renderAnimation(GraphicsContext)}
     * is called, after which {@link Animatable#isActive()} is consulted to
     * determine whether the animation should remain in the container.
     *
     * @param gc the graphics context used for rendering all animations
     */
    public void renderAnimations(GraphicsContext gc) {
        Iterator<T> iterator = animations.iterator();
        while (iterator.hasNext()) {
            T animation = iterator.next();
            animation.renderAnimation(gc);
            if (!animation.isActive()) {
                iterator.remove();
            }
        }
    }
}
