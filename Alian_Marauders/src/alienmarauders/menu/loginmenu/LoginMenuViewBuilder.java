package alienmarauders.menu.loginmenu;

import alienmarauders.Styles;
import alienmarauders.SwitchModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds the login menu UI where the user can enter username, host and port.
 * <p>
 * The view exposes only value getters and event hooks (no control getters) to avoid
 * tight controller-view coupling.
 */
public class LoginMenuViewBuilder {

    private final BorderPane root = new BorderPane();
    private final SwitchModel switchModel;

    private final TextField usernameField = new TextField();
    private final TextField hostField = new TextField(String.valueOf("localhost"));
    private final TextField portField = new TextField(String.valueOf(8888));
    private final Button connectButton = new Button("Connect");
    private final Button cancelButton = new Button("Main menu");

    private Runnable onConnect;
    private Runnable onCancel;

    private boolean built = false;

    /**
     * Creates a new login menu view builder.
     *
     * @param switchModel global switch model (used for background binding)
     */
    public LoginMenuViewBuilder(SwitchModel switchModel) {
        this.switchModel = switchModel;
    }

    /**
     * Sets the callback invoked when the user presses "Connect".
     *
     * @param action the action to run
     */
    public void setOnConnect(Runnable action) {
        this.onConnect = action;
    }

    /**
     * Sets the callback invoked when the user presses "Main menu".
     *
     * @param action the action to run
     */
    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    /**
     * Returns the username input value.
     *
     * @return the username string (may be empty)
     */
    public String getUsername() {
        return usernameField.getText();
    }

    /**
     * Returns the host input value.
     *
     * @return the host string (may be empty)
     */
    public String getHost() {
        return hostField.getText();
    }

    /**
     * Returns the port input value as text.
     *
     * @return the port string (may be empty)
     */
    public String getPortText() {
        return portField.getText();
    }

    /**
     * Builds (once) and returns the root region of the login menu.
     *
     * @return the root {@link Region} containing the login UI
     */
    public Region build() {
        if (built) {
            return root;
        }
        built = true;

        Label title = new Label("Chat Login");

        usernameField.setPromptText("Username");
        hostField.setPromptText("Server address (e.g. localhost)");
        portField.setPromptText("Port (e.g. 8888)");

        connectButton.setOnAction(e -> runIfSet(onConnect));
        cancelButton.setOnAction(e -> runIfSet(onCancel));

        VBox centerBox = new VBox(8, usernameField, hostField, portField);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        HBox buttons = new HBox(10, connectButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        VBox mainBox = new VBox(10, title, centerBox, buttons);
        mainBox.setAlignment(Pos.CENTER);

        root.setCenter(mainBox);
        root.setPickOnBounds(false);

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
