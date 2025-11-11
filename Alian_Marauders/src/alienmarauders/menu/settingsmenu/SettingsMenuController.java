package alienmarauders.menu.settingsmenu;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class SettingsMenuController {
    private final SettingsMenuModel model = new SettingsMenuModel();
    private final SwitchModel switchModel;
    private final SettingsMenuViewBuilder view;

    public SettingsMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new SettingsMenuViewBuilder(model, switchModel, this::onBackToMain);
    }

    private void onBackToMain() {
        switchModel.settingsMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    public Region getView() {
        return view.build();
    }
}
