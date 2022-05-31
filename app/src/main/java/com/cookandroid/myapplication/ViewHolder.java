package com.cookandroid.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ViewHolder extends RecyclerView.ViewHolder {
    public TextView BusNum;
    public TextView BusStop;
    public TextView Remaining_time;


    ViewHolder(Context context, View itemView) {
        super(itemView);

        BusNum = itemView.findViewById(R.id.BusNum);
        BusStop = itemView.findViewById(R.id.BusStop);
        Remaining_time = itemView.findViewById(R.id.Remaining_time);

        }

    }
