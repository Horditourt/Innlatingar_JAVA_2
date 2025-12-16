package alienmarauders.menu.loginmenu;

import alienmarauders.SwitchModel;
import alienmarauders.menu.chatmenu.ChatMenuController;
import alienmarauders.networking.ChatClient;
import alienmarauders.networking.Message;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

import java.io.IOException;

/**
 * Controller responsible for handling login events from the login menu UI.
 * <p>
 * The controller does not access JavaFX controls directly. It uses value getters and event hooks
 * exposed by {@link LoginMenuViewBuilder}.
 */
public class LoginMenuController {

    private final SwitchModel switchModel;
    private final LoginMenuViewBuilder viewBuilder;
    private final Region root;
    private final ChatMenuController chatMenuController;

    private final Runnable goMainMenu;

    /**
     * Creates a new login menu controller (matches existing SwitchController wiring).
     *
     * @param switchModel        global switch model
     * @param chatMenuController controller used for the chat menu (to attach the client)
     */
    public LoginMenuController(SwitchModel switchModel, ChatMenuController chatMenuController) {
        this(switchModel, chatMenuController, () -> {});
    }

    /**
     * Creates a new login menu controller.
     *
     * @param switchModel        global switch model
     * @param chatMenuController controller used for the chat menu (to attach the client)
     * @param goMainMenu         optional callback after switching state back to main menu
     */
    public LoginMenuController(SwitchModel switchModel,
                               ChatMenuController chatMenuController,
                               Runnable goMainMenu) {
        this.switchModel = switchModel;
        this.chatMenuController = chatMenuController;
        this.goMainMenu = (goMainMenu != null) ? goMainMenu : () -> {};

        this.viewBuilder = new LoginMenuViewBuilder(switchModel);
        this.viewBuilder.setOnConnect(this::handleConnect);
        this.viewBuilder.setOnCancel(this::goBackToMainMenu);

        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)
    }

    /**
     * Returns the UI view for this controller.
     *
     * @return the cached login menu root view
     */
    public Region getView() {
        return root;
    }

    /**
     * Reads user input via view value getters and attempts to connect/login to the chat server.
     * Behavior is identical to the previous version.
     */
    private void handleConnect() {
        String username = safeTrim(viewBuilder.getUsername());
        String host = safeTrim(viewBuilder.getHost());
        String portText = safeTrim(viewBuilder.getPortText());

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

        ChatClient client = new ChatClient(host, port, username);

        LoginPhaseListener loginListener = new LoginPhaseListener(
                client,
                message -> loginSuccessful(client, message),
                message -> {
                    String reason = (message != null) ? message.getText() : null;
                    if (reason == null || reason.isBlank()) {
                        reason = "Login rejected by server.";
                    }
                    showError(reason);
                },
                cause -> {
                    String msg = (cause != null)
                            ? "Disconnected: " + cause.getMessage()
                            : "Disconnected from server.";
                    showError(msg);
                }
        );

        client.setListener(loginListener);

        try {
            client.connect();
        } catch (IOException e) {
            showError("Could not connect: " + e.getMessage());
        }
    }

    /**
     * Switches from login back to main menu and runs the optional callback.
     */
    private void goBackToMainMenu() {
        switchModel.loginMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
        goMainMenu.run();
    }

    /**
     * Called when login is successful (signaled by receiving the first USER_LIST).
     * <p>
     * Attaches the client to the chat menu and forwards the initial USER_LIST so the chat screen
     * initializes immediately (identical to previous behavior).
     *
     * @param client          the connected client
     * @param initialUserList the first USER_LIST message received during login
     */
    private void loginSuccessful(ChatClient client, Message initialUserList) {
        // IMPORTANT: attach client AND forward the first USER_LIST to chat menu
        chatMenuController.attachClient(client, initialUserList);

        switchModel.loginMenuActive.set(false);
        switchModel.chatMenuActive.set(true);
    }

    /**
     * Shows an error dialog with a consistent title.
     *
     * @param message the error message to show
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Trims a string safely (null becomes empty string).
     *
     * @param s input string
     * @return trimmed string or empty string if null
     */
    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}
