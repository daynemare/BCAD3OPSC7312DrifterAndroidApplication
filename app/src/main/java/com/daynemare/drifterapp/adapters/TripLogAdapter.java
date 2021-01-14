package com.daynemare.drifterapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daynemare.drifterapp.R;

import java.util.ArrayList;

public class TripLogAdapter extends RecyclerView.Adapter<TripLogAdapter.MyViewHolder>{

    ArrayList<String> arrayDateTime;
    ArrayList<String> arrayTo;
    ArrayList<String> arrayFrom;
    ArrayList<String> imTransportMode;
    Context context;

    public TripLogAdapter(Context con,ArrayList<String> arDateTime,ArrayList<String> arIm,ArrayList<String> arTo,ArrayList<String> arFrom){

        context = con;
        arrayDateTime = arDateTime;
        imTransportMode = arIm;
        arrayTo = arTo;
        arrayFrom = arFrom;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trip_log_rows,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripLogAdapter.MyViewHolder holder, int position) {


        String dateTimeTitle = holder.tvDateTime.getText().toString();
        String toTitle = holder.tvTo.getText().toString();
        String fromTitle = holder.tvFrom.getText().toString();

        holder.tvDateTime.setText(String.format("%s%s", dateTimeTitle, arrayDateTime.get(position)));
        holder.tvTo.setText(String.format("%s%s", toTitle, arrayTo.get(position)));
        holder.tvFrom.setText(String.format("%s%s", fromTitle, arrayFrom.get(position)));

        switch (imTransportMode.get(position)) {
            case ("Driving"):
                holder.iv.setImageResource(R.drawable.ic_directions_car_black_24dp);
                break;
            case ("Cycling"):
                holder.iv.setImageResource(R.drawable.ic_directions_bike_black_24dp);
                break;
            case ("Walking"):
                holder.iv.setImageResource(R.drawable.ic_directions_walk_black_24dp);
                break;

        }
    }

    @Override
    public int getItemCount() {

        return arrayDateTime.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvDateTime;
        TextView tvTo;
        TextView tvFrom;
        ImageView iv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvTo = itemView.findViewById(R.id.tvToContent);
            tvFrom = itemView.findViewById(R.id.tvFromContent);
            iv = itemView.findViewById(R.id.ivTran);

        }
    }
}
