package alienmarauders.menu.chatmenu;

import alienmarauders.SwitchModel;
import alienmarauders.networking.ChatClient;
import alienmarauders.networking.ChatClientListener;
import alienmarauders.networking.Message;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the chat menu.
 * <p>
 * Updates {@link ChatMenuModel} based on events from {@link ChatClient}.
 * The view observes the model (controller does not call view methods directly).
 */
public class ChatMenuController {

    private final ChatMenuModel model = new ChatMenuModel();
    private final SwitchModel switchModel;
    private final ChatMenuViewBuilder viewBuilder;
    private final Region root;

    private ChatClient client;
    private String username;

    /**
     * Creates a new controller for the chat menu.
     *
     * @param switchModel the global switch model used to swap between menus
     */
    public ChatMenuController(SwitchModel switchModel) {
        this.switchModel = switchModel;
        this.viewBuilder = new ChatMenuViewBuilder(
                model,
                switchModel,
                this::onBackToMain,
                this::onSendChat
        );
        this.root = viewBuilder.build(); // build once (no UI rebuild surprises)
    }

    /**
     * Returns the UI view associated with this controller.
     *
     * @return the cached root view
     */
    public Region getView() {
        return root;
    }

    /**
     * Attaches a {@link ChatClient} to this controller and wires a listener that
     * updates the model.
     *
     * @param client the connected chat client instance to use
     */
    public void attachClient(ChatClient client) {
        attachClient(client, null);
    }

    /**
     * Attaches a {@link ChatClient} to this controller and optionally applies an initial
     * {@code USER_LIST} message that was received during login.
     * <p>
     * This fixes the issue where the first USER_LIST is consumed by the login screen
     * and the chat screen would otherwise stay empty until another event happens.
     *
     * @param client          the connected chat client instance
     * @param initialUserList the first USER_LIST message received during login, or {@code null}
     */
    public void attachClient(ChatClient client, Message initialUserList) {
        this.client = client;
        this.username = client.getUsername();
        model.setSelfUsername(username);

        client.setListener(new ChatClientListener() {
            @Override
            public void onChatMessage(Message message) {
                model.addChatLine(ChatLine.chat(
                        message.getFrom(),
                        message.getText(),
                        isSelf(message.getFrom())
                ));
            }

            @Override
            public void onUserList(Message message) {
                applyUserList(message);
            }

            @Override
            public void onUserJoined(Message message) {
                String who = message.getFrom();
                model.addChatLine(ChatLine.system("*** " + who + " joined ***"));
            }

            @Override
            public void onUserLeft(Message message) {
                String who = message.getFrom();
                model.addChatLine(ChatLine.system("*** " + who + " left ***"));
            }

            @Override
            public void onLoginRejected(Message message) {
                String reason = (message != null) ? message.getText() : null;
                if (reason == null || reason.isBlank()) {
                    reason = "Login rejected";
                }
                model.addChatLine(ChatLine.system("*** " + reason + " ***"));
            }

            @Override
            public void onConnectionClosed(Exception cause) {
                String msg = (cause != null)
                        ? "*** Disconnected from server: " + cause.getMessage() + " ***"
                        : "*** Disconnected from server ***";
                model.addChatLine(ChatLine.system(msg));
            }

            private boolean isSelf(String from) {
                return from != null && from.equals(username);
            }
        });

        // Apply the initial USER_LIST immediately so the chat screen is initialized right away.
        if (initialUserList != null) {
            applyUserList(initialUserList);
        }
    }

    private void applyUserList(Message message) {
        List<String> users = message.getUsers();
        if (users != null) {
            model.setUsers(users);
        }

        // Only show "Connected as ..." once when the chat initializes.
        // If you want it every time the list updates, remove the guard.
        if (model.lines.isEmpty()) {
            model.addChatLine(ChatLine.system("*** Connected as " + username + " ***"));
        }
    }

    /**
     * Handles navigation back to the main menu.
     */
    private void onBackToMain() {
        switchModel.chatMenuActive.set(false);
        switchModel.mainMenuActive.set(true);
    }

    /**
     * Sends a chat message to the server based on the user's input.
     *
     * @param text the text entered by the user
     */
    private void onSendChat(String text) {
        if (client == null) {
            model.addChatLine(ChatLine.system("*** Not connected ***"));
            return;
        }
        if (text == null || text.isBlank()) {
            return;
        }

        try {
            client.sendChat(text);
        } catch (IOException e) {
            model.addChatLine(ChatLine.system("*** Failed to send: " + e.getMessage() + " ***"));
        }
    }
}
