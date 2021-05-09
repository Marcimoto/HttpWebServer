package httpwebserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
* A server instance which runs in a thread.
* BackgroundServer itself creates and starts a new thread for each client request.
*
* @param port The port on which the server will listen for requests.
*/
public class AsyncServer implements Runnable {

    private int port;

    public AsyncServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Server is listening for requests...");
                Socket connection = serverSocket.accept();
                new Thread(new ClientHandler(connection)).start();
            }
        } catch (IOException e) {
            // Here: Create a log of the exception and the state of the system -> IMPLEMENT IT!
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
   
}
