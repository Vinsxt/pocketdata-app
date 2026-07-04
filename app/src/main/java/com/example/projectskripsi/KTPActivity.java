package com.example.projectskripsi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class KTPActivity extends AppCompatActivity {

    // ================= CONSTANTS =================
    private static final int PICK_IMAGE_REQUEST = 1;

    // ================= IMAGE =================
    private ImageView imageViewKtp;
    private View cardKtpImage;
    private TextView tvUploadHint;
    private Uri selectedImageUri;

    // ================= INPUTS =================
    private TextInputEditText
            etNik, etNama, etTempatLahir, etTanggalLahir,
            etAgama, etPekerjaan,
            etAlamatJalan, etKelurahan, etKecamatan, etKota, etProvinsi;
    private AutoCompleteTextView etStatus;
    private AutoCompleteTextView etGender;
    private MaterialButton btnSaveKtp;

    // ================= STATE =================
    private Date selectedTanggalLahir; // INI YANG AKAN DISIMPAN

    /**
     * Called when the activity is starting. Sets up the UI and event listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktp);

        // Initialize all views
        initViews();

        // Setup gender dropdown menu
        setupGenderDropdown();

        // Setup marital status dropdown menu
        setupMaritalStatusDropdown();

        // Setup date picker for tanggal lahir input
        etTanggalLahir.setOnClickListener(v -> showDatePicker());

        // Setup image chooser on card click
        cardKtpImage.setOnClickListener(v -> openImageChooser());

        // Setup save button with validation and navigation
        btnSaveKtp.setOnClickListener(v -> {
            if (validateInputs()) goToDocumentMeta();
        });
    }

    /**
     * Initializes all UI components and applies necessary input filters.
     */
    private void initViews() {
        // Image views
        imageViewKtp = findViewById(R.id.imageViewKtp);
        cardKtpImage = findViewById(R.id.cardKtpImage);
        tvUploadHint = findViewById(R.id.tvUploadHint);

        // Personal information inputs
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNama);
        etTempatLahir = findViewById(R.id.etTempatLahir);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        etGender = findViewById(R.id.etGender);
        etAgama = findViewById(R.id.etAgama);
        etStatus = findViewById(R.id.etStatus);
        etPekerjaan = findViewById(R.id.etPekerjaan);

        // Address inputs
        etAlamatJalan = findViewById(R.id.etAlamatJalan);
        etKelurahan = findViewById(R.id.etKelurahan);
        etKecamatan = findViewById(R.id.etKecamatan);
        etKota = findViewById(R.id.etKota);
        etProvinsi = findViewById(R.id.etProvinsi);

        // Save button
        btnSaveKtp = findViewById(R.id.btnSaveKtp);

        // Apply AllCaps input filter to enforce uppercase input for consistency
        InputFilter[] caps = new InputFilter[]{new InputFilter.AllCaps()};

        etNama.setFilters(caps);
        etTempatLahir.setFilters(caps);
        etAgama.setFilters(caps);
        etPekerjaan.setFilters(caps);

        etAlamatJalan.setFilters(caps);
        etKelurahan.setFilters(caps);
        etKecamatan.setFilters(caps);
        etKota.setFilters(caps);
        etProvinsi.setFilters(caps);
    }

    /**
     * Sets up the gender dropdown AutoCompleteTextView with options.
     */
    private void setupGenderDropdown() {
        String[] genders = {"Laki-Laki", "Perempuan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        etGender.setAdapter(adapter);
    }

    /**
     * Sets up the marital status dropdown AutoCompleteTextView with options.
     */
    private void setupMaritalStatusDropdown() {
        String[] statuses = {
                "BELUM KAWIN",
                "KAWIN",
                "CERAI HIDUP",
                "CERAI MATI"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                statuses
        );
        etStatus.setAdapter(adapter);
    }

    // ================= IMAGE PICKER =================

    /**
     * Opens the image chooser to pick an image from device storage.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from image chooser intent and updates the ImageView.
     * @param requestCode The request code passed in startActivityForResult.
     * @param resultCode The result code returned by the child activity.
     * @param data The intent data returned.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            selectedImageUri = data.getData();

            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(imageViewKtp);

            // Reset ImageView state to remove camera placeholder styling (padding, background, color filters)
            imageViewKtp.setPadding(0, 0, 0, 0);
            imageViewKtp.setBackground(null);
            imageViewKtp.setColorFilter(null);
            imageViewKtp.setImageTintList(null);

            tvUploadHint.setVisibility(View.GONE);
        }
    }

    // ================= VALIDATION =================

    /**
     * Validates required inputs before proceeding.
     * @return true if all validations pass; false otherwise.
     */
    private boolean validateInputs() {

        // ================= IMAGE =================
        if (selectedImageUri == null) {
            toast("Upload foto KTP terlebih dahulu");
            return false;
        }

        // ================= NIK =================
        String nik = etNik.getText().toString().trim();
        if (nik.length() != 16 || !nik.matches("\\d{16}")) {
            etNik.setError("NIK harus 16 digit angka");
            etNik.requestFocus();
            return false;
        }

        // ================= NAMA =================
        if (isEmpty(etNama)) {
            etNama.setError("Nama wajib diisi");
            etNama.requestFocus();
            return false;
        }

        // ================= TEMPAT LAHIR =================
        if (isEmpty(etTempatLahir)) {
            etTempatLahir.setError("Tempat lahir wajib diisi");
            etTempatLahir.requestFocus();
            return false;
        }

        // ================= TANGGAL LAHIR =================
        if (selectedTanggalLahir == null) {
            toast("Tanggal lahir wajib dipilih");
            etTanggalLahir.requestFocus();
            return false;
        }

        // ================= GENDER =================
        if (TextUtils.isEmpty(etGender.getText())) {
            etGender.setError("Pilih jenis kelamin");
            etGender.requestFocus();
            return false;
        }

        // ================= AGAMA =================
        if (isEmpty(etAgama)) {
            etAgama.setError("Agama wajib diisi");
            etAgama.requestFocus();
            return false;
        }

        // ================= STATUS =================
        if (TextUtils.isEmpty(etStatus.getText())) {
            etStatus.setError("Pilih status perkawinan");
            etStatus.requestFocus();
            return false;
        }

        // ================= PEKERJAAN =================
        if (isEmpty(etPekerjaan)) {
            etPekerjaan.setError("Pekerjaan wajib diisi");
            etPekerjaan.requestFocus();
            return false;
        }

        // ================= ALAMAT =================
        if (isEmpty(etAlamatJalan)) {
            etAlamatJalan.setError("Alamat jalan wajib diisi");
            etAlamatJalan.requestFocus();
            return false;
        }

        if (isEmpty(etKota)) {
            etKota.setError("Kota/Kabupaten wajib diisi");
            etKota.requestFocus();
            return false;
        }

        if (isEmpty(etProvinsi)) {
            etProvinsi.setError("Provinsi wajib diisi");
            etProvinsi.requestFocus();
            return false;
        }

        // ================= OPTIONAL FIELDS CHECK =================
        // Kelurahan & Kecamatan boleh kosong, tapi kalau ada isinya minimal 3 char
        if (!isEmpty(etKelurahan) && etKelurahan.getText().toString().trim().length() < 3) {
            etKelurahan.setError("Nama kelurahan terlalu pendek");
            etKelurahan.requestFocus();
            return false;
        }

        if (!isEmpty(etKecamatan) && etKecamatan.getText().toString().trim().length() < 3) {
            etKecamatan.setError("Nama kecamatan terlalu pendek");
            etKecamatan.requestFocus();
            return false;
        }

        return true;
    }

    // ================= NAVIGATION =================

    /**
     * Prepares data and navigates to DocumentMetaActivity for Firestore storage.
     */
    private void goToDocumentMeta() {
        Intent intent = new Intent(this, DocumentMetaActivity.class);

        intent.putExtra("document_type", "KTP");

        intent.putExtra("nik", etNik.getText().toString().trim());
        intent.putExtra("nama", etNama.getText().toString().trim());
        intent.putExtra("tempat_lahir", etTempatLahir.getText().toString().trim());
        intent.putExtra("tanggal_lahir", selectedTanggalLahir.getTime());
        intent.putExtra("gender", etGender.getText().toString().trim());
        intent.putExtra("agama", etAgama.getText().toString().trim());
        intent.putExtra("status", etStatus.getText().toString().trim());
        intent.putExtra("pekerjaan", etPekerjaan.getText().toString().trim());

        intent.putExtra("alamat_jalan", etAlamatJalan.getText().toString().trim());
        intent.putExtra("kelurahan", etKelurahan.getText().toString().trim());
        intent.putExtra("kecamatan", etKecamatan.getText().toString().trim());
        intent.putExtra("kota", etKota.getText().toString().trim());
        intent.putExtra("provinsi", etProvinsi.getText().toString().trim());

        intent.putExtra("image_uri", selectedImageUri.toString());

        startActivity(intent);
    }

    // ================= DATE PICKER =================

    /**
     * Shows a date picker dialog and sets the selected date to the tanggal lahir input.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog =
                new android.app.DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            Calendar selectedCal = Calendar.getInstance();
                            selectedCal.set(Calendar.YEAR, selectedYear);
                            selectedCal.set(Calendar.MONTH, selectedMonth);
                            selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay);

                            // Save selected date for Firestore
                            selectedTanggalLahir = selectedCal.getTime();

                            // Display formatted date in UI (dd-MM-yyyy)
                            String formattedDate = String.format("%02d-%02d-%04d",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear);

                            etTanggalLahir.setText(formattedDate);
                        },
                        year, month, day
                );

        datePickerDialog.show();
    }

    private boolean isEmpty(TextInputEditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
