package lu.uni.serval.commons.runner.utils.messaging.socket.processor;

public interface FrameProcessorFactory {
    FrameProcessor getFrameProcessor(int code);
}
