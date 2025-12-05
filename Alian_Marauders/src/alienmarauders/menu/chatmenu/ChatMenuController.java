package alienmarauders.menu.chatmenu;

import alienmarauders.SwitchModel;
import alienmarauders.networking.ChatClient;
import alienmarauders.networking.Message;
import javafx.scene.layout.Region;

import java.io.IOException;

/**
 * Controller for the chat menu.
 * <p>
 * Bridges between the chat GUI and the {@link ChatClient} networking layer.
 */
public class ChatMenuController implements ChatClient.ChatListener {

    private final ChatMenuModel model = new ChatMenuModel();
    private final SwitchModel switchModel;
    private final ChatMenuViewBuilder view;

    private ChatClient client;
    private String username;

    /**
     * Creates a new controller for the chat menu.
     *
     * @param switchModel the global switch model used to swap between menus
     */
    public ChatMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.view = new ChatMenuViewBuilder(
                model,
                switchModel,
                this::onBackToMain,
                this::onSendChat
        );
    }

    /**
     * Returns the UI view associated with this controller.
     *
     * @return a {@link Region} representing the chat screen
     */
    public Region getView() {
        return view.build();
    }

    /**
     * Attaches a {@link ChatClient} to this controller.
     * <p>
     * This must be called after the client has been created and connected in
     * the login menu, so that incoming messages can update the chat view.
     *
     * @param client the connected chat client instance to use
     */
    public void attachClient(ChatClient client) {
        this.client = client;
        this.username = client.getUsername();
        model.setSelfUsername(username);
    }

    /**
     * Handles navigation back to the main menu.
     */
    private void onBackToMain() {
        switchModel.chatMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    /**
     * Handles the "Send" action from the view.
     * <p>
     * This method sends the message to the server via the attached {@link ChatClient}.
     * The actual message display is handled when the server broadcasts the message
     * back to all clients, including the sender.
     *
     * @param text the chat text to send
     */
    private void onSendChat(String text) {
        if (text == null || text.isBlank()) {
            return;
        }

        if (client != null) {
            try {
                client.sendChat(text);
            } catch (IOException e) {
                view.appendSystemMessage("*** Failed to send message: " + e.getMessage() + " ***");
            }
        } else {
            view.appendSystemMessage("*** Not connected to server ***");
        }
    }

    // ===== ChatClient.ChatListener implementation =====

    @Override
    public void onChatMessage(Message message) {
        String from = message.getFrom();
        String text = message.getText();
        boolean isSelf = (username != null && username.equals(from));
        view.appendChatMessage(from, text, isSelf);
    }

    @Override
    public void onUserList(Message message) {
        model.setUsers(message.getUsers());
    }

    @Override
    public void onUserJoined(Message message) {
        String user = message.getFrom();
        model.addUser(user);
        view.appendSystemMessage("* " + user + " joined the chat *");
    }

    @Override
    public void onUserLeft(Message message) {
        String user = message.getFrom();
        model.removeUser(user);
        view.appendSystemMessage("* " + user + " left the chat *");
    }

    @Override
    public void onConnectionClosed(Exception cause) {
        if (cause != null) {
            view.appendSystemMessage("*** Disconnected from server: " + cause.getMessage() + " ***");
        } else {
            view.appendSystemMessage("*** Disconnected from server ***");
        }
    }

    @Override
    public void onLoginRejected(Message message) {
        // Login is handled in LoginMenuController, so this should not be called.
        // We implement it only to satisfy the interface.
    }
}
