package com.astra.sirius;

public class SiriusTaskDispatcher {

    private final SiriusRouter router;
    private final SiriusClient client;

    private String lastNodeName = "UNKNOWN";

    public SiriusTaskDispatcher(
            SiriusRouter router,
            SiriusClient client
    ) {
        this.router = router;
        this.client = client;
    }

    public synchronized String dispatch(
            SiriusTask task
    ) {

        if (router.getNodes().isEmpty()) {

            lastNodeName = "NONE";

            return "ERROR:NO_NODES";
        }

        // Обновляем состояние всех узлов
        router.checkNodes();

        SiriusNodeInfo bestNode = null;

        // Ищем онлайн-узел
        // с минимальной задержкой
        for (SiriusNodeInfo node :
                router.getNodes()) {

            if (!node.isOnline()) {
                continue;
            }

            if (node.getLatencyMs() < 0) {
                continue;
            }

            if (bestNode == null ||
                    node.getLatencyMs()
                            < bestNode.getLatencyMs()) {

                bestNode = node;
            }
        }

        if (bestNode == null) {

            lastNodeName = "NONE";

            return "ERROR:NO_ONLINE_NODES";
        }

        // Запоминаем выбранный узел
        lastNodeName =
                bestNode.getName();

        // Отправляем задачу
        String result =
                client.sendTask(
                        bestNode.getHost(),
                        bestNode.getPort(),
                        task
                );

        return result;
    }

    public synchronized String getLastNodeName() {

        return lastNodeName;
    }
}
