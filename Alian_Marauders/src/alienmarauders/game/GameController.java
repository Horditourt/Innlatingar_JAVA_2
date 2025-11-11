// src/alienmarauders/game/GameController.java
package alienmarauders.game;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class GameController {
    private final GameModel model = new GameModel();
    private final SwitchModel switchModel;
    private final GameViewBuilder view;

    public GameController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new GameViewBuilder(model, this::onBackToMain).withSwitchModel(switchModel);
    }

    private void onBackToMain() {
        switchModel.gameActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    public Region getView() { return view.build(); }
}
