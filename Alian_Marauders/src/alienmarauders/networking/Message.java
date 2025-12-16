package alienmarauders.networking;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Immutable chat protocol message exchanged between client and server.
 * <p>
 * This class is deliberately kept as a pure data container (DTO). Factory
 * helpers are provided in {@link MessageFactory}.
 */
public final class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private final String from;
    private final String text;
    private final List<String> users;
    private final Instant timestamp;

    /**
     * Creates a new immutable message instance.
     *
     * @param type      the message type
     * @param from      the username of the sender, or {@code null} when not applicable
     * @param text      the text body of the message, or {@code null} when not applicable
     * @param users     the list of usernames (for {@link MessageType#USER_LIST}), or {@code null}
     * @param timestamp the timestamp of the message, or {@code null} to use {@link Instant#now()}
     */
    public Message(MessageType type, String from, String text, List<String> users, Instant timestamp) {
        this.type = type;
        this.from = from;
        this.text = text;
        this.users = users;
        this.timestamp = (timestamp != null) ? timestamp : Instant.now();
    }

    /**
     * Returns the message type.
     *
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the sending username, if applicable.
     *
     * @return the sender username or {@code null}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the message body text, if applicable.
     *
     * @return the text or {@code null}
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the server-provided user list, if applicable.
     *
     * @return the user list or {@code null}
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * Returns the message timestamp.
     *
     * @return the timestamp (never {@code null})
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", from='" + from + '\'' +
                ", text='" + text + '\'' +
                ", users=" + users +
                ", timestamp=" + timestamp +
                '}';
    }
}
