package alienmarauders.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {

    // Shared fields for all entities
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Image image;
    protected boolean alive = true;

    public Entity(double x, double y, double width, double height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    // Used by CollisionDetection
    public int getPositionX() {
        return (int) x;
    }

    public int getPositionY() {
        return (int) y;
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    // Every entity must be updatable and renderable
    public abstract void update(double deltaTimeMillis);

    public abstract void render(GraphicsContext gc);
}
