package lu.uni.serval.commons.runner.utils.messaging.frame;

import java.util.Map;

public class RequestFrame implements Frame {
    public static final int CODE = RequestFrame.class.getCanonicalName().hashCode();

    private final Class<? extends Frame> target;
    private final Map<String, String> options;

    public RequestFrame(Class<? extends Frame> target, Map<String, String> options) {
        this.target = target;
        this.options = options;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    public Class<? extends Frame> getTarget() {
        return target;
    }

    public Map<String, String> getOptions() {
        return options;
    }
}
