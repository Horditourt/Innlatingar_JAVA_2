package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import javafx.application.Platform;
import javafx.scene.layout.Region;

public class MainMenuController {

    private MainMenuModel model;
    private SwitchModel switchModel;
    private MainMenuViewBuilder view;

    public MainMenuController(SwitchModel switchModel

    ) {
        this.model = new MainMenuModel();
        this.switchModel = switchModel;
        this.view = new MainMenuViewBuilder(model,
                () -> onStartGame(), // Make sure these methods exist, and do what they are supposed to do I.E.
                                     // switch views
                () -> onChat(),
                () -> onSettings(),
                () -> Platform.exit());
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

    public Region getView() {
        return view.build();
    }
}
