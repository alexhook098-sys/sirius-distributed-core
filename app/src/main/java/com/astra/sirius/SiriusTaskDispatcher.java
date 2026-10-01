package com.astra.sirius;

public class SiriusTaskDispatcher {

    private final SiriusRouter router;
    private final SiriusClient client;

    private int nextNodeIndex = 0;

    private String lastNodeName = "UNKNOWN";

    public SiriusTaskDispatcher(
            SiriusRouter router,
            SiriusClient client
    ) {
        this.router = router;
        this.client = client;
    }

    public synchronized String dispatch(SiriusTask task) {

        if (router.getNodes().isEmpty()) {
            lastNodeName = "NONE";
            return "ERROR:NO_NODES";
        }

        int nodeCount = router.getNodes().size();

        for (int attempt = 0; attempt < nodeCount; attempt++) {

            int index =
                    (nextNodeIndex + attempt) % nodeCount;

            SiriusNodeInfo node =
                    router.getNodes().get(index);

            if (!isNodeOnline(node)) {
                continue;
            }

            lastNodeName = node.getName();

            String result =
                    client.sendTask(
                            node.getHost(),
                            node.getPort(),
                            task
                    );

            nextNodeIndex =
                    (index + 1) % nodeCount;

            return result;
        }

        lastNodeName = "NONE";

        return "ERROR:NO_ONLINE_NODES";
    }

    public synchronized String getLastNodeName() {
        return lastNodeName;
    }

    private boolean isNodeOnline(
            SiriusNodeInfo node
    ) {

        try {

            java.net.Socket socket =
                    new java.net.Socket();

            socket.connect(
                    new java.net.InetSocketAddress(
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
    }
