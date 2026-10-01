package com.astra.sirius;

import java.net.InetSocketAddress;
import java.net.Socket;
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

        SiriusNodeInfo node = selectAvailableNode();

        if (node == null) {
            return null;
        }

        return node.getHost();
    }

    public int getNodePort() {

        SiriusNodeInfo node = selectAvailableNode();

        if (node == null) {
            return -1;
        }

        return node.getPort();
    }

    public SiriusTask createAddTask(int a, int b) {

        return new SiriusTask(
                "ADD",
                a + ":" + b
        );
    }

    public String selectNode() {

        SiriusNodeInfo node = selectAvailableNode();

        if (node == null) {
            return "NO_NODES";
        }

        return node.getHost() + ":" + node.getPort();
    }

    private SiriusNodeInfo selectAvailableNode() {

        for (SiriusNodeInfo node : nodes) {

            if (checkNode(node)) {
                return node;
            }
        }

        return null;
    }

    private boolean checkNode(SiriusNodeInfo node) {

        try {

            Socket socket = new Socket();

            socket.connect(
                    new InetSocketAddress(
                            node.getHost(),
                            node.getPort()
                    ),
                    1000
            );

            socket.close();

            node.setOnline(true);

            return true;

        } catch (Exception e) {

            node.setOnline(false);

            return false;
        }
    }

    @Override
    public String toString() {

        return "SiriusRouter{" +
                "nodes=" + nodes +
                '}';
    }
}
