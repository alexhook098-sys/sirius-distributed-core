package com.astra.sirius;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class SiriusBootReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(
                intent.getAction()
        )) {

            Intent serviceIntent =
                    new Intent(
                            context,
                            SiriusSupervisorService.class
                    );

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.O) {

                context.startForegroundService(
                        serviceIntent
                );

            } else {

                context.startService(
                        serviceIntent
                );
            }

            System.out.println(
                    "[SIRIUS] BOOT RECEIVER"
            );

            System.out.println(
                    "[SIRIUS] SUPERVISOR STARTED"
            );
        }
    }
        }
