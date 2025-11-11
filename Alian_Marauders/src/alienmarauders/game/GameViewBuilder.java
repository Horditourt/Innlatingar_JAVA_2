package alienmarauders.game;

import alienmarauders.menu.chatmenu.ChatMenuModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameViewBuilder {

    private GameModel model;
    private Runnable goMain;
    private final VBox root = new VBox(10);

    public GameViewBuilder(GameModel model, Runnable goMain) {
        this.model = model;
        this.goMain = goMain;

    }

    public Region build() {
        Label title = new Label("Chat Menu");
        Button back = new Button("Back");
        back.setOnAction(e -> goMain.run());

        root.getChildren().addAll(title, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-background-color: transparent;");
        root.setPickOnBounds(false);
        return root;
    }
}
