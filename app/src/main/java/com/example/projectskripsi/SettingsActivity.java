package com.example.projectskripsi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnChangePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupActions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnChangePin = findViewById(R.id.btnChangePin);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        btnChangePin.setOnClickListener(v -> {
            Intent intent = new Intent(this, PinActivity.class);
            intent.putExtra("change_pin", true); // ⬅️ INI YANG DIPAKAI
            startActivity(intent);
        });
    }
}