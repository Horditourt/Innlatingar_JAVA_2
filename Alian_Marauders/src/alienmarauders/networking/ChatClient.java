package alienmarauders.networking;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client-side networking helper for the Alien Marauders chat.
 * <p>
 * Connects to the chat server, sends {@link Message} instances and listens
 * for incoming messages in a background thread.
 */
public class ChatClient {

    /**
     * Listener interface used to deliver events from the networking layer
     * to the JavaFX UI / model layer.
     */
    public interface ChatListener {

        /**
         * Called when a new chat message has been received from the server.
         *
         * @param message the received chat {@link Message}
         */
        void onChatMessage(Message message);

        /**
         * Called when the server sends a complete list of all currently online users.
         *
         * @param message the {@link Message} containing the user list
         */
        void onUserList(Message message);

        /**
         * Called when a user joins the chat.
         *
         * @param message the {@link Message} describing the joining user
         */
        void onUserJoined(Message message);

        /**
         * Called when a user leaves the chat.
         *
         * @param message the {@link Message} describing the departing user
         */
        void onUserLeft(Message message);

        /**
         * Called when the connection is closed or lost.
         *
         * @param cause the exception that caused the disconnection, or {@code null}
         */
        void onConnectionClosed(Exception cause);
    }

    private final String host;
    private final int port;
    private final String username;
    private final ChatListener listener;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private volatile boolean running;

    /**
     * Creates a new chat client.
     *
     * @param host     the server hostname or IP address
     * @param port     the server TCP port
     * @param username the username to use when logging in
     * @param listener the {@link ChatListener} that will receive events
     */
    public ChatClient(String host, int port, String username, ChatListener listener) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.listener = listener;
    }

    /**
     * Connects to the chat server and sends the initial login message.
     * <p>
     * This method performs blocking I/O and should not be called on the JavaFX
     * application thread.
     *
     * @throws IOException if the connection could not be established
     */
    public void connect() throws IOException {
        socket = new Socket(host, port);

        // Same stream ordering as server
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        // Send LOGIN
        sendMessage(Message.login(username));

        running = true;
        listenerThread = new Thread(this::listenLoop, "ChatClient-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Sends a plain text chat message to the server.
     *
     * @param text the chat message text
     * @throws IOException if sending fails
     */
    public void sendChat(String text) throws IOException {
        if (text == null || text.isBlank()) {
            return;
        }
        sendMessage(Message.chat(username, text));
    }

    /**
     * Disconnects from the server and stops the listener thread.
     */
    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Internal helper to send a {@link Message} to the server.
     *
     * @param message the {@link Message} to send
     * @throws IOException if an I/O error occurs
     */
    private void sendMessage(Message message) throws IOException {
        if (out == null || message == null) {
            return;
        }
        synchronized (out) {
            out.writeObject(message);
            out.flush();
        }
    }

    /**
     * Background loop that continuously listens for incoming messages from the server.
     * <p>
     * Every received {@link Message} is dispatched to the configured {@link ChatListener}
     * on the JavaFX application thread using {@link Platform#runLater(Runnable)}.
     */
    private void listenLoop() {
        Exception closeCause = null;
        try {
            while (running && !socket.isClosed()) {
                Object obj = in.readObject();
                if (!(obj instanceof Message message)) {
                    continue;
                }

                // Ensure UI updates happen on JavaFX thread
                Platform.runLater(() -> dispatchMessage(message));
            }
        } catch (Exception e) {
            closeCause = e;
        } finally {
            running = false;
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }

            if (listener != null) {
                Exception finalCause = closeCause;
                Platform.runLater(() -> listener.onConnectionClosed(finalCause));
            }
        }
    }

    /**
     * Dispatches a received {@link Message} to the appropriate listener callback.
     *
     * @param message the message to dispatch
     */
    private void dispatchMessage(Message message) {
        if (listener == null || message == null) {
            return;
        }

        switch (message.getType()) {
            case CHAT -> listener.onChatMessage(message);
            case USER_LIST -> listener.onUserList(message);
            case USER_JOINED -> listener.onUserJoined(message);
            case USER_LEFT -> listener.onUserLeft(message);
            default -> {
                // LOGIN from server side should not normally occur here
            }
        }
    }
}
