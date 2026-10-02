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

        // Требования текущей задачи
        long requiredRam =
                task.getRequiredRamMb();

        System.out.println(
                "[SIRIUS] TASK REQUIREMENTS: "
                        + task.getType()
                        + " | RAM="
                        + requiredRam
                        + " MB"
        );

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
             * CAPABILITY CHECK
             *
             * Если узлу не хватает свободной RAM
             * для задачи — пропускаем его.
             */

            if (freeRam < requiredRam) {

                System.out.println(
                        "[SIRIUS] NODE REJECTED: "
                                + node.getName()
                                + " | FREE RAM="
                                + freeRam
                                + " MB"
                                + " | REQUIRED="
                                + requiredRam
                                + " MB"
                );

                continue;
            }

            /*
             * SMART SCORE
             *
             * Свободная RAM повышает оценку.
             * Меньшая задержка повышает оценку.
             */

            double ramScore =
                    freeRam * 10.0;

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

            return "ERROR:NO_CAPABLE_NODES";
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
