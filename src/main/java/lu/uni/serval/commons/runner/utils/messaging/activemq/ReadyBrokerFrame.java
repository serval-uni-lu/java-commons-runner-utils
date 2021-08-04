package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.Frame;

public class ReadyBrokerFrame implements Frame {
    public static final int CODE = ReadyBrokerFrame.class.getCanonicalName().hashCode();

    @Override
    public int getCode() {
        return CODE;
    }
}
