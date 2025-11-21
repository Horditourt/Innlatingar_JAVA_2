package alienmarauders;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Holds all screens in one StackPane and toggles
 * visibility based on SwitchModel.
 *
 * Each child Region is forced to fill the outer StackPane,
 * so resizing the window resizes the active screen.
 */
public class SwitchViewBuilder {

    private final SwitchModel model;
    private final Region mainMenuView;
    private final Region settingsMenuView;
    private final Region chatMenuView;
    private final Region gameView;

    public SwitchViewBuilder(
            SwitchModel model,
            Region mainMenuView,
            Region settingsMenuView,
            Region chatMenuView,
            Region gameView
    ) {
        this.model = model;
        this.mainMenuView = mainMenuView;
        this.settingsMenuView = settingsMenuView;
        this.chatMenuView = chatMenuView;
        this.gameView = gameView;
    }

    public Region build() {
        StackPane root = new StackPane();

        // Only active view is visible + managed
        bindVisibleManaged(mainMenuView, model.mainMenuActive);
        bindVisibleManaged(settingsMenuView, model.settingsMenuActive);
        bindVisibleManaged(chatMenuView, model.chatMenuActive);
        bindVisibleManaged(gameView, model.gameActive);

        // Force every view to fill the window
        for (Region r : new Region[]{mainMenuView, settingsMenuView, chatMenuView, gameView}) {
            r.setMinSize(0, 0);
            r.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            r.prefWidthProperty().bind(root.widthProperty());
            r.prefHeightProperty().bind(root.heightProperty());
        }

        root.getChildren().addAll(mainMenuView, settingsMenuView, chatMenuView, gameView);
        return root;
    }

    private void bindVisibleManaged(Region view, javafx.beans.property.BooleanProperty active) {
        view.visibleProperty().bind(active);
        view.managedProperty().bind(active);
    }
}
