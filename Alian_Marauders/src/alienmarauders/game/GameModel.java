package alienmarauders.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import alienmarauders.game.entities.*;
import alienmarauders.game.movement.*;
import alienmarauders.game.formation.*;
import javafx.scene.image.Image;

/**
 * Core game model holding all game state (player, enemies, shots, score).
 * <p>
 * The model is updated once per frame from the JavaFX Application Thread
 * via {@link #update(double)}. Collision detection is processed using a
 * thread pool so that collision checks and score updates are performed in
 * a concurrent, but thread-safe, fashion.
 */
public class GameModel {

    private final Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Shot> shots = new ArrayList<>();
    private final Score score = new Score();

    // Shooting state: true while SPACE is held
    private boolean shooting = false;

    // Shooting cooldown (ms) and timer
    private double shotCooldownMillis = 150; // tweak: smaller = faster fire
    private double shotTimer = 0;

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

    // ----- Concurrency support for collisions -----

    /**
     * Executor used to process collision checks concurrently.
     * All tasks are short-lived and CPU-bound.
     */
    private final ExecutorService collisionExecutor =
            Executors.newFixedThreadPool(
                    Math.max(2, Runtime.getRuntime().availableProcessors() - 1)
            );

    /**
     * Simple data holder for a collision result between a shot and an enemy.
     *
     * @param shot  shot that hit an enemy
     * @param enemy enemy that was hit
     */
    private record CollisionResult(Shot shot, Enemy enemy) {}

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

    /**
     * Spawns a single player shot at the top-center of the player sprite.
     */
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

    /**
     * Enables or disables continuous shooting. Typically called from the
     * keyboard handler when SPACE is pressed or released.
     *
     * @param shooting true if the player should currently be firing
     */
    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    /**
     * Handles automatic shooting while the shooting flag is enabled.
     * Spawns a new shot whenever the cooldown timer reaches zero.
     *
     * @param deltaTimeMillis time elapsed since last update in milliseconds
     */
    private void handleShooting(double deltaTimeMillis) {
        if (!shooting) {
            // Reset timer so shot fires immediately next time shooting starts
            shotTimer = 0;
            return;
        }

        if (shotTimer > 0) {
            shotTimer -= deltaTimeMillis;
        }

        if (shotTimer <= 0) {
            playerShoot();
            shotTimer = shotCooldownMillis;
        }
    }

    /**
     * Called when game starts or restarts.
     * Resets all game state and spawns the first wave.
     *
     * @param playWidth  current playfield width in pixels
     * @param playHeight current playfield height in pixels
     */
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

        shooting = false;
        shotTimer = 0;

        player.resetForNewGame(playWidth, playHeight);

        spawnNewWave();
    }

    /**
     * Main game update step, called once per frame from the JavaFX
     * Application Thread by the game controller.
     * <p>
     * This method:
     * <ul>
     *   <li>Updates timers and early-exits during wave intro.</li>
     *   <li>Steps the player, enemies and shots.</li>
     *   <li>Processes collisions using a thread pool.</li>
     *   <li>Advances to the next wave when all enemies are dead.</li>
     * </ul>
     *
     * @param deltaTimeMillis time elapsed since last update in milliseconds
     */
    public void update(double deltaTimeMillis) {

        // timers always tick
        if (flashMillisRemaining > 0) {
            flashMillisRemaining -= deltaTimeMillis;
            if (flashMillisRemaining < 0) flashMillisRemaining = 0;
        }

        if (gameOver) return;

        // wave intro logic isolated here
        if (handleWaveIntro(deltaTimeMillis)) return;

        handleShooting(deltaTimeMillis);

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

        // Collisions and scoring are processed concurrently here
        handleCollisionsConcurrent();

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
     *
     * @param deltaTimeMillis time elapsed since last update in milliseconds
     * @return true if we are still in the intro and gameplay should pause.
     */
    private boolean handleWaveIntro(double deltaTimeMillis) {
        if (waveMillisRemaining > 0) {
            waveMillisRemaining -= deltaTimeMillis;
            if (waveMillisRemaining < 0) waveMillisRemaining = 0;
        }
        return waveMillisRemaining > 0;
    }

    /**
     * Processes all shot-enemy collisions using a thread pool.
     * <p>
     * This method:
     * <ol>
     *   <li>Takes snapshots of all currently alive shots and enemies.</li>
     *   <li>Submits a task per shot to the executor service.</li>
     *   <li>Each task checks that shot against all enemies and returns
     *       the first collision, if any.</li>
     *   <li>After all tasks complete, this method applies the results on
     *       the calling thread (JavaFX Application Thread): killing shots,
     *       damaging enemies and updating the score.</li>
     * </ol>
     * By separating "collision detection" (done concurrently) from "state
     * mutation" (done on a single thread), we remain thread-safe.
     */
    private void handleCollisionsConcurrent() {
        // Snapshot currently alive shots and enemies so worker threads only read
        List<Shot> liveShots = new ArrayList<>();
        for (Shot s : shots) {
            if (s.isAlive()) {
                liveShots.add(s);
            }
        }

        List<Enemy> liveEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                liveEnemies.add(e);
            }
        }

        if (liveShots.isEmpty() || liveEnemies.isEmpty()) {
            return;
        }

        try {
            // Create one task per shot
            List<Callable<CollisionResult>> tasks = new ArrayList<>(liveShots.size());

            for (Shot shot : liveShots) {
                tasks.add(() -> {
                    // Each worker reads from liveShots/liveEnemies only
                    for (Enemy enemy : liveEnemies) {
                        if (CollisionDetection.Aabb(shot, enemy)) {
                            // First hit is enough for this shot
                            return new CollisionResult(shot, enemy);
                        }
                    }
                    return null; // no collision for this shot
                });
            }

            // Invoke all tasks in parallel and wait for completion
            List<Future<CollisionResult>> futures = collisionExecutor.invokeAll(tasks);

            // Apply results on the calling thread (FX thread)
            for (Future<CollisionResult> future : futures) {
                CollisionResult result = future.get();
                if (result == null) {
                    continue;
                }

                Shot shot = result.shot();
                Enemy enemy = result.enemy();

                // Re-check "aliveness" in case this shot/enemy was already
                // processed by a previous collision in this same frame.
                if (!shot.isAlive() || !enemy.isAlive()) {
                    continue;
                }

                shot.kill();
                enemy.takeDamage(1);

                if (!enemy.isAlive()) {
                    score.updateScore(100);
                }
            }
        } catch (InterruptedException ie) {
            // Restore interrupt status and continue; next frame will handle again
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            // For robustness: log or handle unexpected exceptions here in a real project
            ex.printStackTrace();
        }
    }

    /**
     * Removes dead enemies and shots from their respective lists.
     * This is called once per frame after collision processing.
     */
    private void cleanUp() {
        enemies.removeIf(e -> !e.isAlive());
        shots.removeIf(s -> !s.isAlive());
    }

    // ---------------- WAVES / FORMATIONS / STRATEGIES ----------------

    /**
     * Spawns a new wave of enemies using a random formation and movement strategy.
     * Increases the {@code waveText} and starts a short wave-intro banner.
     */
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
                    enemySheets, enemyFrameCounts, movement, speedMultiplier,
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

    /**
     * Shuts down the internal executor service used for collision detection.
     * This should be called once when the game is being torn down, e.g. when
     * the application exits, to allow JVM shutdown without lingering threads.
     */
    public void shutdown() {
        collisionExecutor.shutdownNow();
    }
}
