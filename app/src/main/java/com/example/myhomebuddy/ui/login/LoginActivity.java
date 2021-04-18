package com.example.myhomebuddy.ui.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.MainActivity;
import com.example.myhomebuddy.R;
import com.example.myhomebuddy.RegistrationActivity;
import com.example.myhomebuddy.SellerMainActivity;
import com.example.myhomebuddy.VerificationActivity;

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

public class LoginActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-13-229-96-65.ap-southeast-1.compute.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String USER_ID = "user_id";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_MIDDLE_NAME = "user_middle_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_TYPE = "user_type";
    public static final String USER_IS_ACTIVATED = "user_is_activated";
    public static final String USER_PROPERTIES = "user_properties";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etxtLoginEmail = findViewById(R.id.etxtLoginEmail);
        EditText etxtLoginPassword = findViewById(R.id.etxtLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button registerButton = findViewById(R.id.register);

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnLogin.setOnClickListener(v -> {
            progress.show();
            try {
                OkHttpClient client = new OkHttpClient();
                String json = "{"
                        + "\"email\" : \"" + etxtLoginEmail.getText().toString() + "\","
                        + "\"password\" : \"" + etxtLoginPassword.getText().toString() + "\""
                        + "}";
                Log.d("json", json);
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .url("http://" + host + "/api/login")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        progress.dismiss();
                        Log.e("Fail", e.getMessage());
                    }

                    @Override public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            JSONObject jo = new JSONObject(responseBody.string());
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> {
                                    try {
                                        progress.dismiss();
                                        Toast.makeText(
                                            LoginActivity.this,
                                            jo.getString("error"),
                                            Toast.LENGTH_LONG
                                        ).show();
                                    } catch (JSONException e) {
                                        Log.e("Ex", e.getMessage());
                                    }
                                });
                            } else {
                                String access_token = String.valueOf(jo.get("access_token"));
                                String token_type = String.valueOf(jo.get("token_type"));
                                Log.d("Token", access_token);
                                Log.d("Type", token_type);

                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        SHARED_PREFS_TOKEN,
                                        MODE_PRIVATE
                                );
                                @SuppressLint("CommitPrefEdits")
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(TOKEN, access_token);
                                editor.putString(TOKEN_TYPE, token_type);

                                Request request = new Request.Builder()
                                    .header("Accept", "application/json")
                                    .header("Content-Type", "application/json")
                                    .header(
                                        "Authorization",
                                        token_type + " " + access_token
                                    )
                                    .url("http://" + host + "/api/me")
                                    .get()
                                    .build();

                                call = client.newCall(request);
                                Response res = call.execute();

                                JSONObject joMe = new JSONObject(res.body().string());
                                Log.i("Me", joMe.toString());

                                if (!res.isSuccessful()) {
                                    progress.dismiss();
                                    Toast.makeText(
                                        LoginActivity.this,
                                        joMe.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } else {
                                    JSONObject dataObj = joMe.getJSONObject("data");

                                    editor.putString(
                                        USER_ID,
                                        dataObj.getString("id")
                                    );
                                    editor.putString(
                                        USER_FIRST_NAME,
                                        dataObj.getString("first_name")
                                    );
                                    editor.putString(
                                        USER_MIDDLE_NAME,
                                        dataObj.getString("middle_name")
                                    );
                                    editor.putString(
                                        USER_LAST_NAME,
                                        dataObj.getString("last_name")
                                    );
                                    editor.putString(
                                        USER_EMAIL,
                                        dataObj.getString("email")
                                    );
                                    editor.putString(
                                        USER_TYPE,
                                        dataObj.getString("type")
                                    );
                                    editor.putInt(
                                        USER_IS_ACTIVATED,
                                        dataObj.getInt("is_activated")
                                    );
                                    editor.putString(
                                        USER_PROPERTIES,
                                        dataObj.getString("properties")
                                    );

                                    editor.apply();

                                    if (dataObj.getInt("is_activated") == 0) {
                                        progress.dismiss();
                                        Log.i("Avt", "Not activated");
                                        Intent intent = new Intent(
                                            LoginActivity.this,
                                            VerificationActivity.class
                                        );
                                        startActivity(intent);
                                    } else {
                                        progress.dismiss();
                                        Log.i("Avt", "Activated");
                                        if (dataObj.getString("type").equals("Consumer")) {
                                            Intent intent = new Intent(
                                                    LoginActivity.this,
                                                    MainActivity.class
                                            );
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(
                                                    LoginActivity.this,
                                                    SellerMainActivity.class
                                            );
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("Ex", e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("Ex", e.getMessage());
                Toast.makeText(this,
                    "An error has occured. Please try again.",
                    Toast.LENGTH_LONG
                ).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent registrationActivity = new Intent(
                LoginActivity.this,
                RegistrationActivity.class
            );
            startActivity(registrationActivity);
        });
    }
}