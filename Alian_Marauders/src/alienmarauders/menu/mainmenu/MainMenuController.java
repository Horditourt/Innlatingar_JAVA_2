// src/alienmarauders/menu/mainmenu/MainMenuController.java
package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import javafx.application.Platform;
import javafx.scene.layout.Region;

public class MainMenuController {
    private final MainMenuModel model = new MainMenuModel();
    private final SwitchModel switchModel;
    private final MainMenuViewBuilder view;

    public MainMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new MainMenuViewBuilder(
            model, switchModel,
            this::onStartGame,
            this::onChat,
            this::onSettings,
            Platform::exit
        );
    }

    private void onStartGame() {
        switchModel.mainMenuActive.set(false);
        switchModel.gameActive.set(true);
    }
    private void onChat() {
        switchModel.mainMenuActive.set(false);
        switchModel.chatMenuActive.set(true);
    }
    private void onSettings() {
        switchModel.mainMenuActive.set(false);
        switchModel.settingsMenuActive.set(true);
    }

    public Region getView() { return view.build(); }
}
