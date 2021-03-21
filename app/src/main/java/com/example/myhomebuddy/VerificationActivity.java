package com.example.myhomebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class VerificationActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_TYPE = "user_type";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        TextView txtvInstructionsUnverified = findViewById(R.id.txtvInstructionsUnverified);
        TextView txtvInstructionsUnverified2 = findViewById(R.id.txtvInstructionsUnverified2);
        TextView txtvEmailUnverified = findViewById(R.id.txtvEmailUnverified);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        String userType = shared.getString(USER_TYPE, "Consumer");
        String userEmail = shared.getString(USER_EMAIL, "Consumer");

        Log.i("type", userType);
        Log.i("email", userEmail);

        if (userType.equals("Consumer")) {
            String[] splitedEmail = userEmail.split("@");
            txtvEmailUnverified.setText(replaceLastFour(splitedEmail[0]) + "@" + splitedEmail[1]);
        } else {
            txtvInstructionsUnverified.setText("Please wait for the admin");
            txtvInstructionsUnverified2.setText("to activate your account.");
            txtvEmailUnverified.setVisibility(View.INVISIBLE);
        }
    }

    public static String replaceLastFour(String s) {
        int length = s.length();
        if (length < 4) {
            return s.substring(0, length - 2) + "****";
        } else {
            return s.substring(0, length - 4) + "****";
        }
    }
}