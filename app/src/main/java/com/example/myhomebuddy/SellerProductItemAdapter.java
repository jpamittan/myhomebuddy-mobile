package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhomebuddy.ui.products.Products;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SellerProductItemAdapter extends ArrayAdapter<Products> {

    private final Context mContext;
    private final int mResource;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public SellerProductItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Products> objects) {
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

        ImageView imgvProductItemImage = convertView.findViewById(R.id.imgvProductItemImage);
        TextView txtvProductItemName = convertView.findViewById(R.id.txtvProductItemName);
        TextView txtvProductItemCategory = convertView.findViewById(R.id.txtvProductItemCategory);
        TextView txtvProductItemSubCategory = convertView.findViewById(R.id.txtvProductItemSubCategory);
        TextView txtvProductItemPrice = convertView.findViewById(R.id.txtvProductItemPrice);

        Picasso.get().load(getItem(position).getImage()).into(imgvProductItemImage);
        txtvProductItemName.setText(getItem(position).getName());
        txtvProductItemCategory.setText(getItem(position).getCategory());

        if (getItem(position).getQuantity() <= 0) {
            txtvProductItemSubCategory.setText("Out of stock");
            txtvProductItemSubCategory.setTextColor(Color.parseColor("#E74C3C"));
        } else if (getItem(position).getThreshold() > getItem(position).getQuantity()) {
            txtvProductItemSubCategory.setText("Low stocks");
            txtvProductItemSubCategory.setTextColor(Color.parseColor("#F1C40F"));
        } else {
            txtvProductItemSubCategory.setText(getItem(position).getSubcategory());
        }

        txtvProductItemPrice.setText("₱ " + df.format(getItem(position).getPrice()));

        return convertView;
    }
}
