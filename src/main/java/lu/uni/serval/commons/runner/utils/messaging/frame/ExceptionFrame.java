package lu.uni.serval.commons.runner.utils.messaging.frame;

public class ExceptionFrame implements Frame {
    public static final int CODE = ExceptionFrame.class.getCanonicalName().hashCode();

    private final Exception exception;

    public ExceptionFrame(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
