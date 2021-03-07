package httpwebserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

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
        new Thread(new BackgroundServer(port)).start();
    }

    public Server(int port) {
        this.port = port;
        new Thread(new BackgroundServer(port)).start();
    }

    public int getPort() {
        return this.port;
    }

    public static void main(String[] args) {
        new Server();
    }

    /**
     * Inner class of Server which creates and starts a server instance in a thread. 
     * BackgroundServer itself creates and starts a new thread for each client request.
     * 
     * @param port The port on which the server will listen for requests.
     */
    static class BackgroundServer implements Runnable {

        private int port;

        public BackgroundServer(int port) {
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
                // Here: Create a log of the exception and the state of the system
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

    /**
     * Inner class of Server which processes a client request. Instances of
     * ClientHandler are created and started by BackgroundServer instances.
     * 
     * @param connection The socket which is connected to the client.
     */
    static class ClientHandler implements Runnable {

        private Socket connection;

        public ClientHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(inReader);
                String line = reader.readLine();
                // Reply with a 400 Bad Request, if the request line does not exit
                if (line == null || line.isEmpty()) {
                    sendBadRequest();
                    return;
                }

                String[] request = line.split(" ");
                String method = "";
                String resource = "";
                String httpVersion = "";
                // Reply with default HTTP response, if the request line is not well formatted
                if (request.length != 3) {
                    method = "GET";
                    resource = "/";
                    httpVersion = "HTTP/1.1";
                } else {
                    method = request[0].trim();
                    resource = request[1].trim();
                    httpVersion = request[2].trim();
                }
                // Reply with 405 Method Not Allowed, if not a GET or a HEAD request
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    sendMethodNotAllowed(httpVersion);
                    return;
                }

                File file = new File("." + resource);
                // Reply with 404 File Not Found, if the requested file does not exit
                if (!file.exists()) {
                    sendFileNotFound(httpVersion, file);
                    return;
                }

                // Process the request, check if a file or a directory is requested
                byte[] msgBody = null;
                if (file.isFile()) {
                    msgBody = Files.readAllBytes(file.toPath());
                } else if (file.isDirectory()) {
                    msgBody = getDirectoryContent(file);
                }

                byte[] msgHeader = getResponseHeader(httpVersion, file, msgBody);

                // Send the HTTP response message, it can be extended with cases for POST, PUT...
                switch (method) {
                    case "GET":
                        sendResponse(msgHeader, msgBody);
                        break;
                    case "HEAD":
                        sendResponse(msgHeader);
                        break;
                    default:
                }
            } catch (IOException e) {
                // Here: Create a log of the exception and the state of the system
                try {
                    // In case of an exception respond with a 500 Internal Server Error
                    sendServerError();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        /**
         * Retrieves the content of the directory and builds a HTML document from it.
         * 
         * @param directory The directory specified in the http request.
         * @return Returns a byte array which represents the HTML document.
         */
        private byte[] getDirectoryContent(File directory) {
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html>\n");
            body.append("<html>\n");
            body.append("<head>\n");
            body.append("<title>Simple Web Server</title>");
            body.append("</head>\n");
            body.append("<body>\n");
            body.append("<h1>Simple Web Server</h1>");

            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                String name = files[i].getName();
                if (files[i].isDirectory())
                    name = "/" + name;
                String host = connection.getLocalSocketAddress().toString();
                String path = "http:/" + host + "/" + files[i].getPath();
                body.append("<li><a href=\"" + path + "\">" + name + "</a></li>\n");
            }

            body.append("</body>\n");
            body.append("</html>\n");
            body.append("\n");
            return body.toString().getBytes();
        }

        /**
         * Computes the header of the HTTP response message.
         * 
         * @param httpVersion The HTTP version of the request.
         * @param file The requested file.
         * @param msgBody The body of the response message as byte array.
         * @return Returns a byte array which represents the header of the HTTP response.
         * @throws Throws an IOException if an I/O error occurs when reading the file.
         */
        private byte[] getResponseHeader(String httpVersion, File file, byte[] msgBody) throws IOException {
            StringBuilder header = new StringBuilder();
            header.append(httpVersion + " 200 OK\n");
            header.append("Server: Simple HTTP web server\n");
            header.append("Content-Type: " + Files.probeContentType(file.toPath()) + "; charset=utf-8\n");
            header.append("Content-Length: " + (msgBody.equals(null) ? 0 : msgBody.length) + "\n");
            header.append("Content-Disposition: inline; filename=\"" + file.getName() + "\"\n");
            header.append("\n");
            return header.toString().getBytes();
        }

        /**
         * Sends a "405 Method Not Allowed" HTTP response. 
         * The content of the message is a HTML document which represents the error.
         * 
         * @param httpVersion The HTTP version of the request.
         * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
         */
        private void sendMethodNotAllowed(String httpVersion) throws IOException {
            StringBuilder msg = new StringBuilder();
            msg.append(httpVersion + " 405 Method Not Allowed\n");
            msg.append("Server: Simple HTTP web server\n");
            msg.append("Content-Type: text/html; charset=utf-8\n");
            msg.append("Allow: GET, HEAD\n");
            msg.append("\n");
            msg.append("<!DOCTYPE html>");
            msg.append("<title>405 Method Not Allowed</title>");
            msg.append("<h1>405 Method Not Allowed</h1>");
            msg.append("<p>This method is not allowed.</p>");
            msg.append("\n");
            sendResponse(msg.toString().getBytes());
        }

        /**
         * Sends a "404 File Not Found" HTTP response. 
         * The content of the message is a HTML document which represents the error.
         * 
         * @param httpVersion The HTTP version of the request.
         * @param file The requested file.
         * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
         */
        private void sendFileNotFound(String httpVersion, File file) throws IOException {
            StringBuilder msg = new StringBuilder();
            msg.append(httpVersion + " 404 Not found\n");
            msg.append("Server: Simple HTTP web server\n");
            msg.append("Content-Type: text/html; charset=utf-8\n");
            msg.append("Content-Disposition: inline; filename=\"" + file.getName() + "\"\n");
            msg.append("\n");
            msg.append("<!DOCTYPE html>");
            msg.append("<title>404 File Not Found</title>");
            msg.append("<h1>404 File Not Found</h1>");
            msg.append("<p>The requested file was not found.</p>");
            msg.append("\n");
            sendResponse(msg.toString().getBytes());
        }

        /**
         * Sends a "500 Internal Server Error" HTTP response.
         * The content of the message is a HTML document which represents the error.
         * 
         * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
         */
        private void sendServerError() throws IOException {
            StringBuilder msg = new StringBuilder();
            msg.append("HTTP/1.1 500 Internal Server Error\n");
            msg.append("Server: Simple HTTP web server\n");
            msg.append("Content-Type: text/html; charset=utf-8\n");
            msg.append("\n");
            msg.append("<!DOCTYPE html>");
            msg.append("<title>500 Internal Server Error</title>");
            msg.append("<h1>500 Internal Server Error</h1>");
            msg.append("<p>An error occured in the server.</p>");
            msg.append("\n");
            sendResponse(msg.toString().getBytes());
        }

        /**
         * Sends a "400 Bad Request" HTTP response.
         * The content of the message is a HTML document which represents the error.
         * 
         * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
         */
        private void sendBadRequest() throws IOException {
            StringBuilder msg = new StringBuilder();
            msg.append("HTTP/1.1 400 Bad Request\n");
            msg.append("Server: Simple HTTP web server\n");
            msg.append("Content-Type: text/html; charset=utf-8\n");
            msg.append("\n");
            msg.append("<!DOCTYPE html>");
            msg.append("<title>400 Bad Request</title>");
            msg.append("<h1>400 Bad Request</h1>");
            msg.append("<p>Error due to malformed request syntax.</p>");
            msg.append("\n");
            sendResponse(msg.toString().getBytes());
        }

        /**
         * Sends the response message to the client.
         * 
         * @param msg The message to be send.
         * @throws Throws an IOException in case an error occurs when creating the output stream 
         * or the socket is not connected
         */
        private void sendResponse(byte[] msg) throws IOException {
            OutputStream out = connection.getOutputStream();
            out.write(msg);
            out.flush();
            connection.close();
        }

        /**
         * Sends the response message to the client.
         * 
         * @param msgHeader The header of message to be send.
         * @param msgBody The body of message to be send.
         * @throws Throws an IOException in case an error occurs when creating the output stream 
         * or the socket is not connected
         */
        private void sendResponse(byte[] msgHeader, byte[] msgBody) throws IOException {
            OutputStream out = connection.getOutputStream();
            out.write(msgHeader);
            out.write(msgBody);
            out.flush();
            connection.close();
        }
    }
}
