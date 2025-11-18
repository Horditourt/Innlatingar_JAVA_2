package alienmarauders.game;

import java.util.ArrayList;
import java.util.Iterator;
import alienmarauders.game.entities.*;

public class GameModel {

    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Shot> shots = new ArrayList<>();
    private Score score = new Score();

    // later: Formation and MovementStrategy references

    public GameModel(Player player) {
        this.player = player;
    }

    public Player getPlayer() { return player; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<Shot> getShots() { return shots; }
    public Score getScore() { return score; }

    public void addShot(Shot shot) { shots.add(shot); }

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
            for (Enemy e : enemies) {
                if (CollisionDetection.Aabb(s, e)) {
                    s.kill();
                    e.takeDamage(1);
                    if (!e.isAlive()) {
                        score.updateScore(100);
                    }
                }
            }
        }
        // also check player vs enemies for game over
    }

    private void cleanUp() {
        enemies.removeIf(e -> !e.isAlive());
        shots.removeIf(s -> !s.isAlive());
    }
}
