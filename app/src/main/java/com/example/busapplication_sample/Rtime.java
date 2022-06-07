package com.example.busapplication_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Rtime extends AppCompatActivity {

    String busInfo_apiUrl = "http://openapi.jeonju.go.kr/jeonjubus/openApi/traffic/bus_location_busstop_infomation_common.do?serviceKey=AwRbaT3aKDn7OkKd7kOGQ27Xex7Vk2MEGe40P0nIasIML3R28w15roIg2zY5vALWp98E3VDQYZBdr9sjZRSSCQ%3D%3D"; //버스실시간정보 api
    String serviceKey = "Dqiwvrn7SRueQtG3ki%2F3ty5CPwBfs5nL3pj8lcniVaKIYTpwcki3tKsUUKpFHsiB4QMMhJ%2BDo%2F5eDRr6pbVzig%3D%3D"; //인증키
    TextView realBus_text;
    String realBus_data;
    String brtStdid_info="305001577"; //  brtStdid 데이터 가져오기 (상세페이지에서)
    String busStop = "306101035"; // 정류장id 받아오기 (상세페이지에서)
    String brtId_info = "970"; // 정류장id 받아오기 (상세페이지에서)
    String rStop_info = "38"; // 남은 정류장 RStop 받아오기 (상세페이지에서)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rtime);

        realBus_text = (TextView)findViewById(R.id.rStop);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                realBus_data = getXmlData_busInfo(); // 남은 정류장수 api 데이터

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO Auto-generated method stub
                        realBus_text.setText(realBus_data);
                    }
                });
            }
        }).start();

    }



    // xml 데이터 파싱
    private String getXmlData_busInfo() {
        StringBuffer buffer = new StringBuffer();

        String testUrl = busInfo_apiUrl
                + "&stopStdid=" + busStop;
        //+ "&lKey=LOW";
        Log.i("test", testUrl);

        try {
            URL url = new URL(testUrl); // 문자열로 된 요청 url을 URL 객체로 생성
            InputStream is = url.openStream();
            // Log.i("yes", "no");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); // xml 입력 받기

            String tag;
            String rStop="";
            String rTime="";
            int time=0;

            xpp.next(); // 파일 탐색 시작
            int eventType = xpp.getEventType();

            // Log.i("eventType", eventType+"");

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.i("시작", eventType+"");
                        // buffer.append("파싱시작..\n\n");
                        Log.i("buffer", buffer.toString());
                        break;

                    case XmlPullParser.START_TAG:
                        Log.i("eventType", eventType+"");
                        tag = xpp.getName(); // 태그 이름 얻어오기

                        //if(tag.equals ("list")); // 첫번째 검색결과

                        // 남은 정류장
                        if(tag.equals("RStop")) {
                            xpp.next();
                            rStop = xpp.getText();
                            // buffer.append(rStop);
                            Log.i("rStop", rStop);
                            // buffer.append("\n");
                        }

                        // 남은 시간
                        if(tag.equals("RTime")) {
                            xpp.next();
                            rTime = xpp.getText();
                            Log.i("rTime", rTime);
                            time = Integer.parseInt(rTime)/60;
                            //buffer.append("\n");

                        }

                        // 노선 ID
                        if(tag.equals("brtId")) {
                            xpp.next();
                            if(xpp.getText().equals(brtId_info) && rStop_info.equals(rStop)) {
                                buffer.append("버스 번호 : ");
                                buffer.append(brtId_info);
                                buffer.append("\n");
                                buffer.append("남은 정류장 : ");
                                buffer.append(rStop);
                                buffer.append("개");
                                buffer.append("\n");
                                buffer.append("남은 시간 : ");
                                rTime = Integer.toString(time);
                                buffer.append(rTime);
                                buffer.append("분");
                                //buffer.append(xpp.getText());
                            }
                        }
                        Log.i("buffer", buffer.toString());

                        break;

                    case XmlPullParser.TEXT:
                        Log.i("eventType", eventType+"");
                        Log.i("buffer", buffer.toString());
                        break;

                    case XmlPullParser.END_TAG:
                        Log.i("eventType", eventType+"");
                        tag = xpp.getName(); // 태그 이름 얻어오기

                        if(tag.equals("list")) //buffer.append("\n");
                        Log.i("buffer", buffer.toString());
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.i("error", e.toString());
        }
        // buffer.append("파싱 끝\n");

        return buffer.toString(); //StringBuffer 문자열 객체 반환

    }

}
