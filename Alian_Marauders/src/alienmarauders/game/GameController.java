package alienmarauders.game;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class GameController {

    private GameModel model;
    private SwitchModel switchModel;
    private GameViewBuilder view;

    public GameController(SwitchModel switchModel) {
        this.model = new GameModel();
        this.switchModel = switchModel;
        this.view = new GameViewBuilder(model,
            // e.g. back to main from game
            () -> onBackToMain()
        );
    }

    private void onBackToMain() {
        switchModel.gameActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    public Region getView() {
        return view.build();
    }
}
