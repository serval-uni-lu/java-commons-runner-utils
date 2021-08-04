package lu.uni.serval.commons.runner.utils.messaging.point2point.frame;

public class EndFrame implements Frame{
    public static final int CODE = EndFrame.class.getCanonicalName().hashCode();

    private final String origin;

    public EndFrame(String origin){
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
