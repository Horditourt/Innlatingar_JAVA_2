package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import alienmarauders.game.GameController;
import javafx.application.Platform;
import javafx.scene.layout.Region;

/**
 * Controller for the main menu.
 * <p>
 * Wires user intent from the view's event hooks into navigation and gameplay actions.
 * The controller does not access JavaFX controls directly.
 */
public class MainMenuController {

    private final MainMenuModel model = new MainMenuModel();
    private final SwitchModel switchModel;
    private final GameController gameController;

    private final MainMenuViewBuilder viewBuilder;
    private final Region root;

    /**
     * Creates a new main menu controller.
     *
     * @param switchModel     the global switch model used to swap between screens
     * @param gameController  the game controller used to start the game loop
     */
    public MainMenuController(SwitchModel switchModel, GameController gameController) {
        this.switchModel = switchModel;
        this.gameController = gameController;

        this.viewBuilder = new MainMenuViewBuilder(model, switchModel);
        this.viewBuilder.setOnStartGame(this::onStartGame);
        this.viewBuilder.setOnChat(this::onChat);
        this.viewBuilder.setOnSettings(this::onSettings);
        this.viewBuilder.setOnExit(Platform::exit);

        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)
    }

    /**
     * Handles starting the game and switching to the game screen.
     */
    private void onStartGame() {
        switchModel.mainMenuActive.set(false);
        switchModel.gameActive.set(true);
        gameController.startGameLoopAuto();
    }

    /**
     * Handles switching from main menu to login/chat flow.
     */
    private void onChat() {
        switchModel.mainMenuActive.set(false);
        switchModel.loginMenuActive.set(true);
    }

    /**
     * Handles switching from main menu to settings.
     */
    private void onSettings() {
        switchModel.mainMenuActive.set(false);
        switchModel.settingsMenuActive.set(true);
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
