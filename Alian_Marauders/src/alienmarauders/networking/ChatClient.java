package alienmarauders.networking;

import javafx.application.Platform;

import java.io.Closeable;
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
public class ChatClient implements Closeable {

    private final String host;
    private final int port;
    private final String username;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private volatile boolean running;

    private ChatClientListener listener;

    /**
     * Creates a new chat client that will connect to the given host/port and
     * attempt to login with the provided username.
     *
     * @param host     server host name or IP
     * @param port     server port
     * @param username desired username
     */
    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    /**
     * Sets the listener for receiving callbacks from this client.
     *
     * @param listener the listener, or {@code null} to disable callbacks
     */
    public void setListener(ChatClientListener listener) {
        this.listener = listener;
    }

    /**
     * Returns the username associated with this client.
     *
     * @return the username used when logging in
     */
    public String getUsername() {
        return username;
    }

    /**
     * Connects to the server and starts the background receive loop.
     *
     * @throws IOException if the connection could not be established
     */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Send login request immediately
        send(MessageFactory.login(username));

        running = true;
        Thread receiver = new Thread(this::receiveLoop, "ChatClient-Receiver");
        receiver.setDaemon(true);
        receiver.start();
    }

    /**
     * Sends a chat message to the server.
     *
     * @param text the chat text to send
     * @throws IOException if writing to the socket fails
     */
    public void sendChat(String text) throws IOException {
        send(MessageFactory.chat(username, text));
    }

    /**
     * Sends a raw protocol message to the server.
     *
     * @param message the message to send
     * @throws IOException if writing to the socket fails
     */
    public synchronized void send(Message message) throws IOException {
        if (out == null) {
            throw new IOException("Not connected");
        }
        out.writeObject(message);
        out.flush();
    }

    private void receiveLoop() {
        Exception closeCause = null;
        try {
            while (running) {
                Object obj = in.readObject();
                if (!(obj instanceof Message message)) {
                    continue;
                }
                dispatch(message);
            }
        } catch (Exception ex) {
            closeCause = ex;
        } finally {
            running = false;
            try {
                close();
            } catch (IOException ignored) {
            }
            fireConnectionClosed(closeCause);
        }
    }

    private void dispatch(Message message) {
        ChatClientListener l = this.listener;
        if (l == null) {
            return;
        }

        // Always deliver on JavaFX thread
        Platform.runLater(() -> {
            switch (message.getType()) {
                case CHAT -> l.onChatMessage(message);
                case USER_LIST -> l.onUserList(message);
                case USER_JOINED -> l.onUserJoined(message);
                case USER_LEFT -> l.onUserLeft(message);
                case LOGIN_REJECTED -> l.onLoginRejected(message);
                default -> {
                    // ignore
                }
            }
        });
    }

    private void fireConnectionClosed(Exception cause) {
        ChatClientListener l = this.listener;
        if (l == null) {
            return;
        }
        Platform.runLater(() -> l.onConnectionClosed(cause));
    }

    /**
     * Closes the connection and stops the receive loop.
     *
     * @throws IOException if closing resources fails
     */
    @Override
    public void close() throws IOException {
        running = false;

        IOException first = null;

        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                first = ex;
            } finally {
                in = null;
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                if (first == null) first = ex;
            } finally {
                out = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                if (first == null) first = ex;
            } finally {
                socket = null;
            }
        }

        if (first != null) {
            throw first;
        }
    }
}
