package httpwebserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * ClientHandler processes a client request. Instances of
 * ClientHandler are created and started by BackgroundServer instances.
 *
 * @param connection The socket which is connected to the client.
 */
public class ClientHandler implements Runnable {

    private Socket connection;
    private InputStream in;
    private OutputStream out;
    private Request request;

    public ClientHandler(Socket connection) {
        this.connection = connection;
        try {
            this.in = connection.getInputStream();
            this.out = connection.getOutputStream();
        } catch(IOException e) {
            // Here: Create a log of the exception and the state of the system
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inReader);
            String line = reader.readLine();
            System.out.println("REQUEST: " + line);
            // Reply with a 400 Bad Request, if the request line does not exit
            if (line == null || line.isEmpty()) {
                sendResponse(FailedResponse.getBadRequestMessage().getBytes());
                return;
            }

            this.request = new Request(line);
            if (!methodAllowed()) {
                sendResponse(FailedResponse.getMethodNotAllowedMessage(request.getHttpVersion()).getBytes());
                return;
            }

            if(!request.requestedFileExists()) {
                sendResponse(FailedResponse.getFileNotFoundMessage(request.getHttpVersion(), request.getResource()).getBytes());
                return;
            }

            byte[] message = getResponseMessage(file);
            
            // Send the HTTP response message, it can be extended with cases for POST, PUT...
            switch (method) {
                case "GET":
                    sendResponse(message);
                    break;
                case "HEAD":
                    sendResponse(message);
                    break;
                default:
            }
        } catch (IOException e) {
            // Here: Create a log of the exception and the state of the system
            // try {
            //     // In case of an exception respond with a 500 Internal Server Error
            //     //sendResponse(FailedResponse.getServerErrorMessage().getBytes());
            // } catch (IOException e1) {
            //     e1.printStackTrace();
            // }
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean methodAllowed() throws IOException {
        String method = request.getMethod();
        return (method.equals("GET") || method.equals("HEAD"));
    }

    private byte[] getResponseMessage(File file) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage(connection); 
        byte[] message = null;
        if (file.isFile()) {
            message = responseMessage.getFileResponseMessage(request.getHttpVersion(), file);
        } else if (file.isDirectory()) {
            message = responseMessage.getDirectoryResponseMessage(request.getHttpVersion(), file);
        }
        return message;
    }

    private void sendResponse(byte[] msg) throws IOException {
        out.write(msg);
        out.flush();
        connection.close();
    }
}
