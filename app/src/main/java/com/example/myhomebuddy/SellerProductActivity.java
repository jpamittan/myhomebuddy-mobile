package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.ui.login.LoginActivity;
import com.example.myhomebuddy.ui.products.Products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SellerProductActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ArrayList<Products> products;
    ListView lvSellerProducts;
    ProductItemAdapter productItemAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product);

        lvSellerProducts = findViewById(R.id.lvSellerProducts);
        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        products = new ArrayList<>();

        fetchMyProducts();

        lvSellerProducts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SellerProductDetailActivity.class);
            intent.putExtra("id", products.get(position).getId());
            startActivityForResult(intent ,1);
        });

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerAddProductActivity.class);
            startActivityForResult(intent ,1);
        });
    }

    private void fetchMyProducts() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                        "Authorization",
                        token_type + " " + token
                )
                .url("http://" + host + "/api/seller/product")
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(SellerProductActivity.this,
                            "An error has occured. Please try again.",
                            Toast.LENGTH_LONG
                        ).show();
                    });
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProducts = new JSONObject(responseBody.string());

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Toast.makeText(
                                        SellerProductActivity.this,
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
                                    null
                                ));
                            }
                            productItemAdapter = new ProductItemAdapter(
                                SellerProductActivity.this,
                                R.layout.fragment_products_item,
                                products
                            );
                            runOnUiThread(() -> {
                                progress.dismiss();
                                lvSellerProducts.setAdapter(productItemAdapter);
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(SellerProductActivity.this,
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            progress.dismiss();
            Log.e("Ex", Log.getStackTraceString(e));
            Toast.makeText(this,
                    "An error has occured. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == SellerAddProductActivity.RESULT_OK){
                fetchMyProducts();
            }
        }
    }//onActivityResult
}