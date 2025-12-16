package alienmarauders.networking;

/**
 * Enumerates the types of {@link Message} exchanged between chat client and server.
 */
public enum MessageType {

    /** A login request from a client containing the desired username. */
    LOGIN,

    /** A regular chat message sent from a user to everyone. */
    CHAT,

    /** A message from the server containing the full list of online users. */
    USER_LIST,

    /** A notification that a new user has joined the chat. */
    USER_JOINED,

    /** A notification that a user has left the chat. */
    USER_LEFT,

    /** A notification that a login attempt was rejected by the server. */
    LOGIN_REJECTED
}
