package alienmarauders.game.entities;

import javafx.scene.image.Image;

public abstract class Entity {

    protected int positionX;
    protected int positionY;
    protected int width;
    protected int height;
    protected Image image;
    protected double velocityX;
    protected double velocityY;
    
    public abstract int getPositionX();

    public abstract int getPositionY();

    public abstract int getWidth();
    
    public abstract int getHeight();

    protected abstract void render();
}
