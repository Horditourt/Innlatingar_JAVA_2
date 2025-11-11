package alienmarauders;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class SwitchViewBuilder {
    private SwitchModel model;
    private Region mainMenuView;
    private Region settingsMenuView;
    private Region chatMenuView;
    private Region gameView;

    public SwitchViewBuilder(
            SwitchModel model,
            Region mainMenuView,
            Region settingsMenuView,
            Region chatMenuView,
            Region gameView) {

        this.model = model;
        this.mainMenuView = mainMenuView;
        this.settingsMenuView = settingsMenuView;
        this.chatMenuView = chatMenuView;
        this.gameView = gameView;
    }

    public Region build() {
        StackPane stackPane = new StackPane();
        mainMenuView.visibleProperty().bind(model.mainMenuActive);
        settingsMenuView.visibleProperty().bind(model.settingsMenuActive);
        chatMenuView.visibleProperty().bind(model.chatMenuActive);
        gameView.visibleProperty().bind(model.gameActive);

        /* mainMenuView.managedProperty().bind(mainMenuView.visibleProperty());
        settingsMenuView.managedProperty().bind(settingsMenuView.visibleProperty());
        chatMenuView.managedProperty().bind(chatMenuView.visibleProperty());
        gameView.managedProperty().bind(gameView.visibleProperty()); */

        stackPane.getChildren().addAll(mainMenuView, settingsMenuView, chatMenuView, gameView);
        return stackPane;
    }
}
