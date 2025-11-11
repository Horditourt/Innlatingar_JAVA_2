package alienmarauders.menu.settingsmenu;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class SettingsMenuController {

    private SettingsMenuModel model;
    private SwitchModel switchModel;
    private SettingsMenuViewBuilder view;

    public SettingsMenuController(SwitchModel switchModel

    ) {
        this.model = new SettingsMenuModel();
        this.switchModel = switchModel;
        this.view = new SettingsMenuViewBuilder(model, 
            () -> onStartGame()
        );
    }

    private void onStartGame() {
        switchModel.settingsMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    public Region getView() {
        return view.build();
    }
    
}
