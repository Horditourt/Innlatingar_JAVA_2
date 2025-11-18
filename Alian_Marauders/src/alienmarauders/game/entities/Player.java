package alienmarauders.game.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player extends Entity {
    private double velocityX;
    private double velocityY;
    private double positionX;
    private double positionY;
    private static Color playerColor = Color.BLUE;

    private double speed = 1;
    private double moveLeft = 0;
    private double moveRight = 0;
    private double moveUp = 0;
    private double moveDown = 0;

    public Player( double positionX, double positionY, double width, double height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = (int) width;
        this.height = (int) height;
        
    }

    public void move(double time) {
        this.velocityX = (moveRight - moveLeft) * speed;
        this.velocityY = (moveDown - moveUp) * speed;
    }

    public void movingLeft(boolean val) {
        this.moveLeft = val ? 1 : 0;
    }

    public void movingRight(boolean val) {
        this.moveRight = val ? 1 : 0;
    }

    public void movingUp(boolean val) {
        this.moveUp = val ? 1 : 0;
    }

    public void movingDown(boolean val) {
        this.moveDown = val ? 1 : 0;
    }

    public void render(GraphicsContext gc)  {
        gc.setFill(playerColor);
        gc.fillRect(400, 550, 50, 50);
    }

    private void moveTo(double x, double y) {
        //this.positionX = x;
        //this.positionY = y;
        }

    private void initializeKeyBindings(Canvas canvas, GraphicsContext gc) {
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    movingLeft(true);
                    break;
                case RIGHT:
                    movingRight(true);
                    break;
                case UP:
                    movingUp(true);
                    break;
                case DOWN:
                    movingDown(true);
                    break;
                default:
                    break;
            }
        });

        canvas.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                    movingLeft(false);
                    break;
                case RIGHT:
                    movingRight(false);
                    break;
                case UP:
                    movingUp(false);
                    break;
                case DOWN:
                    movingDown(false);
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public int getPositionX() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPositionX'");
    }

    @Override
    public int getPositionY() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPositionY'");
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWidth'");
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeight'");
    }

    @Override
    protected void render() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }
}
