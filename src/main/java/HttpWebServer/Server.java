package httpwebserver;

/**
 * Creates and starts a BackgroundServer instance in a thread.
 * In case no port is specified, the default port is 8080.
 *
 * @author Marcel Unkauf
 */
public class Server {

    private int port;

    public Server() {
        this.port = 8080;
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
