package httpwebserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Defines failed HTTP Response messages.
 * 
 * @author Marcel Unkauf
 */
public class FailedResponse {

    /**
     * Sends a "405 Method Not Allowed" HTTP response.
     * The content of the message is a HTML document which represents the error.
     *
     * @param httpVersion The HTTP version of the request.
     * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
     */
    public static String getMethodNotAllowedMessage(String httpVersion) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(httpVersion + " 405 Method Not Allowed\n");
        msg.append("Server: Simple HTTP web server\n");
        msg.append("Content-Type: text/html; charset=utf-8\n");
        msg.append("Allow: GET, HEAD\n");
        msg.append("\n");
        msg.append(getResponseMessage(405));
        msg.append("\n");
        return msg.toString();
    }
    
    /**
     * Sends a "404 File Not Found" HTTP response.
     * The content of the message is a HTML document which represents the error.
     *
     * @param httpVersion The HTTP version of the request.
     * @param file The requested file.
     * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
     */
    public static String getFileNotFoundMessage(String httpVersion, File file) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(httpVersion + " 404 Not found\n");
        msg.append("Server: Simple HTTP web server\n");
        msg.append("Content-Type: text/html; charset=utf-8\n");
        msg.append("Content-Disposition: inline; filename=\"" + file.getName() + "\"\n");
        msg.append("\n");
        msg.append(getResponseMessage(404));
        msg.append("\n");
        return msg.toString();
    }

    /**
     * Sends a "500 Internal Server Error" HTTP response.
     * The content of the message is a HTML document which represents the error.
     *
     * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
     */
    public static String getServerErrorMessage() throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append("HTTP/1.1 500 Internal Server Error\n");
        msg.append("Server: Simple HTTP web server\n");
        msg.append("Content-Type: text/html; charset=utf-8\n");
        msg.append("\n");
        msg.append(getResponseMessage(500));
        msg.append("\n");
        return msg.toString();
    }

    /**
     * Sends a "400 Bad Request" HTTP response.
     * The content of the message is a HTML document which represents the error.
     *
     * @throws Throws an IOException if an I/O error occurs when sending the HTTP response.
     */
    public static String getBadRequestMessage() throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append("HTTP/1.1 400 Bad Request\n");
        msg.append("Server: Simple HTTP web server\n");
        msg.append("Content-Type: text/html; charset=utf-8\n");
        msg.append("\n");
        msg.append(getResponseMessage(400));
        msg.append("\n");
        return msg.toString();
    }

    private static String getResponseMessage(int httpResponseStatusCode) throws IOException {
        return readHtmlFile(httpResponseStatusCode);
    }

    private static String readHtmlFile(int httpResponseStatusCode) throws IOException {
        StringBuilder msg = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("./src/main/java/httpwebserver/resources/" + httpResponseStatusCode + ".html"));
        reader.lines().forEach(line -> msg.append(line));
        reader.close();
        return msg.toString();
    }
}
