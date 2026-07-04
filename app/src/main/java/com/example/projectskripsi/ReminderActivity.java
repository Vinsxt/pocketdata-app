package com.example.projectskripsi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1001;

    private ImageView ivImage;
    private View cardImage;
    private TextView tvHint, tvDate;
    private MaterialButton btnNext;
    private MaterialButtonToggleGroup toggleReminder;

    private Uri imageUri;
    private Date reminderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        initViews();

        cardImage.setOnClickListener(v -> pickImage());
        tvDate.setOnClickListener(v -> pickDate());

        btnNext.setOnClickListener(v -> {
            if (validate()) goNext();
        });
    }

    private void initViews() {
        ivImage = findViewById(R.id.ivImage);
        cardImage = findViewById(R.id.cardImage);
        tvHint = findViewById(R.id.tvUploadHint);
        tvDate = findViewById(R.id.tvReminderDate);
        btnNext = findViewById(R.id.btnNext);
        toggleReminder = findViewById(R.id.toggleReminder);
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGE && res == RESULT_OK && data != null) {
            imageUri = data.getData();

            // RESET IMAGEVIEW STATE (ANTI GREY BOX)
            ivImage.setPadding(0, 0, 0, 0);
            ivImage.setImageDrawable(null);
            ivImage.setImageTintList(null);
            ivImage.setColorFilter(null);
            ivImage.setBackground(null);
            ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .into(ivImage);

            tvHint.setVisibility(View.GONE);
        }
    }

    private void pickDate() {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (v, y, m, d) -> {
                    cal.set(y, m, d);
                    reminderDate = cal.getTime();
                    tvDate.setText(
                            String.format("%02d-%02d-%04d", d, m + 1, y)
                    );
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private int[] getSelectedReminderOffsets() {
        List<Integer> offsets = new ArrayList<>();

        if (toggleReminder.getCheckedButtonIds().contains(R.id.btn30)) offsets.add(30);
        if (toggleReminder.getCheckedButtonIds().contains(R.id.btn7)) offsets.add(7);
        if (toggleReminder.getCheckedButtonIds().contains(R.id.btn3)) offsets.add(3);
        if (toggleReminder.getCheckedButtonIds().contains(R.id.btn1)) offsets.add(1);

        int[] result = new int[offsets.size()];
        for (int i = 0; i < offsets.size(); i++) {
            result[i] = offsets.get(i);
        }
        return result;
    }

    private boolean validate() {
        if (imageUri == null) {
            toast("Upload gambar terlebih dahulu");
            return false;
        }
        if (reminderDate == null) {
            toast("Pilih tanggal reminder");
            return false;
        }

        int[] offsets = getSelectedReminderOffsets();
        if (offsets.length == 0) {
            toast("Pilih minimal satu reminder");
            return false;
        }
        return true;
    }

    private void goNext() {
        int[] offsets = getSelectedReminderOffsets();

        Intent i = new Intent(this, DocumentMetaActivity.class);
        i.putExtra("document_type", "REMINDER");
        i.putExtra("reminder_date", reminderDate.getTime());
        i.putExtra("reminder_offsets", offsets);
        i.putExtra("image_uri", imageUri.toString());
        startActivity(i);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}