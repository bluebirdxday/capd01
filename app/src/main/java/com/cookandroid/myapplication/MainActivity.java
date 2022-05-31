package com.cookandroid.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnDS;
    Button btnStar;
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDS = (Button) findViewById(R.id.btnDS);

        btnDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnStar = (Button) findViewById(R.id.btnStar);

        btnStar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){}
        });

        btnFinish = (Button) findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("종료 재확인")
                        .setMessage("애플리케이션을 종료하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "애플리케이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", null)
//"NO"를 사용자가 클릭했을 때에는 null. 즉 아무 작업도 수행하지 않고 다시 main화면으로 돌아가게 됩니다.
                        .show();
            }
        });



    }
}