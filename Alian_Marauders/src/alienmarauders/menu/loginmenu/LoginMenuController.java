package alienmarauders.menu.loginmenu;

import alienmarauders.SwitchModel;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.networking.ChatClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * Controller responsible for handling login events from the login menu UI.
 * <p>
 * This controller reads the username, host and port from the
 * {@link LoginMenuViewBuilder} fields, creates a {@link ChatClient}, and
 * connects it to the chat server. On success it switches to the chat menu.
 */
public class LoginMenuController {

    private final SwitchModel switchModel;
    private final ChatMenuController chatMenuController;
    private final LoginMenuViewBuilder view;

    /**
     * Creates a new controller for the login menu.
     *
     * @param switchModel        the global switch model used for menu navigation
     * @param chatMenuController the controller for the chat menu, which will
     *                           receive messages from the {@link ChatClient}
     */
    public LoginMenuController(SwitchModel switchModel,
                               ChatMenuController chatMenuController) {
        this.switchModel = switchModel;
        this.chatMenuController = chatMenuController;
        this.view = new LoginMenuViewBuilder();

        hookEvents();
    }

    /**
     * Returns the UI view associated with this controller.
     *
     * @return a {@link Region} representing the login screen
     */
    public Region getView() {
        return view.build();
    }

    /**
     * Wires the UI controls to their corresponding event handlers.
     */
    private void hookEvents() {
        view.getConnectButton().setOnAction(e -> handleConnect());
        view.getCancelButton().setOnAction(e -> goBackToMainMenu());
    }

    /**
     * Attempts to connect to the chat server using the values from the text fields.
     * <p>
     * This method performs the network connection in a background thread to avoid
     * blocking the JavaFX application thread. On success, it switches to the
     * chat menu. On failure, it shows an error alert.
     */
    private void handleConnect() {
        String username = view.getUsernameField().getText().trim();
        String host = view.getHostField().getText().trim();
        String portText = view.getPortField().getText().trim();

        if (username.isBlank() || host.isBlank() || portText.isBlank()) {
            showError("All fields must be filled in.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            showError("Port must be a number.");
            return;
        }

        // Run connect in background so UI does not freeze
        new Thread(() -> {
            try {
                // Use the chatMenuController as listener so it receives all events.
                ChatClient client = new ChatClient(host, port, username, chatMenuController);
                client.connect();

                // Attach client to chat controller and switch to chat menu on FX thread.
                Platform.runLater(() -> {
                    chatMenuController.attachClient(client);
                    switchToChatMenu();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(
                        () -> showError("Login failed: " + ex.getMessage())
                );
            }
        }, "Login-Connect-Thread").start();
    }

    /**
     * Switches from the login menu to the main menu without connecting.
     * <p>
     * This is used when the user presses the "Cancel" button.
     */
    private void goBackToMainMenu() {
        switchModel.loginMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    /**
     * Switches from the login menu to the chat menu.
     * <p>
     * Called after a successful connection to the server.
     */
    private void switchToChatMenu() {
        switchModel.loginMenuActive.set(false);
        switchModel.chatMenuActive.set(true);
    }

    /**
     * Shows an error message in an alert dialog.
     *
     * @param message the error text to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
