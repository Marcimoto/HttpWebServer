package httpwebserver;

public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String errorMessage) {
        super(errorMessage);
    }
}
