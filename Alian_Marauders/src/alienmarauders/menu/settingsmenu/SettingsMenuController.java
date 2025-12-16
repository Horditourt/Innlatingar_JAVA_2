package alienmarauders.menu.settingsmenu;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

/**
 * Controller for the settings menu.
 * <p>
 * Handles navigation events and delegates UI construction to the view builder.
 * The controller does not access JavaFX controls directly.
 */
public class SettingsMenuController {

    private final SettingsMenuModel model = new SettingsMenuModel();
    private final SwitchModel switchModel;

    private final SettingsMenuViewBuilder viewBuilder;
    private final Region root;

    /**
     * Creates a new settings menu controller.
     *
     * @param switchModel the global switch model used to swap between screens
     */
    public SettingsMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;

        this.viewBuilder = new SettingsMenuViewBuilder(model, switchModel);
        this.viewBuilder.setOnBackToMain(this::onBackToMain);

        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)
    }

    /**
     * Switches from the settings menu back to the main menu.
     */
    private void onBackToMain() {
        switchModel.settingsMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    /**
     * Returns the UI view associated with this controller.
     *
     * @return the cached root view
     */
    public Region getView() {
        return root;
    }
}
