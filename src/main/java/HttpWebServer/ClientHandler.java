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
    private RequestMessage request;
    private File requestedFile;

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
            setRequest(readInRequest());
            processRequest();
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

    private String readInRequest() throws IOException {
        InputStreamReader inReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inReader);
        String request = reader.readLine();
        System.out.println("REQUEST: " + request);
        return request;
    }

    private void setRequest(String input) throws IOException {
        if (isBadRequest(input)) {
            sendResponse(FailedResponse.getBadRequestMessage().getBytes());
            System.exit(0);
        } else {
            this.request = new RequestMessage(input);
        }
    }

    private void processRequest() throws IOException {
        checkIsMethodAllowed();
        setRequestedFile();
        byte[] message = getResponseMessage();
        sendResponse(message);
    }

    private boolean isBadRequest(String line) {
        return (line == null || line.isEmpty());
    }

    private void checkIsMethodAllowed() throws IOException {
        if (!methodAllowed()) {
            sendResponse(FailedResponse.getMethodNotAllowedMessage(request.getHttpVersion()).getBytes());
            System.exit(0);;
        }
    }

    private boolean methodAllowed() throws IOException {
        String method = request.getMethod();
        return (method.equals("GET") || method.equals("HEAD"));
    }

    private void setRequestedFile() throws IOException {
        this.requestedFile = new File("." + request.getResource());
        if(requestedFile.exists()) {
            sendResponse(FailedResponse.getFileNotFoundMessage(request.getHttpVersion(), request.getResource()).getBytes());
            System.exit(0);
        }
    }

    private byte[] getResponseMessage() throws IOException {
        ResponseMessage responseMessage = new ResponseMessage(connection); 
        byte[] message = null;
        if (requestedFile.isFile()) {
            message = responseMessage.getFileResponseMessage(request.getHttpVersion(), requestedFile);
        } else if (requestedFile.isDirectory()) {
            message = responseMessage.getDirectoryResponseMessage(request.getHttpVersion(), requestedFile);
        }
        return message;
    }

    private void sendResponse(byte[] msg) throws IOException {
        out.write(msg);
        out.flush();
        connection.close();
    }
}
