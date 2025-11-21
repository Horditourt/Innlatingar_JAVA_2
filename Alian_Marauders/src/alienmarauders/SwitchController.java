package alienmarauders;

import alienmarauders.menu.mainmenu.MainMenuController;
import alienmarauders.menu.settingsmenu.SettingsMenuController;
import javafx.scene.layout.Region;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.game.GameController;

public class SwitchController {
    private final SwitchModel model;
    private final GameController gameController;
    private final SwitchViewBuilder view;

    public SwitchController() {
        this.model = new SwitchModel();
        this.gameController = new GameController(model);
        this.view = new SwitchViewBuilder(
                model,
                new MainMenuController(model, gameController).getView(),
                new SettingsMenuController(model).getView(),
                new ChatMenuController(model).getView(),
                gameController.getView());

    }

    public Region getView() {
        return view.build();
    }

}
