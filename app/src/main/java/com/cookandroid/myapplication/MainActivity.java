package com.cookandroid.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnDS;
    Button btnStar;
    Button btnFinish;

    //예늘 변수 즐겨찾기리스트

    TTS tts;
    Button btn_Insert;
    Button btn_Select;
    EditText edit_Depart;
    EditText edit_Arrival;
    TextView text_Depart;
    TextView text_Arrival;

    long nowIndex;
    String depart;
    String arrival;

    String sort = "depart";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDS = (Button) findViewById(R.id.btnDS);

        /*예늘 변수
        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);
        btn_Select = (Button) findViewById(R.id.btn_select);
        btn_Select.setOnClickListener(this);
        edit_Depart = (EditText) findViewById(R.id.edit_depart);
        edit_Arrival = (EditText) findViewById(R.id.edit_arrival);
        text_Depart = (TextView) findViewById(R.id.text_depart);
        text_Arrival = (TextView) findViewById(R.id.text_arrival);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);

        tts = new TTS(this);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        btn_Insert.setEnabled(true);
    }

    public void setInsertMode(){
        edit_Depart.setText("");
        edit_Arrival.setText("");
        btn_Insert.setEnabled(true);
    }

         */

        btnDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnStar = (Button) findViewById(R.id.btnStar);

        btnStar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){}
        });

        btnFinish = (Button) findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("종료 재확인")
                        .setMessage("애플리케이션을 종료하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "애플리케이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", null)
//"NO"를 사용자가 클릭했을 때에는 null. 즉 아무 작업도 수행하지 않고 다시 main화면으로 돌아가게 됩니다.
                        .show();
            }
        });



    }
}