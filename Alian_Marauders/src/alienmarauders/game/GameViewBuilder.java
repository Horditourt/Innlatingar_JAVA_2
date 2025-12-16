package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import alienmarauders.game.entities.Enemy;
import alienmarauders.game.entities.Player;
import alienmarauders.game.entities.Shot;
import alienmarauders.game.graphics.AnimationContainer;
import alienmarauders.game.graphics.Animatable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Builds and renders the game view.
 * <p>
 * The view owns JavaFX nodes (canvas, labels, buttons) and exposes only:
 * <ul>
 *     <li>event hooks (setOnBack, setOnRestart)</li>
 *     <li>value getters (getPlayWidth, getPlayHeight)</li>
 *     <li>small view actions (requestCanvasFocus, render)</li>
 * </ul>
 * This keeps controllers from touching JavaFX controls directly.
 */
public class GameViewBuilder {

    private final GameModel model;

    private Runnable onBack;
    private Runnable onRestart;
    private SwitchModel switchModel;

    private Canvas canvas;
    private GraphicsContext gc;
    private Label gameOverLabel;
    private Label waveLabel;

    private StackPane root;
    private boolean built = false;

    /**
     * Creates a new game view builder.
     *
     * @param model the game model to render
     */
    public GameViewBuilder(GameModel model) {
        this.model = model;
    }

    /**
     * Sets the callback invoked when the user presses "Back".
     *
     * @param action the action to run
     */
    public void setOnBack(Runnable action) {
        this.onBack = action;
    }

    /**
     * Sets the callback invoked when the user triggers a restart (ENTER on game over).
     *
     * @param action the action to run
     */
    public void setOnRestart(Runnable action) {
        this.onRestart = action;
    }

    /**
     * Applies the global {@link SwitchModel} for background binding.
     *
     * @param switchModel the switch model used for background selection
     * @return this builder (for chaining)
     */
    public GameViewBuilder withSwitchModel(SwitchModel switchModel) {
        this.switchModel = switchModel;
        return this;
    }

    /**
     * Builds (once) and returns the root region of the game view.
     *
     * @return the root {@link Region} containing the game UI
     */
    public Region build() {
        if (built) {
            return root;
        }
        built = true;

        // initial size; listeners will keep it in sync after layout
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        Button back = new Button("Back");
        back.setOnAction(e -> runIfSet(onBack));

        root = new StackPane();
        root.setMinSize(0, 0);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        gameOverLabel = new Label();
        gameOverLabel.setVisible(false);
        gameOverLabel.managedProperty().bind(gameOverLabel.visibleProperty());
        gameOverLabel.setStyle(Styles.gameOverStyle());
        StackPane.setAlignment(gameOverLabel, Pos.CENTER);

        waveLabel = new Label();
        waveLabel.setVisible(false);
        waveLabel.managedProperty().bind(waveLabel.visibleProperty());
        waveLabel.setStyle(Styles.waveBannerStyle());
        StackPane.setAlignment(waveLabel, Pos.TOP_CENTER);

        root.getChildren().addAll(canvas, back, gameOverLabel, waveLabel);
        StackPane.setAlignment(back, Pos.BOTTOM_RIGHT);

        // Focus & key bindings on the model's player
        Player player = model.getPlayer();
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        player.initializeKeyBindings(
                canvas,
                () -> model.setShooting(true),
                () -> model.setShooting(false)
        );

        canvas.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER && model.isGameOver() && onRestart != null) {
                onRestart.run();
                e.consume();
            }
        });

        // Resize behavior: canvas fills root, and player bounds update accordingly
        root.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            if (w <= 0) {
                return;
            }
            canvas.setWidth(w);
            model.getPlayer().setBounds(canvas.getWidth(), canvas.getHeight());
        });

        root.heightProperty().addListener((obs, oldH, newH) -> {
            double h = newH.doubleValue();
            if (h <= 0) {
                return;
            }
            canvas.setHeight(h);
            model.getPlayer().setBounds(canvas.getWidth(), canvas.getHeight());
        });

        // Optional: background style from settings
        if (switchModel != null) {
            root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        }

        root.setPickOnBounds(false);
        return root;
    }

    /**
     * Renders the current model state to the canvas.
     * Behavior is identical to the previous version.
     */
    public void render() {
        if (gc == null || canvas == null) {
            return;
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        model.getPlayer().render(gc);
        model.getScore().render(gc);

        // hide enemies + shots during wave intro
        if (!model.isWaveBannerActive()) {
            for (Enemy e : model.getEnemies()) {
                e.render(gc);
            }
            for (Shot s : model.getShots()) {
                s.render(gc);
            }
        }

        // Render short-lived animations (e.g. explosions) on top
        AnimationContainer<Animatable> animationContainer = model.getAnimations();
        if (animationContainer != null && !animationContainer.isEmpty()) {
            animationContainer.renderAnimations(gc);
        }

        // flash overlay
        if (model.isFlashRed()) {
            gc.setFill(Color.rgb(255, 0, 0, 0.55));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // wave overlay
        if (model.isWaveBannerActive()) {
            waveLabel.setText(model.getWaveText());
            waveLabel.setVisible(true);
        } else {
            waveLabel.setVisible(false);
        }

        // game over overlay
        if (model.isGameOver()) {
            gameOverLabel.setText("GAME OVER\nPress Back");
            gameOverLabel.setVisible(true);
        } else {
            gameOverLabel.setVisible(false);
        }
    }

    /**
     * Requests focus on the canvas so key input continues working (e.g. after restart).
     */
    public void requestCanvasFocus() {
        if (canvas != null) {
            canvas.requestFocus();
        }
    }

    /**
     * Returns the current play-area width as reported by the canvas.
     *
     * @return canvas width if valid, otherwise a safe default (800)
     */
    public double getPlayWidth() {
        return (canvas != null && canvas.getWidth() > 0) ? canvas.getWidth() : 800;
    }

    /**
     * Returns the current play-area height as reported by the canvas.
     *
     * @return canvas height if valid, otherwise a safe default (600)
     */
    public double getPlayHeight() {
        return (canvas != null && canvas.getHeight() > 0) ? canvas.getHeight() : 600;
    }

    /**
     * Runs the given action if it is non-null.
     *
     * @param action the runnable to execute
     */
    private void runIfSet(Runnable action) {
        if (action != null) {
            action.run();
        }
    }
}
