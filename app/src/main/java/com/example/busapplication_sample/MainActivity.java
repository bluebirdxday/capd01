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
    // ListView busStation_text;
    // IconTextListAdapter adapter;
    String brtStdid_info="305001258"; //  brtStdid 데이터 가져오기 (상세페이지에서)
    String busStop = "306101035"; // 정류장id 받아오기 (상세페이지에서)
    String brtId_info = "970"; // 정류장id 받아오기 (상세페이지에서)
    String rStop_info = "38"; // 남은 정류장 RStop 받아오기 (상세페이지에서)
    String rStop_data="";
    String[] busStopArr = new String[100];
    int i=0;
    int flag=0;

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

        realBus_text = (TextView)findViewById(R.id.rStop);
        // busStation_text = (ListView) findViewById(R.id.busStation);
        // adapter = new IconTextListAdapter(this);
        busStation_text = (TextView)findViewById(R.id.busStation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //realBus_data = getXmlData_busInfo(); // 남은 정류장수 api 데이터
                busStation_data = getXmlData_nextBusStop();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO Auto-generated method stub
                        realBus_text.setText(realBus_data);
                        busStation_text.setText(busStation_data);
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
            String brtStdid_data="";

            xpp.next(); // 파일 탐색 시작
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱시작\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // 태그 이름 가져오기

                        if(tag.equals("brtStdid")){
                            xpp.next();
                            if(xpp.getText().equals(brtStdid_info)) {
                                brtStdid_data = brtStdid_info;
                                //buffer.append(nextBusStop);
                                //buffer.append("\n");
                            }
                        }

                        if(tag.equals("RStop")){
                            xpp.next();
                            if(xpp.getText().equals(rStop_info)) {
                                rStop_data = rStop_info;
                                //buffer.append(nextBusStop);
                                //buffer.append("\n");
                            }
                        }

                        if(tag.equals("viaStopname")) {
                            xpp.next();
                            // buffer.append(xpp.getText());
                            nextBusStop = xpp.getText();
                            //Log.i("nextBusStop", nextBusStop);
                            if(brtStdid_data.equals(brtStdid_info) && rStop_data.equals(rStop_info)) {
                                Log.i("nextBusStop", nextBusStop);
                                nextBus_data = getXmlData_busStation(nextBusStop);
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

    private String getXmlData_busStation(String nextBusStop) {
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
                    buffer.append("이번정류장 : " + busStopArr[check]);
                    buffer.append("\n");
                    buffer.append("다음정류장 : " + busStopArr[check+1]);
                    buffer.append("\n");
                    buffer.append("다다음정류장 : " + busStopArr[check+2]);
                    buffer.append("\n");
                    Log.i("check:", busStopArr[check]);
                    break;
                }
            }

        } catch(Exception e) {

        }
        //buffer.append("파싱 끝\n");


        return buffer.toString(); // 문자열 객체 반환
    }
}