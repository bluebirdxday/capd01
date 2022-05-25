package com.example.capd;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capd.CoordinatesCalculate.Coordinates;
import com.example.capd.CoordinatesCalculate.GetCoordinatesRepo;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    Button btn_change;
    EditText edit_location;
    TextView tv_x, tv_y, tv_allxy;
    String location;
    String x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_change = (Button)findViewById(R.id.btn_change);
        edit_location = (EditText)findViewById(R.id.edit_location);
        tv_x = (TextView)findViewById(R.id.tv_x);
        tv_y = (TextView)findViewById(R.id.tv_y);
        tv_allxy = (TextView) findViewById(R.id.tv_allxy);

        final TTS myTTS = new TTS(this);
        myTTS.initTTS(this);

        btn_change.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text = btn_change.getText().toString();

                myTTS.startTTS(text);

                return false;
            }
        });


        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = edit_location.getText().toString();

                GetCoordinatesRepo.getINSTANCE().getCoordinatesList(location, 1, 10, new GetCoordinatesRepo.CoordinatesResponseListener() {
                    @Override
                    public void onSuccessResponse(Coordinates coordinatesData) {

                        x = coordinatesData.documentsList.get(0).getX();
                        tv_x.setText(x);
                        y = coordinatesData.documentsList.get(0).getY();
                        tv_y.setText(y);

                        tv_allxy.setText(new Gson().toJson(coordinatesData));


                    }

                    @Override
                    public void onFailResponse() {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });





    }
}
