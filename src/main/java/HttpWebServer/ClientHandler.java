package httpwebserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
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
            String request = readInRequest();
            setRequest(request);
            processRequest();
        } catch (BadRequestException errorMessage) {        //think about logging stuff
            System.out.println(errorMessage);
        } catch (MethodNotAllowedException errorMessage) {
            System.out.println(errorMessage);
        } catch (FileNotFoundException errorMessage) {
            System.out.println(errorMessage);  
        } catch (IOException errorMessage) {
            errorMessage.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private String readInRequest() throws IOException {
        InputStreamReader inReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inReader);
        String request = reader.readLine();
        System.out.println("REQUEST: " + request);
        return request;
    }

    private void setRequest(String input) throws IOException, BadRequestException {
        if (!isBadRequest(input)) {
            this.request = new RequestMessage(input);
        } else {
            sendResponse(FailedResponse.getBadRequestMessage().getBytes());
            throw new BadRequestException("Bad request");
        }
    }

    private void processRequest() throws IOException, MethodNotAllowedException {
        checkIsMethodAllowed();
        File requestedFile = setRequestedFile();
        byte[] message = getResponseMessage(requestedFile);
        sendResponse(message);
    }

    private boolean isBadRequest(String line) {
        return (line == null || line.isEmpty());
    }

    private void checkIsMethodAllowed() throws IOException, MethodNotAllowedException {
        if (!methodAllowed()) {
            sendResponse(FailedResponse.getMethodNotAllowedMessage(request.getHttpVersion()).getBytes());
            throw new MethodNotAllowedException("Method is not allowed");
        }
    }

    private boolean methodAllowed() { //Add an ENUM!
        String method = request.getMethod();
        return (method.equals("GET") || method.equals("HEAD"));
    }

    private File setRequestedFile() throws IOException, FileNotFoundException {
        File requestedFile = new File("." + request.getResource());
        if(requestedFile.exists()) {
            return requestedFile;
        } else {
            sendResponse(FailedResponse.getFileNotFoundMessage(request.getHttpVersion(), request.getResource()).getBytes());
            throw new FileNotFoundException("Requested file not found");
        }
    }

    private byte[] getResponseMessage(File requestedFile) throws IOException {
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
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
