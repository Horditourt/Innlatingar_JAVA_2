package alienmarauders.game.formation;

import java.util.ArrayList;
import alienmarauders.game.entities.Enemy;

public interface Formation {
    void createEnemies();
    ArrayList<Enemy> getEnemies();
}
