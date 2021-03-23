package com.example.myhomebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
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

public class ConsumerProductReviewActivity extends AppCompatActivity {

    int productId;
    private Boolean blnProceedRegister;
//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ProgressDialog progress;
    Float ratings;
    RatingBar rbRateProduct;
    TextView etxtmReviewMessage;
    Button btnSubmitProductReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_product_review);

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

        rbRateProduct = findViewById(R.id.rbRateProduct);
        etxtmReviewMessage = findViewById(R.id.etxtmReviewMessage);
        btnSubmitProductReview = findViewById(R.id.btnSubmitProductReview);

        rbRateProduct.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            Log.i("rbRate", String.valueOf(rating));
        });

        btnSubmitProductReview.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Rate Product")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    ratings = rbRateProduct.getRating();
                    blnProceedRegister = !etxtmReviewMessage.getText().toString().isEmpty();
                    if (blnProceedRegister) {
                        progress.show();
                        OkHttpClient client = new OkHttpClient();
                        String json = "{"
                            + "\"product_id\" : " + productId + ","
                            + "\"ratings\" : " + ratings + ","
                            + "\"message\" : \"" + etxtmReviewMessage.getText().toString() + "\""
                        + "}";
                        Log.d("json", json);
                        RequestBody body = RequestBody.create(JSON, json);
                        Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header(
                                    "Authorization",
                                    token_type + " " + token
                            )
                            .url("http://" + host + "/api/consumer/product/review")
                            .post(body)
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("Code", String.valueOf(response.code()));
                                        Log.i("RS", String.valueOf(res));
                                        if (response.code() == 201) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                progress.dismiss();
                                                Intent returnIntent = new Intent();
                                                setResult(
                                                    ConsumerProductReviewActivity.RESULT_OK,
                                                    returnIntent
                                                );
                                                finish();
                                            });
                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e("RSx", e.getMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("RF", res.toString());
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            progress.dismiss();
                                            Toast.makeText(
                                                ConsumerProductReviewActivity.this,
                                                "An error has occured.",
                                                Toast.LENGTH_LONG
                                            ).show();
                                        });
                                    } catch (JSONException | IOException e) {
                                        Log.e("RFx", e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                new Handler(Looper.getMainLooper()).post(progress::dismiss);
                                Log.e("Fx", e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(
                            ConsumerProductReviewActivity.this,
                            "Please fill up all the fields.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }
}