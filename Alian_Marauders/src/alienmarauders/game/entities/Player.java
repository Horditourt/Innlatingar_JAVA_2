package alienmarauders.game.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player extends Entity {

    private static final Color PLAYER_COLOR = Color.BLUE;

    private double speed = 0.3; // tweak as you like

    // Movement flags: 0 or 1
    private double moveLeft = 0;
    private double moveRight = 0;
    private double moveUp = 0;
    private double moveDown = 0;

    // Bounding box for containment (set from outside)
    private double maxX = 800; // default, overwritten by setBounds
    private double maxY = 600;

    // Main constructor
    public Player(double x, double y, double width, double height, Image image) {
        super(x, y, width, height, image);
    }

    // Convenience constructor (no image)
    public Player(double x, double y, double width, double height) {
        this(x, y, width, height, null);
    }

    // Input handlers
    public void movingLeft(boolean val) {
        moveLeft = val ? 1 : 0;
    }

    public void movingRight(boolean val) {
        moveRight = val ? 1 : 0;
    }

    public void movingUp(boolean val) {
        moveUp = val ? 1 : 0;
    }

    public void movingDown(boolean val) {
        moveDown = val ? 1 : 0;
    }

    public void setBounds(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public void update(double deltaTimeMillis) {
        double dt = deltaTimeMillis; // using ms scale

        double vx = (moveRight - moveLeft) * speed;
        double vy = (moveDown - moveUp) * speed;

        x += vx * dt;
        y += vy * dt;

        // simple clamping so we don't fly off top/left
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x + width > maxX)
            x = maxX - width;
        if (y + height > maxY)
            y = maxY - height;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(PLAYER_COLOR);
            gc.fillRect(x, y, width, height);
        }
    }

    /**
     * Called from GameModel.reset(w,h).
     * Re-centers player and clears any stuck movement flags.
     */
    public void resetForNewGame(double playWidth, double playHeight) {
        // center bottom with a small margin
        this.x = playWidth / 2.0 - this.width / 2.0;
        this.y = playHeight - this.height - 40;

        // clear input so player doesn't keep drifting
        moveLeft = moveRight = moveUp = moveDown = 0;

        // bounds follow playfield
        setBounds(playWidth, playHeight);
    }

    // Call this once from the view, with the canvas used in the game
    public void initializeKeyBindings(Canvas canvas, Runnable onShootStart, Runnable onShootStop) {
        canvas.setFocusTraversable(true);

        canvas.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case LEFT -> movingLeft(true);
                case RIGHT -> movingRight(true);
                case UP -> movingUp(true);
                case DOWN -> movingDown(true);
                case A -> movingLeft(true);
                case D -> movingRight(true);
                case W -> movingUp(true);
                case S -> movingDown(true);
                case SPACE -> {
                    if (onShootStart != null) {
                        onShootStart.run();
                    }
                }
                default -> {
                }
            }
        });

        canvas.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case LEFT -> movingLeft(false);
                case RIGHT -> movingRight(false);
                case UP -> movingUp(false);
                case DOWN -> movingDown(false);
                case A -> movingLeft(false);
                case D -> movingRight(false);
                case W -> movingUp(false);
                case S -> movingDown(false);
                case SPACE -> {
                if (onShootStop != null) {
                    onShootStop.run();
                }
            }
                default -> {
                }
            }
        });
    }
}
