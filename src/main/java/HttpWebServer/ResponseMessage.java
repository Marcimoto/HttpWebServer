package httpwebserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import org.apache.commons.lang3.ArrayUtils;

public class ResponseMessage {

    private Socket connection;

    public ResponseMessage(Socket connection) {
        this.connection = connection;
    }

    public byte[] getFileResponseMessage(String httpVersion, File file) throws IOException {
        byte[] body = ResponseBody.getFileContent(file);
        byte[] header = ResponseHeader.getResponseHeader(httpVersion, file, body);
        byte[] httpMessage = ArrayUtils.addAll(header, body);
        return httpMessage;
    }

    public byte[] getDirectoryResponseMessage(String httpVersion, File file) throws IOException {
        byte[] body = ResponseBody.getDirectoryContent(file, connection);
        byte[] header = ResponseHeader.getResponseHeader(httpVersion, file, body);
        byte[] httpMessage = ArrayUtils.addAll(header, body);
        return httpMessage;
    }
}
