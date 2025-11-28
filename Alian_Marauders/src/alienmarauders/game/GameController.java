// src/alienmarauders/game/GameController.java
package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.game.entities.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.application.Platform;

public class GameController {
    private final GameModel model;
    private final SwitchModel switchModel;
    private final GameViewBuilder view;

    private AnimationTimer gameLoop;
    private boolean firstFrame = true;
    private long lastNanoTime;

    public GameController(SwitchModel switchModel) {
        this.switchModel = switchModel;

        Image playerImage = new Image("/alienmarauders/images/Player.png");
        Player player = new Player(200.0, 300.0, 60.0, 60.0, playerImage);

        this.model = new GameModel(player);
        this.view = new GameViewBuilder(
                model,
                this::onBackToMain,
                this::onRestartGame).withSwitchModel(switchModel);

        initializeGameLoop();

    }

    private void onBackToMain() {
        switchModel.gameActive.set(false);
        switchModel.mainMenuActive.set(true);
        stopGameLoop();
    }

    private void onRestartGame() {
        // stop if already stopped/running (safe)
        stopGameLoop();
        startGameLoopAuto();

        // make sure canvas keeps focus after restart
        Platform.runLater(() -> view.requestCanvasFocus());
    }

    public void startGameLoop(double w, double h) {
        model.reset(w, h);
        firstFrame = true;
        gameLoop.start();
    }

    public void startGameLoopAuto() {
        // Run after the current layout pass so canvas size is correct
        Platform.runLater(() -> {
            double w = view.getPlayWidth();
            double h = view.getPlayHeight();

            // fallback just in case
            if (w <= 0 || h <= 0) {
                w = 800;
                h = 600;
            }

            startGameLoop(w, h);
        });
    }

    public void stopGameLoop() {
        gameLoop.stop();
    }

    public Region getView() {
        return view.build();
    }

    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                if (firstFrame) {
                    lastNanoTime = currentNanoTime;
                    firstFrame = false;
                    return;
                }
                long elapsedNanos = currentNanoTime - lastNanoTime;
                lastNanoTime = currentNanoTime;
                double deltaMillis = elapsedNanos / 1_000_000.0;

                // update model
                model.update(deltaMillis);

                // render
                view.render(model);

                // check for game over
                if (model.isGameOver() && !model.isFlashRed()) {
                    Platform.runLater(() -> stopGameLoop());
                }
            }
        };
    }
}
