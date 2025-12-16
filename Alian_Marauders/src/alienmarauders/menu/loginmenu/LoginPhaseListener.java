package alienmarauders.menu.loginmenu;

import alienmarauders.networking.ChatClient;
import alienmarauders.networking.ChatClientListener;
import alienmarauders.networking.Message;
import javafx.application.Platform;

import java.util.function.Consumer;

/**
 * Listener used during the login phase.
 * <p>
 * This is a top-level class (no nested types) to comply with the refactor rule.
 * It forwards:
 * <ul>
 *     <li>the first {@code USER_LIST} as "login success"</li>
 *     <li>login rejection</li>
 *     <li>connection closed</li>
 * </ul>
 * All callbacks are invoked via {@link Platform#runLater(Runnable)} to ensure JavaFX thread safety.
 */
public class LoginPhaseListener implements ChatClientListener {

    private final Consumer<Message> onFirstUserList;
    private final Consumer<Message> onLoginRejected;
    private final Consumer<Exception> onConnectionClosed;

    private boolean loginCompleted = false;

    /**
     * Creates a login-phase listener.
     *
     * @param client             the chat client associated with this login attempt
     * @param onFirstUserList    callback invoked for the first USER_LIST (signals success)
     * @param onLoginRejected    callback invoked if the server rejects login
     * @param onConnectionClosed callback invoked if the connection closes during login
     */
    public LoginPhaseListener(ChatClient client,
                              Consumer<Message> onFirstUserList,
                              Consumer<Message> onLoginRejected,
                              Consumer<Exception> onConnectionClosed) {
        this.onFirstUserList = onFirstUserList;
        this.onLoginRejected = onLoginRejected;
        this.onConnectionClosed = onConnectionClosed;
    }

    @Override
    public void onUserList(Message message) {
        if (loginCompleted) {
            return;
        }
        loginCompleted = true;

        // Forward this first USER_LIST into the chat screen so it initializes immediately
        Platform.runLater(() -> {
            if (onFirstUserList != null) {
                onFirstUserList.accept(message);
            }
        });
    }

    @Override
    public void onLoginRejected(Message message) {
        if (loginCompleted) {
            return;
        }
        loginCompleted = true;

        Platform.runLater(() -> {
            if (onLoginRejected != null) {
                onLoginRejected.accept(message);
            }
        });
    }

    @Override
    public void onConnectionClosed(Exception cause) {
        if (loginCompleted) {
            return;
        }
        loginCompleted = true;

        Platform.runLater(() -> {
            if (onConnectionClosed != null) {
                onConnectionClosed.accept(cause);
            }
        });
    }
}
