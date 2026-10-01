package com.astra.sirius;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SiriusNode {

    private static final int PORT = 8766;

    private ServerSocket serverSocket;
    private Thread serverThread;

    public void start() {

        if (serverThread != null &&
                serverThread.isAlive()) {

            return;
        }

        serverThread = new Thread(() -> {

            try {

                serverSocket =
                        new ServerSocket(PORT);

                System.out.println(
                        "[SIRIUS] NODE ONLINE"
                );

                System.out.println(
                        "[SIRIUS] Listening on port "
                                + PORT
                );

                while (
                        !Thread.currentThread()
                                .isInterrupted()
                ) {

                    Socket client =
                            serverSocket.accept();

                    System.out.println(
                            "[SIRIUS] Connection from "
                                    + client.getInetAddress()
                    );

                    handleClient(client);
                }

            } catch (Exception e) {

                System.out.println(
                        "[SIRIUS] NODE ERROR: "
                                + e.getMessage()
                );
            }
        });

        serverThread.start();
    }

    private void handleClient(
            Socket client
    ) {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    client.getInputStream()
                            )
                    );

            PrintWriter writer =
                    new PrintWriter(
                            client.getOutputStream(),
                            true
                    );

            String message =
                    reader.readLine();

            if (message != null) {

                System.out.println(
                        "[SIRIUS] TASK: "
                                + message
                );

                String result =
                        processTask(message);

                writer.println(result);
            }

            client.close();

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] CLIENT ERROR: "
                            + e.getMessage()
            );
        }
    }

    private String processTask(
            String task
    ) {

        // Проверка соединения
        if (task.equals("PING")) {

            return "PONG";
        }

        // Информация об устройстве
        if (task.equals("INFO")) {

            return getDeviceInfo();
        }

        // Арифметическая задача
        if (task.startsWith("TASK:ADD:")) {

            try {

                String data =
                        task.substring(9);

                String[] numbers =
                        data.split(":");

                if (numbers.length != 2) {

                    return "ERROR:INVALID_TASK";
                }

                int a =
                        Integer.parseInt(
                                numbers[0]
                        );

                int b =
                        Integer.parseInt(
                                numbers[1]
                        );

                int result =
                        a + b;

                System.out.println(
                        "[SIRIUS] CALC: "
                                + a
                                + " + "
                                + b
                                + " = "
                                + result
                );

                return "RESULT:" + result;

            } catch (Exception e) {

                return "ERROR:INVALID_NUMBERS";
            }
        }

        return "ERROR:UNKNOWN_TASK";
    }

    // Получение характеристик устройства
    private String getDeviceInfo() {

        long totalRam =
                getMemInfo(
                        "MemTotal"
                );

        long availableRam =
                getMemInfo(
                        "MemAvailable"
                );

        int cpuCores =
                Runtime.getRuntime()
                        .availableProcessors();

        String architecture =
                System.getProperty(
                        "os.arch",
                        "UNKNOWN"
                );

        System.out.println(
                "[SIRIUS] DEVICE INFO"
        );

        System.out.println(
                "RAM TOTAL: "
                        + totalRam
                        + " MB"
        );

        System.out.println(
                "RAM AVAILABLE: "
                        + availableRam
                        + " MB"
        );

        System.out.println(
                "CPU CORES: "
                        + cpuCores
        );

        System.out.println(
                "ARCH: "
                        + architecture
        );

        return
                "INFO"
                        + ":RAM_TOTAL="
                        + totalRam
                        + ":RAM_AVAILABLE="
                        + availableRam
                        + ":CPU_CORES="
                        + cpuCores
                        + ":ARCH="
                        + architecture;
    }

    // Читаем информацию из /proc/meminfo
    private long getMemInfo(
            String key
    ) {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    "/proc/meminfo"
                            )
                    );

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                if (line.startsWith(key)) {

                    String[] parts =
                            line.split(
                                    "\\s+"
                            );

                    reader.close();

                    if (parts.length >= 2) {

                        // /proc/meminfo
                        // использует kB
                        long kb =
                                Long.parseLong(
                                        parts[1]
                                );

                        return kb / 1024;
                    }
                }
            }

            reader.close();

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] MEMINFO ERROR: "
                            + e.getMessage()
            );
        }

        return -1;
    }

    public void stop() {

        try {

            if (serverSocket != null) {

                serverSocket.close();
            }

        } catch (Exception ignored) {
        }

        serverThread = null;
    }
                    }
