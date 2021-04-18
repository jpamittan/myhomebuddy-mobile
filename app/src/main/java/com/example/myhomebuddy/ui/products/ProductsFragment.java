package com.example.myhomebuddy.ui.products;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myhomebuddy.ConsumerProductDetailActivity;
import com.example.myhomebuddy.ProductItemAdapter;
import com.example.myhomebuddy.R;
import com.example.myhomebuddy.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProductsFragment extends Fragment {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-13-229-96-65.ap-southeast-1.compute.amazonaws.com";
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    View root;
    SearchView svSellerProducts;
    ListView lvProducts;
    ProductItemAdapter productItemAdapter;
    ProgressDialog progress;
    String token_type;
    String token;
    ArrayList<Products> products;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_products, container, false);
        svSellerProducts = root.findViewById(R.id.svSellerProducts);
        lvProducts = root.findViewById(R.id.lvProducts);
        products = new ArrayList<>();

        SharedPreferences shared = getActivity().getSharedPreferences(
            SHARED_PREFS_TOKEN,
            Context.MODE_PRIVATE
        );
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this.getContext());
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        fetchProducts();

        svSellerProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ConsumerProductDetailActivity.class);
            intent.putExtra("id", products.get(position).getId());
            startActivity(intent);
        });

        return root;
    }

    private void fetchProducts() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                        "Authorization",
                        token_type + " " + token
                )
                .url("http://" + host + "/api/consumer/product")
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    progress.dismiss();
                    Log.e("Fail", e.getMessage());
                    Toast.makeText(
                        getActivity(),
                        "An error has occured. Please try again.",
                        Toast.LENGTH_LONG
                    ).show();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    progress.dismiss();
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProducts = new JSONObject(responseBody.string());

                        if (!response.isSuccessful()) {
                            getActivity()
                                .runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(
                                            getActivity(),
                                            joProducts.getString("error"),
                                            Toast.LENGTH_LONG
                                        ).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        } else {
                            JSONArray dataArr = joProducts.getJSONArray("data");
                            Log.i("Products", dataArr.toString());

                            products.clear();
                            for (int i = 0; i < dataArr.length(); i++) {
                                products.add(new Products(
                                    dataArr.getJSONObject(i).getInt("id"),
                                    dataArr.getJSONObject(i).getString("name"),
                                    dataArr.getJSONObject(i).getString("image"),
                                    dataArr.getJSONObject(i).getString("category"),
                                    dataArr.getJSONObject(i).getString("sub_category"),
                                    Double.parseDouble(
                                            dataArr.getJSONObject(i).getString("price")
                                    ),
                                    dataArr.getJSONObject(i).getInt("quantity"),
                                    dataArr.getJSONObject(i).getInt("stock_threshold"),
                                    null
                                ));
                            }
                            productItemAdapter = new ProductItemAdapter(
                                getActivity(),
                                R.layout.fragment_products_item,
                                products
                            );
                            getActivity()
                                .runOnUiThread(() -> lvProducts.setAdapter(productItemAdapter));
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        Toast.makeText(getActivity(),
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            });
        } catch (Exception e) {
            progress.dismiss();
            Log.e("Ex", Log.getStackTraceString(e));
            Toast.makeText(getActivity(),
                "An error has occured. Please try again.",
                Toast.LENGTH_LONG
            ).show();
        }
    }
}