package alienmarauders.menu.mainmenu;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds the Main Menu view.
 * <p>
 * The view exposes only event hooks (no control getters) to avoid tight coupling.
 * Background styling is bound to {@link SwitchModel#backgroundName}.
 */
public class MainMenuViewBuilder {

    private final VBox root = new VBox(12);
    private final SwitchModel switchModel;

    private Runnable onStartGame;
    private Runnable onChat;
    private Runnable onSettings;
    private Runnable onExit;

    private boolean built = false;

    /**
     * Creates a new Main Menu view builder.
     *
     * @param model      the main menu model (kept for consistency with the existing architecture)
     * @param switchModel global switch model used for background binding
     */
    public MainMenuViewBuilder(MainMenuModel model, SwitchModel switchModel) {
        this.switchModel = switchModel;
    }

    /**
     * Sets the callback invoked when the user presses "Start game".
     *
     * @param action the action to run
     */
    public void setOnStartGame(Runnable action) {
        this.onStartGame = action;
    }

    /**
     * Sets the callback invoked when the user presses "Chat".
     *
     * @param action the action to run
     */
    public void setOnChat(Runnable action) {
        this.onChat = action;
    }

    /**
     * Sets the callback invoked when the user presses "Settings".
     *
     * @param action the action to run
     */
    public void setOnSettings(Runnable action) {
        this.onSettings = action;
    }

    /**
     * Sets the callback invoked when the user presses "Exit".
     *
     * @param action the action to run
     */
    public void setOnExit(Runnable action) {
        this.onExit = action;
    }

    /**
     * Builds (once) and returns the root of the Main Menu.
     *
     * @return the root {@link Region}
     */
    public Region build() {
        if (built) {
            return root;
        }
        built = true;

        Label title = new Label("Alien Marauders");
        Button startGame = new Button("Start game");
        Button chat = new Button("Chat");
        Button settings = new Button("Settings");
        Button exit = new Button("Exit");

        startGame.setOnAction(e -> runIfSet(onStartGame));
        chat.setOnAction(e -> runIfSet(onChat));
        settings.setOnAction(e -> runIfSet(onSettings));
        exit.setOnAction(e -> runIfSet(onExit));

        root.getChildren().addAll(title, startGame, chat, settings, exit);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        // Background bound to model
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }

    /**
     * Runs the given action if it is non-null.
     *
     * @param action the runnable to execute
     */
    private void runIfSet(Runnable action) {
        if (action != null) {
            action.run();
        }
    }
}
