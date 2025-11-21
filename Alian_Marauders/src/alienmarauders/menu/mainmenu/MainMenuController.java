// src/alienmarauders/menu/mainmenu/MainMenuController.java
package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import alienmarauders.game.GameController;
import javafx.application.Platform;
import javafx.scene.layout.Region;

public class MainMenuController {
    private final MainMenuModel model = new MainMenuModel();
    private final SwitchModel switchModel;
    private final GameController gameController;
    private final MainMenuViewBuilder view;

    public MainMenuController(SwitchModel switchModel, GameController gameController) {
        this.switchModel = switchModel;
        this.gameController = gameController;

        this.view = new MainMenuViewBuilder(
                model, switchModel,
                this::onStartGame,
                this::onChat,
                this::onSettings,
                Platform::exit);
    }

    private void onStartGame() {
        switchModel.mainMenuActive.set(false);
        switchModel.gameActive.set(true);
        gameController.startGameLoop(800, 600);

    }

    private void onChat() {
        switchModel.mainMenuActive.set(false);
        switchModel.chatMenuActive.set(true);
    }

    private void onSettings() {
        switchModel.mainMenuActive.set(false);
        switchModel.settingsMenuActive.set(true);
    }

    public Region getView() {
        return view.build();
    }
}
