package lu.uni.serval.commons.runner.utils.messaging.point2point.processor;

public interface FrameProcessorFactory {
    FrameProcessor getFrameProcessor(int code);
}
