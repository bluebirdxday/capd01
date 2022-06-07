package com.example.dbtest3;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";

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

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long depart) {
            Log.e("On Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            Log.e("On Click", "nowIndex = " + nowIndex);
            Log.e("On Click", "Data: " + arrayData.get(position));
            String[] tempData = arrayData.get(position).split("\\s+");
            Log.e("On Click", "Split Result = " + tempData);

            btn_Insert.setEnabled(false);
        }
    };

    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long depart) {
            Log.e("Long Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            Log.e("Long Click", "nowIndex = " + nowIndex);
            Log.e("Long Click", "Data: " + arrayData.get(position));
            String[] nowData = arrayData.get(position).split("\\s+");
            Log.e("Long Click", "Split Result = " + nowData);
            String viewData = nowData[0] + ", " + nowData[1];
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumn(nowIndex);
                            showDatabase(sort);
                            setInsertMode();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            setInsertMode();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){

            int int_id = iCursor.getColumnIndex("_id");
            int int_depart = iCursor.getColumnIndex("depart");
            int int_arrival = iCursor.getColumnIndex("arrival");

            String tempIndex = iCursor.getString(int_id);
            String tempDepart = iCursor.getString(int_depart);
            tempDepart = setTextLength(tempDepart,10);
            String tempArrival = iCursor.getString(int_arrival);
            tempArrival = setTextLength(tempArrival,10);

            String Result = tempDepart + tempArrival;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:
                depart = edit_Depart.getText().toString();
                arrival = edit_Arrival.getText().toString();
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(depart, arrival);
                showDatabase(sort);
                setInsertMode();
                edit_Depart.requestFocus();
                edit_Depart.setCursorVisible(true);
                break;

            case R.id.btn_select:
                showDatabase(sort);
                break;
        }
    }
}
