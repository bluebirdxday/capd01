package com.project.jjbus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    //private static final String TAG = IntroActivity.class.getSimpleName();
    private static final String TAG = "jjbus";

    private boolean executed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        this.executed = true;

        // 인트로 화면을 1초동안 보여주고 메인으로 이동
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            this.executed = false;

            // 메인으로 이동
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }, Constants.LoadingDelay.LONG);
    }

    @Override
    public void onBackPressed() {
        if (this.executed) {
            return;
        }

        moveTaskToBack(true);
        finish();
    }
}
