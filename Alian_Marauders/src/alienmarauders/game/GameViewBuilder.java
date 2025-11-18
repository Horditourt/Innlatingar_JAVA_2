package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.game.entities.Enemy;
import alienmarauders.game.entities.Player;
import alienmarauders.game.entities.Shot;
import alienmarauders.Styles;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameViewBuilder {
    private final GameModel model;
    private final Runnable goMain;
    private Canvas canvas;
    private GraphicsContext gc;
    private SwitchModel switchModel;
    private Image backgroundImages;

    public GameViewBuilder(GameModel model, Runnable goMain) {
        this.model = model;
        this.goMain = goMain;
    }

    public GameViewBuilder withSwitchModel(SwitchModel switchModel) {
        this.switchModel = switchModel;
        return this;
    }

    public Region build() {
        StackPane root = new StackPane();
        Label title = new Label("Game (placeholder)");
        Button back = new Button("Main menu");
        back.setOnAction(e -> goMain.run());

        canvas = new Canvas(800, 600);
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        Player player = model.getPlayer();
        player.initializeKeyBindings(canvas);

        gc = canvas.getGraphicsContext2D();
        // backgroundImages = new
        // Image("alienmarauders/resources/images/backgrounds/space.png");
        // Player player = new Player(400, 500, 30, 30, backgroundImages);
        // player.render(gc);
        root.getChildren().add(canvas);
        root.getChildren().addAll(title, back);
        root.setAlignment(Pos.BOTTOM_RIGHT);
        root.setPickOnBounds(false);

        if (switchModel != null) {
            root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        }
        return root;
    }

    public void render(GameModel model) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // background if any
        model.getPlayer().render(gc);
        for (Enemy e : model.getEnemies()) {
            e.render(gc);
        }
        for (Shot s : model.getShots()) {
            s.render(gc);
        }
        model.getScore().render(gc);
    }
}
