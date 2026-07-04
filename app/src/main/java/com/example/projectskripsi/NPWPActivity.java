package com.example.projectskripsi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
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

public class NPWPActivity extends AppCompatActivity {

    // ================= CONSTANTS =================
    private static final int PICK_IMAGE_REQUEST = 1;

    // ================= IMAGE =================
    private ImageView imageView;
    private View cardImage;
    private TextView tvUploadHint;
    private Uri selectedImageUri;

    // ================= INPUTS =================
    private TextInputEditText
            etNpwp,
            etNik,
            etNama,
            etTanggalTerdaftar;

    private MaterialButton btnSave;

    // ================= STATE =================
    private Date tanggalTerdaftar; // disimpan ke Firestore

    // ================= LIFECYCLE =================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npwpactivity);

        initViews();
        setupActions();
    }

    // ================= INIT =================

    /**
     * Bind all XML views
     */
    private void initViews() {
        imageView = findViewById(R.id.imageView);
        cardImage = findViewById(R.id.cardImage);
        tvUploadHint = findViewById(R.id.tvUploadHint);

        etNpwp = findViewById(R.id.etNpwp);
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNama);
        etTanggalTerdaftar = findViewById(R.id.etTanggalTerdaftar);

        btnSave = findViewById(R.id.btnSave);

        InputFilter[] caps = new InputFilter[]{new InputFilter.AllCaps()};
        etNama.setFilters(caps);
    }

    /**
     * Setup click listeners
     */
    private void setupActions() {
        cardImage.setOnClickListener(v -> openImageChooser());
        etTanggalTerdaftar.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> goNext());
    }

    // ================= IMAGE PICKER =================

    /**
     * Open gallery to pick NPWP image
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

            // 🔥 RESET IMAGEVIEW STATE (ANTI GREY BOX)
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
     * Pick NPWP registration date
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    tanggalTerdaftar = calendar.getTime();

                    etTanggalTerdaftar.setText(
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
     * Validate input and move to DocumentMetaActivity
     */
    private void goNext() {

        // ================= IMAGE =================
        if (selectedImageUri == null) {
            toast("Upload foto NPWP terlebih dahulu");
            return;
        }

        // ================= NPWP =================
        String npwp = etNpwp.getText().toString().trim();
        if (npwp.isEmpty()) {
            etNpwp.setError("Nomor NPWP wajib diisi");
            etNpwp.requestFocus();
            return;
        }

        // NPWP pribadi = 15 digit angka
        if (!npwp.matches("\\d{15}")) {
            etNpwp.setError("NPWP harus 15 digit angka");
            etNpwp.requestFocus();
            return;
        }

        // ================= NIK =================
        String nik = etNik.getText().toString().trim();
        if (nik.isEmpty()) {
            etNik.setError("NIK wajib diisi");
            etNik.requestFocus();
            return;
        }

        if (!nik.matches("\\d{16}")) {
            etNik.setError("NIK harus 16 digit angka");
            etNik.requestFocus();
            return;
        }

        // ================= NAMA =================
        String nama = etNama.getText().toString().trim();
        if (nama.isEmpty()) {
            etNama.setError("Nama wajib diisi");
            etNama.requestFocus();
            return;
        }

        // Nama hanya huruf & spasi
        if (!nama.matches("[A-Z ]+")) {
            etNama.setError("Nama hanya boleh huruf");
            etNama.requestFocus();
            return;
        }

        // ================= TANGGAL TERDAFTAR =================
        if (tanggalTerdaftar == null) {
            toast("Tanggal terdaftar wajib diisi");
            etTanggalTerdaftar.requestFocus();
            return;
        }

        // Tidak boleh tanggal masa depan
        if (tanggalTerdaftar.after(new Date())) {
            toast("Tanggal terdaftar tidak boleh di masa depan");
            return;
        }

        // ================= NAVIGATE =================
        Intent intent = new Intent(this, DocumentMetaActivity.class);
        intent.putExtra("document_type", "NPWP");

        intent.putExtra("nomor_npwp", npwp);
        intent.putExtra("nik", nik);
        intent.putExtra("nama", nama);
        intent.putExtra("tanggal_terdaftar", tanggalTerdaftar.getTime());
        intent.putExtra("image_uri", selectedImageUri.toString());

        startActivity(intent);
    }

    // ================= UTIL =================

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmpty(TextInputEditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }
}