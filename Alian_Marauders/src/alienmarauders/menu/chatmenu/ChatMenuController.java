package alienmarauders.menu.chatmenu;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class ChatMenuController {

    private ChatMenuModel model;
    private SwitchModel switchModel;
    private ChatMenuViewBuilder view;

    public ChatMenuController(SwitchModel switchModel

    ) {
        this.model = new ChatMenuModel();
        this.switchModel = switchModel;
        this.view = new ChatMenuViewBuilder(model, 
            () -> onBackToMain()
        );
    }

    private void onBackToMain() {
    switchModel.chatMenuActive.set(false);
    switchModel.mainMenuActive.set(true);
}


    public Region getView() {
        return view.build();
    }

}
