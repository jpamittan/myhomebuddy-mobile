package com.example.myhomebuddy.ui.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myhomebuddy.ProductItemAdapter;
import com.example.myhomebuddy.R;
import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);
        ListView lvProducts = root.findViewById(R.id.lvProducts);
        ArrayList<Products> products = new ArrayList<>();
        products.add(new Products(
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png",
                "Alkaline",
                "Muntinlupa City",
                40.00
        ));
        products.add(new Products(
                "Petron LPG Gasul",
                "https://gosul.ph/wp-content/uploads/2020/06/Petron-Gasul-Pol-Valve-11kgs.png",
                "Gasul",
                "Las Pinas City",
                950.00
        ));
        products.add(new Products(
                "LifeMate Mineral Water",
                "https://cf.shopee.ph/file/2e6973aabdec06d6fdd915151b55a0b4",
                "Mineral",
                "Makati City",
                30.00
        ));
        products.add(new Products(
                "Solane LPG",
                "https://jnagaexpress.files.wordpress.com/2018/06/gasul.jpg",
                "Gasul",
                "Pasay City",
                900.00
        ));
        ProductItemAdapter productItemAdapter = new ProductItemAdapter(
                this.getContext(),
                R.layout.fragment_products_item,
                products
        );
        lvProducts.setAdapter(productItemAdapter);

        return root;
    }
}