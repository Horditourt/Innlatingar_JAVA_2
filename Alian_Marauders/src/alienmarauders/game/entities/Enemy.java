package alienmarauders.game.entities;

import alienmarauders.game.movement.MovementStrategies;
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
    private final int maxHitPoints = 3;
    private int hitPoints = maxHitPoints;
    // pixels per millisecond
    private double speedY = 0.05;

    private MovementStrategies movement = MovementStrategies.DOWN;

    // bounds for zigzag movement
    private double maxX = 800;
    // private double maxY = 600;

    private double zigzagSpeedX = 0.05; // px/ms
    private int zigzagDir = 1;

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

    public void setSpeedMultiplier(double mult) {
        this.speedY = 0.03 * mult;
        this.zigzagSpeedX = 0.05 * mult;
    }

    public void setMovement(MovementStrategies movement) {
        this.movement = movement;
    }

    public void setBounds(double maxX, double maxY) {
        this.maxX = maxX;
        // this.maxY = maxY;
    }

    public double getHealthRatio() {
        return Math.max(0, (double) hitPoints / maxHitPoints);
    }

    public void takeDamage(int amount) {
        this.hitPoints -= amount;
        if (hitPoints <= 0) {
            kill();
        }
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
        double dt = deltaTimeMillis;

        switch (movement) {
            case NO_MOVE -> {
                // do nothing
            }
            case DOWN -> {
                y += speedY * dt;
            }
            case ZIGZAG -> {
                y += speedY * dt;
                x += zigzagDir * zigzagSpeedX * dt;

                if (x <= 0) {
                    x = 0;
                    zigzagDir = 1;
                } else if (x + width >= maxX) {
                    x = maxX - width;
                    zigzagDir = -1;
                }
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (images != null && images.length > 0) {
            gc.drawImage(images[0], x, y, width, height);
            drawHealthBar(gc);
        } else if (image != null) {
            gc.drawImage(image, x, y, width, height);
            drawHealthBar(gc);
        } else {
            // fallback: draw a red rectangle if image missing
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, width, height);
            drawHealthBar(gc);
        }
    }

    private void drawHealthBar(GraphicsContext gc) {
        // --- Health bar ---
        double ratio = getHealthRatio();

        double barWidth = width;
        double barHeight = 6;
        double barX = x;
        double barY = y - barHeight + 2; // slightly above enemy

        // background (red)
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);

        // foreground (green showing remaining health)
        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(barX, barY, barWidth * ratio, barHeight);

        // border (black)
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.strokeRect(barX, barY, barWidth, barHeight);

    }

}
