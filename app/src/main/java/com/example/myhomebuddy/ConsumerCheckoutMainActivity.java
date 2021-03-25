package com.example.myhomebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConsumerCheckoutMainActivity extends AppCompatActivity {

    private Boolean blnProceedRegister = true;
//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ProgressDialog progress;

    ImageView imgvCheckoutProductImage;
    TextView txtvCheckoutName;
    TextView txtvCheckoutPrice;
    TextView txtvSchedulesTitle;
    ListView lvCheckoutSchedules;
    TextView txtvCheckoutQty;
    TextView txtvCheckoutAmount;
    Spinner spnModePayment;
    LinearLayout llCreditDebit;
    TextView etCheckoutName;
    TextView etCheckoutCardNo;
    TextView etCheckoutMM;
    TextView etCheckoutYY;
    TextView etCheckoutCVC;
    TextView etCheckoutAddress;
    TextView etCheckoutAddress2;
    TextView etCheckoutCity;
    TextView etCheckoutState;
    TextView etCheckoutZip;
    Button btnCheckout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_checkout_main);

        imgvCheckoutProductImage = findViewById(R.id.imgvCheckoutProductImage);
        txtvCheckoutName = findViewById(R.id.txtvCheckoutName);
        txtvCheckoutPrice = findViewById(R.id.txtvCheckoutPrice);
        txtvSchedulesTitle = findViewById(R.id.txtvSchedulesTitle);
        lvCheckoutSchedules = findViewById(R.id.lvCheckoutSchedules);
        txtvCheckoutQty = findViewById(R.id.txtvCheckoutQty);
        txtvCheckoutAmount = findViewById(R.id.txtvCheckoutAmount);
        spnModePayment = findViewById(R.id.spnModePayment);
        llCreditDebit = findViewById(R.id.llCreditDebit);
        etCheckoutName = findViewById(R.id.etCheckoutName);
        etCheckoutCardNo = findViewById(R.id.etCheckoutCardNo);
        etCheckoutMM = findViewById(R.id.etCheckoutMM);
        etCheckoutYY = findViewById(R.id.etCheckoutYY);
        etCheckoutCVC = findViewById(R.id.etCheckoutCVC);
        etCheckoutAddress = findViewById(R.id.etCheckoutAddress);
        etCheckoutAddress2 = findViewById(R.id.etCheckoutAddress2);
        etCheckoutCity = findViewById(R.id.etCheckoutCity);
        etCheckoutState = findViewById(R.id.etCheckoutState);
        etCheckoutZip = findViewById(R.id.etCheckoutZip);
        btnCheckout = findViewById(R.id.btnCheckout);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        Intent intent = getIntent();
        int productId = intent.getIntExtra("productId", 0);
        String dateFrom = intent.getStringExtra("dateFrom");
        String dateto = intent.getStringExtra("dateto");
        String frequency = intent.getStringExtra("frequency");
        String delivery_days = intent.getStringExtra("delivery_days");
        float productPrice = intent.getFloatExtra("productPrice", 0);
        int total_quantity = intent.getIntExtra("total_quantity", 0);
        float total_amount = intent.getFloatExtra("total_amount", 0);
        String productImage = intent.getStringExtra("productImage");
        String productName = intent.getStringExtra("productName");
        ArrayList<String> dates = intent.getStringArrayListExtra("dates");
        ArrayList<String> dateTimes = intent.getStringArrayListExtra("dateTimes");
        ArrayList<Integer> datePcs = intent.getIntegerArrayListExtra("datePcs");

        StringBuilder orderSchedulesSB = new StringBuilder();
        ArrayList<String> listSchedules = new ArrayList<>();
        for(int i = 0; i < dates.size(); i++){
            listSchedules.add((i + 1) + ".  Date: " + dates.get(i) + "  Time: " + dateTimes.get(i) + "  Pc(s): " + datePcs.get(i));
            orderSchedulesSB.append(
                "{"
                    + "\"schedule_date\": \"" + dates.get(i) + "\","
                    + "\"schedule_time\": \"" + dateTimes.get(i) + "\","
                    + "\"qty\": " + datePcs.get(i) + ","
                    + "\"total_amount\": " + (datePcs.get(i) * productPrice) + ""
                + "},"
            );
        }

        txtvSchedulesTitle.setText(listSchedules.size() + " Total schedules for delivery");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            listSchedules
        );

        lvCheckoutSchedules.setAdapter(adapter);
        Picasso.get().load(productImage).into(imgvCheckoutProductImage);
        txtvCheckoutName.setText("Name: " + productName);
        txtvCheckoutPrice.setText("Price: ₱" + productPrice);
        txtvCheckoutQty.setText("Quantity: " + total_quantity);
        txtvCheckoutAmount.setText("Amount: ₱" + total_amount);

        spnModePayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    llCreditDebit.setVisibility(View.GONE);
                } else {
                    llCreditDebit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnCheckout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Checkout")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Log.d("Mode", spnModePayment.getSelectedItem().toString());
                    if (spnModePayment.getSelectedItem().toString().equals("Credit/Debit")) {
                        blnProceedRegister = !etCheckoutName.getText().toString().isEmpty() &&
                            !etCheckoutCardNo.getText().toString().isEmpty() &&
                            !etCheckoutMM.getText().toString().isEmpty() &&
                            !etCheckoutYY.getText().toString().isEmpty() &&
                            !etCheckoutCVC.getText().toString().isEmpty() &&
                            !etCheckoutAddress.getText().toString().isEmpty() &&
                            !etCheckoutAddress2.getText().toString().isEmpty() &&
                            !etCheckoutCity.getText().toString().isEmpty() &&
                            !etCheckoutState.getText().toString().isEmpty() &&
                            !etCheckoutZip.getText().toString().isEmpty();
                    }

                    if (blnProceedRegister) {
                        progress.show();
                        OkHttpClient client = new OkHttpClient();
                        String json = "";
                        String orderSchedules = "[" + orderSchedulesSB.substring(0, orderSchedulesSB.length() - 1) + "]";
                        if (spnModePayment.getSelectedItem().toString().equals("Cash on delivery")) {
                            json = "{"
                                + "\"product_id\" : " + productId + ","
                                + "\"subscription_from\" : \"" + dateFrom + "\","
                                + "\"subscription_to\" : \"" + dateto + "\","
                                + "\"frequency\" : \"" + frequency + "\","
                                + "\"delivery_days\" : " + delivery_days + ","
                                + "\"total_quantity\" : " + total_quantity + ","
                                + "\"total_amount\" : " + total_amount + ","
                                + "\"payment_method\" : \"" + spnModePayment.getSelectedItem().toString() + "\","
                                + "\"order_schedules\" : " + orderSchedules+ ""
                            + "}";
                        } else {
                            String paymentProps = "{"
                                + "\"name\" : \"" + etCheckoutName.getText().toString() + "\","
                                + "\"card_no\" : \"" + etCheckoutCardNo.getText().toString() + "\","
                                + "\"mm\" : \"" + etCheckoutMM.getText().toString() + "\","
                                + "\"yy\" : \"" + etCheckoutYY.getText().toString() + "\","
                                + "\"cvc\" : \"" + etCheckoutCVC.getText().toString() + "\","
                                + "\"address\" : \"" + etCheckoutAddress.getText().toString() + "\","
                                + "\"address_2\" : \"" + etCheckoutAddress2.getText().toString() + "\","
                                + "\"city\" : \"" + etCheckoutCity.getText().toString() + "\","
                                + "\"state\" : \"" + etCheckoutState.getText().toString() + "\","
                                + "\"zip\" : \"" + etCheckoutZip.getText().toString() + "\""
                            + "}";
                            json = "{"
                                + "\"product_id\" : " + productId + ","
                                + "\"subscription_from\" : \"" + dateFrom + "\","
                                + "\"subscription_to\" : \"" + dateto + "\","
                                + "\"frequency\" : \"" + frequency + "\","
                                + "\"delivery_days\" : \"" + delivery_days + "\","
                                + "\"total_quantity\" : " + total_quantity + ","
                                + "\"total_amount\" : " + total_amount + ","
                                + "\"payment_method\" : \"" + spnModePayment.getSelectedItem().toString() + "\","
                                + "\"payment_properties\" : " + paymentProps + ","
                                + "\"order_schedules\" : " + orderSchedules+ ""
                            + "}";
                        }
                        Log.d("json", json);
                        RequestBody body = RequestBody.create(JSON, json);
                        Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header(
                                    "Authorization",
                                    token_type + " " + token
                            )
                            .url("http://" + host + "/api/order")
                            .post(body)
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject res = new JSONObject(
                                            Objects.requireNonNull(response.body()).string()
                                        );
                                        Log.d("Code", String.valueOf(response.code()));
                                        Log.i("RS", String.valueOf(res));
                                        if (response.code() == 201) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                progress.dismiss();
                                                Intent checkoutResult = new Intent(
                                                    ConsumerCheckoutMainActivity.this,
                                                    ConsumerCheckoutResultActivity.class
                                                );
                                                startActivity(checkoutResult);
                                                finish();
                                            });
                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e("RSx", e.getMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject res = new JSONObject(
                                            Objects.requireNonNull(response.body()).string()
                                        );
                                        Log.d("RF", res.toString());
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            progress.dismiss();
                                            Toast.makeText(
                                                ConsumerCheckoutMainActivity.this,
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
                            this,
                            "Please fill up all the fields.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }
}