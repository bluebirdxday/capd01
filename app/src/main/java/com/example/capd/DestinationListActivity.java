package com.example.capd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capd.CoordinatesCalculate.Coordinates;
import com.example.capd.CoordinatesCalculate.Data;
import com.example.capd.CoordinatesCalculate.GetCoordinatesRepo;

import java.util.ArrayList;
import java.util.List;


public class DestinationListActivity extends AppCompatActivity {

    Coordinates dataList;
    List<Data> destinationData;
    Intent intent;
    String destination;

    RecyclerView recyclerView;
    DestinationAdapter destinationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_list);

        intent = new Intent(this.getIntent());
        destination = intent.getStringExtra("destination");

        destinationData = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        GetCoordinatesRepo.getINSTANCE().getCoordinatesList(destination, 1, 10, new GetCoordinatesRepo.CoordinatesResponseListener() {
            @Override
            public void onSuccessResponse(Coordinates coordinatesData) {
                dataList = coordinatesData;

                destinationData = dataList.documentsList;

                destinationAdapter = new DestinationAdapter(getApplicationContext(), destinationData);
                recyclerView.setAdapter(destinationAdapter);
            }

            @Override
            public void onFailResponse() {
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
