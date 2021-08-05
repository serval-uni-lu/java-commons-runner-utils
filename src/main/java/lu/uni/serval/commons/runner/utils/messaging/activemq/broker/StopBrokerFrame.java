package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.Frame;

public class StopBrokerFrame implements Frame {
    public static final int CODE = StopBrokerFrame.class.getCanonicalName().hashCode();

    @Override
    public int getCode() {
        return CODE;
    }
}
