package com.example.capd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReconfirmDestinationActivity extends AppCompatActivity {

    Button yes_btn, no_btn;
    TextView question;
    TTS tts;
    String destination;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reconfirm_destination);

        yes_btn = (Button)findViewById(R.id.yes_btn);
        no_btn = (Button)findViewById(R.id.no_btn);
        question = (TextView)findViewById(R.id.question);

        Intent intent = getIntent();
        destination = intent.getStringExtra("destination");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        tts = new TTS(this);

        question.setText("목적지를 " + destination + "로 설정하시겠습니까?");

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.startTTS(question.getText().toString());
            }
        });

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent(getApplicationContext(), DestinationSearchActivity.class); //DestinationSearchActivity는 임의로 지정한 것이므로 추후에 버스리스트 액티비티로 수정 필요
                intent2.putExtra("destination", destination);
                intent2.putExtra("latitude", latitude);
                intent2.putExtra("longitude", longitude);
                startActivity(intent2);
            }
        });

        yes_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tts.startTTS(yes_btn.getText().toString());
                return true;
            }
        });


        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        no_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tts.startTTS(no_btn.getText().toString());
                return true;
            }
        });
    }
}
