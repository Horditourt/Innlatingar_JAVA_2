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
 */
public class LoginMenuViewBuilder {

    private final BorderPane root = new BorderPane();
    private final SwitchModel switchModel;

    private final TextField usernameField = new TextField();
    private final TextField hostField = new TextField(String.valueOf("localhost"));
    private final TextField portField = new TextField(String.valueOf(8888));
    private final Button connectButton = new Button("Connect");
    private final Button cancelButton = new Button("Main menu");

    public LoginMenuViewBuilder(SwitchModel switchModel) {
        this.switchModel = switchModel;
    }

    /**
     * Builds and returns the root region of the login menu.
     *
     * @return the root {@link Region} containing the login UI
     */
    public Region build() {
        Label title = new Label("Chat Login");

        usernameField.setPromptText("Username");
        hostField.setPromptText("Server address (e.g. localhost)");
        portField.setPromptText("Port (e.g. 8888)");

        VBox centerBox = new VBox(8, usernameField, hostField, portField);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        HBox buttons = new HBox(10, connectButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        VBox mainBox = new VBox(10, title, centerBox, buttons);
        mainBox.setAlignment(Pos.CENTER);

        root.setCenter(mainBox);

        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }

    /**
     * Returns the text field used for username input.
     *
     * @return the username {@link TextField}
     */
    public TextField getUsernameField() {
        return usernameField;
    }

    /**
     * Returns the text field used for host input.
     *
     * @return the host {@link TextField}
     */
    public TextField getHostField() {
        return hostField;
    }

    /**
     * Returns the text field used for port input.
     *
     * @return the port {@link TextField}
     */
    public TextField getPortField() {
        return portField;
    }

    /**
     * Returns the button used to trigger a connection attempt.
     *
     * @return the connect {@link Button}
     */
    public Button getConnectButton() {
        return connectButton;
    }

    /**
     * Returns the button used to cancel login and go back to the main menu.
     *
     * @return the cancel {@link Button}
     */
    public Button getCancelButton() {
        return cancelButton;
    }
}
