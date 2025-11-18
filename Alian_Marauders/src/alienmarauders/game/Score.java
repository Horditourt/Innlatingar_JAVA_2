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
        gc.setFill(Color.RED);
        gc.fillText("Score: " + score, 1,1 
        // TODO Globals.WINDOW_WIDTH * 0.025,
        // TODO Globals.WINDOW_HEIGHT * 0.975
        );
    }
    
}
