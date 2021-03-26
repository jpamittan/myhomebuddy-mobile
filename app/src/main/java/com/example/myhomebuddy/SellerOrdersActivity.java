package com.example.myhomebuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.ui.schedule.MyOrder;

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

public class SellerOrdersActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ListView lvSellerOrders;
    SellerOrderItemAdapter sellerOrderItemAdapter;
    ArrayList<MyOrder> myOrders;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);

        lvSellerOrders = findViewById(R.id.lvSellerOrders);
        myOrders = new ArrayList<>();

        SharedPreferences shared = getSharedPreferences(
                SHARED_PREFS_TOKEN,
                Context.MODE_PRIVATE
        );
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        fetchOrders();

        lvSellerOrders.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SellerOrderDetailActivity.class);
            intent.putExtra("id", myOrders.get(position).getId());
            startActivity(intent);
        });
    }

    private void fetchOrders() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                    "Authorization",
                    token_type + " " + token
                )
                .url("http://" + host + "/api/order")
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    progress.dismiss();
                    Log.e("Fail", e.getMessage());
                    Toast.makeText(
                        SellerOrdersActivity.this,
                        "An error has occured. Please try again.",
                        Toast.LENGTH_LONG
                    ).show();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    progress.dismiss();
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joOrders = new JSONObject(responseBody.string());

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(
                                        SellerOrdersActivity.this,
                                        joOrders.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            JSONArray dataArr = joOrders.getJSONObject("data")
                                .getJSONArray("product_orders");
                            Log.i("Order", dataArr.toString());

                            myOrders.clear();
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONArray jaProductOrders = dataArr.getJSONObject(i)
                                    .getJSONArray("orders");
                                for (int y = 0; y < jaProductOrders.length(); y++) {
                                    JSONObject joConsumerProperties =
                                        new JSONObject(
                                            jaProductOrders.getJSONObject(y)
                                                .getJSONObject("consumer")
                                                .getString("properties")
                                        );
                                    myOrders.add(new MyOrder(
                                        jaProductOrders.getJSONObject(y).getInt("id"),
                                        jaProductOrders.getJSONObject(y).getInt("user_id"),
                                        "",
                                        jaProductOrders.getJSONObject(y).getJSONObject("consumer")
                                            .getString("first_name"),
                                        jaProductOrders.getJSONObject(y).getJSONObject("consumer")
                                            .getString("middle_name"),
                                        jaProductOrders.getJSONObject(y).getJSONObject("consumer")
                                            .getString("last_name"),
                                        jaProductOrders.getJSONObject(y).getJSONObject("consumer")
                                            .getString("email"),
                                        joConsumerProperties.getString("phone_no"),
                                        joConsumerProperties.getJSONObject("address")
                                            .getString("city")
                                    ));
                                }
                            }
                            sellerOrderItemAdapter = new SellerOrderItemAdapter(
                                SellerOrdersActivity.this,
                                R.layout.fragment_seller_order_item,
                                myOrders
                            );

                            runOnUiThread(() -> lvSellerOrders.setAdapter(sellerOrderItemAdapter));
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        Toast.makeText(
                            SellerOrdersActivity.this,
                            "An error has occured. Please try again.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                }
            });
        } catch (Exception e) {
            progress.dismiss();
            Log.e("Ex", Log.getStackTraceString(e));
            Toast.makeText(
                SellerOrdersActivity.this,
                "An error has occured. Please try again.",
                Toast.LENGTH_LONG
            ).show();
        }
    }
}