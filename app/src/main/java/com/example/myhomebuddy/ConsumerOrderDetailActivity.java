package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.ui.schedule.ScheduleItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ConsumerOrderDetailActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ProgressDialog progress;
    int productId;
    ImageView imgvOrderProductImage;
    TextView txtvOrderProductName;
    TextView txtvOrderProductCategory;
    TextView txtvOrderProductSubCategory;
    TextView txtvOrderProductPrice;
    TextView txtvOrderSellerName;
    TextView txtvOrderSellerAddress;
    TextView txtvOrderSellerAddress2;
    TextView txtvOrderSellerAddress3;
    TextView txtvOrderQty;
    TextView txtvOrderTotalPrice;
    ListView lvOrderSchedules;
    ArrayList<ScheduleItem> scheduleItems;
    ScheduleItemAdapter scheduleItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_order_detail);

        imgvOrderProductImage = findViewById(R.id.imgvOrderProductImage);
        txtvOrderProductName = findViewById(R.id.txtvOrderProductName);
        txtvOrderProductCategory = findViewById(R.id.txtvOrderProductCategory);
        txtvOrderProductSubCategory = findViewById(R.id.txtvOrderProductSubCategory);
        txtvOrderProductPrice = findViewById(R.id.txtvOrderProductPrice);
        txtvOrderSellerName = findViewById(R.id.txtvOrderSellerName);
        txtvOrderSellerAddress = findViewById(R.id.txtvOrderSellerAddress);
        txtvOrderSellerAddress2 = findViewById(R.id.txtvOrderSellerAddress2);
        txtvOrderSellerAddress3 = findViewById(R.id.txtvOrderSellerAddress3);
        txtvOrderQty = findViewById(R.id.txtvOrderQty);
        txtvOrderTotalPrice = findViewById(R.id.txtvOrderTotalPrice);
        lvOrderSchedules = findViewById(R.id.lvOrderSchedules);
        scheduleItems = new ArrayList<>();

        Intent intent = getIntent();
        productId = intent.getIntExtra("id", 0);
        Log.i("ID", String.valueOf(productId));

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        fetchOrder(productId);

        txtvOrderProductName.setOnClickListener(v -> {
            Intent prodIntent = new Intent(this, ConsumerProductDetailActivity.class);
            prodIntent.putExtra("id", productId);
            startActivity(prodIntent);
        });

        lvOrderSchedules.setOnItemClickListener((parent, view, position, id) -> {
            Intent details = new Intent(
                this,
                SellerOrderScheduleDetailsActivity.class
            );
            details.putExtra("id", scheduleItems.get(position).getId());
            details.putExtra("date", scheduleItems.get(position).getDate());
            details.putExtra("time", scheduleItems.get(position).getTime());
            details.putExtra("qty", String.valueOf(scheduleItems.get(position).getQty()));
            details.putExtra("amt", String.valueOf(scheduleItems.get(position).getPrice()));
            details.putExtra("status", scheduleItems.get(position).getStatus());

            startActivityForResult(details, 1);
        });
    }

    private void fetchOrder(int productId) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                        "Authorization",
                        token_type + " " + token
                )
                .url("http://" + host + "/api/order/" + productId)
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(ConsumerOrderDetailActivity.this,
                            "An error has occured. Please try again.",
                            Toast.LENGTH_LONG
                        ).show();
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProduct = new JSONObject(responseBody.string());
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Toast.makeText(
                                        ConsumerOrderDetailActivity.this,
                                        joProduct.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            JSONObject data = joProduct.getJSONObject("data");
                            Log.i("Order", data.toString());

                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Picasso.get()
                                        .load(data.getJSONObject("product").getString("image"))
                                        .into(imgvOrderProductImage);
                                    txtvOrderProductName.setText(
                                        "Name: " + data.getJSONObject("product").getString("name")
                                    );
                                    txtvOrderProductCategory.setText(
                                        "Category: " + data.getJSONObject("product")
                                            .getString("category")
                                    );
                                    txtvOrderProductSubCategory.setText(
                                        "Sub-category: " + data.getJSONObject("product")
                                            .getString("sub_category")
                                    );
                                    txtvOrderProductPrice.setText(
                                            "Price: ₱" + data.getJSONObject("product")
                                                .getString("price")
                                    );
                                    JSONObject joSeller = data.getJSONObject("product")
                                        .getJSONObject("seller");
                                    JSONObject joProperties =
                                        new JSONObject(joSeller.getString("properties"));
                                    JSONObject joBusiness = joProperties.getJSONObject("business");
                                    JSONObject joAddress = joProperties.getJSONObject("address");
                                    txtvOrderSellerName.setText(
                                        "Name: " + joBusiness.getString("name")
                                    );
                                    txtvOrderSellerAddress.setText(
                                        "Address: " + joAddress.getString("unit")
                                    );
                                    txtvOrderSellerAddress2.setText(
                                        joAddress.getString("street") + ", "
                                                + joAddress.getString("barangay")
                                    );
                                    txtvOrderSellerAddress3.setText(
                                        joAddress.getString("city") + ", "
                                            + joAddress.getString("zip")
                                    );
                                    txtvOrderQty.setText(
                                        "Quantity: " + data.getString("total_quantity")
                                    );
                                    txtvOrderTotalPrice.setText(
                                        "Total amount: " + data.getString("total_amount")
                                    );

                                    scheduleItems.clear();
                                    JSONArray jaOrders = data.getJSONArray("order_schedules");
                                    for(int i = 0; i < jaOrders.length(); i++){
                                        scheduleItems.add(new ScheduleItem(
                                                jaOrders.getJSONObject(i).getInt("id"),
                                                jaOrders.getJSONObject(i)
                                                    .getString("schedule_date"),
                                                jaOrders.getJSONObject(i)
                                                    .getString("schedule_time"),
                                                Float.parseFloat(
                                                    jaOrders.getJSONObject(i)
                                                        .getString("total_amount")
                                                ),
                                                jaOrders.getJSONObject(i).getInt("qty"),
                                                jaOrders.getJSONObject(i).getString("status")
                                            )
                                        );
                                    }
                                    scheduleItemAdapter = new ScheduleItemAdapter(
                                        ConsumerOrderDetailActivity.this,
                                        R.layout.fragment_schedule_item,
                                        scheduleItems
                                    );
                                    lvOrderSchedules.setAdapter(scheduleItemAdapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(ConsumerOrderDetailActivity.this,
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
            if(resultCode == SellerOrderScheduleDetailsActivity.RESULT_OK){
                fetchOrder(productId);
            }
        }
    }//onActivityResult
}