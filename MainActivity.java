package com.example.dbtest;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private UserDatabaseHelper userDatabaseHelper;
    public static final String TABLE_NAME = "user";
    SQLiteDatabase database;

    Button selectButton, insertButton, deleteButton;
    EditText startEdit, destinationEdit;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectButton = findViewById(R.id.select_button);
        insertButton = findViewById(R.id.insert_button);
        deleteButton = findViewById(R.id.delete_button);
        startEdit = findViewById(R.id.editText1);
        destinationEdit = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView);

        userDatabaseHelper = UserDatabaseHelper.getInstance(this);
        database = userDatabaseHelper.getWritableDatabase();

        //조회버튼
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectData(TABLE_NAME);
            }
        });

        //추가버튼
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = startEdit.getText().toString().trim();
                String destination = destinationEdit.getText().toString().trim();
                insertData(start, destination);
            }
        });

        //삭제버튼
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = startEdit.getText().toString().trim();
                String destination = destinationEdit.getText().toString().trim();
                deleteData(start, destination);
            }
        });
    }

    //조회 데이터
    private void selectData(String tableName) {
        if(database != null){
            String sql = "SELECT start, destination FROM " + tableName;
            Cursor cursor = database.rawQuery(sql, null);

            println("데이터 갯수 : " + cursor.getCount());

            for (int i = 0; i <cursor.getCount(); i++) {
                cursor.moveToNext();
                String start = cursor.getString(0);
                String destination = cursor.getString(1);
                println("[" + i + "] 출발지 : " + start + ", 목적지 : " + destination);
            }
            cursor.close();
        }
    }

    //추가 데이터
    private void insertData(String start, String destination) {
        if (database != null) {
            String sql = "INSERT INTO user(start, destination) VALUES(?, ?)";
            Object [] params = {start, destination};
            database.execSQL(sql, params);
        }
    }

    //삭제 데이터
    private void deleteData(String start, String destination) {
        if (database != null){
            String sql = "DELETE FROM user WHERE start AND destination = ?";
            Object [] params = {start, destination};
            database.execSQL(sql, params);
        }
    }

    public void println (String data) {
        textView.append(data + "\n");
    }

    @Override
    protected void onDestroy() {
        userDatabaseHelper.close();
        super.onDestroy();
    }
}
