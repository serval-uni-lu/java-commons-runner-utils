package lu.uni.serval.commons.runner.utils.messaging.frame;

public class ErrorFrame implements Frame {
    public static final int CODE = EndFrame.class.getCanonicalName().hashCode();

    private final Class<? extends Exception> type;
    private final String message;

    public ErrorFrame(Class<? extends Exception> type, String message) {
        this.type = type;
        this.message = message;
    }

    public Class<? extends Exception> getType() {
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
