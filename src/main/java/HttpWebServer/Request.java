package httpwebserver;

import java.io.File;

public class Request {

    private String method = "";
    private String resource = "";
    private String httpVersion = "";
    private boolean requestedFileExists;

    public Request(String line) {
        String[] request = line.split(" ");
        if(requestWellFormated(request)) {
            this.method = request[0].trim();
            this.resource = request[1].trim();
            this.resource = filterOutIPAddress(resource);
            this.httpVersion = request[2].trim();
            setRequestedFileExists();
        } else {
            setDefaultRequest();
        }
    }

    public String getMethod() {
        return this.method;
    }

    public String getResource() {
        return this.resource;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public boolean requestedFileExists() {
        return this.requestedFileExists;
    }

    private String filterOutIPAddress(String resource) {
        // Filter out the IP address from the requested resource, only IPv4 addresses!
        return resource.replaceAll("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\/\\b", "");
    }

    private boolean requestWellFormated(String[] request) {
        return (request.length == 3);
    }

    private void setRequestedFileExists() {
        File requestedFile = new File("." + resource);
        this.requestedFileExists = requestedFile.exists();   
    }

    private void setDefaultRequest() {
        this.method = "GET";
        this.resource = "/";
        this.httpVersion = "HTTP/1.1";
        this.requestedFileExists = true;
    }
}
