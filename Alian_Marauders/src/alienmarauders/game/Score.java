package alienmarauders.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Score {
    int score;

    public Score() {
        this.score = 0;
    }

    public void updateScore(int amount) {
        score += amount;
    }

    public void resetScore() {
        score = 0;
    }

    public void render(GraphicsContext gc) {
        gc.setFont(new Font(22));
        gc.setFill(Color.BEIGE);

        double x = gc.getCanvas().getWidth() * 0.025; // ~2.5% from the left
        double y = gc.getCanvas().getHeight() * 0.95; // ~95% down

        gc.fillText("Score: " + score, x, y);
    }
}
