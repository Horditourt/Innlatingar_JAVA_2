package alienmarauders;

import alienmarauders.menu.mainmenu.MainMenuController;
import alienmarauders.menu.settingsmenu.SettingsMenuController;
import javafx.scene.layout.Region;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.game.GameController;

public class SwitchController {
    private final SwitchModel model;
    private final SwitchViewBuilder view;

    public SwitchController() {
        this.model = new SwitchModel();
        this.view = new SwitchViewBuilder(model, 
            new MainMenuController(model).getView(),
            new SettingsMenuController(model).getView(),
            new ChatMenuController(model).getView(),
            new GameController(model).getView()
        );
        
    }

    public Region getView() {
        return view.build();
    }
    
}
