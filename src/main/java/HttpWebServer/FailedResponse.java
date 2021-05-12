package httpwebserver;

import java.io.File;
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
        HtmlFile htmlFile = new HtmlFile(405);
        msg.append(htmlFile.getHtmlPage());
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
        HtmlFile htmlFile = new HtmlFile(404);
        msg.append(htmlFile.getHtmlPage());
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
        HtmlFile htmlFile = new HtmlFile(500);
        msg.append(htmlFile.getHtmlPage());
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
        HtmlFile htmlFile = new HtmlFile(400);
        msg.append(htmlFile.getHtmlPage());
        msg.append("\n");
        return msg.toString();
    }
}
