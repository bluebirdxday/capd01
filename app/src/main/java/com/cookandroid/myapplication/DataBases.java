package com.cookandroid.myapplication;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String DEPART = "depart";
        public static final String ARRIVAL = "arrival";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +DEPART+" text not null , "
                +ARRIVAL+" text not null );";
    }
}