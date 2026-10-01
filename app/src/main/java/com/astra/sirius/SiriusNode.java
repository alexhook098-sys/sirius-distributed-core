package com.astra.sirius;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SiriusNode {

    private static final int PORT = 8766;

    private ServerSocket serverSocket;
    private Thread serverThread;

    public void start() {

        if (serverThread != null && serverThread.isAlive()) {
            return;
        }

        serverThread = new Thread(() -> {

            try {
                serverSocket = new ServerSocket(PORT);

                System.out.println("[SIRIUS] NODE ONLINE");
                System.out.println("[SIRIUS] Listening on port " + PORT);

                while (!Thread.currentThread().isInterrupted()) {

                    Socket client = serverSocket.accept();

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

    private void handleClient(Socket client) {

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

            String message = reader.readLine();

            if (message != null) {

                System.out.println(
                        "[SIRIUS] TASK: " + message
                );

                if (message.equals("PING")) {

                    writer.println("PONG");

                } else {

                    writer.println("SIRIUS_OK");
                }
            }

            client.close();

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] CLIENT ERROR: "
                            + e.getMessage()
            );
        }
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
