package lu.uni.serval.commons.runner.utils.messaging.frame;

public class TextFrame implements Frame{
    public static final int CODE = StopFrame.class.getCanonicalName().hashCode();

    private final String text;

    public TextFrame(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
