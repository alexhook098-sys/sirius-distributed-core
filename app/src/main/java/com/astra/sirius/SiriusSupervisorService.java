package com.astra.sirius;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

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
                createNotification()
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

        while (running &&
                !Thread.currentThread().isInterrupted()) {

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

        // Здесь позже будет:
        //
        // 1. Проверка Vivo
        // 2. Проверка Samsung
        // 3. Проверка Huawei
        // 4. Проверка ASTRA
        // 5. Проверка Voice Bridge
        // 6. Автоматическое восстановление
    }


    private Notification createNotification() {

        return new Notification.Builder(
                this,
                CHANNEL_ID
        )
                .setContentTitle(
                        "SIRIUS Supervisor"
                )
                .setContentText(
                        "Distributed Core monitoring"
                )
                .setSmallIcon(
                        android.R.drawable.ic_menu_info_details
                )
                .setOngoing(true)
                .build();
    }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

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
