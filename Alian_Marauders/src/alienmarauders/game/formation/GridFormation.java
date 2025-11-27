package alienmarauders.game.formation;

import java.util.ArrayList;

import alienmarauders.game.entities.Enemy;
import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.image.Image;

public class GridFormation implements Formation {

    private final double playWidth;
    private final double playHeight;
    private final Image enemyImage;
    private final MovementStrategy movementStrategy;
    private final double speedMultiplier;

    private final int cols;
    private final int rows;
    private final double startY;

    private final ArrayList<Enemy> enemies = new ArrayList<>();

    public GridFormation(double playWidth,
                         double playHeight,
                         Image enemyImage,
                         MovementStrategy movementStrategy,
                         double speedMultiplier,
                         int cols,
                         int rows,
                         double startY) {

        this.playWidth = playWidth;
        this.playHeight = playHeight;
        this.enemyImage = enemyImage;
        this.movementStrategy = movementStrategy;
        this.speedMultiplier = speedMultiplier;
        this.cols = cols;
        this.rows = rows;
        this.startY = startY;
    }

    @Override
    public void createEnemies() {
        enemies.clear();

        double enemyWidth = 40;
        double enemyHeight = 30;
        double horizontalSpacing = 20;
        double verticalSpacing = 20;

        double totalWidth = cols * enemyWidth + (cols - 1) * horizontalSpacing;
        double startX = (playWidth - totalWidth) / 2.0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (enemyWidth + horizontalSpacing);
                double y = startY + row * (enemyHeight + verticalSpacing);

                Enemy enemy = new Enemy(x, y, enemyWidth, enemyHeight, enemyImage, 1);
                enemy.setBounds(playWidth, playHeight);
                enemy.setMovementStrategy(movementStrategy);
                enemy.setSpeedMultiplier(speedMultiplier);

                enemies.add(enemy);
            }
        }
    }

    @Override
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
