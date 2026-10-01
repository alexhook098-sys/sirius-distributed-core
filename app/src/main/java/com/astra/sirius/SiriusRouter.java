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

    // Проверяем все зарегистрированные узлы
    public void checkNodes() {

        for (SiriusNodeInfo node : nodes) {
            checkNode(node);
        }
    }

    // Проверка одного узла + измерение задержки
    private boolean checkNode(
            SiriusNodeInfo node
    ) {

        Socket socket = new Socket();

        try {

            long startTime =
                    System.nanoTime();

            socket.connect(
                    new InetSocketAddress(
                            node.getHost(),
                            node.getPort()
                    ),
                    1000
            );

            long endTime =
                    System.nanoTime();

            long latency =
                    (endTime - startTime)
                            / 1_000_000;

            node.setLatencyMs(latency);
            node.setOnline(true);

            socket.close();

            return true;

        } catch (Exception e) {

            node.setOnline(false);
            node.setLatencyMs(-1);

            try {
                socket.close();
            } catch (Exception ignored) {
            }

            return false;
        }
    }

    // Возвращает первый доступный узел
    public SiriusNodeInfo selectAvailableNode() {

        for (SiriusNodeInfo node : nodes) {

            if (checkNode(node)) {
                return node;
            }
        }

        return null;
    }

    // Получить адрес доступного узла
    public String selectNode() {

        SiriusNodeInfo node =
                selectAvailableNode();

        if (node == null) {
            return "NO_NODES";
        }

        return node.getAddress();
    }

    // Получить выбранный узел
    public SiriusNodeInfo getAvailableNode() {

        return selectAvailableNode();
    }

    // Адрес узла для отправки задачи
    public String getNodeHost() {

        SiriusNodeInfo node =
                selectAvailableNode();

        if (node == null) {
            return null;
        }

        return node.getHost();
    }

    public int getNodePort() {

        SiriusNodeInfo node =
                selectAvailableNode();

        if (node == null) {
            return -1;
        }

        return node.getPort();
    }

    // Создание тестовой задачи
    public SiriusTask createAddTask(
            int a,
            int b
    ) {

        return new SiriusTask(
                "ADD",
                a + ":" + b
        );
    }

    // Текстовый список состояния узлов
    public String getNodesStatus() {

        checkNodes();

        StringBuilder result =
                new StringBuilder();

        for (SiriusNodeInfo node : nodes) {

            result.append(
                    node.getName()
            );

            if (node.isOnline()) {

                result.append(
                        "  ● ONLINE"
                );

                result.append(
                        "  • "
                );

                result.append(
                        node.getLatencyMs()
                );

                result.append(
                        " ms"
                );

            } else {

                result.append(
                        "  ● OFFLINE"
                );
            }

            result.append("\n");
        }

        return result.toString().trim();
    }

    @Override
    public String toString() {

        return "SiriusRouter{" +
                "nodes=" + nodes +
                '}';
    }
                }
