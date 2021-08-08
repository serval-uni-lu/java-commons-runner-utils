package lu.uni.serval.commons.runner.utils.messaging.frame;

public class ReadyFrame implements Frame {
    public static final int CODE = ReadyFrame.class.getCanonicalName().hashCode();

    @Override
    public int getCode() {
        return CODE;
    }
}
