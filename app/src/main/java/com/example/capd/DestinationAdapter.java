package com.example.capd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.capd.CoordinatesCalculate.Data;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {

    private List<Data> dataList;
    private Context context;

    public DestinationAdapter(Context context, List<Data> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DestinationAdapter.DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destination_list_item, parent, false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        holder.destinationName.setText(dataList.get(position).getPlace_name());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DestinationViewHolder extends RecyclerView.ViewHolder{

        TextView destinationName;

        public DestinationViewHolder(View itemView){
            super(itemView);
            destinationName = (TextView)itemView.findViewById(R.id.destinationName);
        }
    }


}

