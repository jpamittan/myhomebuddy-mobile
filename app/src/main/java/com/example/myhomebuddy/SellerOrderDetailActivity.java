package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

public class SellerOrderDetailActivity extends AppCompatActivity {

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
    ImageView imgvSOrderProductImage;
    TextView txtvSOrderProductName;
    TextView txtvSOrderProductCategory;
    TextView txtvSOrderProductSubCategory;
    TextView txtvSOrderProductPrice;
    TextView txtvSOrderConsumerName;
    TextView txtvSOrderConsumerAddress;
    TextView txtvSOrderConsumerAddress2;
    TextView txtvSOrderConsumerAddress3;
    TextView txtvSOrderConsumerEmail;
    TextView txtvSOrderConsumerContact;
    TextView txtvSOrderModePayment;
    TextView txtvSOrderQty;
    TextView txtvSOrderTotalPrice;
    ListView lvSOrderSchedules;
    ArrayList<ScheduleItem> scheduleItems;
    ScheduleItemAdapter scheduleItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_detail);

        imgvSOrderProductImage = findViewById(R.id.imgvSOrderProductImage);
        txtvSOrderProductName = findViewById(R.id.txtvSOrderProductName);
        txtvSOrderProductCategory = findViewById(R.id.txtvSOrderProductCategory);
        txtvSOrderProductSubCategory = findViewById(R.id.txtvSOrderProductSubCategory);
        txtvSOrderProductPrice = findViewById(R.id.txtvSOrderProductPrice);
        txtvSOrderConsumerName = findViewById(R.id.txtvSOrderConsumerName);
        txtvSOrderConsumerAddress = findViewById(R.id.txtvSOrderConsumerAddress);
        txtvSOrderConsumerAddress2 = findViewById(R.id.txtvSOrderConsumerAddress2);
        txtvSOrderConsumerAddress3 = findViewById(R.id.txtvSOrderConsumerAddress3);
        txtvSOrderConsumerEmail = findViewById(R.id.txtvSOrderConsumerEmail);
        txtvSOrderConsumerContact = findViewById(R.id.txtvSOrderConsumerContact);
        txtvSOrderModePayment = findViewById(R.id.txtvSOrderModePayment);
        txtvSOrderQty = findViewById(R.id.txtvSOrderQty);
        txtvSOrderTotalPrice = findViewById(R.id.txtvSOrderTotalPrice);
        lvSOrderSchedules = findViewById(R.id.lvSOrderSchedules);

        Intent intent = getIntent();
        productId = intent.getIntExtra("id", 0);
        Log.i("ID", String.valueOf(productId));

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        scheduleItems = new ArrayList<>();

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        fetchOrder(productId);

        txtvSOrderProductName.setOnClickListener(v -> {
            Intent prodDetails = new Intent(this, SellerProductDetailActivity.class);
            prodDetails.putExtra("id",productId);
            startActivity(prodDetails);
        });

        lvSOrderSchedules.setOnItemClickListener((parent, view, position, id) -> {
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
                        Toast.makeText(SellerOrderDetailActivity.this,
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
                                        SellerOrderDetailActivity.this,
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
                                        .load(data.getJSONObject("product")
                                            .getString("image"))
                                        .into(imgvSOrderProductImage);
                                    txtvSOrderProductName.setText(
                                        "Name: " + data.getJSONObject("product")
                                            .getString("name")
                                    );
                                    txtvSOrderProductCategory.setText(
                                        "Category: " + data.getJSONObject("product")
                                                .getString("category")
                                    );
                                    txtvSOrderProductSubCategory.setText(
                                        "Sub-category: " + data.getJSONObject("product")
                                                .getString("sub_category")
                                    );
                                    txtvSOrderProductPrice.setText(
                                        "Price: ₱" + data.getJSONObject("product")
                                                .getString("price")
                                    );
                                    JSONObject joConsumer = data.getJSONObject("consumer");
                                    JSONObject joProperties =
                                        new JSONObject(joConsumer.getString("properties"));
                                    JSONObject joAddress = joProperties.getJSONObject("address");
                                    txtvSOrderConsumerName.setText(
                                        "Name: " + joConsumer.getString("first_name") + " " +
                                        joConsumer.getString("middle_name") + " " +
                                        joConsumer.getString("last_name")
                                    );
                                    txtvSOrderConsumerAddress.setText(
                                        "Address: " + joAddress.getString("unit") + " " +
                                        joAddress.getString("subdivision")
                                    );
                                    txtvSOrderConsumerAddress2.setText(
                                        joAddress.getString("street") + ", "
                                        + joAddress.getString("barangay")
                                    );
                                    txtvSOrderConsumerAddress3.setText(
                                        joAddress.getString("city") + ", "
                                        + joAddress.getString("zip")
                                    );
                                    txtvSOrderConsumerEmail.setText(
                                        "Email: " + joConsumer.getString("email")
                                    );
                                    txtvSOrderConsumerContact.setText(
                                        "Contact: " + joProperties.getString("phone_no")
                                    );
                                    txtvSOrderModePayment.setText(
                                        "Mode of payment: " + data.getString("payment_method")
                                    );
                                    txtvSOrderQty.setText(
                                        "Quantity: " + data.getString("total_quantity")
                                    );
                                    txtvSOrderTotalPrice.setText(
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
                                            SellerOrderDetailActivity.this,
                                            R.layout.fragment_schedule_item,
                                            scheduleItems
                                    );
                                    lvSOrderSchedules.setAdapter(scheduleItemAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(SellerOrderDetailActivity.this,
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