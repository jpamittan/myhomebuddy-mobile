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

import com.example.myhomebuddy.ui.schedule.MyOrder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerOrderItemAdapter extends ArrayAdapter<MyOrder> {

    private final Context mContext;
    private final int mResource;

    public SellerOrderItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MyOrder> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imgvSOrderProductItemImage = convertView.findViewById(R.id.imgvSOrderProductItemImage);
        TextView txtvSOrderProductItemName = convertView.findViewById(R.id.txtvSOrderProductItemName);
        TextView txtvSOrderProductItemContact = convertView.findViewById(R.id.txtvSOrderProductItemContact);
        TextView txtvSOrderProductItemLocation = convertView.findViewById(R.id.txtvSOrderProductItemLocation);

        if (!getItem(position).getImage().equals("")) {
            Picasso.get().load(getItem(position).getImage()).into(imgvSOrderProductItemImage);
        }
        txtvSOrderProductItemName.setText(
            getItem(position).getFirst_name() + " " +
            getItem(position).getMiddle_name() + " " +
            getItem(position).getLast_name()
        );
        txtvSOrderProductItemContact.setText(
            getItem(position).getEmail() + " / " + getItem(position).getContact()
        );
        txtvSOrderProductItemLocation.setText(getItem(position).getCity());

        return convertView;
    }
}
