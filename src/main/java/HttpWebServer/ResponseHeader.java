package httpwebserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResponseHeader {

    /**
     * Computes the header of the HTTP response message.
     *
     * @param httpVersion The HTTP version of the request.
     * @param file The requested file.
     * @param msgBody The body of the response message as byte array.
     * @return Returns a byte array which represents the header of the HTTP response.
     * @throws Throws an IOException if an I/O error occurs when reading the file.
     */
    public static byte[] getResponseHeader(String httpVersion, File file, byte[] msgBody) throws IOException {
        StringBuilder header = new StringBuilder();
        header.append(httpVersion + " 200 OK\n");
        header.append("Server: Simple HTTP web server\n");
        header.append("Content-Type: " + Files.probeContentType(file.toPath()) + "; charset=utf-8\n");
        header.append("Content-Length: " + (msgBody.equals(null) ? 0 : msgBody.length) + "\n");
        header.append("Content-Disposition: inline; filename=\"" + file.getName() + "\"\n");
        header.append("\n");
        return header.toString().getBytes();
    }
}
