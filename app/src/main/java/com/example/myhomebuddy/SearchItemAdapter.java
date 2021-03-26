package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchItemAdapter extends ArrayAdapter<Search> {

    private final Context mContext;
    private final int mResource;

    public SearchItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Search> objects) {
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

        ImageView imgvSearchImage = convertView.findViewById(R.id.imgvSearchImage);
        TextView txtvSearchName = convertView.findViewById(R.id.txtvSearchName);
        TextView txtvSearchDesc = convertView.findViewById(R.id.txtvSearchDesc);
        RatingBar rbSearchRatings = convertView.findViewById(R.id.rbSearchRatings);
        TextView txtbSearchRatings = convertView.findViewById(R.id.txtbSearchRatings);

        Picasso.get().load(getItem(position).getImage()).into(imgvSearchImage);
        txtvSearchName.setText(getItem(position).getName());
        txtvSearchDesc.setText(getItem(position).getDescription());
        rbSearchRatings.setRating(getItem(position).getRatings());
        txtbSearchRatings.setText(String.valueOf(getItem(position).getRatings()));

        return convertView;
    }
}
