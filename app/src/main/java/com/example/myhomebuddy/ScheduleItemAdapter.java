package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhomebuddy.ui.schedule.ScheduleItem;

import java.util.ArrayList;

public class ScheduleItemAdapter extends ArrayAdapter<ScheduleItem> {

    private final Context mContext;
    private final int mResource;

    public ScheduleItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ScheduleItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n", "DefaultLocale"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView txtvScheduleItemDate = convertView.findViewById(R.id.txtvScheduleItemDate);
        TextView txtvScheduleItemQty = convertView.findViewById(R.id.txtvScheduleItemQty);
        TextView txtvScheduleItemStatus = convertView.findViewById(R.id.txtvScheduleItemStatus);

        txtvScheduleItemDate.setText(getItem(position).getDate() + " " + getItem(position).getTime());
        txtvScheduleItemQty.setText(
            String.format(
                "₱ %s - %s pc(s)",
                getItem(position).getPrice(),
                getItem(position).getQty()
            )
        );
        if (getItem(position).getStatus().equals("Cancelled")) {
            txtvScheduleItemStatus.setTextColor(Color.parseColor("#E74C3C"));
        } else {
            txtvScheduleItemStatus.setTextColor(Color.parseColor("#2ECC71"));
        }
        txtvScheduleItemStatus.setText(getItem(position).getStatus());
        if (
            getItem(position).getStatus().equals(null) ||
            getItem(position).getStatus().equals("null") ||
            getItem(position).getStatus().isEmpty()
        ) {
            txtvScheduleItemStatus.setVisibility(View.GONE);
        }

        return convertView;
    }
}
