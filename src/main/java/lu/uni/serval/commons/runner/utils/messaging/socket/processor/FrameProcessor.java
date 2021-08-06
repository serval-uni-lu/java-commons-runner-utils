package lu.uni.serval.commons.runner.utils.messaging.socket.processor;


import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;

public interface FrameProcessor {
    boolean process(Frame frame);
}
