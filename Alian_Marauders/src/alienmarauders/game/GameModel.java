package alienmarauders.game;

import java.util.ArrayList;
import java.util.Random;
import alienmarauders.game.entities.*;
import alienmarauders.game.movement.MovementStrategies;
import javafx.scene.image.Image;

public class GameModel {

    private final Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Shot> shots = new ArrayList<>();
    private final Score score = new Score();
    private Image enemyImage;

    private double playWidth = 800;
    private double playHeight = 600;

    private boolean gameOver = false;

    private int wave = 1;
    private double speedMultiplier = 1.0;

    private double flashMillisRemaining = 0;
    private double waveMillisRemaining = 0;
    private String waveText = "";

    private final Random rng = new Random();

    public GameModel(Player player) {
        this.player = player;
    }

    public Player getPlayer() { return player; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<Shot> getShots() { return shots; }
    public Score getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public int getWave() { return wave; }
    public boolean isFlashRed() { return flashMillisRemaining > 0; }
    public boolean isWaveBannerActive() { return waveMillisRemaining > 0; }
    public String getWaveText() { return waveText; }



    public void playerShoot() {
        double shotWidth = 5;
        double shotHeight = 15;

        double px = player.getPositionX();
        double py = player.getPositionY();
        double pWidth = player.getWidth();

        double shotX = px + pWidth / 2 - shotWidth / 2;
        double shotY = py - shotHeight;

        shots.add(new Shot(shotX, shotY, shotWidth, shotHeight, null));
    }

    /** Called when game starts or restarts */
    public void reset(double playWidth, double playHeight) {
        this.playWidth = playWidth;
        this.playHeight = playHeight;

        enemies.clear();
        shots.clear();
        score.resetScore();

        gameOver = false;
        wave = 1;
        speedMultiplier = 1.0;
        flashMillisRemaining = 0;


        player.resetForNewGame(playWidth, playHeight);

        spawnNewWave();
    }

    public void update(double deltaTimeMillis) {

        // timers always tick
        if (flashMillisRemaining > 0) {
            flashMillisRemaining -= deltaTimeMillis;
            if (flashMillisRemaining < 0) flashMillisRemaining = 0;
        }

        if (waveMillisRemaining > 0) {
           waveMillisRemaining -= deltaTimeMillis;
           if (waveMillisRemaining < 0) waveMillisRemaining = 0;
        }

        if (gameOver) return;

        // stop the game during wave intro
        if (waveMillisRemaining > 0) return;

        player.update(deltaTimeMillis);

        for (Enemy e : enemies) {
            e.update(deltaTimeMillis);

            // lose if enemy reaches bottom
            if (e.getPositionY() + e.getHeight() >= playHeight) {
                flashMillisRemaining = 700;
                gameOver = true;
            }

            // lose if enemy collides with player
            if (CollisionDetection.Aabb(player, e)) {
                flashMillisRemaining = 700;
                gameOver = true;
            }
        }

        for (Shot s : shots) {
            s.update(deltaTimeMillis);
        }

        handleCollisions();
        cleanUp();

        // If all enemies dead -> next wave, faster
        if (!gameOver && enemies.isEmpty()) {
            wave++;
            speedMultiplier *= 1.10; // 10% harder each wave
            spawnNewWave();
        }
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
                    break; // one shot hits one enemy
                }
            }
        }
    }

    private void cleanUp() {
        enemies.removeIf(e -> !e.isAlive());
        shots.removeIf(s -> !s.isAlive());
    }

    // ---------------- WAVES / FORMATIONS / STRATEGIES ----------------

    protected void spawnNewWave() {
        if (enemyImage == null) {
            enemyImage = new Image("/alienmarauders/images/planets.png");
        }

        // Random formation choice
        int formation = rng.nextInt(3); // 0,1,2
        int cols, rows;
        double startY;

        switch (formation) {
            case 0 -> { cols = 8; rows = 3; startY = 50; }   // standard grid
            case 1 -> { cols = 6; rows = 4; startY = 30; }   // taller block
            default -> { cols = 10; rows = 2; startY = 70; } // wide thin line
        }

        // Random movement choice
        MovementStrategies move = switch (rng.nextInt(3)) {
            case 0 -> MovementStrategies.NO_MOVE;
            case 1 -> MovementStrategies.DOWN;
            default -> MovementStrategies.ZIGZAG;
        };

        double enemyWidth = 40;
        double enemyHeight = 30;
        double horizontalSpacing = 20;
        double verticalSpacing = 20;

        double totalWidth = cols * enemyWidth + (cols - 1) * horizontalSpacing;
        double startX = (playWidth - totalWidth) / 2.0;

        enemies.clear();

        waveText = "WAVE " + wave;
        waveMillisRemaining = 1200;  // 1.2 seconds


        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (enemyWidth + horizontalSpacing);
                double y = startY + row * (enemyHeight + verticalSpacing);

                Enemy enemy = new Enemy(x, y, enemyWidth, enemyHeight, enemyImage, 1);
                enemy.setBounds(playWidth, playHeight);
                enemy.setMovement(move);
                enemy.setSpeedMultiplier(speedMultiplier);

                enemies.add(enemy);
            }
        }
    }
}
