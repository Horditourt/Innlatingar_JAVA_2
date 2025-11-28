package alienmarauders.game;

import java.util.ArrayList;
import java.util.Random;
import alienmarauders.game.entities.*;
import alienmarauders.game.movement.*;
import alienmarauders.game.formation.*;
import javafx.scene.image.Image;

public class GameModel {

    private final Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Shot> shots = new ArrayList<>();
    private final Score score = new Score();
        // Enemy sprite sheets (each is a row of animation frames)
    private final Image blueMonsterSheet =
            new Image("/alienmarauders/images/BlueMonster.png");
    private final Image greenMonsterSheet =
            new Image("/alienmarauders/images/GreenMonster.png");
    private final Image redMonsterSheet =
            new Image("/alienmarauders/images/RedMonster.png");

    private final Image[] enemySheets = {
            blueMonsterSheet,
            greenMonsterSheet,
            redMonsterSheet
    };

    // Matching frame counts: blue=4, green=2, red=2
    private final int[] enemyFrameCounts = {
            4,  // Blue
            2,  // Green
            2   // Red
    };

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

        if (gameOver) return;

        // wave intro logic isolated here
        if (handleWaveIntro(deltaTimeMillis)) return;

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

    /**
    * Handles wave intro timing.
    * @return true if we are still in the intro and gameplay should pause.
    */
    private boolean handleWaveIntro(double deltaTimeMillis) {
        if (waveMillisRemaining > 0) {
            waveMillisRemaining -= deltaTimeMillis;
            if (waveMillisRemaining < 0) waveMillisRemaining = 0;
        }
        return waveMillisRemaining > 0;
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
        // Choose random movement strategy
        MovementStrategy movement;
        int m = rng.nextInt(3); // 0,1,2
        switch (m) {
            case 0 -> movement = new NoMovementStrategy();
            case 1 -> movement = new MoveDownStrategy();
            default -> movement = new ZigZagMovementStrategy();
        }
        movement.setSpeedMultiplier(speedMultiplier);

        // Random formation choice using that movement
    Formation formation;
    int f = rng.nextInt(3); // 0, 1 or 2

    if (f == 0) {
        formation = new GridFormation(
                playWidth, playHeight,
                enemySheets, enemyFrameCounts,movement, speedMultiplier,
                8, 3, 50, rng   // cols, rows, startY
        );
    } else if (f == 1) {
        formation = new VFormation(
                playWidth, playHeight,
                enemySheets, enemyFrameCounts, movement, speedMultiplier,
                5, rng          // rows
        );
    } else {
        formation = new ArcFormation(
                playWidth, playHeight,
                enemySheets, enemyFrameCounts, movement, speedMultiplier,
                9, rng          // enemyCount along the arc
        );
    }

        waveText = "WAVE " + wave;
        waveMillisRemaining = 1200;

        formation.createEnemies();
        enemies.clear();
        enemies.addAll(formation.getEnemies());
    }
}
