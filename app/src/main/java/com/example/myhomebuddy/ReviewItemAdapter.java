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

import com.example.myhomebuddy.ui.reviews.Reviews;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReviewItemAdapter extends ArrayAdapter<Reviews> {

    private final Context mContext;
    private final int mResource;

    public ReviewItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Reviews> objects) {
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

        ImageView imgvReviewUserImage = convertView.findViewById(R.id.imgvReviewUserImage);
        TextView txtvReviewUserFullname = convertView.findViewById(R.id.txtvReviewUserFullname);
        TextView txtvReviewRatings = convertView.findViewById(R.id.txtvReviewRatings);
        RatingBar rbReviewRatings = convertView.findViewById(R.id.rbReviewRatings);
        TextView txtvReviewCreatedAt = convertView.findViewById(R.id.txtvReviewCreatedAt);
        TextView txtvReviewMessage = convertView.findViewById(R.id.txtvReviewMessage);

        JSONObject user = getItem(position).getUser();

        try {
            txtvReviewUserFullname.setText(
                user.getString("first_name") + " "
                + user.getString("middle_name") + " "
                + user.getString("last_name")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtvReviewRatings.setText(String.valueOf(getItem(position).getRatings()));
        rbReviewRatings.setRating(getItem(position).getRatings());
        txtvReviewCreatedAt.setText(getItem(position).getCreated_at());
        txtvReviewMessage.setText(getItem(position).getMessage());

        return convertView;
    }
}
