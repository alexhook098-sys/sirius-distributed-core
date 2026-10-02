package com.astra.sirius;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class SiriusNodeService extends Service {

    private static final String CHANNEL_ID =
            "sirius_node";

    private static final int NOTIFICATION_ID =
            2001;

    private SiriusNode siriusNode;


    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();

        startForeground(
                NOTIFICATION_ID,
                createNotification()
        );

        siriusNode = new SiriusNode();

        siriusNode.start();

        System.out.println(
                "[SIRIUS NODE SERVICE] STARTED"
        );
    }


    private Notification createNotification() {

        return new Notification.Builder(
                this,
                CHANNEL_ID
        )
                .setContentTitle(
                        "SIRIUS Node"
                )
                .setContentText(
                        "Distributed node ONLINE • Port 8766"
                )
                .setSmallIcon(
                        android.R.drawable
                                .ic_menu_info_details
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
                            "SIRIUS Node",
                            NotificationManager
                                    .IMPORTANCE_LOW
                    );

            channel.setDescription(
                    "SIRIUS distributed node"
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

        if (siriusNode != null) {

            siriusNode.stop();
        }

        System.out.println(
                "[SIRIUS NODE SERVICE] STOPPED"
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
