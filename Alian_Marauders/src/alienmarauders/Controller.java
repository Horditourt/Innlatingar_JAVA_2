package alienmarauders;

import alienmarauders.menu.chatmenu.ChatMenuViewBuilder;
import alienmarauders.menu.settingsmenu.SettingsMenuViewBuilder;
import alienmarauders.menu.mainmenu.MainMenuViewBuilder;
import alienmarauders.game.Game; // Your game view (rename if needed)

import javafx.scene.Scene;
import javafx.scene.layout.Region;

public class Controller {
    private final Model model;
    private final ViewBuilder view;

    // Keep references to the view roots
    private final Region mainMenuRoot;
    private final Region chatMenuRoot;
    private final Region settingsMenuRoot;
    private final Region gameRoot;

    public Controller(Model model, ViewBuilder view,
                      MainMenuViewBuilder mainMenu,
                      ChatMenuViewBuilder chatMenu,
                      SettingsMenuViewBuilder settingsMenu,
                      Game game) {
        this.model = model;
        this.view = view;

        this.mainMenuRoot = mainMenu.getRoot();
        this.chatMenuRoot = chatMenu.getRoot();
        this.settingsMenuRoot = settingsMenu.getRoot();
        this.gameRoot = game.getRoot();
    }

    public void initialize(Scene scene) {
        // Put all screens into the content layer (order doesn’t matter)
        view.getContentLayer().getChildren().setAll(
                mainMenuRoot, chatMenuRoot, settingsMenuRoot, gameRoot
        );

        // Hide/show by binding visible -> model flags
        // Also bind managed to visible so hidden nodes don't affect layout
        bindVisibility(mainMenuRoot,
                model.chatMenuVisibleProperty().not()
                     .and(model.settingsMenuVisibleProperty().not())
                     .and(model.gameVisibleProperty().not()));

        bindVisibility(chatMenuRoot,     model.chatMenuVisibleProperty());
        bindVisibility(settingsMenuRoot, model.settingsMenuVisibleProperty());
        bindVisibility(gameRoot,         model.gameVisibleProperty());
    }

    private void bindVisibility(Region node, javafx.beans.value.ObservableValue<Boolean> isVisible) {
        node.visibleProperty().bind(isVisible);
        node.managedProperty().bind(node.visibleProperty());
    }

    // Expose actions (wire to buttons inside each view)
    public void goMain()     { model.showMain(); }
    public void goChat()     { model.showChat(); }
    public void goSettings() { model.showSettings(); }
    public void goGame()     { model.showGame(); }
}
