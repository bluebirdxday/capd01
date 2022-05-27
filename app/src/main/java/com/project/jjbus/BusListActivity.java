package com.project.jjbus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BusListActivity extends AppCompatActivity {
    //private static final String TAG = BusListActivity.class.getSimpleName();
    private static final String TAG = "jjbus";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);

        // 출발정류장, 도착정류장
        Intent intent = getIntent();
        String startStation = intent.getStringExtra("start_station");
        String arrivalStation = intent.getStringExtra("arrival_station");

        ((TextView) findViewById(R.id.txtTitle)).setText(startStation + " -> " + arrivalStation);

        // 리사이클러뷰
        this.recyclerView = findViewById(R.id.recyclerView);

        findViewById(R.id.btnNavigation).setOnClickListener(view -> {
            // 내비게이션

        });

        findViewById(R.id.btnFavorites).setOnClickListener(view -> {
            // 즐겨찾기

        });

        // 버스 목록
        listBus();
    }

    /* 버스 목록 */
    private void listBus() {
        // 출발정류장 리스트 구성
        BusAdapter adapter = new BusAdapter((view, position) -> {
            // 답승

            // 방면
            String via = TextUtils.isEmpty(GlobalVariable.findStopBusItems.get(position).brtVianame.content) ?
                    "" : GlobalVariable.findStopBusItems.get(position).brtVianame.content;

            // 버스번호
            String busNo = GlobalVariable.findStopBusItems.get(position).brtId.content;
            if (!GlobalVariable.findStopBusItems.get(position).brtClass.content.equals("0")) {
                busNo += "-" + GlobalVariable.findStopBusItems.get(position).brtClass.content;
            }

            // 남은 정류장수
            String rStop = GlobalVariable.findStopBusItems.get(position).RStop.content + "개소 전";

            // 도착 예정시간
            String rTime;
            if (Utils.isNumeric(GlobalVariable.findStopBusItems.get(position).RTime.content)) {
                long time = Long.parseLong(GlobalVariable.findStopBusItems.get(position).RTime.content);
                time *= 1000;
                rTime = Utils.getDisplayTime(time);
            } else {
                rTime = "확인안됨";
            }

            // 버스정보 activity 호출
            Intent intent = new Intent(this, BusInfoActivity.class);
            intent.putExtra("via", via);
            intent.putExtra("bus_no", busNo);
            intent.putExtra("r_stop", rStop);
            intent.putExtra("r_time", rTime);
            startActivity(intent);

        }, GlobalVariable.findStopBusItems);
        this.recyclerView.setAdapter(adapter);
    }
}
