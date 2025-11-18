// src/alienmarauders/game/GameController.java
package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.game.entities.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Region;

public class GameController {
    private final GameModel model = new GameModel();
    private final SwitchModel switchModel;
    private final GameViewBuilder view;

    public GameController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new GameViewBuilder(model, this::onBackToMain).withSwitchModel(switchModel);
    }

    private void onBackToMain() {
        switchModel.gameActive.set(false);
        switchModel.mainMenuActive.set(true);
        stopGameLoop();
    }

    public static void startGameLoop() {
        gameLoop.start();
    }

    public static void stopGameLoop() {
        gameLoop.stop();
    }

    public Region getView() { return view.build(); }

    static AnimationTimer gameLoop = new AnimationTimer() {
        private boolean firstFrame = true;
        long lastNanoTime = 0;
        @Override
        public void handle(long currentNanoTime) {
            if (firstFrame) {
                lastNanoTime = currentNanoTime;
                firstFrame = false;
            }
            long timeElapsedMilli = (currentNanoTime - lastNanoTime) / 1_000_000;
            lastNanoTime = currentNanoTime;
            // Update the model with the time elapsed since last frame
            //model.update(timeElapsedMilli);
            System.out.println("Game loop tick: " + timeElapsedMilli + " ms");

            

            //Collosion detection and response would go here

            // remove entities that are marked for removal
            // (e.g., enemies that have been destroyed)

            //check if we need to end the game

            //else continue the game and spawn new enemies as needed

            //rendering would go here
        }
    };
    
}
