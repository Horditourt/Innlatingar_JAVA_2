package alienmarauders;

import alienmarauders.game.GameController;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.menu.loginmenu.LoginMenuController;
import alienmarauders.menu.mainmenu.MainMenuController;
import alienmarauders.menu.settingsmenu.SettingsMenuController;
import javafx.scene.layout.Region;

/**
 * Top-level controller that wires together the different menu controllers
 * and provides the root view for the application.
 */
public class SwitchController {

    private final SwitchModel model;
    private final GameController gameController;
    private final MainMenuController mainMenuController;
    private final SettingsMenuController settingsMenuController;
    private final ChatMenuController chatMenuController;
    private final LoginMenuController loginMenuController;

    private final SwitchViewBuilder viewBuilder;
    private final Region root;

    /**
     * Creates the application controller and wires all screens into a single switch view.
     */
    public SwitchController() {
        this.model = new SwitchModel();
        this.gameController = new GameController(model);

        this.mainMenuController = new MainMenuController(model, gameController);
        this.settingsMenuController = new SettingsMenuController(model);
        this.chatMenuController = new ChatMenuController(model);
        this.loginMenuController = new LoginMenuController(model, chatMenuController);

        this.viewBuilder = new SwitchViewBuilder(
                model,
                mainMenuController.getView(),
                settingsMenuController.getView(),
                chatMenuController.getView(),
                gameController.getView(),
                loginMenuController.getView()
        );

        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)
    }

    /**
     * Returns the root view for the application.
     *
     * @return the cached root view
     */
    public Region getView() {
        return root;
    }

    /**
     * Returns the single {@link GameController} instance used by this
     * switch controller.
     *
     * @return the game controller managing the Alien Marauders game
     */
    public GameController getGameController() {
        return gameController;
    }
}
