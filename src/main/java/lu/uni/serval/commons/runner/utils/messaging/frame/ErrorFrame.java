package lu.uni.serval.commons.runner.utils.messaging.frame;

public class ErrorFrame implements Frame {
    public static final int CODE = EndFrame.class.getCanonicalName().hashCode();

    private final String type;
    private final String message;

    public ErrorFrame(Class<? extends Exception> exception, String message) {
        this.type = exception.getCanonicalName();
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
