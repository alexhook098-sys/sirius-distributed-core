package com.astra.sirius;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class SiriusSupervisorService
        extends Service {

    private static final String CHANNEL_ID =
            "sirius_supervisor";

    private static final int NOTIFICATION_ID =
            1001;

    private Thread supervisorThread;

    private volatile boolean running = false;

    private SiriusRouter router;


    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();

        router = new SiriusRouter(
                "192.168.100.27",
                8766
        );

        router.addNode(
                "Samsung",
                "192.168.100.5",
                8766
        );

        router.addNode(
                "Huawei",
                "192.168.100.3",
                8766
        );

        startForeground(
                NOTIFICATION_ID,
                createNotification(
                        "Supervisor ONLINE"
                )
        );

        running = true;

        supervisorThread = new Thread(
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
                !Thread.currentThread().isInterrupted()
        ) {

            try {

                checkSystem();

                Thread.sleep(30000);

            } catch (InterruptedException e) {

                Thread.currentThread()
                        .interrupt();

                break;

            } catch (Exception e) {

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

        System.out.println(
                "[SIRIUS SUPERVISOR] Checking nodes..."
        );

        if (router == null) {
            return;
        }

        router.checkNodes();

        int online = 0;
        int total = router.getNodes().size();

        for (SiriusNodeInfo node :
                router.getNodes()) {

            if (node.isOnline()) {
                online++;
            }

            System.out.println(
                    "[SIRIUS SUPERVISOR] "
                            + node.getName()
                            + " = "
                            + (
                            node.isOnline()
                                    ? "ONLINE"
                                    : "OFFLINE"
                    )
            );
        }

        updateNotification(
                "Nodes ONLINE: "
                        + online
                        + "/"
                        + total
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


    private void updateNotification(
            String text
    ) {

        NotificationManager manager =
                getSystemService(
                        NotificationManager.class
                );

        if (manager != null) {

            manager.notify(
                    NOTIFICATION_ID,
                    createNotification(text)
            );
        }
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

        if (supervisorThread != null) {

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
