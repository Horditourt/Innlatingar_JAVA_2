package alienmarauders.menu.mainmenu;

import alienmarauders.Controller;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class MainMenuViewBuilder {
    private final VBox root = new VBox(12);

    public MainMenuViewBuilder(Controller controller) {
        Button startGame = new Button("Start Game");
        Button chat = new Button("Chat");
        Button settings = new Button("Settings");
        Button Exit = new Button("Exit");

        startGame.setOnAction(e -> controller.goGame());
        chat.setOnAction(e -> controller.goChat());
        settings.setOnAction(e -> controller.goSettings());
        Exit.setOnAction(e -> Platform.exit());

        root.getChildren().addAll(startGame, chat, settings, Exit);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);
        root.setStyle("-fx-padding: 20; -fx-background-color: transparent;");
    }

    public Region getRoot() { return root; }
}
