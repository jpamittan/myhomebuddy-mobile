package com.example.myhomebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.myhomebuddy.ui.login.LoginActivity;

public class RegistrationSuccessful extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_successful);

        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationSuccessful.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}