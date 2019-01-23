package com.unknownroads.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackSelectActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        SharedPreferences preferences = getSharedPreferences("unknownroads.preferences", Context.MODE_PRIVATE);
        float bestLap = preferences.getFloat("bestLap", 0.0f);


        setContentView(R.layout.activity_track_select);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView textView = findViewById(R.id.textView6);
        textView.setText(String.valueOf(bestLap));


        TextView clayout = findViewById(R.id.textView7);
        clayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("clicked", "click");
                startGame(v);

            }

        });
    }

    public void startGame(View view) {
        startActivity(new Intent(getBaseContext(), AndroidLauncher.class));

    }
}
