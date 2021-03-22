package com.example.myhomebuddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        btnProducts.setOnClickListener(v -> {
            startActivity(new Intent(this, SellerProductActivity.class));
        });

        btnMyOrders.setOnClickListener(v -> {

        });

        btnBillingAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerBillingAccountActivity.class);
            startActivityForResult(intent ,1);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, SellerProfileActivity.class));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == SellerBillingAccountActivity.RESULT_OK){
                Toast.makeText(
                    this,
                    "Billing account save successfully.",
                    Toast.LENGTH_LONG
                ).show();
            }
        }
    }//onActivityResult
}