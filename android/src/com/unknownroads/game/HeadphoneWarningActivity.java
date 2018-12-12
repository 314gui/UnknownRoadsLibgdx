package com.unknownroads.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;


public class HeadphoneWarningActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "fcul.cm.UnknownRoadsProto.EXTRA_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone_warning);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(HeadphoneWarningActivity.this, TitleActivity.class);
                HeadphoneWarningActivity.this.startActivity(mainIntent);
                HeadphoneWarningActivity.this.finish();
            }
        }, 3000);

    }
}
