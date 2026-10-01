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

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Запускаем локальный узел SIRIUS
        siriusNode = new SiriusNode();
        siriusNode.start();

        // Создаём Router с первым узлом — Vivo
        siriusRouter = new SiriusRouter(
                "192.168.100.27",
                8766
        );

        // Добавляем второй узел — Samsung
        siriusRouter.addNode(
                "Samsung",
                "192.168.100.5",
                8766
        );

        // Client
        siriusClient = new SiriusClient();

        // Главный контейнер
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        // Фоновая картинка
        ImageView background = new ImageView(this);
        background.setImageResource(R.drawable.sirius_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        root.addView(
                background,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        // Затемнение
        View darkOverlay = new View(this);
        darkOverlay.setBackgroundColor(Color.argb(80, 0, 0, 0));

        root.addView(
                darkOverlay,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        // Центральный блок
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(40, 40, 40, 40);

        // Название
        TextView title = new TextView(this);
        title.setText("✦ SIRIUS ✦");
        title.setTextColor(Color.WHITE);
        title.setTextSize(38);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        // Подзаголовок
        TextView subtitle = new TextView(this);
        subtitle.setText("DISTRIBUTED CORE");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 15, 0, 25);

        // Статус
        status = new TextView(this);
        status.setText("●  SIRIUS ONLINE");
        status.setTextColor(Color.rgb(100, 255, 120));
        status.setTextSize(19);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 0, 0, 30);

        // Кнопка теста
        Button testButton = new Button(this);
        testButton.setText("TEST NODE");
        testButton.setTextSize(16);

        testButton.setOnClickListener(v -> {

            status.setText("●  SENDING TASK...");
            status.setTextColor(Color.YELLOW);

            new Thread(() -> {

                SiriusTask task =
                        siriusRouter.createAddTask(15, 27);

                String result =
                        siriusClient.sendTask(
                                siriusRouter.getNodeHost(),
                                siriusRouter.getNodePort(),
                                task
                        );

                runOnUiThread(() -> {

                    if (result.equals("RESULT:42")) {

                        status.setText(
                                "●  NODE RESULT: 42"
                        );

                        status.setTextColor(
                                Color.rgb(100, 255, 120)
                        );

                    } else {

                        status.setText(
                                "●  " + result
                        );

                        status.setTextColor(Color.RED);
                    }
                });

            }).start();
        });

        content.addView(title);
        content.addView(subtitle);
        content.addView(status);
        content.addView(testButton);

        FrameLayout.LayoutParams contentParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        contentParams.gravity = Gravity.CENTER;

        root.addView(content, contentParams);

        setContentView(root);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (siriusNode != null) {
            siriusNode.stop();
        }
    }
            }
