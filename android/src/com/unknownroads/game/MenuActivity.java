package com.unknownroads.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "fcul.cm.UnknownRoadsProto.EXTRA_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout leftlayout = (LinearLayout) findViewById(R.id.leftlayout);
        leftlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("menu","tutorial");
                showTutorial(v);
            }

        });

        LinearLayout rightlayout = (LinearLayout) findViewById(R.id.rightlayout);
        rightlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("menu","race");
                showRace(v);
            }

        });

    }
    public void showRace(View view) {

        Intent intent = new Intent(this, TrackSelectActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "empty");
        startActivity(intent);

    }

    public void showTutorial(View view) {

        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "empty");
        startActivity(intent);

    }
}
