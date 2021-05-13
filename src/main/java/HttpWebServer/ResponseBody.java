package httpwebserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

public class ResponseBody {

    public static byte[] getFileContent(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Retrieves the content of the directory and builds a HTML document from it.
     *
     * @param directory The directory specified in the http request.
     * @return Returns a byte array which represents the HTML document.
     */
    public static byte[] getDirectoryContent(File directory, Socket connection) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>\n");
        body.append("<html>\n");
        body.append("<head>\n");
        body.append("<title>Simple Web Server</title>");
        body.append("</head>\n");
        body.append("<body>\n");
        body.append("<h1>Simple Web Server</h1>");
        String content = readDirectoryContent(directory, connection);
        body.append(content);
        body.append("</body>\n");
        body.append("</html>\n");
        body.append("\n");
        return body.toString().getBytes();
    }

    private static String readDirectoryContent(File directory, Socket connection) {
        StringBuilder content = new StringBuilder();
        File[] files = directory.listFiles();
        String host = connection.getLocalAddress().getHostAddress();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (files[i].isDirectory()) {
                name = "/" + name;
            }
            String path = "http:/" + host + "/" + files[i].getPath();
            content.append("<li><a href=" + path + ">" + name + "</a></li>\n");
        }
        return content.toString();
    }
}
