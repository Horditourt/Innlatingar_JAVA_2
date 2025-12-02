package alienmarauders;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Holds all screens in one StackPane and toggles
 * visibility based on {@link SwitchModel}.
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
    private final Region loginMenuView;

    /**
     * Creates a new switch view builder with all application screens.
     *
     * @param model            the shared switch model
     * @param mainMenuView     the main menu view
     * @param settingsMenuView the settings menu view
     * @param chatMenuView     the chat menu view
     * @param gameView         the game view
     * @param loginMenuView    the login menu view
     */
    public SwitchViewBuilder(
            SwitchModel model,
            Region mainMenuView,
            Region settingsMenuView,
            Region chatMenuView,
            Region gameView,
            Region loginMenuView
    ) {
        this.model = model;
        this.mainMenuView = mainMenuView;
        this.settingsMenuView = settingsMenuView;
        this.chatMenuView = chatMenuView;
        this.gameView = gameView;
        this.loginMenuView = loginMenuView;
    }

    /**
     * Builds and returns the root StackPane that contains all screens.
     *
     * @return the root {@link Region} containing all views
     */
    public Region build() {
        StackPane root = new StackPane();

        // Only active view is visible + managed
        bindVisibleManaged(mainMenuView, model.mainMenuActive);
        bindVisibleManaged(settingsMenuView, model.settingsMenuActive);
        bindVisibleManaged(chatMenuView, model.chatMenuActive);
        bindVisibleManaged(gameView, model.gameActive);
        bindVisibleManaged(loginMenuView, model.loginMenuActive);

        // Force every view to fill the window
        for (Region r : new Region[]{mainMenuView, settingsMenuView, chatMenuView, gameView, loginMenuView}) {
            r.setMinSize(0, 0);
            r.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            r.prefWidthProperty().bind(root.widthProperty());
            r.prefHeightProperty().bind(root.heightProperty());
        }

        root.getChildren().addAll(mainMenuView, settingsMenuView, chatMenuView, gameView, loginMenuView);
        return root;
    }

    /**
     * Binds the visible and managed properties of a view to a BooleanProperty,
     * hiding and excluding it from layout when inactive.
     *
     * @param view   the view to bind
     * @param active the property indicating whether the view is active
     */
    private void bindVisibleManaged(Region view, javafx.beans.property.BooleanProperty active) {
        view.visibleProperty().bind(active);
        view.managedProperty().bind(active);
    }
}
