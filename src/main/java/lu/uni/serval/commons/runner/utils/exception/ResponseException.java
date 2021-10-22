package lu.uni.serval.commons.runner.utils.exception;

public class ResponseException extends Exception {
    private final Class<? extends Exception> type;
    private final String message;


    public ResponseException(Class<? extends Exception> type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String getMessage(){
        return String.format("[%s]%s", this.type.getSimpleName(), this.message);
    }

    public Class<? extends Exception> getType() {
        return type;
    }
}
