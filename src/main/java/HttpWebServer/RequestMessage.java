package httpwebserver;

public class RequestMessage {

    private Method method = null;
    private String resource = "";
    private String httpVersion = "";

    public RequestMessage(String input) {
        String[] request = input.split(" ");
        if(RequestMessage.isWellFormated(request)) {
            this.method = defineMethod(request[0].trim());
            this.resource = request[1].trim();
            this.resource = filterOutIPAddress(resource);
            this.httpVersion = request[2].trim();
        } else {
            setDefaultRequest();
        }
    }

    private static boolean isWellFormated(String[] request) {
        return (request.length == 3);
    }

    private Method defineMethod(String method) {
        switch(method) {
            case "GET":
                return Method.GET;
            case "HEAD":
                return Method.HEAD;
            default:
                return null;
        }
    }

    private String filterOutIPAddress(String resource) {
        // Filter out the IP address from the requested resource, only IPv4 addresses!
        return resource.replaceAll("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\/\\b", "");
    }

    private void setDefaultRequest() {
        this.method = Method.GET;
        this.resource = "/";
        this.httpVersion = "HTTP/1.1";
    }
    
    public Method getMethod() {
        return this.method;
    }

    public String getResource() {
        return this.resource;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }
}
