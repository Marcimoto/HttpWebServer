package httpwebserver;

/**
 * Creates and starts a BackgroundServer instance in a thread.
 * In case no port is specified is the default port 8080.
 *
 * @author Marcel Unkauf
 */
public class Server {

    private int port;

    public Server() {
        int defaultPort = 8080;
        this.port = defaultPort;
        new Thread(new AsyncServer(port)).start();
    }

    public Server(int port) {
        this.port = port;
        new Thread(new AsyncServer(port)).start();
    }

    public int getPort() {
        return this.port;
    }

    public static void main(String[] args) {
        new Server();
    }
}
