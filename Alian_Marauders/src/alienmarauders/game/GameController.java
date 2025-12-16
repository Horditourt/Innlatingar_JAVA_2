package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.game.entities.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

/**
 * Controller for the game screen.
 * <p>
 * Owns the update loop and coordinates state transitions via {@link SwitchModel}.
 * The controller does not access JavaFX controls directly.
 */
public class GameController {

    private final GameModel model;
    private final SwitchModel switchModel;
    private final GameViewBuilder viewBuilder;
    private final Region root;

    private AnimationTimer gameLoop;
    private boolean firstFrame = true;
    private long lastNanoTime;

    /**
     * Creates a new game controller and initializes the game model and view.
     *
     * @param switchModel the global switch model used to swap between screens
     */
    public GameController(SwitchModel switchModel) {
        this.switchModel = switchModel;

        Image playerImage = new Image("/alienmarauders/images/Player.png");
        Player player = new Player(200.0, 300.0, 60.0, 60.0, playerImage);

        this.model = new GameModel(player);

        this.viewBuilder = new GameViewBuilder(model);
        this.viewBuilder.setOnBack(this::onBackToMain);
        this.viewBuilder.setOnRestart(this::onRestartGame);
        this.viewBuilder.withSwitchModel(switchModel);

        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)

        initializeGameLoop();
    }

    /**
     * Switches from the game back to the main menu and stops the game loop.
     */
    private void onBackToMain() {
        switchModel.gameActive.set(false);
        switchModel.mainMenuActive.set(true);
        stopGameLoop();
    }

    /**
     * Restarts the game loop and re-focuses the canvas after the UI updates.
     * Behavior is identical to the previous version.
     */
    private void onRestartGame() {
        // stop if already stopped/running (safe)
        stopGameLoop();
        startGameLoopAuto();

        // make sure canvas keeps focus after restart
        Platform.runLater(viewBuilder::requestCanvasFocus);
    }

    /**
     * Starts the game loop and resets the model using the provided play-area size.
     *
     * @param w play area width in pixels
     * @param h play area height in pixels
     */
    public void startGameLoop(double w, double h) {
        model.reset(w, h);
        firstFrame = true;
        gameLoop.start();
    }

    /**
     * Starts the game loop using the current play-area size from the view.
     * <p>
     * Runs after the current layout pass so the canvas size is correct.
     */
    public void startGameLoopAuto() {
        Platform.runLater(() -> {
            double w = viewBuilder.getPlayWidth();
            double h = viewBuilder.getPlayHeight();

            // fallback just in case
            if (w <= 0 || h <= 0) {
                w = 800;
                h = 600;
            }

            startGameLoop(w, h);
        });
    }

    /**
     * Stops the game loop animation timer if it is currently running.
     * This does not shut down background resources in the model, so the
     * game can be started again later.
     */
    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Shuts down the game controller and its underlying model.
     * <p>
     * This method:
     * <ul>
     *   <li>Stops the game loop animation timer, if running.</li>
     *   <li>Delegates to {@link GameModel#shutdown()} to terminate
     *       the internal executor service used for concurrent collision
     *       detection.</li>
     * </ul>
     * It should be called once when the application is being closed, and
     * the controller will no longer be used afterwards.
     */
    public void shutdown() {
        stopGameLoop();
        model.shutdown();
    }

    /**
     * Returns the UI view associated with this controller.
     *
     * @return the cached root view
     */
    public Region getView() {
        return root;
    }

    /**
     * Initializes the AnimationTimer game loop used to update and render the game.
     * Behavior is identical to the previous version.
     */
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
                viewBuilder.render();

                // check for game over
                if (model.isGameOver() && !model.isFlashRed()) {
                    Platform.runLater(GameController.this::stopGameLoop);
                }
            }
        };
    }
}
