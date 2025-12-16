package alienmarauders.networking;

/**
 * Listener interface for receiving chat events from {@link ChatClient}.
 * <p>
 * Uses default methods so implementers only override what they need
 * (supports the Interface Segregation Principle in a lightweight way).
 */
public interface ChatClientListener {

    /**
     * Called when a new chat message has been received from the server.
     *
     * @param message the received message
     */
    default void onChatMessage(Message message) {}

    /**
     * Called when the server sends the full user list.
     *
     * @param message the received message (type {@link MessageType#USER_LIST})
     */
    default void onUserList(Message message) {}

    /**
     * Called when the server reports that a user joined.
     *
     * @param message the received message (type {@link MessageType#USER_JOINED})
     */
    default void onUserJoined(Message message) {}

    /**
     * Called when the server reports that a user left.
     *
     * @param message the received message (type {@link MessageType#USER_LEFT})
     */
    default void onUserLeft(Message message) {}

    /**
     * Called when the connection closes (cleanly or due to an error).
     *
     * @param cause the exception cause, or {@code null} if closed normally
     */
    default void onConnectionClosed(Exception cause) {}

    /**
     * Called when a login attempt is rejected.
     *
     * @param message the received message (type {@link MessageType#LOGIN_REJECTED})
     */
    default void onLoginRejected(Message message) {}
}
