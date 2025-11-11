package alienmarauders.menu.chatmenu;

import alienmarauders.SwitchModel;
import javafx.scene.layout.Region;

public class ChatMenuController {
    private final ChatMenuModel model = new ChatMenuModel();
    private final SwitchModel switchModel;
    private final ChatMenuViewBuilder view;

    public ChatMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new ChatMenuViewBuilder(model, switchModel, this::onBackToMain);
    }

    private void onBackToMain() {
        switchModel.chatMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    public Region getView() { return view.build(); }
}
