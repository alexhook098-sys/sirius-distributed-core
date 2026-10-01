package com.astra.sirius;

public class SiriusNodeInfo {

    private final String name;
    private final String host;
    private final int port;

    private boolean online;
    private long latencyMs;

    public SiriusNodeInfo(
            String name,
            String host,
            int port
    ) {
        this.name = name;
        this.host = host;
        this.port = port;

        this.online = false;
        this.latencyMs = -1;
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

    public void setOnline(
            boolean online
    ) {
        this.online = online;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(
            long latencyMs
    ) {
        this.latencyMs = latencyMs;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    // Оценка состояния узла
    public String getHealthStatus() {

        if (!online) {
            return "OFFLINE";
        }

        if (latencyMs < 0) {
            return "UNKNOWN";
        }

        if (latencyMs <= 10) {
            return "EXCELLENT";
        }

        if (latencyMs <= 50) {
            return "GOOD";
        }

        if (latencyMs <= 200) {
            return "FAIR";
        }

        if (latencyMs <= 500) {
            return "SLOW";
        }

        return "VERY SLOW";
    }

    @Override
    public String toString() {

        return "SiriusNodeInfo{" +
                "name='" + name + '\'' +
                ", address='" + getAddress() + '\'' +
                ", online=" + online +
                ", latencyMs=" + latencyMs +
                ", health='" +
                getHealthStatus() +
                '\'' +
                '}';
    }
    }
