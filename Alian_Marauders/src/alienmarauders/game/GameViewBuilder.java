package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.game.entities.Player;
import alienmarauders.Styles;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameViewBuilder {
    private final GameModel model;
    private final Runnable goMain;
    private Canvas canvas;
    private GraphicsContext gc;
    private SwitchModel switchModel;

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
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Player player = new Player(400, 500, 30, 30);
        player.render(gc);
        player.initializeKeyBindings(canvas, gc);
        root.getChildren().addAll(canvas, title, back);
        root.setAlignment(Pos.BOTTOM_RIGHT);
        root.setPickOnBounds(false);

        if (switchModel != null) {
            root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        }
        return root;
    }
}
