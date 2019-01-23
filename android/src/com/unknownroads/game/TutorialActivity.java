package com.unknownroads.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class TutorialActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "fcul.cm.UnknownRoadsProto.EXTRA_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        LinearLayout clayout = (LinearLayout) findViewById(R.id.tutoriallayout);
        clayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("clicked", "click");
                showMenu(v);

            }

        });
    }

    public void showMenu(View view) {

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "empty");
        startActivity(intent);

    }
}
