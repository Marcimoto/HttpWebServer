package HttpWebServer;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Server {
    private int port;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        this.port = 8080;
        new Thread(new BackgroundServer(port)).start();
    }

    public int getPort() {
        return this.port;
    }

    static class BackgroundServer implements Runnable {
        private int port;

        public BackgroundServer(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            this.port = 8080;
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(port);
                while (true) {
                    System.out.println("Server is listening for requests...");
                    Socket connection = serverSocket.accept();
                    new Thread(new ClientHandler(connection)).start();
                }
            } catch (IOException e) {
                // catch exceptions and explain a log, maybe something better than an
                // IOException
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

                if (line == null || line.isEmpty()) {
                    sendBadRequest();
                    return;
                }

                // What if no httpVersion is given?? think about default behavior!
                String[] request = line.split(" ");
                String method = "", resource = "", httpVersion = "";

                if (request.length != 3) {
                    method = "GET";
                    resource = "/";
                    httpVersion = "HTTP/1.1";
                } else {
                    method = request[0].trim();
                    resource = request[1].trim();
                    httpVersion = request[2].trim();
                }

                if (!method.equals("GET") && !method.equals("HEAD")) {
                    sendMethodNotFound(httpVersion);
                    return;
                }

                File file = new File("." + resource);

                if (!file.exists()) {
                    sendFileNotFound(httpVersion, file);
                    return;
                }

                // Process request
                byte[] msgBody = null;
                if (file.isFile()) {
                    msgBody = Files.readAllBytes(file.toPath());
                } else if (file.isDirectory()) {
                    msgBody = getDirectoryContent(file);
                }

                byte[] msgHeader = getResponseHeader(httpVersion, file, msgBody);

                // Send the HTTP response message
                switch (method) {
                    case "GET":
                        sendResponse(msgHeader, msgBody);
                        break;
                    case "HEAD":
                        sendResponse(msgHeader);
                        break;
                }

            } catch (IOException e) {
                try {
                    sendServerError();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        private byte[] getDirectoryContent(File file) {
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html>\n");
            body.append("<html>\n");
            body.append("<head>\n");
            body.append("<title>Simple Web Server</title>");
            body.append("</head>\n");
            body.append("<body>\n");
            body.append("<h1>Simple Web Server</h1>");

            File[] files = file.listFiles();
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

        private void sendMethodNotFound(String httpVersion) throws IOException {
            StringBuilder msg = new StringBuilder();
            msg.append(httpVersion + " 405 Not found\n");
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

        private void sendResponse(byte[] msg) throws IOException {
            OutputStream out = connection.getOutputStream();
            out.write(msg);
            out.flush();
            connection.close();
        }

        private void sendResponse(byte[] msgHeader, byte[] msgBody) throws IOException {
            OutputStream out = connection.getOutputStream();
            out.write(msgHeader);
            out.write(msgBody);
            out.flush();
            connection.close();
        }
    }
}
