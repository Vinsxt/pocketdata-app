package com.example.projectskripsi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
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

/**
 * SIMActivity
 *
 * Handles SIM form input:
 * - Image upload & preview
 * - Personal and SIM-related data
 * - Date picking (birth date & expiry date)
 * - Validation
 * - Forwarding data to DocumentMetaActivity
 */
public class SIMActivity extends AppCompatActivity {

    // ================= CONSTANTS =================
    private static final int PICK_IMAGE_REQUEST = 1;

    // ================= IMAGE =================
    private ImageView imageViewSim;
    private View cardSimImage;
    private TextView tvUploadHint;
    private Uri selectedImageUri;

    // ================= INPUTS =================
    private TextInputEditText
            etNomorSim,
            etNama,
            etTempatLahir,
            etTanggalLahir,
            etMasaBerlaku,
            etPekerjaan,
            etPolda;

    private AutoCompleteTextView
            etJenisSim,
            etGolonganDarah,
            etGender;

    private MaterialButton btnSaveSim;

    // ================= STATE (Firestore-safe) =================
    private Date tanggalLahir;
    private Date masaBerlaku;

    // ================= LIFECYCLE =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim);

        initViews();
        setupDropdowns();
        setupActions();
    }

    // ================= INIT =================

    /**
     * Bind all views and apply input filters.
     */
    private void initViews() {
        imageViewSim = findViewById(R.id.imageViewSim);
        cardSimImage = findViewById(R.id.cardSimImage);
        tvUploadHint = findViewById(R.id.tvUploadHint);

        etNomorSim = findViewById(R.id.etNomorSim);
        etNama = findViewById(R.id.etNama);
        etTempatLahir = findViewById(R.id.etTempatLahir);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        etMasaBerlaku = findViewById(R.id.etMasaBerlaku);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etPolda = findViewById(R.id.etPolda);

        etJenisSim = findViewById(R.id.etJenisSim);
        etGolonganDarah = findViewById(R.id.etGolonganDarah); // MUST match XML
        etGender = findViewById(R.id.etGender);

        btnSaveSim = findViewById(R.id.btnSaveSim);

        InputFilter[] caps = new InputFilter[]{new InputFilter.AllCaps()};
        etNama.setFilters(caps);
        etTempatLahir.setFilters(caps);
        etPekerjaan.setFilters(caps);
        etPolda.setFilters(caps);
    }

    /**
     * Setup dropdown adapters for SIM type, blood type, and gender.
     */
    private void setupDropdowns() {
        etJenisSim.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"A", "B1", "B2", "C", "D"}
        ));

        etGolonganDarah.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"A", "B", "AB", "O"}
        ));

        etGender.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Laki-Laki", "Perempuan"}
        ));
    }

    /**
     * Attach click listeners for date pickers, image picker, and save button.
     */
    private void setupActions() {
        etTanggalLahir.setOnClickListener(v -> showDatePicker(true));
        etMasaBerlaku.setOnClickListener(v -> showDatePicker(false));
        cardSimImage.setOnClickListener(v -> openImageChooser());

        btnSaveSim.setOnClickListener(v -> {
            if (validateInputs()) {
                goToDocumentMeta();
            }
        });
    }

    // ================= VALIDATION =================

    /**
     * Validate required inputs before proceeding.
     */
    private boolean validateInputs() {

        // ================= IMAGE =================
        if (selectedImageUri == null) {
            toast("Upload foto SIM terlebih dahulu");
            return false;
        }

        // ================= NOMOR SIM =================
        String nomorSim = etNomorSim.getText().toString().trim();
        if (nomorSim.isEmpty()) {
            etNomorSim.setError("Nomor SIM wajib diisi");
            etNomorSim.requestFocus();
            return false;
        }

        // Realistis: nomor SIM min 10 digit (beda daerah bisa beda format)
        if (!nomorSim.matches("[A-Z0-9]{10,}")) {
            etNomorSim.setError("Format nomor SIM tidak valid");
            etNomorSim.requestFocus();
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
        if (tanggalLahir == null) {
            toast("Tanggal lahir wajib dipilih");
            etTanggalLahir.requestFocus();
            return false;
        }

        // Minimal umur 17 tahun (aturan SIM Indonesia)
        Calendar minAge = Calendar.getInstance();
        minAge.add(Calendar.YEAR, -17);
        if (tanggalLahir.after(minAge.getTime())) {
            toast("Usia minimal pemilik SIM adalah 17 tahun");
            return false;
        }

        // ================= JENIS SIM =================
        if (isEmpty(etJenisSim)) {
            etJenisSim.setError("Pilih jenis SIM");
            etJenisSim.requestFocus();
            return false;
        }

        // ================= GOLONGAN DARAH =================
        if (isEmpty(etGolonganDarah)) {
            etGolonganDarah.setError("Pilih golongan darah");
            etGolonganDarah.requestFocus();
            return false;
        }

        // ================= GENDER =================
        if (isEmpty(etGender)) {
            etGender.setError("Pilih jenis kelamin");
            etGender.requestFocus();
            return false;
        }

        // ================= PEKERJAAN =================
        if (isEmpty(etPekerjaan)) {
            etPekerjaan.setError("Pekerjaan wajib diisi");
            etPekerjaan.requestFocus();
            return false;
        }

        // ================= POLDA =================
        if (isEmpty(etPolda)) {
            etPolda.setError("Polda penerbit wajib diisi");
            etPolda.requestFocus();
            return false;
        }

        // ================= MASA BERLAKU =================
        if (masaBerlaku == null) {
            toast("Tanggal masa berlaku wajib dipilih");
            etMasaBerlaku.requestFocus();
            return false;
        }

        // Masa berlaku harus > hari ini
        if (masaBerlaku.before(new Date())) {
            toast("Masa berlaku SIM sudah kadaluarsa");
            return false;
        }

        return true;
    }

    // ================= NAVIGATION =================

    /**
     * Send SIM data to DocumentMetaActivity.
     */
    private void goToDocumentMeta() {

        if (selectedImageUri == null) {
            toast("Upload foto SIM terlebih dahulu");
            return;
        }

        Intent intent = new Intent(this, DocumentMetaActivity.class);

        intent.putExtra("document_type", "SIM");

        intent.putExtra("nomor_sim", etNomorSim.getText().toString().trim());
        intent.putExtra("nama", etNama.getText().toString().trim());
        intent.putExtra("tempat_lahir", etTempatLahir.getText().toString().trim());
        intent.putExtra("tanggal_lahir", tanggalLahir.getTime());
        intent.putExtra("masa_berlaku", masaBerlaku.getTime());

        intent.putExtra("jenis_sim", etJenisSim.getText().toString());
        intent.putExtra("golongan_darah", etGolonganDarah.getText().toString());
        intent.putExtra("gender", etGender.getText().toString());

        intent.putExtra("pekerjaan", etPekerjaan.getText().toString().trim());
        intent.putExtra("polda", etPolda.getText().toString().trim());

        intent.putExtra("image_uri", selectedImageUri.toString());

        startActivity(intent);
    }

    // ================= DATE PICKER =================

    /**
     * Show date picker for birth date or expiry date.
     */
    private void showDatePicker(boolean isBirthDate) {
        Calendar calendar = Calendar.getInstance();

        new android.app.DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, day);

                    String formatted = String.format(
                            "%02d-%02d-%04d",
                            day,
                            month + 1,
                            year
                    );

                    if (isBirthDate) {
                        tanggalLahir = selected.getTime();
                        etTanggalLahir.setText(formatted);
                    } else {
                        masaBerlaku = selected.getTime();
                        etMasaBerlaku.setText(formatted);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ================= IMAGE PICKER =================

    /**
     * Open gallery to select SIM image.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handle selected image and correctly reset ImageView state.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            selectedImageUri = data.getData();

            // RESET IMAGEVIEW STATE
            imageViewSim.setPadding(0, 0, 0, 0);
            imageViewSim.setImageTintList(null);
            imageViewSim.setBackground(null);
            imageViewSim.setImageDrawable(null);

            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(imageViewSim);

            tvUploadHint.setVisibility(View.GONE);
        }
    }

    // ================= UTILS =================

    private boolean isEmpty(TextInputEditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    private boolean isEmpty(AutoCompleteTextView et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    /**
     * Show short toast message.
     */
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
