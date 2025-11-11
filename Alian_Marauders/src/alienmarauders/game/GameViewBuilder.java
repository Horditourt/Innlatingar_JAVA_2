package alienmarauders.game;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameViewBuilder {
    private final GameModel model;
    private final Runnable goMain;
    private final VBox root = new VBox(10);
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
        Label title = new Label("Game (placeholder)");
        Button back = new Button("Main menu");
        back.setOnAction(e -> goMain.run());

        root.getChildren().addAll(title, back);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        if (switchModel != null) {
            root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        }
        return root;
    }
}
