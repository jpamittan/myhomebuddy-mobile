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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhomebuddy.ui.reviews.Reviews;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SellerBillingAccountActivity extends AppCompatActivity {

    private Boolean blnProceedRegister = false;
//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;

    Spinner spnAccountType;
    TextView txtvCardNo;
    EditText etvtCardNo;
    TextView txtvMM;
    TextView txtvYY;
    TextView txtvCVC;
    EditText etxtMM;
    EditText etxtYY;
    EditText etxtCVC;
    EditText etxtBillingAccountFullname;
    EditText etxtBillingAccountAddress;
    EditText etxtBillingAccountAddress2;
    EditText etxtBillingAccountCity;
    EditText etxtBillingAccountState;
    EditText etxtBillingAccountZip;
    Spinner spnCountry;
    Button btnBillingAccountSave;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_billing_account);

        spnAccountType = findViewById(R.id.spnAccountType);
        txtvCardNo = findViewById(R.id.txtvCardNo);
        etvtCardNo = findViewById(R.id.etvtCardNo);
        txtvMM = findViewById(R.id.txtvMM);
        txtvYY = findViewById(R.id.txtvYY);
        txtvCVC = findViewById(R.id.txtvCVC);
        etxtMM = findViewById(R.id.etxtMM);
        etxtYY = findViewById(R.id.etxtYY);
        etxtCVC = findViewById(R.id.etxtCVC);
        etxtBillingAccountFullname = findViewById(R.id.etxtBillingAccountFullname);
        etxtBillingAccountAddress = findViewById(R.id.etxtBillingAccountAddress);
        etxtBillingAccountAddress2 = findViewById(R.id.etxtBillingAccountAddress2);
        etxtBillingAccountCity = findViewById(R.id.etxtBillingAccountCity);
        etxtBillingAccountState = findViewById(R.id.etxtBillingAccountState);
        etxtBillingAccountZip = findViewById(R.id.etxtBillingAccountZip);
        spnCountry = findViewById(R.id.spnCountry);
        btnBillingAccountSave = findViewById(R.id.btnBillingAccountSave);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        fetchBillingAccount();

        spnAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    txtvMM.setVisibility(View.VISIBLE);
                    txtvYY.setVisibility(View.VISIBLE);
                    txtvCVC.setVisibility(View.VISIBLE);
                    etxtMM.setVisibility(View.VISIBLE);
                    etxtYY.setVisibility(View.VISIBLE);
                    etxtCVC.setVisibility(View.VISIBLE);
                } else {
                    txtvMM.setVisibility(View.GONE);
                    txtvYY.setVisibility(View.GONE);
                    txtvCVC.setVisibility(View.GONE);
                    etxtMM.setText("");
                    etxtYY.setText("");
                    etxtCVC.setText("");
                    etxtMM.setVisibility(View.GONE);
                    etxtYY.setVisibility(View.GONE);
                    etxtCVC.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnBillingAccountSave.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Billing account")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String json = null;
                    if (
                        spnAccountType.getSelectedItem().toString().equals("GCash") ||
                        spnAccountType.getSelectedItem().toString().equals("Paymaya")
                    ) {
                        blnProceedRegister = !etvtCardNo.getText().toString().isEmpty() &&
                            !etxtBillingAccountFullname.getText().toString().isEmpty() &&
                            !etxtBillingAccountAddress.getText().toString().isEmpty() &&
                            !etxtBillingAccountAddress2.getText().toString().isEmpty() &&
                            !etxtBillingAccountCity.getText().toString().isEmpty() &&
                            !etxtBillingAccountState.getText().toString().isEmpty() &&
                            !etxtBillingAccountZip.getText().toString().isEmpty();
                            json = "{"
                                + "\"account_type\" : \"" + spnAccountType.getSelectedItem().toString() + "\","
                                + "\"card_no\" : \"" + etvtCardNo.getText().toString() + "\","
                                + "\"account_name\" : \"" + etxtBillingAccountFullname.getText().toString() + "\","
                                + "\"address_line_a\" : \"" + etxtBillingAccountAddress.getText().toString() + "\","
                                + "\"address_line_b\" : \"" + etxtBillingAccountAddress2.getText().toString() + "\","
                                + "\"city\" : \"" + etxtBillingAccountCity.getText().toString() + "\","
                                + "\"state\" : \"" + etxtBillingAccountState.getText().toString() + "\","
                                + "\"zip\" : \"" + etxtBillingAccountZip.getText().toString() + "\","
                                + "\"country\" : \"" + spnCountry.getSelectedItem().toString() + "\""
                            + "}";
                    } else {
                        blnProceedRegister = !etvtCardNo.getText().toString().isEmpty() &&
                            !etxtMM.getText().toString().isEmpty() &&
                            !etxtYY.getText().toString().isEmpty() &&
                            !etxtCVC.getText().toString().isEmpty() &&
                            !etxtBillingAccountFullname.getText().toString().isEmpty() &&
                            !etxtBillingAccountAddress.getText().toString().isEmpty() &&
                            !etxtBillingAccountAddress2.getText().toString().isEmpty() &&
                            !etxtBillingAccountCity.getText().toString().isEmpty() &&
                            !etxtBillingAccountState.getText().toString().isEmpty() &&
                            !etxtBillingAccountZip.getText().toString().isEmpty();
                            json = "{"
                                + "\"account_type\" : \"" + spnAccountType.getSelectedItem().toString() + "\","
                                + "\"card_no\" : \"" + etvtCardNo.getText().toString() + "\","
                                + "\"mm\" : \"" + etxtMM.getText().toString() + "\","
                                + "\"yy\" : \"" + etxtYY.getText().toString() + "\","
                                + "\"cvc\" : \"" + etxtCVC.getText().toString() + "\","
                                + "\"account_name\" : \"" + etxtBillingAccountFullname.getText().toString() + "\","
                                + "\"address_line_a\" : \"" + etxtBillingAccountAddress.getText().toString() + "\","
                                + "\"address_line_b\" : \"" + etxtBillingAccountAddress2.getText().toString() + "\","
                                + "\"city\" : \"" + etxtBillingAccountCity.getText().toString() + "\","
                                + "\"state\" : \"" + etxtBillingAccountState.getText().toString() + "\","
                                + "\"zip\" : \"" + etxtBillingAccountZip.getText().toString() + "\","
                                + "\"country\" : \"" + spnCountry.getSelectedItem().toString() + "\""
                            + "}";
                    }
                    if (blnProceedRegister) {
                        progress.show();
                        OkHttpClient client = new OkHttpClient();

                        Log.d("json", json);
                        RequestBody body = RequestBody.create(JSON, json);
                        Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header(
                                "Authorization",
                                token_type + " " + token
                            )
                            .url("http://" + host + "/api/billing/account")
                            .post(body)
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                runOnUiThread(() -> progress.dismiss());
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("Code", String.valueOf(response.code()));
                                        Log.i("RS", String.valueOf(res));
                                        if (response.code() == 201) {
                                            Intent returnIntent = new Intent();
                                            setResult(
                                                SellerBillingAccountActivity.RESULT_OK,
                                                returnIntent
                                            );
                                            finish();
                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e("RSx", e.getMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("RF", res.toString());
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            Toast.makeText(
                                                SellerBillingAccountActivity.this,
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
                                runOnUiThread(() -> progress.dismiss());
                                Log.e("Fx", e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(
                            SellerBillingAccountActivity.this,
                            "Please fill up all the fields.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }

    private void fetchBillingAccount() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header(
                            "Authorization",
                            token_type + " " + token
                    )
                    .url("http://" + host + "/api/billing/account")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(SellerBillingAccountActivity.this,
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProduct = new JSONObject(responseBody.string());
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Toast.makeText(
                                            SellerBillingAccountActivity.this,
                                            joProduct.getString("error"),
                                            Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            JSONObject data = joProduct.getJSONObject("data");
                            Log.i("Product", data.toString());

                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    ArrayList<String> accountTypes = retrieveAllItems(spnAccountType);
                                    spnAccountType.setSelection(
                                            accountTypes.indexOf(data.getString("account_type"))
                                    );
                                    String cardNo = data.getString("card_no");
                                    cardNo = cardNo.substring(cardNo.length() - 4);
                                    txtvCardNo.setText("Mobile / Card no: ****" + cardNo);
                                    etxtBillingAccountAddress.setText(
                                            data.getString("address_line_a")
                                    );
                                    etxtBillingAccountAddress2.setText(
                                            data.getString("address_line_b")
                                    );
                                    etxtBillingAccountCity.setText(data.getString("city"));
                                    etxtBillingAccountState.setText(data.getString("state"));
                                    etxtBillingAccountZip.setText(data.getString("zip"));
                                    ArrayList<String> countries = retrieveAllItems(spnCountry);
                                    spnCountry.setSelection(
                                        countries.indexOf(data.getString("country"))
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(SellerBillingAccountActivity.this,
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

    public ArrayList<String> retrieveAllItems(Spinner spn) {
        Adapter adapter = spn.getAdapter();
        int n = adapter.getCount();
        ArrayList<String> str = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) {
            str.add((String) adapter.getItem(i));
        }
        return str;
    }
}

