package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhomebuddy.ui.schedule.ScheduleItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ScheduleItemAdapter extends ArrayAdapter<ScheduleItem> {

    private final Context mContext;
    private final int mResource;

    public ScheduleItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ScheduleItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imgvScheduleItemImage = convertView.findViewById(R.id.imgvScheduleItemImage);
        TextView txtvScheduleItemName = convertView.findViewById(R.id.txtvScheduleItemName);
        TextView txtvScheduleItemQty = convertView.findViewById(R.id.txtvScheduleItemQty);
        TextView txtvScheduleItemTime = convertView.findViewById(R.id.txtvScheduleItemTime);
        TextView txtvScheduleItemMonth = convertView.findViewById(R.id.txtvScheduleItemMonth);
        TextView txtvScheduleItemDay = convertView.findViewById(R.id.txtvScheduleItemDay);

        Picasso.get().load(getItem(position).getImage()).into(imgvScheduleItemImage);
        txtvScheduleItemName.setText(getItem(position).getName());
        txtvScheduleItemQty.setText(String.format("%s pc(s)", String.valueOf(getItem(position).getQty())));
        txtvScheduleItemTime.setText(getItem(position).getTime());
        txtvScheduleItemMonth.setText(getItem(position).getMonth());
        txtvScheduleItemDay.setText(String.valueOf(getItem(position).getDay()));

        return convertView;
    }
}
