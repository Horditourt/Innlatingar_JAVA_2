package alienmarauders.networking;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Represents a message sent between chat clients and the server.
 * <p>
 * Messages are transferred over an {@link java.io.ObjectInputStream} and
 * {@link java.io.ObjectOutputStream}, so this class must be
 * {@link Serializable}.
 */
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The type of a {@link Message}. Determines how the server and clients
     * should interpret the payload.
     */
    public enum Type {
        /**
         * A login request from a client containing the desired username.
         */
        LOGIN,

        /**
         * A regular chat message sent from a user to everyone.
         */
        CHAT,

        /**
         * A message from the server containing the full list of online users.
         */
        USER_LIST,

        /**
         * A notification that a new user has joined the chat.
         */
        USER_JOINED,

        /**
         * A notification that a user has left the chat (logged out / disconnected).
         */
        USER_LEFT
    }

    private final Type type;
    private final String from;
    private final String text;
    private final List<String> users;
    private final Instant timestamp;

    /**
     * Creates a new immutable message instance.
     *
     * @param type      the message type
     * @param from      the username of the sender, or {@code null} when not
     *                  applicable
     * @param text      the text body of the message, or {@code null} when not
     *                  applicable
     * @param users     the list of usernames (for {@link Type#USER_LIST}), may be
     *                  {@code null}
     * @param timestamp the time the message was created
     */
    public Message(Type type, String from, String text, List<String> users, Instant timestamp) {
        this.type = type;
        this.from = from;
        this.text = text;
        this.users = (users == null) ? null : List.copyOf(users);
        this.timestamp = timestamp;
    }

    /**
     * Creates a login message for the given username.
     *
     * @param username the username that wishes to log in
     * @return a {@link Message} of type {@link Type#LOGIN}
     */
    public static Message login(String username) {
        return new Message(Type.LOGIN, username, null, null, Instant.now());
    }

    /**
     * Creates a regular chat message.
     *
     * @param from the sender username
     * @param text the chat text
     * @return a {@link Message} of type {@link Type#CHAT}
     */
    public static Message chat(String from, String text) {
        return new Message(Type.CHAT, from, text, null, Instant.now());
    }

    /**
     * Creates a message containing the full list of all online users.
     *
     * @param users the list of usernames currently online
     * @return a {@link Message} of type {@link Type#USER_LIST}
     */
    public static Message userList(List<String> users) {
        return new Message(Type.USER_LIST, null, null, users, Instant.now());
    }

    /**
     * Creates a notification message for when a user joins.
     *
     * @param username the username that joined
     * @return a {@link Message} of type {@link Type#USER_JOINED}
     */
    public static Message userJoined(String username) {
        return new Message(Type.USER_JOINED, username, null, null, Instant.now());
    }

    /**
     * Creates a notification message for when a user leaves.
     *
     * @param username the username that left
     * @return a {@link Message} of type {@link Type#USER_LEFT}
     */
    public static Message userLeft(String username) {
        return new Message(Type.USER_LEFT, username, null, null, Instant.now());
    }

    /**
     * Returns the type of this message.
     *
     * @return the {@link Type}
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the sender username, if applicable.
     *
     * @return the sender username, or {@code null}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the text body of the message, if applicable.
     *
     * @return the message text, or {@code null}
     */
    public String getText() {
        return text;
    }

    /**
     * Returns an unmodifiable view of the user list stored in this message.
     *
     * @return the user list, or {@code null} if not a {@link Type#USER_LIST}
     *         message
     */
    public List<String> getUsers() {
        return users == null ? null : Collections.unmodifiableList(users);
    }

    /**
     * Returns the timestamp when this message was created.
     *
     * @return the {@link Instant} creation time
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}
