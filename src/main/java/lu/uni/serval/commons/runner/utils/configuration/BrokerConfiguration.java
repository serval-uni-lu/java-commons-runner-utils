package lu.uni.serval.commons.runner.utils.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrokerConfiguration {
    @JsonProperty(namespace = "protocol", defaultValue = "tcp")
    private String protocol = "tcp";
    @JsonProperty(namespace = "host", defaultValue = "localhost")
    private String host = "localhost";
    @JsonProperty(namespace = "port", defaultValue = "61616")
    private int port = 61616;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
