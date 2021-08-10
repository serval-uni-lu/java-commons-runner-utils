package lu.uni.serval.commons.runner.utils.messaging.frame;

public class ClosingFrame implements Frame{
    public static final int CODE = ClosingFrame.class.getCanonicalName().hashCode();

    @Override
    public int getCode() {
        return CODE;
    }
}
