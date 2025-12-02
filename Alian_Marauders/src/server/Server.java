package server;

import alienmarauders.networking.Message;
import alienmarauders.networking.Message.Type;

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
 * The server listens on a TCP port, accepts clients and relays {@link Message}
 * instances between them. Each connected client is handled in its own thread.
 */
public class Server {

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running;

    /**
     * Starts the server and begins accepting incoming client connections.
     * <p>
     * This method blocks until the server socket is closed. It should therefore
     * typically be called from the {@code main} method of a dedicated server
     * application.
     *
     * @param port the TCP port to bind the server on
     */
    public void start(int port) {
        running = true;
        System.out.println("Starting chat server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

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
            // Close all clients if server goes down
            for (ClientHandler client : clients) {
                client.close();
            }
            clients.clear();
        }
    }

    /**
     * Stops the server from accepting new clients and closes all existing connections.
     * <p>
     * Note: with this simple implementation, calling this method will cause
     * {@link #start(int)} to exit with an exception the next time {@code accept()}
     * is called.
     */
    public void stop() {
        running = false;
        // The ServerSocket will exit its loop when closed by the surrounding try-with-resources block.
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message the {@link Message} to send
     */
    private void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.sendAsync(message);
        }
    }

    /**
     * Returns a snapshot of the current usernames connected to the server.
     *
     * @return list of usernames
     */
    private List<String> getUsernames() {
        return clients.stream()
                .map(ClientHandler::getUsername)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    /**
     * Removes a disconnected client handler from the server.
     *
     * @param handler the client handler to remove
     */
    private void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    /**
     * Handles a single client connection in its own thread.
     */
    private class ClientHandler extends Thread {

        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;

        /**
         * Creates a new client handler for an accepted socket.
         *
         * @param socket the underlying socket connection to the client
         */
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Returns the username associated with this client.
         *
         * @return the username, or {@code null} if not logged in yet
         */
        public String getUsername() {
            return username;
        }

        @Override
        public void run() {
            try {
                // IMPORTANT: create ObjectOutputStream first and flush,
                // then ObjectInputStream, to avoid deadlock.
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Client handler started for " + socket.getRemoteSocketAddress());

                // First message must be LOGIN
                Message first = (Message) in.readObject();
                if (first.getType() != Type.LOGIN || first.getFrom() == null || first.getFrom().isBlank()) {
                    System.out.println("Client sent invalid login, closing connection.");
                    close();
                    return;
                }

                this.username = first.getFrom();
                System.out.println("User '" + username + "' logged in.");

                // Notify this client of all existing users
                sendAsync(Message.userList(getUsernames()));

                // Notify everyone that a new user joined
                broadcast(Message.userJoined(username));

                // Main receive loop
                while (!socket.isClosed()) {
                    Message incoming = (Message) in.readObject();
                    handleMessage(incoming);
                }
            } catch (IOException e) {
                System.out.println("Connection to '" + username + "' lost: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Unknown object received from client: " + e.getMessage());
            } finally {
                // Clean up
                if (username != null) {
                    System.out.println("User '" + username + "' disconnected.");
                    removeClient(this);
                    broadcast(Message.userLeft(username));
                }
                close();
            }
        }

        /**
         * Processes an incoming message from the client.
         *
         * @param message the received {@link Message}
         */
        private void handleMessage(Message message) {
            if (message == null) {
                return;
            }

            switch (message.getType()) {
                case CHAT -> {
                    // Relay chat messages to all clients
                    System.out.println("[" + username + "]: " + message.getText());
                    broadcast(Message.chat(username, message.getText()));
                }
                default -> System.out.println("Unhandled message type from client: " + message.getType());
            }
        }

        /**
         * Sends a message to this client asynchronously.
         * <p>
         * Any {@link IOException} is logged and causes the connection to be closed.
         *
         * @param message the {@link Message} to send
         */
        private void sendAsync(Message message) {
            if (out == null || message == null) {
                return;
            }
            try {
                synchronized (out) {
                    out.writeObject(message);
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("Failed to send message to '" + username + "': " + e.getMessage());
                close();
            }
        }

        /**
         * Closes the client connection and underlying streams.
         */
        private void close() {
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
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
