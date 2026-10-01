package com.astra.sirius;

public class SiriusNodeInfo {

    private final String name;
    private final String host;
    private final int port;

    private boolean online;

    public SiriusNodeInfo(
            String name,
            String host,
            int port
    ) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.online = false;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    @Override
    public String toString() {
        return "SiriusNodeInfo{" +
                "name='" + name + '\'' +
                ", address='" + getAddress() + '\'' +
                ", online=" + online +
                '}';
    }
}
