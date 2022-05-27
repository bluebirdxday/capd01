package com.project.jjbus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG = "jjbus";

    private BackPressHandler backPressHandler;
    private RequestQueue requestQueue;              // volley requestQueue

    private ProgressDialog progressDialog;          // 로딩 dialog
    private EditText editStart, editArrival;

    private ArrayList<StationItem> startStations;   // 출발정류장 array
    private ArrayList<StationItem> arrivalStations; // 도착정류장 array

    private InputMethodManager imm;                 // 키보드를 숨기기 위해 필요함

    private int mode;                               // 모드 (0:출발, 1:도착)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 종료 핸들러
        this.backPressHandler = new BackPressHandler(this);

        // 로딩 dialog
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("처리중...");
        this.progressDialog.setCancelable(false);

        this.editStart = findViewById(R.id.editStart);
        this.editArrival = findViewById(R.id.editArrival);

        // 키보드를 숨기기 위해 필요함
        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.btnSearch).setOnClickListener(view -> {
            // 검색
            if (checkData()) {
                this.startStations = new ArrayList<>();
                this.arrivalStations = new ArrayList<>();

                this.progressDialog.show();

                // 로딩 dialog 를 표시하기 위해 딜레이를 줌
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    this.mode = 0;      // 출발

                    // 공공데이터 api 호출 (정류장명으로 정류장 목록 구하기)
                    callOpenApi(this.editStart.getText().toString());
                }, Constants.LoadingDelay.SHORT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.backPressHandler.onBackPressed();
    }

    /* 입력 데이터 체크 */
    private boolean checkData() {
        // 출발정류장 입력 체크
        String start = this.editStart.getText().toString();
        if (TextUtils.isEmpty(start)) {
            Toast.makeText(this, R.string.msg_start_station_check_empty, Toast.LENGTH_SHORT).show();
            this.editStart.requestFocus();
            return false;
        }

        // 비밀번호 입력 체크
        String arrival = this.editArrival.getText().toString();
        if (TextUtils.isEmpty(arrival)) {
            Toast.makeText(this, R.string.msg_arrival_station_check_empty, Toast.LENGTH_SHORT).show();
            this.editArrival.requestFocus();
            return false;
        }

        // 키보드 숨기기
        this.imm.hideSoftInputFromWindow(this.editArrival.getWindowToken(), 0);

        return true;
    }

    /* 공공데이터 api 호출 (정류장명으로 정류장 목록 구하기) */
    private void callOpenApi(String station) {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        try {
            String url = Constants.OpenApi.STATION_API_URL;
            url += "?serviceKey=" + Constants.OpenApi.API_KEY;
            url += "&searchFld=stopnm";                                 // 승강장명으로 검색
            url += "&searchNm=" + URLEncoder.encode(station, "UTF-8");   // 승강장명

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
                            StationJsonData data = gson.fromJson(json, StationJsonData.class);

                            Log.d(TAG, "다중");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                if (data.RFC30.routeList.list != null) {
                                    for (StationItem item : data.RFC30.routeList.list) {
                                        Log.d(TAG, "id:" + item.stopStandardid.content);
                                        Log.d(TAG, "name:" + item.stopKname.content);
                                        Log.d(TAG, "mark:" + item.reMark.content);

                                        if (mode == 0) {
                                            // 출발
                                            this.startStations.add(item);
                                        } else {
                                            // 도착
                                            this.arrivalStations.add(item);
                                        }
                                    }
                                }
                            } else {
                                // 실패
                                this.progressDialog.dismiss();
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // 단일
                            StationJsonSingleData data = gson.fromJson(json, StationJsonSingleData.class);

                            Log.d(TAG, "단일");

                            if (data.RFC30.code.content.equals(Constants.OpenApi.SUCCESS_RESULT_CODE)) {
                                // 성공
                                if (data.RFC30.routeList.list != null) {
                                    if (mode == 0) {
                                        // 출발
                                        this.startStations.add(data.RFC30.routeList.list);
                                    } else {
                                        // 도착
                                        this.arrivalStations.add(data.RFC30.routeList.list);
                                    }
                                }
                            } else {
                                // 실패
                                this.progressDialog.dismiss();
                                Toast.makeText(this, data.RFC30.msg.content, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (mode == 0) {
                            if (this.startStations.size() == 0) {
                                // 출발정류장이 없음
                                this.progressDialog.dismiss();
                                Toast.makeText(this, R.string.msg_start_station_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                this.mode = 1;      // 도착
                                // 공공데이터 api 호출 (도착정류장)
                                callOpenApi(this.editArrival.getText().toString());
                            }
                        } else {
                            this.progressDialog.dismiss();

                            if (this.arrivalStations.size() == 0) {
                                // 도착정류장이 없음
                                Toast.makeText(this, R.string.msg_arrival_station_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                // 전역변수에 정류장 정보 저장
                                GlobalVariable.startStations = this.startStations;
                                GlobalVariable.arrivalStations = this.arrivalStations;

                                // 정류장 선택 activity 로 이동
                                Intent intent = new Intent(this, StationSelectActivity.class);
                                startActivity(intent);
                            }
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

    /* Back Press Class */
    private class BackPressHandler {
        private final Context context;
        private Toast toast;

        private long backPressedTime = 0;

        public BackPressHandler(Context context) {
            this.context = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                this.backPressedTime = System.currentTimeMillis();

                this.toast = Toast.makeText(this.context, R.string.msg_back_press_end, Toast.LENGTH_SHORT);
                this.toast.show();
                return;
            }

            if (System.currentTimeMillis() <= this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                // 종료
                moveTaskToBack(true);
                finish();
                this.toast.cancel();
            }
        }
    }
}