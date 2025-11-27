package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import alienmarauders.game.entities.Enemy;
import alienmarauders.game.entities.Player;
import alienmarauders.game.entities.Shot;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class GameViewBuilder {

    private final GameModel model;
    private final Runnable onBack;
    private final Runnable onRestart;
    private SwitchModel switchModel;

    private Canvas canvas;
    private GraphicsContext gc;
    private Label gameOverLabel;
    private Label waveLabel;

    public GameViewBuilder(GameModel model, Runnable onBack, Runnable onRestart) {
        this.model = model;
        this.onBack = onBack;
        this.onRestart = onRestart;
    }

    public GameViewBuilder withSwitchModel(SwitchModel switchModel) {
        this.switchModel = switchModel;
        return this;
    }

    public Region build() {
        // initial size; bindings will override after layout
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());

        StackPane root = new StackPane();

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
        root.getChildren().add(waveLabel);

        root.getChildren().addAll(canvas, back, gameOverLabel);

        StackPane.setAlignment(back, Pos.BOTTOM_RIGHT);

        // 🔹 Focus & key bindings on the *model's* player
        Player player = model.getPlayer();

        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        player.initializeKeyBindings(canvas, () -> model.playerShoot());

        canvas.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER
                    && model.isGameOver()
                    && onRestart != null) {
                onRestart.run();
                e.consume();
            }
        });

        // 🔹 Set bounds when canvas size becomes valid and on each resize
        root.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            if (w <= 0)
                return;

            canvas.setWidth(w);
            model.getPlayer().setBounds(canvas.getWidth(), canvas.getHeight());
        });

        root.heightProperty().addListener((obs, oldH, newH) -> {
            double h = newH.doubleValue();
            if (h <= 0)
                return;

            canvas.setHeight(h);
            model.getPlayer().setBounds(canvas.getWidth(), canvas.getHeight());
        });

        // Optional: background style from settings
        if (switchModel != null) {
            root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        }

        return root;
    }

    public void render(GameModel model) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        model.getPlayer().render(gc);
        model.getScore().render(gc);
        // hide enemies + shots during wave intro
        if (!model.isWaveBannerActive()) {
            for (Enemy e : model.getEnemies())
                e.render(gc);
            for (Shot s : model.getShots())
                s.render(gc);
        }

        // flash overlay
        if (model.isFlashRed()) {
            gc.setFill(javafx.scene.paint.Color.rgb(255, 0, 0, 0.55));
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

    public void requestCanvasFocus() {
        if (canvas != null) {
            canvas.requestFocus();
        }
    }

    public double getPlayWidth() {
        return (canvas != null && canvas.getWidth() > 0) ? canvas.getWidth() : 800;
    }

    public double getPlayHeight() {
        return (canvas != null && canvas.getHeight() > 0) ? canvas.getHeight() : 600;
    }

}
