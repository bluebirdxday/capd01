package com.example.capd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capd.CoordinatesCalculate.Coordinates;
import com.example.capd.CoordinatesCalculate.GetCoordinatesRepo;


// 목적지 검색 후 결과 리스트 액티비티
public class DestinationListActivity extends AppCompatActivity {

    String destination;

    RecyclerView recyclerView;
    DestinationAdapter destinationAdapter;

    TTS tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_list);

        Intent intent = new Intent(this.getIntent());
        destination = intent.getStringExtra("destination");

        tts = new TTS(this);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        GetCoordinatesRepo.getINSTANCE().getCoordinatesList(destination, 1, 10, new GetCoordinatesRepo.CoordinatesResponseListener() {
            @Override
            public void onSuccessResponse(Coordinates coordinatesData) {

                Coordinates c = coordinatesData;
                destinationAdapter = new DestinationAdapter(c.getDocumentsList());

                recyclerView.setAdapter(destinationAdapter);
            }

            @Override
            public void onFailResponse() {
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), DestinationSearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        tts.close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        tts.close();
        super.onStop();
    }
}