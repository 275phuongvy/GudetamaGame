package com.examples.gudetama;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    boolean quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        quit = false;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (! quit)
                    doStuff();
            }
        }, 3000);
    }

    public void doStuff() {
        Intent intent = new Intent(TitleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        quit = true;
        super.onBackPressed();
    }

}