package com.project.jjbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class StationSelectActivity extends AppCompatActivity {
    //private static final String TAG = StationSelectActivity.class.getSimpleName();
    private static final String TAG = "jjbus";

    private RequestQueue requestQueue;                  // volley requestQueue

    private ProgressDialog progressDialog;              // 로딩 dialog
    private RecyclerView recyclerView1, recyclerView2;

    private String startId;                             // 출발정류장 id
    private String arrivalId;                           // 도착정류장 id

    private String startName;                           // 출발정류장명
    private String arrivalName;                         // 도착정류장명

    private ArrayList<StopBusItem> startStopBusItems;   // 출발정류장 실시간노선 array
    private ArrayList<BusItem> arrivalBusItems;         // 도작정류장 경유노선 array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_select);

        // 로딩 dialog
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("처리중...");
        this.progressDialog.setCancelable(false);

        // 출발정류장 리사이클러뷰
        this.recyclerView1 = findViewById(R.id.recyclerView1);
        this.recyclerView1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 도착정류장 리사이클러뷰
        this.recyclerView2 = findViewById(R.id.recyclerView2);
        this.recyclerView2.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        findViewById(R.id.btnOk).setOnClickListener(v -> {
            // 확인
            if (checkData()) {
                this.progressDialog.show();

                // 로딩 dialog 를 표시하기 위해 딜레이를 줌
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // 출발정류장 기준
                    // 공공데이터 api 호출 (정류소별 버스 도착정보 목록 조회)
                    callOpenApi1(this.startId);
                }, Constants.LoadingDelay.SHORT);
            }
        });

        // 정류장 목록
        listStation();
    }

    /* 정류장 목록 */
    private void listStation() {
        // 출발정류장 리스트 구성
        StationAdapter adapter1 = new StationAdapter(GlobalVariable.startStations);
        this.recyclerView1.setAdapter(adapter1);

        // 도착정류장 리스트 구성
        StationAdapter adapter2 = new StationAdapter(GlobalVariable.arrivalStations);
        this.recyclerView2.setAdapter(adapter2);
    }

    /* 정류장 선택 체크 */
    private boolean checkData() {
        boolean selected = false;

        // 출발정류장 선태 체크
        for (StationItem item : GlobalVariable.startStations) {
            if (item.selected) {
                this.startId = item.stopStandardid.content;
                this.startName = item.stopKname.content;
                selected = true;
                break;
            }
        }

        if (selected) {
            selected = false;

            // 도착정류장 선태 체크
            for (StationItem item : GlobalVariable.arrivalStations) {
                if (item.selected) {
                    this.arrivalId = item.stopStandardid.content;
                    this.arrivalName = item.stopKname.content;
                    selected = true;
                    break;
                }
            }

            if (!selected) {
                Toast.makeText(this, R.string.msg_arrival_station_select_empty, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, R.string.msg_start_station_select_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /* 공공데이터 api 호출 (정류소별 버스 도착정보 목록 조회) */
    private void callOpenApi1(String stationId) {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        try {
            String url = Constants.OpenApi.STOP_BUS_API_URL;
            url += "?serviceKey=" + Constants.OpenApi.API_KEY;
            url += "&stopStdid=" + URLEncoder.encode(stationId, "UTF-8");   // 정류장 표준 id 로 검색

            Log.d(TAG, url);

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        //Log.d(TAG, "result:" + response);

                        // XML 을 JSON 으로 변환
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        String json = xmlToJson.toString();
                        Log.d(TAG, "json:" + json);

                        // JSON to Object
                        Gson gson = new Gson();

                        // 단일 항목인지 다중 항목인지 체크
                        String[] find = response.split("<list class=");
                        if (find.length > 2) {
                            // 다중
                            StopBusJsonData data = gson.fromJson(json, StopBusJsonData.class);

                            Log.d(TAG, "다중");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                this.startStopBusItems = new ArrayList<>();

                                if (data.RFC30.routeList.list != null) {
                                    this.startStopBusItems.addAll(data.RFC30.routeList.list);
                                }
                            } else {
                                // 실패
                                this.progressDialog.dismiss();
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // 단일
                            StopBusJsonSingleData data = gson.fromJson(json, StopBusJsonSingleData.class);

                            Log.d(TAG, "단일");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                this.startStopBusItems = new ArrayList<>();

                                if (data.RFC30.routeList.list != null) {
                                    this.startStopBusItems.add(data.RFC30.routeList.list);
                                }
                            } else {
                                // 실패
                                this.progressDialog.dismiss();
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (this.startStopBusItems.size() > 0) {
                            // 도착정류장의 승강장별 경유노선 목록 조회
                            callOpenApi2(arrivalId);
                        } else {
                            this.progressDialog.dismiss();
                            Toast.makeText(this, R.string.msg_start_station_via_bus_empty, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // 오류
                        onError(error.toString());
                    }
            );

            request.setShouldCache(false);      // 이전 결과가 있어도 새로 요청 (cache 사용 안함)
            this.requestQueue.add(request);
        } catch (Exception e) {
            // 오류
            onError(e.toString());
        }
    }

    /* 공공데이터 api 호출 (승강장별 경유노선 목록 조회) */
    private void callOpenApi2(String stationId) {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        try {
            String url = Constants.OpenApi.BUS_API_URL;
            url += "?serviceKey=" + Constants.OpenApi.API_KEY;
            url += "&stopStandardid=" + URLEncoder.encode(stationId, "UTF-8");  // 정류장 표준 id 로 검색

            Log.d(TAG, url);

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        //Log.d(TAG, "result:" + response);

                        // XML 을 JSON 으로 변환
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        String json = xmlToJson.toString();
                        Log.d(TAG, "json:" + json);

                        // JSON to Object
                        Gson gson = new Gson();

                        this.progressDialog.dismiss();

                        // 단일 항목인지 다중 항목인지 체크
                        String[] find = response.split("<list class=");
                        if (find.length > 2) {
                            // 다중
                            BusJsonData data = gson.fromJson(json, BusJsonData.class);

                            Log.d(TAG, "다중");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                this.arrivalBusItems = new ArrayList<>();

                                if (data.RFC30.routeList.list != null) {
                                    this.arrivalBusItems.addAll(data.RFC30.routeList.list);
                                }

                            } else {
                                // 실패
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // 단일
                            BusJsonSingleData data = gson.fromJson(json, BusJsonSingleData.class);

                            Log.d(TAG, "단일");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                this.arrivalBusItems = new ArrayList<>();

                                if (data.RFC30.routeList.list != null) {
                                    this.arrivalBusItems.add(data.RFC30.routeList.list);
                                }
                            } else {
                                // 실패
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (this.arrivalBusItems.size() > 0) {
                            // 노선 체크
                            checkBus();
                        } else {
                            Toast.makeText(this, R.string.msg_arrival_station_via_bus_empty, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // 오류
                        onError(error.toString());
                    }
            );

            request.setShouldCache(false);      // 이전 결과가 있어도 새로 요청 (cache 사용 안함)
            this.requestQueue.add(request);
        } catch (Exception e) {
            // 오류
            onError(e.toString());
        }
    }

    /* 오류 확인 */
    private void onError(String error) {
        Log.d(TAG, "error:" + error);
        this.progressDialog.dismiss();
        Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT).show();
    }

    /* 노선 확인 */
    private void checkBus() {
        GlobalVariable.findStopBusItems = new ArrayList<>();

        // 도착지를 경유하는 노선 찾기
        for (BusItem item : this.arrivalBusItems) {
            /*
            Log.d(TAG, "노선 id:" + item.brtStdId.content);
            Log.d(TAG, "노선번호:" + item.brtId.content);
            Log.d(TAG, "노선확장:" + item.brtClass.content);
            Log.d(TAG, "기점명:" + item.brtSname.content);
            Log.d(TAG, "종점명:" + item.brtEname.content);
            */

            for (StopBusItem stopItem : this.startStopBusItems) {
                if (item.brtStdId.content.equals(stopItem.brtStdid.content)) {
                    Log.d(TAG, "find 노선 id:" + stopItem.brtStdid.content);

                    GlobalVariable.findStopBusItems.add(stopItem);
                    break;
                }
            }
        }

        if (GlobalVariable.findStopBusItems.size() == 0) {
            Toast.makeText(this, R.string.msg_arrival_station_via_bus_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // 버스목록 activity 호출
        Intent intent = new Intent(this, BusListActivity.class);
        intent.putExtra("start_station", this.startName);
        intent.putExtra("arrival_station", this.arrivalName);
        startActivity(intent);

        finish();
    }
}
