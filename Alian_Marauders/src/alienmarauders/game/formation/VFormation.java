package alienmarauders.game.formation;

import java.util.ArrayList;

import alienmarauders.game.entities.Enemy;
import alienmarauders.game.movement.MovementStrategy;
import javafx.scene.image.Image;

public class VFormation implements Formation {

    private final double playWidth;
    private final double playHeight;
    private final Image enemyImage;
    private final MovementStrategy movementStrategy;
    private final double speedMultiplier;

    private final int rows;

    private final ArrayList<Enemy> enemies = new ArrayList<>();

    public VFormation(double playWidth,
                      double playHeight,
                      Image enemyImage,
                      MovementStrategy movementStrategy,
                      double speedMultiplier,
                      int rows) {

        this.playWidth = playWidth;
        this.playHeight = playHeight;
        this.enemyImage = enemyImage;
        this.movementStrategy = movementStrategy;
        this.speedMultiplier = speedMultiplier;
        this.rows = rows;
    }

    @Override
    public void createEnemies() {
        enemies.clear();

        double enemyWidth = 40;
        double enemyHeight = 30;
        double verticalSpacing = 25;

        double centerX = playWidth / 2.0;
        double startY = 40;

        for (int row = 0; row < rows; row++) {
            int count = row + 1;
            double rowWidth = count * enemyWidth + (count - 1) * 10;
            double rowStartX = centerX - rowWidth / 2.0;
            double y = startY + row * (enemyHeight + verticalSpacing);

            for (int i = 0; i < count; i++) {
                double x = rowStartX + i * (enemyWidth + 10);

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
