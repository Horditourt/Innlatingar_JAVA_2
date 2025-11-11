package alienmarauders.menu.mainmenu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class MainMenuViewBuilder {
    private final VBox root = new VBox(12);
    private MainMenuModel model;
    private Runnable goGame;
    private Runnable goChat;
    private Runnable goSettings;
    private Runnable goExit;
    

    public MainMenuViewBuilder(MainMenuModel model, Runnable goGame, Runnable goChat, Runnable goSettings,
            Runnable goExit) {
        this.model = model;
        this.goGame = goGame;
        this.goChat = goChat;
        this.goSettings = goSettings;
        this.goExit = goExit;

    }

    public Region build() {
        Button startGame = new Button("Start Game");
        Button chat = new Button("Chat");
        Button settings = new Button("Settings");
        Button Exit = new Button("Exit");

        startGame.setOnAction(e -> goGame.run());
        chat.setOnAction(e -> goChat.run());
        settings.setOnAction(e -> goSettings.run());
        Exit.setOnAction(e -> goExit.run());

        root.getChildren().addAll(startGame, chat, settings, Exit);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);
        root.setStyle("-fx-padding: 20; -fx-background-color: transparent;");
        return root;
    }
}
