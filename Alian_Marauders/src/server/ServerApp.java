package server;

/**
 * Standalone server application launcher.
 * <p>
 * Run this class to start the chat server before starting the game client.
 */
public class ServerApp {

    /**
     * Entry point used to start the chat server.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.start(8888); // use 8888 as default port, as in the assignment
    }
}
