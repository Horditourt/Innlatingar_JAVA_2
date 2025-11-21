package alienmarauders.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Enemy extends Entity {
    // The asset greenmonster.png contains two images (numImages = 2),
    // this is used for animation purposes, but for now
    // we just want to use the first image, i.e. when
    // rendering you can use images[0]
    private Image[] images;
    private int numImages;
    // Initial position of the enemy
    // Find appropriate hp and speed
    private int hitPoints = 3;
    private double speed = 0.05;

    public Enemy(double posX0, double posY0, double width, double height, Image image, int numImages) {
        super(posX0, posY0, width, height, image);
        this.numImages = numImages;
        images = new Image[numImages];
        getImageStrides(image);
        for (int i = 0; i < numImages; i++) {
            this.images[i] = new Image(
                    image.getUrl(),
                    width,
                    height,
                    false,
                    false,
                    false);
        }

    }

    public void takeDamage(int amount) {
        this.hitPoints -= amount;
    }

    private void getImageStrides(Image image) {
        PixelReader PixelReader = image.getPixelReader();
        PixelWriter PixelWriter = null;
        int w = (int) image.getWidth() / numImages;
        int h = (int) image.getHeight();
        for (int i = 0; i < numImages; i++) {
            WritableImage imageSection = new WritableImage(w, h);
            PixelWriter = imageSection.getPixelWriter();
            for (int y = 0; y < h; y++) {
                int offset = i * w;
                for (int x = offset; x < offset + w; x++) {
                    PixelWriter.setColor(
                            x - offset, y,
                            PixelReader.getColor(x, y));
                }
            }
            images[i] = imageSection;
        }
    }

    @Override
    public void update(double deltaTimeMillis) {
        // basic vertical drift (moves down slowly)
        double speedY = 0.03; // pixels per millisecond => 30 px/sec

        y += speedY * deltaTimeMillis;

        // optional: if it goes off bottom, mark as dead
        // (or trigger game over, etc.)
        // if (y > maxY) { kill(); }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (images != null && images.length > 0) {
            gc.drawImage(images[0], x, y, width, height);
        } else if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            // fallback: draw a red rectangle if image missing
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, width, height);
        }
    }

}
