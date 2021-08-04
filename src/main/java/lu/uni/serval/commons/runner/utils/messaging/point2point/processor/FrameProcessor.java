package lu.uni.serval.commons.runner.utils.messaging.point2point.processor;


import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.Frame;

public interface FrameProcessor {
    boolean process(Frame frame);
}
