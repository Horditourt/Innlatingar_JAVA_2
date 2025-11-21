package alienmarauders.game;

import java.util.ArrayList;
import alienmarauders.game.entities.*;
import javafx.scene.image.Image;

public class GameModel {

    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Shot> shots = new ArrayList<>();
    private Score score = new Score();
    private Image enemyImage;

    // later: Formation and MovementStrategy references

    public GameModel(Player player) {
        this.player = player;
    }

    public GameModel() {
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Shot> getShots() {
        return shots;
    }

    public Score getScore() {
        return score;
    }

    public void addShot(Shot shot) {
        shots.add(shot);
    }

    public void playerShoot() {
        double shotWidth = 5;
        double shotHeight = 15;

        double px = player.getPositionX();
        double py = player.getPositionY();
        double pWidth = player.getWidth();

        // Spawn from the center-top of the player
        double shotX = px + pWidth / 2 - shotWidth / 2;
        double shotY = py - shotHeight;

        Shot shot = new Shot(shotX, shotY, shotWidth, shotHeight, null);
        shots.add(shot);
    }

    public void initEnemyFormation(double playWidth, double playHeight) {
        enemies.clear(); // start fresh
        enemyImage = new Image("/alienmarauders/images/planets.png");

        int cols = 8;
        int rows = 3;

        double enemyWidth = 40;
        double enemyHeight = 30;

        double horizontalSpacing = 20;
        double verticalSpacing = 20;

        double totalWidth = cols * enemyWidth + (cols - 1) * horizontalSpacing;
        double startX = (playWidth - totalWidth) / 2.0; // center horizontally
        double startY = 50; // top offset

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (enemyWidth + horizontalSpacing);
                double y = startY + row * (enemyHeight + verticalSpacing);

                Enemy enemy = new Enemy(x, y, enemyWidth, enemyHeight, enemyImage, 1);
                enemies.add(enemy);
            }
        }
    }

    public void reset(double playWidth, double playHeight) {
        // Clear old data
        enemies.clear();
        shots.clear();
        score.resetScore();

        // Reset player position and state
        player.resetForNewGame(playWidth, playHeight);

        // Spawn enemies
        initEnemyFormation(playWidth, playHeight);
    }

    public void update(double deltaTimeMillis) {
        // 1. Update entities
        player.update(deltaTimeMillis);
        for (Enemy e : enemies) {
            e.update(deltaTimeMillis);
        }
        for (Shot s : shots) {
            s.update(deltaTimeMillis);
        }

        // 2. Handle collisions (single-threaded for now)
        handleCollisions();

        // 3. Remove dead entities
        cleanUp();

        // 4. Check game over / spawn new waves (later)
    }

    private void handleCollisions() {

    for (Shot s : shots) {
        if (!s.isAlive()) continue;

        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;

            if (CollisionDetection.Aabb(s, e)) {
                s.kill();
                e.takeDamage(1);

                if (!e.isAlive()) {
                    score.updateScore(100);
                }

                break; // shot hits only one enemy
            }
        }
    }
}


    private void cleanUp() {
        enemies.removeIf(e -> !e.isAlive());
        shots.removeIf(s -> !s.isAlive());
    }
}
