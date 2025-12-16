package server;

import alienmarauders.networking.Message;
import alienmarauders.networking.MessageFactory;
import alienmarauders.networking.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple multi-client chat server.
 * <p>
 * Clients connect and send a {@link MessageType#LOGIN} first. If accepted, the server:
 * <ul>
 *     <li>broadcasts {@link MessageType#USER_JOINED}</li>
 *     <li>sends {@link MessageType#USER_LIST} to all clients</li>
 *     <li>relays {@link MessageType#CHAT} messages to everyone</li>
 * </ul>
 * <p>
 * Compatibility note:
 * This class supports both the "new" API {@code new Server(port).start()}
 * and the legacy API used by {@link ServerApp}: {@code new Server().start(8888)}.
 */
public class Server {

    /** Default port used by the assignment / legacy launcher. */
    public static final int DEFAULT_PORT = 8888;

    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    /**
     * Creates a server using {@link #DEFAULT_PORT}.
     * <p>
     * This exists for backwards compatibility with older code that expects
     * {@code new Server()} to compile.
     */
    public Server() {
        this(DEFAULT_PORT);
    }

    /**
     * Creates a server listening on a given port.
     *
     * @param port server port
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Legacy convenience method: starts a server on the given port.
     * <p>
     * This matches older call sites that do:
     * {@code new Server().start(8888);}
     *
     * @param port port to listen on
     */
    public void start(int port) {
        new Server(port).start();
    }

    /**
     * Starts the accept loop and handles clients until stopped.
     */
    public void start() {
        System.out.println("Server starting on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connecting from " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server stopped due to error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            running = false;
            for (ClientHandler client : clients) {
                client.closeQuietly();
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message message to send
     */
    private void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    /**
     * Sends an updated USER_LIST to all clients.
     */
    private void broadcastUserList() {
        List<String> names = clients.stream()
                .map(ClientHandler::getUsername)
                .filter(n -> n != null && !n.isBlank())
                .toList();

        broadcast(MessageFactory.userList(names));
    }

    /**
     * Removes a client handler from the list and broadcasts left/user list.
     *
     * @param handler handler to remove
     */
    private void removeClient(ClientHandler handler) {
        clients.remove(handler);
        if (handler.getUsername() != null) {
            broadcast(MessageFactory.userLeft(handler.getUsername()));
            broadcastUserList();
        }
    }

    /**
     * One connected client session handler.
     */
    private class ClientHandler extends Thread {

        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        private String username;

        /**
         * Creates a new handler thread for a socket.
         *
         * @param socket accepted socket
         */
        ClientHandler(Socket socket) {
            super("ClientHandler-" + socket.getRemoteSocketAddress());
            this.socket = socket;
        }

        /**
         * Returns the logged in username for this client.
         *
         * @return username (null until logged in)
         */
        public String getUsername() {
            return username;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // First message must be LOGIN
                Object firstObj = in.readObject();
                if (!(firstObj instanceof Message first)) {
                    send(MessageFactory.loginRejected("Invalid login message"));
                    closeQuietly();
                    removeClient(this);
                    return;
                }

                if (first.getType() != MessageType.LOGIN || first.getFrom() == null || first.getFrom().isBlank()) {
                    send(MessageFactory.loginRejected("Username required"));
                    closeQuietly();
                    removeClient(this);
                    return;
                }

                String requested = first.getFrom().trim();

                // Reject duplicate usernames
                boolean exists = clients.stream()
                        .anyMatch(c -> c != this && requested.equalsIgnoreCase(c.getUsername()));

                if (exists) {
                    send(MessageFactory.loginRejected("Username already taken"));
                    closeQuietly();
                    removeClient(this);
                    return;
                }

                this.username = requested;

                System.out.println("User logged in: " + username);

                // Notify everyone and send list
                broadcast(MessageFactory.userJoined(username));
                broadcastUserList();

                // Now handle normal messages
                while (running && !socket.isClosed()) {
                    Object obj = in.readObject();
                    if (!(obj instanceof Message message)) {
                        continue;
                    }

                    if (message.getType() == MessageType.CHAT) {
                        broadcast(MessageFactory.chat(message.getFrom(), message.getText()));
                    }
                }

            } catch (Exception e) {
                System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
            } finally {
                closeQuietly();
                removeClient(this);
            }
        }

        /**
         * Sends a message to this client.
         *
         * @param message message to send
         */
        public synchronized void send(Message message) {
            if (out == null) return;
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException ignored) {
                closeQuietly();
            }
        }

        /**
         * Closes the client socket and streams quietly.
         */
        public void closeQuietly() {
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {
            }
            try {
                if (out != null) out.close();
            } catch (IOException ignored) {
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
