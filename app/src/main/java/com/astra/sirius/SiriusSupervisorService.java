package com.astra.sirius;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.net.InetSocketAddress;
import java.net.Socket;

public class SiriusSupervisorService extends Service {

    private static final String CHANNEL_ID =
            "sirius_supervisor";

    private static final int NOTIFICATION_ID =
            1001;

    private Thread supervisorThread;

    private volatile boolean running = false;


    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();

        startForeground(
                NOTIFICATION_ID,
                createNotification(
                        "Nodes ONLINE: checking..."
                )
        );

        running = true;

        supervisorThread =
                new Thread(
                        this::runSupervisor
                );

        supervisorThread.start();

        System.out.println(
                "[SIRIUS SUPERVISOR] STARTED"
        );
    }


    private void runSupervisor() {

        while (
                running &&
                !Thread.currentThread()
                        .isInterrupted()
        ) {

            try {

                checkSystem();

                Thread.sleep(30000);

            } catch (
                    InterruptedException e
            ) {

                Thread.currentThread()
                        .interrupt();

                break;

            } catch (
                    Exception e
            ) {

                System.out.println(
                        "[SIRIUS SUPERVISOR] ERROR: "
                                + e.getMessage()
                );
            }
        }
    }


    private void checkSystem() {

        System.out.println(
                "[SIRIUS SUPERVISOR] CHECK"
        );

        int onlineNodes = 0;


        // =====================================================
        // VIVO
        // =====================================================

        if (
                checkNode(
                        "Vivo",
                        "192.168.100.27",
                        8766
                )
        ) {

            onlineNodes++;
        }


        // =====================================================
        // SAMSUNG
        // =====================================================

        if (
                checkNode(
                        "Samsung",
                        "192.168.100.5",
                        8766
                )
        ) {

            onlineNodes++;
        }


        // =====================================================
        // HUAWEI
        // =====================================================

        if (
                checkNode(
                        "Huawei",
                        "192.168.100.3",
                        8766
                )
        ) {

            onlineNodes++;
        }


        // =====================================================
        // UPDATE NOTIFICATION
        // =====================================================

        updateNotification(
                onlineNodes
        );


        System.out.println(
                "[SIRIUS SUPERVISOR] NODES ONLINE: "
                        + onlineNodes
                        + "/3"
        );
    }


    private boolean checkNode(
            String name,
            String host,
            int port
    ) {

        Socket socket =
                new Socket();

        try {

            long startTime =
                    System.currentTimeMillis();

            socket.connect(
                    new InetSocketAddress(
                            host,
                            port
                    ),
                    1000
            );

            long latency =
                    System.currentTimeMillis()
                            - startTime;

            socket.close();

            System.out.println(
                    "[SIRIUS SUPERVISOR] "
                            + name
                            + " ONLINE "
                            + latency
                            + " ms"
            );

            return true;

        } catch (Exception e) {

            try {

                socket.close();

            } catch (Exception ignored) {
            }

            System.out.println(
                    "[SIRIUS SUPERVISOR] "
                            + name
                            + " OFFLINE"
            );

            return false;
        }
    }


    private void updateNotification(
            int onlineNodes
    ) {

        NotificationManager manager =
                getSystemService(
                        NotificationManager.class
                );

        if (manager == null) {
            return;
        }


        String text =
                "Nodes ONLINE: "
                        + onlineNodes
                        + "/3";


        manager.notify(
                NOTIFICATION_ID,
                createNotification(
                        text
                )
        );
    }


    private Notification createNotification(
            String text
    ) {

        return new Notification.Builder(
                this,
                CHANNEL_ID
        )
                .setContentTitle(
                        "SIRIUS Supervisor"
                )
                .setContentText(
                        text
                )
                .setSmallIcon(
                        android.R.drawable
                                .ic_menu_info_details
                )
                .setOngoing(true)
                .build();
    }


    private void createNotificationChannel() {

        if (
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.O
        ) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "SIRIUS Supervisor",
                            NotificationManager
                                    .IMPORTANCE_LOW
                    );

            channel.setDescription(
                    "SIRIUS system monitoring"
            );


            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );


            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }


    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        running = false;


        if (
                supervisorThread != null
        ) {

            supervisorThread.interrupt();
        }


        System.out.println(
                "[SIRIUS SUPERVISOR] STOPPED"
        );


        super.onDestroy();
    }


    @Override
    public IBinder onBind(
            Intent intent
    ) {

        return null;
    }
                    }
