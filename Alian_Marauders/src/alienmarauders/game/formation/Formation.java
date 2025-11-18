package alienmarauders.game.formation;

import java.util.ArrayList;

import alienmarauders.game.entities.Enemy;

public interface Formation {
    public void createEnemeies();
    public ArrayList<Enemy> getEnemies();
}
