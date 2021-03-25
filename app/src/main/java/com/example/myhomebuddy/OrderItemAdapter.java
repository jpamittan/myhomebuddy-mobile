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

import com.example.myhomebuddy.ui.schedule.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderItemAdapter extends ArrayAdapter<Order> {

    private final Context mContext;
    private final int mResource;

    public OrderItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Order> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n" })
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imgvOrderItemImage = convertView.findViewById(R.id.imgvOrderItemImage);
        TextView txtvOrderItemName = convertView.findViewById(R.id.txtvOrderItemName);
        TextView txtvOrderItemSeller = convertView.findViewById(R.id.txtvOrderItemSeller);
        TextView txtvOrderItemCategory = convertView.findViewById(R.id.txtvOrderItemCategory);

        Picasso.get().load(getItem(position).getImage()).into(imgvOrderItemImage);
        txtvOrderItemName.setText(getItem(position).getProduct_name());
        txtvOrderItemSeller.setText(getItem(position).getSeller_name());
        txtvOrderItemCategory.setText(
            getItem(position).getCategory() + " - " + getItem(position).getSub_category()
        );

        return convertView;
    }
}
