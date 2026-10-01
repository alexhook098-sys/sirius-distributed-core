package com.astra.sirius;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SiriusRouter {

    private final List<SiriusNodeInfo> nodes;

    public SiriusRouter(
            String nodeHost,
            int nodePort
    ) {

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

    // Проверяем все узлы
    public void checkNodes() {

        for (SiriusNodeInfo node : nodes) {

            checkNode(node);

            if (node.isOnline()) {
                requestNodeInfo(node);
            }
        }
    }

    // Проверка узла + измерение задержки
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

    // Запрашиваем характеристики устройства
    private void requestNodeInfo(
            SiriusNodeInfo node
    ) {

        try {

            Socket socket =
                    new Socket();

            socket.connect(
                    new InetSocketAddress(
                            node.getHost(),
                            node.getPort()
                    ),
                    1000
            );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            writer.println("INFO");

            String response =
                    reader.readLine();

            socket.close();

            if (response == null) {
                return;
            }

            parseNodeInfo(
                    node,
                    response
            );

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] INFO ERROR "
                            + node.getName()
                            + ": "
                            + e.getMessage()
            );
        }
    }

    // Разбираем INFO-ответ
    private void parseNodeInfo(
            SiriusNodeInfo node,
            String response
    ) {

        try {

            if (!response.startsWith("INFO:")) {
                return;
            }

            String[] parts =
                    response.split(":");

            for (String part : parts) {

                if (part.startsWith(
                        "RAM_TOTAL="
                )) {

                    long value =
                            Long.parseLong(
                                    part.substring(
                                            "RAM_TOTAL="
                                                    .length()
                                    )
                            );

                    node.setTotalRamMb(value);
                }

                else if (part.startsWith(
                        "RAM_AVAILABLE="
                )) {

                    long value =
                            Long.parseLong(
                                    part.substring(
                                            "RAM_AVAILABLE="
                                                    .length()
                                    )
                            );

                    node.setAvailableRamMb(value);
                }

                else if (part.startsWith(
                        "CPU_CORES="
                )) {

                    int value =
                            Integer.parseInt(
                                    part.substring(
                                            "CPU_CORES="
                                                    .length()
                                    )
                            );

                    node.setCpuCores(value);
                }

                else if (part.startsWith(
                        "ARCH="
                )) {

                    String value =
                            part.substring(
                                    "ARCH=".length()
                            );

                    node.setCpuArchitecture(
                            value
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] INFO PARSE ERROR: "
                            + e.getMessage()
            );
        }
    }

    // Первый доступный узел
    public SiriusNodeInfo selectAvailableNode() {

        for (SiriusNodeInfo node : nodes) {

            if (checkNode(node)) {
                return node;
            }
        }

        return null;
    }

    public String selectNode() {

        SiriusNodeInfo node =
                selectAvailableNode();

        if (node == null) {
            return "NO_NODES";
        }

        return node.getAddress();
    }

    public SiriusNodeInfo getAvailableNode() {

        return selectAvailableNode();
    }

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

    // Статус всех узлов
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

                result.append(
                        "  • "
                );

                result.append(
                        node.getHealthStatus()
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
