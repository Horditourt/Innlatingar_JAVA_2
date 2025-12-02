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
                view.appendMessage("*** Failed to send message: " + e.getMessage() + " ***");
            }
        } else {
            view.appendMessage("*** Not connected to server ***");
        }
    }


    // ===== ChatClient.ChatListener implementation =====

    /**
     * Called when a new chat message is received from the server.
     *
     * @param message the received chat {@link Message}
     */
    @Override
    public void onChatMessage(Message message) {
        String from = message.getFrom() == null ? "Unknown" : message.getFrom();
        String text = message.getText() == null ? "" : message.getText();
        view.appendMessage(from + ": " + text);
    }

    /**
     * Called when the server sends the complete list of current users.
     *
     * @param message the {@link Message} containing the user list
     */
    @Override
    public void onUserList(Message message) {
        model.setUsers(message.getUsers());
    }

    /**
     * Called when a user joins the chat.
     *
     * @param message the {@link Message} describing the joining user
     */
    @Override
    public void onUserJoined(Message message) {
        String username = message.getFrom();
        model.addUser(username);
        view.appendMessage("* " + username + " joined the chat *");
    }

    /**
     * Called when a user leaves the chat.
     *
     * @param message the {@link Message} describing the departing user
     */
    @Override
    public void onUserLeft(Message message) {
        String username = message.getFrom();
        model.removeUser(username);
        view.appendMessage("* " + username + " left the chat *");
    }

    /**
     * Called when the connection to the server is closed or lost.
     *
     * @param cause the underlying exception, or {@code null} if closed normally
     */
    @Override
    public void onConnectionClosed(Exception cause) {
        if (cause != null) {
            view.appendMessage("*** Disconnected from server: " + cause.getMessage() + " ***");
        } else {
            view.appendMessage("*** Disconnected from server ***");
        }
    }
}
