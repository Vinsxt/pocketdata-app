package com.example.projectskripsi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DocumentUpdateActivity extends AppCompatActivity {

    // ================= COMMON =================
    private String docId, type;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private String currentImageUrl;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    // ================= LAYOUTS =================
    private View layoutKtp, layoutSim, layoutStnk, layoutNpwp;

    // ================= KTP =================
    private ImageView ktpImage;
    private TextInputEditText etNik, etNama, etTempatLahir, etTanggalLahir, etAgama,
            etNomorRumah, etRtRw, etKelDesa, etKecamatan, etKota, etProvinsi, etPekerjaan;
    private MaterialAutoCompleteTextView etGender, etStatus;
    private MaterialButton btnSaveKtp;
    private Date ktpTanggalLahir;

    // ================= SIM =================
    private ImageView simImage;
    private TextInputEditText sim_etNomorSim, sim_etNama, sim_etTempatLahir,
            sim_etTanggalLahir, sim_etPekerjaan, sim_etPolda, sim_etMasaBerlaku;
    private MaterialAutoCompleteTextView sim_etJenisSim, sim_etGolonganDarah, sim_etGender;
    private MaterialButton btnSaveSim;
    private Date simTanggalLahir, simMasaBerlaku;

    // ================= STNK =================
    private ImageView stnkImage;
    private TextInputEditText stnk_etNomorPolisi, stnk_etNamaPemilik, stnk_etMerkTipe,
            stnk_etJenisModel, stnk_etTahunPembuatan, stnk_etIsiSilinder,
            stnk_etNomorRangka, stnk_etNomorMesin, stnk_etNomorBpkb, stnk_etMasaBerlaku;
    private MaterialButton stnk_btnSave;
    private Date stnkMasaBerlaku;

    // ================= NPWP =================
    private ImageView npwpImage;
    private TextInputEditText npwp_etNpwp, npwp_etNik, npwp_etNama, npwp_etTanggalTerdaftar;
    private MaterialButton npwp_btnSave;
    private Date npwpTanggal;

    // ================= IMAGE PICKER =================
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedImageUri = uri;
                            getActiveImageView().setImageURI(uri);
                        }
                    });

    // ================= ON CREATE =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_update);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        docId = getIntent().getStringExtra("docId");
        type = getIntent().getStringExtra("type");

        initViews();
        hideAllLayouts();
        loadDocument();

        ktpImage.setOnClickListener(v -> pickImage());
        simImage.setOnClickListener(v -> pickImage());
        stnkImage.setOnClickListener(v -> pickImage());
        npwpImage.setOnClickListener(v -> pickImage());

        btnSaveKtp.setOnClickListener(v -> updateKtp());
        btnSaveSim.setOnClickListener(v -> updateSim());
        stnk_btnSave.setOnClickListener(v -> updateStnk());
        npwp_btnSave.setOnClickListener(v -> updateNpwp());

    }

    // ================= INIT =================
    private void initViews() {
        layoutKtp = findViewById(R.id.layoutKtp);
        layoutSim = findViewById(R.id.layoutSim);
        layoutStnk = findViewById(R.id.layoutStnk);
        layoutNpwp = findViewById(R.id.layoutNpwp);

        // KTP
        ktpImage = findViewById(R.id.imageViewKtp);
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNama);
        etTempatLahir = findViewById(R.id.etTempatLahir);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        etGender = findViewById(R.id.etGender);
        etAgama = findViewById(R.id.etAgama);
        etNomorRumah = findViewById(R.id.etNomorRumah);
        etRtRw = findViewById(R.id.etRtRw);
        etKelDesa = findViewById(R.id.etKelDesa);
        etKecamatan = findViewById(R.id.etKecamatan);
        etKota = findViewById(R.id.etKota);
        etProvinsi = findViewById(R.id.etProvinsi);
        etStatus = findViewById(R.id.etStatus);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        btnSaveKtp = findViewById(R.id.btnSaveKtp);

        // SIM
        simImage = findViewById(R.id.imageViewSim);
        sim_etNomorSim = findViewById(R.id.sim_etNomorSim);
        sim_etNama = findViewById(R.id.sim_etNama);
        sim_etTempatLahir = findViewById(R.id.sim_etTempatLahir);
        sim_etTanggalLahir = findViewById(R.id.sim_etTanggalLahir);
        sim_etJenisSim = findViewById(R.id.sim_etJenisSim);
        sim_etGolonganDarah = findViewById(R.id.sim_etGolonganDarah);
        sim_etGender = findViewById(R.id.sim_etGender);
        sim_etPekerjaan = findViewById(R.id.sim_etPekerjaan);
        sim_etPolda = findViewById(R.id.sim_etPolda);
        sim_etMasaBerlaku = findViewById(R.id.sim_etMasaBerlaku);
        btnSaveSim = findViewById(R.id.btnSaveSim);

        // STNK
        stnkImage = findViewById(R.id.stnk_imageView);
        stnk_etNomorPolisi = findViewById(R.id.stnk_etNomorPolisi);
        stnk_etMerkTipe = findViewById(R.id.stnk_etMerkTipe);
        stnk_etJenisModel = findViewById(R.id.stnk_etJenisModel);
        stnk_etTahunPembuatan = findViewById(R.id.stnk_etTahunPembuatan);
        stnk_etIsiSilinder = findViewById(R.id.stnk_etIsiSilinder);
        stnk_etNomorRangka = findViewById(R.id.stnk_etNomorRangka);
        stnk_etNomorMesin = findViewById(R.id.stnk_etNomorMesin);
        stnk_etNomorBpkb = findViewById(R.id.stnk_etNomorBpkb);
        stnk_etMasaBerlaku = findViewById(R.id.stnk_etMasaBerlaku);
        stnk_btnSave = findViewById(R.id.stnk_btnSave);

        // NPWP
        npwpImage = findViewById(R.id.npwp_imageView);
        npwp_etNpwp = findViewById(R.id.npwp_etNpwp);
        npwp_etNik = findViewById(R.id.npwp_etNik);
        npwp_etNama = findViewById(R.id.npwp_etNama);
        npwp_etTanggalTerdaftar = findViewById(R.id.npwp_etTanggalTerdaftar);
        npwp_btnSave = findViewById(R.id.npwp_btnSave);

        String[] jenisSimList = {"SIM A", "SIM B1", "SIM B2", "SIM C", "SIM D"};
        String[] golDarahList = {"A", "B", "AB", "O"};
        String[] genderList = {"Laki-Laki", "Perempuan"};

        sim_etJenisSim.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                jenisSimList
        ));

        sim_etGolonganDarah.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                golDarahList
        ));

        sim_etGender.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                genderList
        ));
    }

    private void showKtp(KTP ktp) {
        layoutKtp.setVisibility(View.VISIBLE);

        if (ktp == null) return;

        // image
        currentImageUrl = ktp.getImageUrl();
        Glide.with(this)
                .load(currentImageUrl)
                .into(ktpImage);

        // basic fields
        etNik.setText(ktp.getNik());
        etNama.setText(ktp.getNama());
        etTempatLahir.setText(ktp.getTempatLahir());
        etAgama.setText(ktp.getAgama());
        etPekerjaan.setText(ktp.getPekerjaan());

        // gender + status
        etGender.setText(ktp.getJenisKelamin(), false);
        etStatus.setText(ktp.getStatusPerkawinan(), false);

        // tanggal lahir
        if (ktp.getTanggalLahir() != null) {
            ktpTanggalLahir = ktp.getTanggalLahir();
            etTanggalLahir.setText(sdf.format(ktpTanggalLahir));
        }

        etTanggalLahir.setOnClickListener(v ->
                showDatePicker(date -> {
                    ktpTanggalLahir = date;
                    etTanggalLahir.setText(sdf.format(date));
                })
        );

        // alamat (nested object)
        if (ktp.getAlamat() != null) {
            etNomorRumah.setText(ktp.getAlamat().getNomorRumah());
            etRtRw.setText(ktp.getAlamat().getRtRw());
            etKelDesa.setText(ktp.getAlamat().getKelDesa());
            etKecamatan.setText(ktp.getAlamat().getKecamatan());
            etKota.setText(ktp.getAlamat().getKota());
            etProvinsi.setText(ktp.getAlamat().getProvinsi());
        }
    }


    private void showSim(SIM sim) {
        layoutSim.setVisibility(View.VISIBLE);

        if (sim == null) return;

        currentImageUrl = sim.getImageUrl();
        Glide.with(this).load(currentImageUrl).into(simImage);

        sim_etNomorSim.setText(sim.getNomorSIM());
        sim_etNama.setText(sim.getNama());
        sim_etTempatLahir.setText(sim.getTempatLahir());
        sim_etJenisSim.setText(sim.getJenisSIM(), false);
        sim_etGolonganDarah.setText(sim.getGolonganDarah(), false);
        sim_etGender.setText(sim.getJenisKelamin(), false);
        sim_etPekerjaan.setText(sim.getPekerjaan());
        sim_etPolda.setText(sim.getPoldaPenerbit());

        if (sim.getTanggalLahir() != null) {
            simTanggalLahir = sim.getTanggalLahir();
            sim_etTanggalLahir.setText(sdf.format(simTanggalLahir));
        }

        if (sim.getMasaBerlaku() != null) {
            simMasaBerlaku = sim.getMasaBerlaku();
            sim_etMasaBerlaku.setText(sdf.format(simMasaBerlaku));
        }

        sim_etTanggalLahir.setOnClickListener(v ->
                showDatePicker(date -> {
                    simTanggalLahir = date;
                    sim_etTanggalLahir.setText(sdf.format(date));
                })
        );

        sim_etMasaBerlaku.setOnClickListener(v ->
                showDatePicker(date -> {
                    simMasaBerlaku = date;
                    sim_etMasaBerlaku.setText(sdf.format(date));
                })
        );
    }

    private void showStnk(STNK stnk) {
        layoutStnk.setVisibility(View.VISIBLE);

        if (stnk == null) return;

        currentImageUrl = stnk.getImageUrl();
        Glide.with(this).load(currentImageUrl).into(stnkImage);

        stnk_etNomorPolisi.setText(stnk.getNomorRegistrasi());
        stnk_etMerkTipe.setText(stnk.getMerkTipe());
        stnk_etJenisModel.setText(stnk.getJenisModel());

        stnk_etTahunPembuatan.setText(
                String.valueOf(stnk.getTahunPembuatan())
        );

        stnk_etIsiSilinder.setText(
                String.valueOf(stnk.getIsiSilinder())
        );

        stnk_etNomorRangka.setText(stnk.getNomorRangka());
        stnk_etNomorMesin.setText(stnk.getNomorMesin());
        stnk_etNomorBpkb.setText(stnk.getNomorBpkb());

        if (stnk.getMasaBerlaku() != null) {
            stnkMasaBerlaku = stnk.getMasaBerlaku();
            stnk_etMasaBerlaku.setText(sdf.format(stnkMasaBerlaku));
        }

        stnk_etMasaBerlaku.setOnClickListener(v ->
                showDatePicker(date -> {
                    stnkMasaBerlaku = date;
                    stnk_etMasaBerlaku.setText(sdf.format(date));
                })
        );
    }

    private void showNpwp(NPWP npwp) {
        layoutNpwp.setVisibility(View.VISIBLE);

        if (npwp == null) return;

        currentImageUrl = npwp.getImageUrl();
        Glide.with(this).load(currentImageUrl).into(npwpImage);

        npwp_etNpwp.setText(npwp.getNomorNpwp());
        npwp_etNik.setText(npwp.getNik());
        npwp_etNama.setText(npwp.getNama());

        if (npwp.getTanggalTerdaftar() != null) {
            npwpTanggal = npwp.getTanggalTerdaftar();
            npwp_etTanggalTerdaftar.setText(sdf.format(npwpTanggal));
        }

        npwp_etTanggalTerdaftar.setOnClickListener(v ->
                showDatePicker(date -> {
                    npwpTanggal = date;
                    npwp_etTanggalTerdaftar.setText(sdf.format(date));
                })
        );
    }

    private void updateKtp() {
        Map<String, Object> data = new HashMap<>();

        data.put("nik", etNik.getText().toString());
        data.put("nama", etNama.getText().toString());
        data.put("tempatLahir", etTempatLahir.getText().toString());
        data.put("tanggalLahir", ktpTanggalLahir);
        data.put("jenisKelamin", etGender.getText().toString());
        data.put("agama", etAgama.getText().toString());
        data.put("statusPerkawinan", etStatus.getText().toString());
        data.put("pekerjaan", etPekerjaan.getText().toString());

        // alamat sebagai object
        Map<String, Object> alamat = new HashMap<>();
        alamat.put("nomorRumah", etNomorRumah.getText().toString());
        alamat.put("rtRw", etRtRw.getText().toString());
        alamat.put("kelDesa", etKelDesa.getText().toString());
        alamat.put("kecamatan", etKecamatan.getText().toString());
        alamat.put("kota", etKota.getText().toString());
        alamat.put("provinsi", etProvinsi.getText().toString());

        data.put("alamat", alamat);

        saveDocument(data);
    }


    private void updateSim() {
        Map<String, Object> data = new HashMap<>();

        data.put("nomorSIM", sim_etNomorSim.getText().toString());
        data.put("nama", sim_etNama.getText().toString());
        data.put("tempatLahir", sim_etTempatLahir.getText().toString());
        data.put("tanggalLahir", simTanggalLahir);
        data.put("jenisSIM", sim_etJenisSim.getText().toString());
        data.put("golonganDarah", sim_etGolonganDarah.getText().toString());
        data.put("jenisKelamin", sim_etGender.getText().toString());
        data.put("pekerjaan", sim_etPekerjaan.getText().toString());
        data.put("poldaPenerbit", sim_etPolda.getText().toString());
        data.put("masaBerlaku", simMasaBerlaku);

        saveDocument(data);
    }


    private void updateStnk() {
        Map<String, Object> data = new HashMap<>();

        data.put("nomorRegistrasi", stnk_etNomorPolisi.getText().toString());
        data.put("merkTipe", stnk_etMerkTipe.getText().toString());
        data.put("jenisModel", stnk_etJenisModel.getText().toString());
        data.put("tahunPembuatan",
                Integer.parseInt(stnk_etTahunPembuatan.getText().toString()));
        data.put("isiSilinder", stnk_etIsiSilinder.getText().toString());
        data.put("nomorRangka", stnk_etNomorRangka.getText().toString());
        data.put("nomorMesin", stnk_etNomorMesin.getText().toString());
        data.put("nomorBpkb", stnk_etNomorBpkb.getText().toString());
        data.put("masaBerlaku", stnkMasaBerlaku);

        saveDocument(data);
    }


    private void updateNpwp() {
        Map<String, Object> data = new HashMap<>();
        data.put("nomorNpwp", npwp_etNpwp.getText().toString());
        data.put("nik", npwp_etNik.getText().toString());
        data.put("nama", npwp_etNama.getText().toString());
        data.put("tanggalTerdaftar", npwpTanggal);

        saveDocument(data);
    }


    private void saveDocument(Map<String, Object> data) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        if (selectedImageUri != null) {
            StorageReference ref = storage.getReference()
                    .child("documents/" + uid + "/" + docId + ".jpg");

            ref.putFile(selectedImageUri)
                    .continueWithTask(task -> ref.getDownloadUrl())
                    .addOnSuccessListener(uri -> {
                        data.put("imageUrl", uri.toString());
                        updateFirestore(uid, data);
                    });
        } else {
            data.put("imageUrl", currentImageUrl);
            updateFirestore(uid, data);
        }
    }

    private void updateFirestore(String uid, Map<String, Object> data) {
        db.collection("users")
                .document(uid)
                .collection("documents")
                .document(docId)
                .update(data)
                .addOnSuccessListener(v -> {
                    // Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }


    // ================= UTIL =================
    private void hideAllLayouts() {
        layoutKtp.setVisibility(View.GONE);
        layoutSim.setVisibility(View.GONE);
        layoutStnk.setVisibility(View.GONE);
        layoutNpwp.setVisibility(View.GONE);
    }

    private ImageView getActiveImageView() {
        switch (type) {
            case "SIM": return simImage;
            case "STNK": return stnkImage;
            case "NPWP": return npwpImage;
            default: return ktpImage;
        }
    }

    private void pickImage() {
        imagePicker.launch("image/*");
    }

    // ================= LOAD =================
    private void loadDocument() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("documents")
                .document(docId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    switch (type) {
                        case "KTP":
                            KTP ktp = snapshot.toObject(KTP.class);
                            showKtp(ktp);
                            break;
                        case "SIM":
                            SIM sim = snapshot.toObject(SIM.class);
                            showSim(sim);
                            break;
                        case "STNK":
                            STNK stnk = snapshot.toObject(STNK.class);
                            showStnk(stnk);
                            break;
                        case "NPWP":
                            NPWP npwp = snapshot.toObject(NPWP.class);
                            showNpwp(npwp);
                            break;
                    }
                });
    }

    private interface DateCallback {
        void onPick(Date date);
    }

    private void showDatePicker(DateCallback callback) {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(this,
                (view, y, m, d) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m, d);
                    callback.onPick(c.getTime());
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

}
