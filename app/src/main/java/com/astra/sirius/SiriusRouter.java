package com.astra.sirius;

import java.util.ArrayList;
import java.util.List;

public class SiriusRouter {

    private final List<SiriusNodeInfo> nodes;

    public SiriusRouter(String nodeHost, int nodePort) {

        nodes = new ArrayList<>();

        nodes.add(
                new SiriusNodeInfo(
                        "Vivo",
                        nodeHost,
                        nodePort
                )
        );
    }

    public void addNode(
            String name,
            String host,
            int port
    ) {

        nodes.add(
                new SiriusNodeInfo(
                        name,
                        host,
                        port
                )
        );
    }

    public List<SiriusNodeInfo> getNodes() {
        return nodes;
    }

    public String getNodeHost() {

        if (nodes.isEmpty()) {
            return null;
        }

        return nodes.get(0).getHost();
    }

    public int getNodePort() {

        if (nodes.isEmpty()) {
            return -1;
        }

        return nodes.get(0).getPort();
    }

    public SiriusTask createAddTask(int a, int b) {

        return new SiriusTask(
                "ADD",
                a + ":" + b
        );
    }

    public String selectNode() {

        if (nodes.isEmpty()) {
            return "NO_NODES";
        }

        SiriusNodeInfo node = nodes.get(0);

        return node.getHost() + ":" + node.getPort();
    }

    @Override
    public String toString() {

        return "SiriusRouter{" +
                "nodes=" + nodes +
                '}';
    }
            }
