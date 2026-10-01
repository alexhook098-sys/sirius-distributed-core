package com.astra.sirius;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private SiriusNode siriusNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Запускаем сетевой узел SIRIUS
        siriusNode = new SiriusNode();
        siriusNode.start();

        // Главный контейнер
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        // Фоновая картинка SIRIUS
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

        // Затемнение поверх картинки
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
        subtitle.setPadding(0, 15, 0, 35);

        // Статус
        TextView status = new TextView(this);
        status.setText("●  SIRIUS ONLINE");
        status.setTextColor(Color.rgb(100, 255, 120));
        status.setTextSize(19);
        status.setGravity(Gravity.CENTER);

        content.addView(title);
        content.addView(subtitle);
        content.addView(status);

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
