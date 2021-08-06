package lu.uni.serval.commons.runner.utils.messaging.frame;

public class StopFrame implements Frame {
    public static final int CODE = StopFrame.class.getCanonicalName().hashCode();

    @Override
    public int getCode() {
        return CODE;
    }
}
