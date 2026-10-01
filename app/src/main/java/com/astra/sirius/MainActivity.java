package com.astra.sirius;

import android.app.Activity;
import android.os.Bundle;
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

    private SiriusNode siriusNode;
    private SiriusRouter siriusRouter;
    private SiriusClient siriusClient;
    private SiriusTaskDispatcher dispatcher;

    private TextView status;
    private TextView nodesStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Локальный узел SIRIUS
        siriusNode = new SiriusNode();
        siriusNode.start();

        // Router
        siriusRouter = new SiriusRouter(
                "192.168.100.27",
                8766
        );

        // Samsung как второй узел
        siriusRouter.addNode(
                "Samsung",
                "192.168.100.5",
                8766
        );

        // Client
        siriusClient = new SiriusClient();

        // Dispatcher
        dispatcher = new SiriusTaskDispatcher(
                siriusRouter,
                siriusClient
        );

        // Главный контейнер
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        // Фон
        ImageView background = new ImageView(this);
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

        // Затемнение
        View darkOverlay = new View(this);
        darkOverlay.setBackgroundColor(
                Color.argb(80, 0, 0, 0)
        );

        root.addView(
                darkOverlay,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        // Центральный блок
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(
                LinearLayout.VERTICAL
        );
        content.setGravity(Gravity.CENTER);
        content.setPadding(40, 40, 40, 40);

        // Заголовок
        TextView title = new TextView(this);
        title.setText("✦ SIRIUS ✦");
        title.setTextColor(Color.WHITE);
        title.setTextSize(38);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        title.setGravity(Gravity.CENTER);

        // Подзаголовок
        TextView subtitle = new TextView(this);
        subtitle.setText("DISTRIBUTED CORE");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 15, 0, 20);

        // Статус
        status = new TextView(this);
        status.setText("●  SIRIUS ONLINE");
        status.setTextColor(
                Color.rgb(100, 255, 120)
        );
        status.setTextSize(19);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 0, 0, 15);

        // Статус узлов
        nodesStatus = new TextView(this);
        nodesStatus.setText("Checking nodes...");
        nodesStatus.setTextColor(Color.WHITE);
        nodesStatus.setTextSize(16);
        nodesStatus.setGravity(Gravity.CENTER);
        nodesStatus.setPadding(0, 0, 0, 20);

        // CHECK NODES
        Button checkButton = new Button(this);
        checkButton.setText("CHECK NODES");
        checkButton.setTextSize(16);

        checkButton.setOnClickListener(v -> {

            nodesStatus.setText(
                    "Checking nodes..."
            );

            new Thread(() -> {

                String result =
                        siriusRouter.getNodesStatus();

                runOnUiThread(() ->
                        nodesStatus.setText(result)
                );

            }).start();
        });

        // TEST DISTRIBUTION
        Button testButton = new Button(this);
        testButton.setText("TEST DISTRIBUTION");
        testButton.setTextSize(16);

        testButton.setOnClickListener(v -> {

            status.setText(
                    "●  DISTRIBUTING TASK..."
            );

            status.setTextColor(Color.YELLOW);

            new Thread(() -> {

                SiriusTask task =
                        siriusRouter.createAddTask(
                                15,
                                27
                        );

                String result =
                        dispatcher.dispatch(task);

                runOnUiThread(() -> {

                    if (result.equals("RESULT:42")) {

                        status.setText(
                                "●  TASK RESULT: 42"
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
                                "●  " + result
                        );

                        status.setTextColor(
                                Color.RED
                        );
                    }
                });

            }).start();
        });

        content.addView(title);
        content.addView(subtitle);
        content.addView(status);
        content.addView(nodesStatus);
        content.addView(checkButton);
        content.addView(testButton);

        FrameLayout.LayoutParams contentParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        contentParams.gravity = Gravity.CENTER;

        root.addView(
                content,
                contentParams
        );

        setContentView(root);

        // Первая проверка узлов
        new Thread(() -> {

            String result =
                    siriusRouter.getNodesStatus();

            runOnUiThread(() ->
                    nodesStatus.setText(result)
            );

        }).start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (siriusNode != null) {
            siriusNode.stop();
        }
    }
            }
