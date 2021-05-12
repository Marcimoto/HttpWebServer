package httpwebserver;

import java.io.File;
import java.net.Socket;

public class ResponseBody {

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

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (files[i].isDirectory()) {
                name = "/" + name;
            }
            String host = connection.getLocalAddress().getHostAddress();
            String path = "http:/" + host + "/" + files[i].getPath();
            body.append("<li><a href=" + path + ">" + name + "</a></li>\n");
        }
        body.append("</body>\n");
        body.append("</html>\n");
        body.append("\n");
        return body.toString().getBytes();
    }
}
