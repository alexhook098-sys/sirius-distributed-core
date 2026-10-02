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

        // Обновляем состояние узлов
        router.checkNodes();

        SiriusNodeInfo bestNode = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        // Анализируем все доступные узлы
        for (SiriusNodeInfo node :
                router.getNodes()) {

            if (!node.isOnline()) {
                continue;
            }

            long latency =
                    node.getLatencyMs();

            long freeRam =
                    node.getAvailableRamMb();

            if (latency < 0) {
                continue;
            }

            /*
             * SMART SCORE
             *
             * Свободная RAM повышает оценку.
             * Большая задержка снижает оценку.
             *
             * RAM имеет больший вес,
             * потому что для тяжёлых задач
             * свободная память важнее ping.
             */

            double ramScore = 0;

            if (freeRam > 0) {

                ramScore =
                        freeRam * 10.0;
            }

            double latencyScore =
                    10000.0
                            / (latency + 1);

            double totalScore =
                    ramScore
                            + latencyScore;

            System.out.println(
                    "[SIRIUS] NODE SCORE: "
                            + node.getName()
                            + " | RAM="
                            + freeRam
                            + " MB"
                            + " | LATENCY="
                            + latency
                            + " ms"
                            + " | SCORE="
                            + totalScore
            );

            if (bestNode == null ||
                    totalScore > bestScore) {

                bestNode = node;
                bestScore = totalScore;
            }
        }

        if (bestNode == null) {

            lastNodeName = "NONE";

            return "ERROR:NO_ONLINE_NODES";
        }

        // Запоминаем выбранный узел
        lastNodeName =
                bestNode.getName();

        System.out.println(
                "[SIRIUS] SELECTED NODE: "
                        + bestNode.getName()
                        + " | SCORE="
                        + bestScore
        );

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
