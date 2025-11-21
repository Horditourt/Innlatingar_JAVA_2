package alienmarauders.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Shot extends Entity {

    // pixels per millisecond (e.g. 0.6 = 600px per second upwards)
    private final double speedY = -0.6;

    public Shot(double x, double y, double width, double height, Image image) {
        super(x, y, width, height, image);
    }

    @Override
    public void update(double deltaTimeMillis) {
        // Move upwards
        y += speedY * deltaTimeMillis;

        // If the shot has left the top of the screen, kill it
        if (y + height < 0) {
            kill();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x, y, width, height);
        }
    }
}
