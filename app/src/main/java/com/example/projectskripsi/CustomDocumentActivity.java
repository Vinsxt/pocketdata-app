package com.example.projectskripsi;

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

public class CustomDocumentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 2001;

    private ImageView ivImage;
    private View cardImage;
    private TextView tvHint;
    private MaterialButton btnNext;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_document);

        initViews();

        cardImage.setOnClickListener(v -> pickImage());
        btnNext.setOnClickListener(v -> {
            if (imageUri != null) goNext();
            else toast("Upload gambar terlebih dahulu");
        });
    }

    private void initViews() {
        ivImage = findViewById(R.id.ivImage);
        cardImage = findViewById(R.id.cardImage);
        tvHint = findViewById(R.id.tvUploadHint);
        btnNext = findViewById(R.id.btnNext);
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

    private void goNext() {
        Intent i = new Intent(this, DocumentMetaActivity.class);
        i.putExtra("document_type", "CUSTOM");
        i.putExtra("image_uri", imageUri.toString());
        startActivity(i);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}