package lu.uni.serval.commons.runner.utils.messaging.frame;

public class AddressFrame implements Frame{
    public static final int CODE = AddressFrame.class.getCanonicalName().hashCode();

    private final String host;
    private final int port;

    public AddressFrame(int port) {
        this.host = "localhost";
        this.port = port;
    }

    public AddressFrame(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
