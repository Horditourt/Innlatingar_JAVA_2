package alienmarauders.networking;

import java.time.Instant;
import java.util.List;

/**
 * Factory helpers for creating common {@link Message} instances.
 * <p>
 * Keeps {@link Message} as a simple immutable DTO (no mixed responsibilities).
 */
public final class MessageFactory {

    private MessageFactory() {
        // Utility class
    }

    /**
     * Creates a login request message.
     *
     * @param username desired username
     * @return a {@link MessageType#LOGIN} message
     */
    public static Message login(String username) {
        return new Message(MessageType.LOGIN, username, null, null, Instant.now());
    }

    /**
     * Creates a chat message.
     *
     * @param from sender username
     * @param text message text
     * @return a {@link MessageType#CHAT} message
     */
    public static Message chat(String from, String text) {
        return new Message(MessageType.CHAT, from, text, null, Instant.now());
    }

    /**
     * Creates a user list message.
     *
     * @param users list of current online users
     * @return a {@link MessageType#USER_LIST} message
     */
    public static Message userList(List<String> users) {
        return new Message(MessageType.USER_LIST, null, null, users, Instant.now());
    }

    /**
     * Creates a user joined notification message.
     *
     * @param username user that joined
     * @return a {@link MessageType#USER_JOINED} message
     */
    public static Message userJoined(String username) {
        return new Message(MessageType.USER_JOINED, username, null, null, Instant.now());
    }

    /**
     * Creates a user left notification message.
     *
     * @param username user that left
     * @return a {@link MessageType#USER_LEFT} message
     */
    public static Message userLeft(String username) {
        return new Message(MessageType.USER_LEFT, username, null, null, Instant.now());
    }

    /**
     * Creates a login rejected message.
     *
     * @param reason rejection reason
     * @return a {@link MessageType#LOGIN_REJECTED} message
     */
    public static Message loginRejected(String reason) {
        return new Message(MessageType.LOGIN_REJECTED, null, reason, null, Instant.now());
    }
}
