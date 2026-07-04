package com.example.projectskripsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class CategorySelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_select);
        View main = findViewById(R.id.main);

        final int paddingLeft = main.getPaddingLeft();
        final int paddingTop = main.getPaddingTop();
        final int paddingRight = main.getPaddingRight();
        final int paddingBottom = main.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    paddingLeft + systemBars.left,
                    paddingTop + systemBars.top,
                    paddingRight + systemBars.right,
                    paddingBottom + systemBars.bottom
            );
            return insets;
        });

        // close button
        ImageButton btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> finish());

        // INTENT TO KTPActivity
        MaterialButton btnKtp = findViewById(R.id.btn_ktp);
        btnKtp.setOnClickListener(v -> {
            startActivity(new Intent(this, KTPActivity.class));
        });

        // INTENT TO SIMActivity
        MaterialButton btnSim = findViewById(R.id.btn_sim);
        btnSim.setOnClickListener(v -> {
            startActivity(new Intent(this, SIMActivity.class));
        });

        // INTENT TO NPWPActivity
        MaterialButton btnNpwp = findViewById(R.id.btn_npwp);
        btnNpwp.setOnClickListener(v -> {
            startActivity(new Intent(this, NPWPActivity.class));
        });

        // INTENT TO STNKActivity
        MaterialButton btnStnk = findViewById(R.id.btn_stnk);
        btnStnk.setOnClickListener(v -> {
            startActivity(new Intent(this, STNKActivity.class));
        });

        // INTENT TO ReminderActivity
        MaterialButton btnReminder = findViewById(R.id.btn_reminder);
        btnReminder.setOnClickListener(v -> {
            startActivity(new Intent(this, ReminderActivity.class));
        });

        // INTENT TO CustomDocumentActivity
        MaterialButton btnCustom = findViewById(R.id.btn_custom);
        btnCustom.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomDocumentActivity.class));
        });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}