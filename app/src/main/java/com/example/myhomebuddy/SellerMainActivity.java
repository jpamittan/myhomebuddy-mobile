package com.example.myhomebuddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.ui.login.LoginActivity;

public class SellerMainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        Button btnProducts = findViewById(R.id.btnProducts);
        Button btnMyOrders = findViewById(R.id.btnMyOrders);
        Button btnBillingAccount = findViewById(R.id.btnBillingAccount);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    SharedPreferences settings = getSharedPreferences(
                        SHARED_PREFS_TOKEN,
                        MODE_PRIVATE
                    );
                    settings.edit().clear().apply();
                    Intent intent = new Intent(
                        SellerMainActivity.this,
                        LoginActivity.class
                    );
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        btnProducts.setOnClickListener(v -> {
            Intent intent = new Intent(
                SellerMainActivity.this,
                SellerProductActivity.class
            );
            startActivity(intent);
        });

        btnMyOrders.setOnClickListener(v -> {

        });

        btnBillingAccount.setOnClickListener(v -> {

        });

        btnProfile.setOnClickListener(v -> {

        });
    }
}