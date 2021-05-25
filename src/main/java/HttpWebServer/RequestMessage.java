package httpwebserver;

public class RequestMessage {

    private String method = "";
    private String resource = "";
    private String httpVersion = "";

    public RequestMessage(String input) {
        String[] request = input.split(" ");
        if(requestWellFormated(request)) {
            this.method = request[0].trim();
            this.resource = request[1].trim();
            this.resource = filterOutIPAddress(resource);
            this.httpVersion = request[2].trim();
        } else {
            setDefaultRequest();
        }
    }

    private String filterOutIPAddress(String resource) {
        // Filter out the IP address from the requested resource, only IPv4 addresses!
        return resource.replaceAll("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\/\\b", "");
    }

    private boolean requestWellFormated(String[] request) {
        return (request.length == 3);
    }

    private void setDefaultRequest() {
        this.method = "GET";
        this.resource = "/";
        this.httpVersion = "HTTP/1.1";
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
}
