package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class MainMenuViewBuilder {
    private final VBox root = new VBox(12);
    //private final MainMenuModel model;
    private final Runnable goGame, goChat, goSettings, goExit;
    private final SwitchModel switchModel;

    public MainMenuViewBuilder(
            MainMenuModel model,
            SwitchModel switchModel,
            Runnable goGame,
            Runnable goChat,
            Runnable goSettings,
            Runnable goExit
    ) {
        //this.model = model;
        this.switchModel = switchModel;
        this.goGame = goGame;
        this.goChat = goChat;
        this.goSettings = goSettings;
        this.goExit = goExit;
    }

    public Region build() {
        Label title = new Label("Alien Marauders");
        Button startGame = new Button("Start game");
        Button chat = new Button("Chat");
        Button settings = new Button("Settings");
        Button exit = new Button("Exit");

        startGame.setOnAction(e -> goGame.run());
        chat.setOnAction(e -> goChat.run());
        settings.setOnAction(e -> goSettings.run());
        exit.setOnAction(e -> goExit.run());

        root.getChildren().addAll(title, startGame, chat, settings, exit);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        // Background bound to model
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }
}
