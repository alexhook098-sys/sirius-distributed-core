package com.astra.sirius;

public class SiriusRouter {

    private final String nodeHost;
    private final int nodePort;

    public SiriusRouter(String nodeHost, int nodePort) {
        this.nodeHost = nodeHost;
        this.nodePort = nodePort;
    }

    public String getNodeHost() {
        return nodeHost;
    }

    public int getNodePort() {
        return nodePort;
    }

    public SiriusTask createAddTask(int a, int b) {
        return new SiriusTask(
                "ADD",
                a + ":" + b
        );
    }

    public String selectNode() {
        return nodeHost + ":" + nodePort;
    }

    @Override
    public String toString() {
        return "SiriusRouter{" +
                "node='" + selectNode() + '\'' +
                '}';
    }
}
