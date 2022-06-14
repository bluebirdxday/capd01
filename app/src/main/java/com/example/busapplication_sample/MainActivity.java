package com.example.busapplication_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity {

    String busInfo_apiUrl = "http://openapi.jeonju.go.kr/jeonjubus/openApi/traffic/bus_location_busstop_infomation_common.do?serviceKey=AwRbaT3aKDn7OkKd7kOGQ27Xex7Vk2MEGe40P0nIasIML3R28w15roIg2zY5vALWp98E3VDQYZBdr9sjZRSSCQ%3D%3D"; //버스실시간정보 api
    String busStation_apiUrl = "http://openapi.jeonju.go.kr/jeonjubus/openApi/traffic/bus_location_busstop_list_common.do"; // 버스 정류장 api 정보
    String serviceKey = "Dqiwvrn7SRueQtG3ki%2F3ty5CPwBfs5nL3pj8lcniVaKIYTpwcki3tKsUUKpFHsiB4QMMhJ%2BDo%2F5eDRr6pbVzig%3D%3D"; //인증키
    TextView realBus_text;
    String realBus_data;
    String busStation_data; // 버스 번호 데이터
    TextView busStation_text;
    String brtStdid_info="305001258"; //  brtStdid 버스번호ID 데이터 가져오기 (상세페이지에서)
    String busStop = "306101035"; // 정류장id 받아오기 (상세페이지에서)
    String brtId_info = "970"; // 버스 번호 받아오기 (상세페이지에서)
    String bidNo_info = "305041401"; // 버스 ID 받아오기 (상세페이지에서)
    String bidNo_data = "";
    String[] busStopArr = new String[100];
    int i=0;
    int flag=0;
    String busStop1 = "";
    String busStop2 = "";
    String busStop3 = "";
    TextView busStop_1;
    TextView busStop_2;
    TextView busStop_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button rTime_btu = (Button) findViewById(R.id.rTime_btu);
        rTime_btu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Rtime.class);
                startActivity(intent);
            }
        });

        busStation_text = (TextView)findViewById(R.id.busStation);
        busStop_1 = findViewById(R.id.busStop_1);
        busStop_2 = findViewById(R.id.busStop_2);
        busStop_3 = findViewById(R.id.busStop_3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                busStation_data = getXmlData_nextBusStop();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO Auto-generated method stub
                        //realBus_text.setText(realBus_data);
                        busStation_text.setText(busStation_data);
                        busStop_1.setText(busStop1);
                        busStop_2.setText(busStop2);
                        busStop_3.setText(busStop3);
                    }
                });
            }
        }).start();



    }

    private String getXmlData_nextBusStop() {
        StringBuffer buffer = new StringBuffer();

        String testUrl = busInfo_apiUrl
                + "&stopStdid=" + busStop;

        try {
            URL url = new URL(testUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); // xml 입력 받기

            String tag;
            String nextBusStop="";
            String nextBus_data="";
            String brtId_data="";

            xpp.next(); // 파일 탐색 시작
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱시작\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // 태그 이름 가져오기
                        // 버스 번호 가져오기
                        if(tag.equals("brtId")){
                            xpp.next();
                            if(xpp.getText().equals(brtId_info)) {
                                brtId_data = brtId_info;
                                //buffer.append(nextBusStop);
                                //buffer.append("\n");
                            }
                        }

                        if(tag.equals("bidNo")){
                            xpp.next();
                            if(xpp.getText().equals(bidNo_info)) {
                                bidNo_data = bidNo_info;
                                //buffer.append(nextBusStop);
                                //buffer.append("\n");
                            }
                        }

                        // 지나간 정류장
                        if(tag.equals("viaStopname")) {
                            xpp.next();
                            // buffer.append(xpp.getText());
                            nextBusStop = xpp.getText();
                            //Log.i("nextBusStop", nextBusStop);
                            if(brtId_data.equals(brtId_info) && bidNo_data.equals(bidNo_info)) {
                                Log.i("nextBusStop", nextBusStop);
                                nextBus_data = getXmlData_busStation1(nextBusStop);
                                buffer.append(nextBus_data);

                            }

                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag=xpp.getName();
                        if(tag.equals("list")) //buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }

        } catch(Exception e) {

        }
        //buffer.append("파싱 끝\n");
        Log.i("buffer", buffer.toString());
        return buffer.toString();
    }

    private String getXmlData_busStation1(String nextBusStop) {
        StringBuffer buffer = new StringBuffer();


        String testUrl = busStation_apiUrl
                + "?serviceKey=" + serviceKey
                + "&brtStdid=" + brtStdid_info
                + "&lKey=LOW";

        try{
            URL url = new URL(testUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); // xml 입력받기

            String tag="";
            //String stopKname_data="";
            xpp.next(); // 파일 탐색 시작
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // 태그 이름 얻어오기

                        // 정류장
                        if(tag.equals("stopKname")) {
                            xpp.next();
                            busStopArr[i] = xpp.getText();
                            Log.i("arr:", busStopArr[i]);
                            i++;
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if(tag.equals("list")) //buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
            for(int check=0; check<busStopArr.length; check++){
                if(busStopArr[check].equals(nextBusStop)){
                    //buffer.append("이번정류장 : " + busStopArr[check] + "\n");
                    //buffer.append("다음정류장 : " + busStopArr[check+1] + "\n");
                    //buffer.append("다다음정류장 : " + busStopArr[check+2] + "\n");

                    busStop1 = "이번정류장 : " + busStopArr[check] + "\n";
                    busStop2 = "다음정류장 : " + busStopArr[check+1] + "\n";
                    busStop3 = "다다음정류장 : " + busStopArr[check+2] + "\n";
                    Log.i("check:", busStopArr[check]);
                    break;
                }
            }



        } catch(Exception e) {

        }
        return buffer.toString(); // 문자열 객체 반환
    }

}