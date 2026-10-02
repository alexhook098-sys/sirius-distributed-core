package com.astra.sirius;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private SiriusRouter siriusRouter;
    private SiriusClient siriusClient;
    private SiriusTaskDispatcher dispatcher;

    private TextView status;
    private TextView nodesStatus;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        // =====================================================
        // SIRIUS NODE SERVICE
        // =====================================================

        try {

            Intent nodeIntent =
                    new Intent(
                            this,
                            SiriusNodeService.class
                    );

            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                startForegroundService(
                        nodeIntent
                );

            } else {

                startService(
                        nodeIntent
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] NODE SERVICE ERROR: "
                            + e.getMessage()
            );
        }


        // =====================================================
        // SIRIUS SUPERVISOR
        // =====================================================

        try {

            Intent supervisorIntent =
                    new Intent(
                            this,
                            SiriusSupervisorService.class
                    );

            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                startForegroundService(
                        supervisorIntent
                );

            } else {

                startService(
                        supervisorIntent
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "[SIRIUS] SUPERVISOR ERROR: "
                            + e.getMessage()
            );
        }


        // =====================================================
        // ROUTER
        // =====================================================

        siriusRouter = new SiriusRouter(
                "192.168.100.27",
                8766
        );


        // Samsung

        siriusRouter.addNode(
                "Samsung",
                "192.168.100.5",
                8766
        );


        // Huawei

        siriusRouter.addNode(
                "Huawei",
                "192.168.100.3",
                8766
        );


        // =====================================================
        // CLIENT
        // =====================================================

        siriusClient =
                new SiriusClient();


        // =====================================================
        // DISPATCHER
        // =====================================================

        dispatcher =
                new SiriusTaskDispatcher(
                        siriusRouter,
                        siriusClient
                );


        // =====================================================
        // MAIN CONTAINER
        // =====================================================

        FrameLayout root =
                new FrameLayout(this);

        root.setBackgroundColor(
                Color.BLACK
        );


        // =====================================================
        // BACKGROUND
        // =====================================================

        ImageView background =
                new ImageView(this);

        background.setImageResource(
                R.drawable.sirius_background
        );

        background.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        root.addView(
                background,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );


        // =====================================================
        // DARK OVERLAY
        // =====================================================

        View darkOverlay =
                new View(this);

        darkOverlay.setBackgroundColor(
                Color.argb(
                        80,
                        0,
                        0,
                        0
                )
        );

        root.addView(
                darkOverlay,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );


        // =====================================================
        // CONTENT
        // =====================================================

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setGravity(
                Gravity.CENTER
        );

        content.setPadding(
                40,
                40,
                40,
                40
        );


        // =====================================================
        // TITLE
        // =====================================================

        TextView title =
                new TextView(this);

        title.setText(
                "✦ SIRIUS ✦"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(
                38
        );

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );


        // =====================================================
        // SUBTITLE
        // =====================================================

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                "DISTRIBUTED CORE"
        );

        subtitle.setTextColor(
                Color.LTGRAY
        );

        subtitle.setTextSize(
                17
        );

        subtitle.setGravity(
                Gravity.CENTER
        );

        subtitle.setPadding(
                0,
                15,
                0,
                20
        );


        // =====================================================
        // STATUS
        // =====================================================

        status =
                new TextView(this);

        status.setText(
                "●  SIRIUS ONLINE"
        );

        status.setTextColor(
                Color.rgb(
                        100,
                        255,
                        120
                )
        );

        status.setTextSize(
                19
        );

        status.setGravity(
                Gravity.CENTER
        );

        status.setPadding(
                0,
                0,
                0,
                15
        );


        // =====================================================
        // NODES STATUS
        // =====================================================

        nodesStatus =
                new TextView(this);

        nodesStatus.setText(
                "Checking nodes..."
        );

        nodesStatus.setTextColor(
                Color.WHITE
        );

        nodesStatus.setTextSize(
                14
        );

        nodesStatus.setGravity(
                Gravity.CENTER
        );

        nodesStatus.setPadding(
                0,
                0,
                0,
                20
        );


        // =====================================================
        // CHECK NODES
        // =====================================================

        Button checkButton =
                new Button(this);

        checkButton.setText(
                "CHECK NODES"
        );

        checkButton.setTextSize(
                16
        );

        checkButton.setOnClickListener(
                v -> {

                    nodesStatus.setText(
                            "Checking nodes..."
                    );

                    new Thread(() -> {

                        String result =
                                siriusRouter
                                        .getNodesStatus();

                        String detailedResult =
                                buildDetailedNodeStatus(
                                        result
                                );

                        runOnUiThread(() ->
                                nodesStatus.setText(
                                        detailedResult
                                )
                        );

                    }).start();
                }
        );


        // =====================================================
        // TEST DISTRIBUTION
        // =====================================================

        Button testButton =
                new Button(this);

        testButton.setText(
                "TEST DISTRIBUTION"
        );

        testButton.setTextSize(
                16
        );

        testButton.setOnClickListener(
                v -> {

                    status.setText(
                            "●  DISTRIBUTING TASK..."
                    );

                    status.setTextColor(
                            Color.YELLOW
                    );

                    new Thread(() -> {

                        SiriusTask task =
                                siriusRouter
                                        .createAddTask(
                                                15,
                                                27
                                        );

                        String result =
                                dispatcher.dispatch(
                                        task
                                );

                        String executedBy =
                                dispatcher
                                        .getLastNodeName();

                        runOnUiThread(() -> {

                            if (result.equals(
                                    "RESULT:42"
                            )) {

                                status.setText(
                                        "●  TASK RESULT: 42\n"
                                                + "EXECUTED BY: "
                                                + executedBy
                                );

                                status.setTextColor(
                                        Color.rgb(
                                                100,
                                                255,
                                                120
                                        )
                                );

                            } else {

                                status.setText(
                                        "●  "
                                                + result
                                                + "\n"
                                                + "NODE: "
                                                + executedBy
                                );

                                status.setTextColor(
                                        Color.RED
                                );
                            }
                        });

                    }).start();
                }
        );


        // =====================================================
        // ADD UI
        // =====================================================

        content.addView(
                title
        );

        content.addView(
                subtitle
        );

        content.addView(
                status
        );

        content.addView(
                nodesStatus
        );

        content.addView(
                checkButton
        );

        content.addView(
                testButton
        );


        FrameLayout.LayoutParams contentParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        contentParams.gravity =
                Gravity.CENTER;


        root.addView(
                content,
                contentParams
        );


        setContentView(
                root
        );


        // =====================================================
        // FIRST NODE CHECK
        // =====================================================

        new Thread(() -> {

            String result =
                    siriusRouter
                            .getNodesStatus();

            String detailedResult =
                    buildDetailedNodeStatus(
                            result
                    );

            runOnUiThread(() ->
                    nodesStatus.setText(
                            detailedResult
                    )
            );

        }).start();
    }


    // =========================================================
    // DETAILED NODE STATUS
    // =========================================================

    private String buildDetailedNodeStatus(
            String basicStatus
    ) {

        StringBuilder result =
                new StringBuilder();


        for (SiriusNodeInfo node :
                siriusRouter.getNodes()) {

            result.append(
                    node.getName()
            );


            if (node.isOnline()) {

                result.append(
                        "  ● ONLINE"
                );

                result.append(
                        " • "
                );

                result.append(
                        node.getLatencyMs()
                );

                result.append(
                        " ms"
                );

                result.append(
                        " • "
                );

                result.append(
                        node.getHealthStatus()
                );

                result.append(
                        "\n"
                );


                // RAM

                if (node.getTotalRamMb() >= 0) {

                    result.append(
                            "RAM: "
                    );

                    result.append(
                            node.getTotalRamMb()
                    );

                    result.append(
                            " MB"
                    );


                    if (
                            node.getAvailableRamMb()
                                    >= 0
                    ) {

                        result.append(
                                "  |  FREE: "
                        );

                        result.append(
                                node.getAvailableRamMb()
                        );

                        result.append(
                                " MB"
                        );
                    }

                    result.append(
                            "\n"
                    );
                }


                // CPU

                if (
                        node.getCpuCores()
                                >= 0
                ) {

                    result.append(
                            "CPU: "
                    );

                    result.append(
                            node.getCpuCores()
                    );

                    result.append(
                            " cores"
                    );

                    result.append(
                            "\n"
                    );
                }


                // ARCHITECTURE

                if (
                        node.getCpuArchitecture()
                                != null
                                &&
                        !node.getCpuArchitecture()
                                .equals(
                                        "UNKNOWN"
                                )
                ) {

                    result.append(
                            "ARCH: "
                    );

                    result.append(
                            node.getCpuArchitecture()
                    );

                    result.append(
                            "\n"
                    );
                }


            } else {

                result.append(
                        "  ● OFFLINE"
                );

                result.append(
                        "\n"
                );
            }


            result.append(
                    "\n"
            );
        }


        return result
                .toString()
                .trim();
    }
                }
