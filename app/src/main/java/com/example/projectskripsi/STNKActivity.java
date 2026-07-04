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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class STNKActivity extends AppCompatActivity {

    // ================= CONSTANT =================
    private static final int PICK_IMAGE_REQUEST = 1;

    // ================= IMAGE =================
    private ImageView imageView;
    private View cardImage;
    private TextView tvUploadHint;
    private Uri selectedImageUri;

    // ================= INPUTS =================
    private TextInputEditText
            etNomorPolisi,
            etNamaPemilik,
            etMerkTipe,
            etJenisModel,
            etTahunPembuatan,
            etIsiSilinder,
            etNomorRangka,
            etNomorMesin,
            etNomorBpkb,
            etMasaBerlaku;

    private MaterialButton btnSave;

    // ================= STATE =================
    private Date masaBerlaku; // untuk Firestore

    // ================= LIFECYCLE =================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stnkactivity);

        initViews();
        setupActions();
    }

    // ================= INIT =================

    /**
     * Bind semua view dari XML
     */
    private void initViews() {
        imageView = findViewById(R.id.imageView);
        cardImage = findViewById(R.id.cardImage);
        tvUploadHint = findViewById(R.id.tvUploadHint);
        etNomorPolisi    = findViewById(R.id.etNomorPolisi);
        etMerkTipe       = findViewById(R.id.etMerkTipe);
        etJenisModel     = findViewById(R.id.etJenisModel);
        etTahunPembuatan = findViewById(R.id.etTahunPembuatan);
        etIsiSilinder    = findViewById(R.id.etIsiSilinder);
        etNomorRangka    = findViewById(R.id.etNomorRangka);
        etNomorMesin     = findViewById(R.id.etNomorMesin);
        etNomorBpkb      = findViewById(R.id.etNomorBpkb);
        etMasaBerlaku    = findViewById(R.id.etMasaBerlaku);

        btnSave = findViewById(R.id.btnSave);
    }

    /**
     * Setup semua click listener
     */
    private void setupActions() {
        cardImage.setOnClickListener(v -> openImageChooser());
        etMasaBerlaku.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> goNext());
    }

    // ================= IMAGE PICKER =================

    /**
     * Open gallery untuk memilih foto STNK
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            selectedImageUri = data.getData();

            // RESET IMAGEVIEW STATE (ANTI GREY BOX)
            imageView.setPadding(0, 0, 0, 0);
            imageView.setImageDrawable(null);
            imageView.setImageTintList(null);
            imageView.setColorFilter(null);
            imageView.setBackground(null);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(imageView);

            tvUploadHint.setVisibility(View.GONE);
        }
    }

    // ================= DATE PICKER =================

    /**
     * Pick masa berlaku STNK
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    masaBerlaku = calendar.getTime();

                    etMasaBerlaku.setText(
                            String.format("%02d-%02d-%04d", day, month + 1, year)
                    );
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ================= NAVIGATION =================

    /**
     * Validasi input dan lanjut ke DocumentMetaActivity
     */
    private void goNext() {

        // ================= IMAGE =================
        if (selectedImageUri == null) {
            toast("Upload foto STNK terlebih dahulu");
            return;
        }

        // ================= NOMOR POLISI =================
        String nomorPolisi = etNomorPolisi.getText().toString().trim();
        if (nomorPolisi.isEmpty()) {
            etNomorPolisi.setError("Nomor polisi wajib diisi");
            etNomorPolisi.requestFocus();
            return;
        }

        // ================= MERK / TIPE =================
        String merkTipe = etMerkTipe.getText().toString().trim();
        if (merkTipe.isEmpty()) {
            etMerkTipe.setError("Merk / tipe wajib diisi");
            etMerkTipe.requestFocus();
            return;
        }

        // ================= JENIS MODEL =================
        String jenisModel = etJenisModel.getText().toString().trim();
        if (jenisModel.isEmpty()) {
            etJenisModel.setError("Jenis / model wajib diisi");
            etJenisModel.requestFocus();
            return;
        }

        // ================= TAHUN PEMBUATAN =================
        String tahunPembuatanStr = etTahunPembuatan.getText().toString().trim();
        if (tahunPembuatanStr.isEmpty()) {
            etTahunPembuatan.setError("Tahun pembuatan wajib diisi");
            etTahunPembuatan.requestFocus();
            return;
        }

        int tahunPembuatan;
        try {
            tahunPembuatan = Integer.parseInt(tahunPembuatanStr);
        } catch (NumberFormatException e) {
            etTahunPembuatan.setError("Tahun harus berupa angka");
            etTahunPembuatan.requestFocus();
            return;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (tahunPembuatan < 1980 || tahunPembuatan > currentYear) {
            etTahunPembuatan.setError("Tahun tidak valid");
            etTahunPembuatan.requestFocus();
            return;
        }

        // ================= ISI SILINDER =================
        String isiSilinder = etIsiSilinder.getText().toString().trim();
        if (isiSilinder.isEmpty()) {
            etIsiSilinder.setError("Isi silinder wajib diisi");
            etIsiSilinder.requestFocus();
            return;
        }

        // ================= NOMOR RANGKA =================
        String nomorRangka = etNomorRangka.getText().toString().trim();
        if (nomorRangka.isEmpty()) {
            etNomorRangka.setError("Nomor rangka wajib diisi");
            etNomorRangka.requestFocus();
            return;
        }

        // ================= NOMOR MESIN =================
        String nomorMesin = etNomorMesin.getText().toString().trim();
        if (nomorMesin.isEmpty()) {
            etNomorMesin.setError("Nomor mesin wajib diisi");
            etNomorMesin.requestFocus();
            return;
        }

        // ================= NOMOR BPKB =================
        String nomorBpkb = etNomorBpkb.getText().toString().trim();
        if (nomorBpkb.isEmpty()) {
            etNomorBpkb.setError("Nomor BPKB wajib diisi");
            etNomorBpkb.requestFocus();
            return;
        }

        // ================= MASA BERLAKU =================
        if (masaBerlaku == null) {
            toast("Masa berlaku wajib diisi");
            etMasaBerlaku.requestFocus();
            return;
        }

        if (masaBerlaku.before(new Date())) {
            toast("Masa berlaku tidak boleh sudah lewat");
            return;
        }

        // ================= NAVIGATION =================
        Intent intent = new Intent(this, DocumentMetaActivity.class);
        intent.putExtra("document_type", "STNK");

        intent.putExtra("nomor_polisi", nomorPolisi);
        intent.putExtra("merk_tipe", merkTipe);
        intent.putExtra("jenis_model", jenisModel);
        intent.putExtra("tahun_pembuatan", tahunPembuatan);
        intent.putExtra("isi_silinder", isiSilinder);
        intent.putExtra("nomor_rangka", nomorRangka);
        intent.putExtra("nomor_mesin", nomorMesin);
        intent.putExtra("nomor_bpkb", nomorBpkb);

        intent.putExtra("masa_berlaku", masaBerlaku.getTime());
        intent.putExtra("image_uri", selectedImageUri.toString());

        startActivity(intent);
    }

    // ================= UTIL =================

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}