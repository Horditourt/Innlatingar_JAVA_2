package alienmarauders.menu.loginmenu;

import alienmarauders.SwitchModel;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.networking.ChatClient;
import alienmarauders.networking.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * Controller responsible for handling login events from the login menu UI.
 * <p>
 * It creates a {@link ChatClient}, listens for the login result, and on
 * success hands the client off to {@link ChatMenuController}.
 */
public class LoginMenuController {

    private final SwitchModel switchModel;
    private final ChatMenuController chatMenuController;
    private final LoginMenuViewBuilder view;

    /**
     * Creates a new controller for the login menu.
     *
     * @param switchModel        the global switch model used for menu navigation
     * @param chatMenuController the controller for the chat menu
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
     * The actual login result is reported asynchronously via the
     * {@link LoginListener} inner class.
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

        new Thread(() -> {
            try {
                ChatClient client = new ChatClient(host, port, username, null);
                // Listen for login result first
                LoginListener loginListener = new LoginListener(client);
                client.setListener(loginListener);
                client.connect();
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
     * Called after a successful login handshake.
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

    /**
     * Temporary listener used only during the login phase.
     * <p>
     * Once the server accepts the login (indicated by the first USER_LIST
     * message), this listener hands the client to the {@link ChatMenuController}
     * and switches the visible screen to the chat menu.
     */
    private class LoginListener implements ChatClient.ChatListener {

        private final ChatClient client;
        private boolean loginCompleted = false;

        /**
         * Creates a new login listener for the given client.
         *
         * @param client the client whose login is being observed
         */
        LoginListener(ChatClient client) {
            this.client = client;
        }

        @Override
        public void onChatMessage(Message message) {
            // Ignored during login phase
        }

        @Override
        public void onUserList(Message message) {
            // First USER_LIST means login was accepted
            if (!loginCompleted) {
                loginCompleted = true;

                // Hand the client to the chat controller
                chatMenuController.attachClient(client);

                // Future messages should go directly to chat controller
                client.setListener(chatMenuController);

                // Forward this first user list to the chat controller
                chatMenuController.onUserList(message);

                switchToChatMenu();
            } else {
                // Should not really happen here, but forward just in case
                chatMenuController.onUserList(message);
            }
        }

        @Override
        public void onUserJoined(Message message) {
            // Normally we will not see this before onUserList,
            // but if we do, forward to chat controller after loginCompleted.
            if (loginCompleted) {
                chatMenuController.onUserJoined(message);
            }
        }

        @Override
        public void onUserLeft(Message message) {
            if (loginCompleted) {
                chatMenuController.onUserLeft(message);
            }
        }

        @Override
        public void onLoginRejected(Message message) {
            String reason = (message.getText() == null)
                    ? "Login rejected by server."
                    : message.getText();
            showError(reason);
            client.disconnect();
        }

        @Override
        public void onConnectionClosed(Exception cause) {
            if (!loginCompleted) {
                showError("Connection closed before login completed"
                        + (cause != null ? ": " + cause.getMessage() : "."));
            } else {
                // After login, the chat controller will receive future closes
                chatMenuController.onConnectionClosed(cause);
            }
        }
    }
}
