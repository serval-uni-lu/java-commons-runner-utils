package lu.uni.serval.commons.runner.utils.exception;

public class ResponseException extends Exception {
    private final String type;
    private final String message;


    public ResponseException(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String getMessage(){
        return String.format("[%s] %s", this.type, this.message);
    }

    public String getType() {
        return type;
    }
}
