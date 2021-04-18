package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SellerOrderScheduleDetailsActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-13-229-96-65.ap-southeast-1.compute.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String USER_TYPE = "user_type";
    String token_type;
    String token;
    String user_type;
    ProgressDialog progress;
    TextView txtvScheduleDate;
    TextView txtvScheduleTime;
    TextView txtvScheduleQty;
    TextView txtvScheduleAmount;
    TextView txtvScheduleRemarks;
    TextView txtvScheduleStatus;
    EditText etmRemarks;
    Button btnScheduleCancel;
    Button btnScheduleDelivered;
    Button btnScheduleClear;
    int orderScheduleId;
    String scheduleDate;
    String scheduleTime;
    String scheduleQty;
    String scheduleAmount;
    String status;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_schedule_details);

        txtvScheduleDate = findViewById(R.id.txtvScheduleDate);
        txtvScheduleTime = findViewById(R.id.txtvScheduleTime);
        txtvScheduleQty = findViewById(R.id.txtvScheduleQty);
        txtvScheduleAmount = findViewById(R.id.txtvScheduleAmount);
        txtvScheduleStatus = findViewById(R.id.txtvScheduleStatus);
        txtvScheduleRemarks = findViewById(R.id.txtvScheduleRemarks);
        etmRemarks = findViewById(R.id.etmRemarks);
        btnScheduleCancel = findViewById(R.id.btnScheduleCancel);
        btnScheduleDelivered = findViewById(R.id.btnScheduleDelivered);
        btnScheduleClear = findViewById(R.id.btnScheduleClear);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));
        user_type = (shared.getString(USER_TYPE, ""));

        Intent intent = getIntent();
        orderScheduleId = intent.getIntExtra("id", 0);
        scheduleDate = intent.getStringExtra("date");
        scheduleTime = intent.getStringExtra("time");
        scheduleQty = intent.getStringExtra("qty");
        scheduleAmount = intent.getStringExtra("amt");
        status = intent.getStringExtra("status");

        if (status.equals("Cancelled")) {
            txtvScheduleStatus.setTextColor(Color.parseColor("#E74C3C"));
        } else {
            txtvScheduleStatus.setTextColor(Color.parseColor("#2ECC71"));
        }
        txtvScheduleStatus.setText(status);

        if (user_type.equals("Seller")) {
            btnScheduleClear.setVisibility(View.GONE);
        } else {
            txtvScheduleRemarks.setVisibility(View.GONE);
            etmRemarks.setVisibility(View.GONE);
            btnScheduleCancel.setVisibility(View.GONE);
            btnScheduleDelivered.setVisibility(View.GONE);
        }

        if (
            status.isEmpty() ||
            status.equals(null) ||
            status.equals("null") ||
            status.equals("")
        ) {
            txtvScheduleStatus.setVisibility(View.GONE);
            btnScheduleClear.setVisibility(View.GONE);
        } else {
            etmRemarks.setFocusable(false);
            etmRemarks.setFocusableInTouchMode(false);
            etmRemarks.setClickable(false);
            btnScheduleCancel.setVisibility(View.GONE);
            btnScheduleDelivered.setVisibility(View.GONE);
        }

        txtvScheduleDate.setText("Date: " + scheduleDate);
        txtvScheduleTime.setText("Time: " + scheduleTime);
        txtvScheduleQty.setText("Quantity: " + scheduleQty);
        txtvScheduleAmount.setText("Total amount: " + scheduleAmount);
        Log.i("ID", String.valueOf(orderScheduleId));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnScheduleCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Cancel schedule")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String json = "{"
                        + "\"status\": \"cancelled\","
                        + "\"remarks\": \"" + etmRemarks.getText().toString() + "\""
                    + "}";
                    scheduleTransaction(json, "Post");
                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        btnScheduleDelivered.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delivered schedule")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String json = "{"
                        + "\"status\": \"delivered\","
                        + "\"remarks\": \"" + etmRemarks.getText().toString() + "\""
                    + "}";
                    scheduleTransaction(json, "Post");
                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        btnScheduleClear.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Clear schedule")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    scheduleTransaction("", "Delete");
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }

    private void scheduleTransaction(String json, String command)
    {
        progress.show();
        try {
            OkHttpClient client = new OkHttpClient();
            Log.i("JSON", json);
            Request request = null;
            if (command.equals("Post")) {
                RequestBody body = RequestBody.create(JSON, json);
                request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header(
                            "Authorization",
                            token_type + " " + token
                    )
                    .url("http://" + host + "/api/order/schedule/" + orderScheduleId)
                    .post(body)
                    .build();
            } else if (command.equals("Delete")) {
                request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header(
                            "Authorization",
                            token_type + " " + token
                    )
                    .url("http://" + host + "/api/order/schedule/" + orderScheduleId)
                    .delete()
                    .build();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(SellerOrderScheduleDetailsActivity.this,
                            "An error has occured. Please try again.",
                            Toast.LENGTH_LONG
                        ).show();
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject jo = new JSONObject(responseBody.string());

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Toast.makeText(
                                        SellerOrderScheduleDetailsActivity.this,
                                        jo.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            runOnUiThread(() -> progress.dismiss());
                            Log.i("Res", jo.toString());
                            Intent returnIntent = new Intent();
                            setResult(
                                SellerOrderScheduleDetailsActivity.RESULT_OK,
                                returnIntent
                            );
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(SellerOrderScheduleDetailsActivity.this,
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
}