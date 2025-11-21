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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class GameViewBuilder {

    private final GameModel model;
    private final Runnable onBack;
    private SwitchModel switchModel;

    private Canvas canvas;
    private GraphicsContext gc;

    public GameViewBuilder(GameModel model, Runnable onBack) {
        this.model = model;
        this.onBack = onBack;
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

        root.getChildren().addAll(canvas, back);

        StackPane.setAlignment(back, Pos.BOTTOM_RIGHT);

        // 🔹 Focus & key bindings on the *model's* player
        Player player = model.getPlayer();

        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        player.initializeKeyBindings(canvas, () -> model.playerShoot());

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
        if (model.getEnemies().isEmpty()) {
            double w = canvas.getWidth();
            double h = canvas.getHeight();

            // if size isn't ready yet, fall back to something sane
            if (w <= 0 || h <= 0) {
                w = 800;
                h = 600;
            }

            model.initEnemyFormation(w, h);
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // draw entities
        model.getPlayer().render(gc);
        model.getScore().render(gc);
        for (Enemy e : model.getEnemies()) {
            e.render(gc);
        }
        for (Shot s : model.getShots()) {
            s.render(gc);
        }
    }
}
