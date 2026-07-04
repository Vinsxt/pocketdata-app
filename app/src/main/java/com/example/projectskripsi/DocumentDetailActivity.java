package com.example.projectskripsi;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

/**
 * DocumentDetailActivity
 *
 * Displays detailed information for a single document (KTP, SIM, STNK, NPWP).
 * Handles:
 * - Firestore data loading
 * - Conditional UI rendering per document type
 * - Copy-to-clipboard actions
 * - Delete document flow
 */

public class DocumentDetailActivity extends AppCompatActivity {

    // ===================== IMAGE & HEADER =====================
    private ImageView ivImage;
    private TextView tvTitle, tvDesc;

    // ===================== KTP =====================
    private android.view.View layoutKtp;
    private TextView tvNik, tvAlamat;
    private ImageView btnCopyNik, btnCopyAlamat;

    // ===================== SIM =====================
    private android.view.View layoutSim;
    private TextView tvNomorSim, tvMasaBerlaku;
    private ImageView btnCopyNomorSim, btnCopyMasaBerlaku;

    // ===================== STNK =====================
    private android.view.View layoutStnk;
    private TextView tvNomorPolisi, tvMerkTipe, tvJenisModel,
            tvTahun, tvIsiSilinder, tvNomorRangka,
            tvNomorMesin, tvNomorBpkb, tvMasaBerlakuStnk;

    private ImageView btnCopyNomorPolisi, btnCopyMerkTipe, btnCopyJenisModel,
            btnCopyNomorRangka, btnCopyNomorMesin, btnCopyNomorBpkb;

    // ===================== NPWP =====================
    private android.view.View layoutNpwp;
    private TextView tvNpwpNumber, tvNpwpNik, tvNpwpNama, tvNpwpTanggal;
    private ImageView btnCopyNpwpNumber, btnCopyNpwpNik;

    // ===================== ACTIONS =====================
    private MaterialButton btnDelete;
    private MaterialButton btnUpdate;
    private ImageButton btnBack;
    private ImageButton btnDownload;

    // ===================== FIREBASE =====================
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // ===================== STATE =====================
    private String docId;
    private String type;
    private String imageUrl;
    // Store loaded document object for alarm cancellation
    private Object loadedDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        initViews();
        initFirebase();
        readIntent();
        loadDocument();

        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnDownload.setOnClickListener(v -> downloadImage());
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentUpdateActivity.class);
            intent.putExtra("docId", docId);
            intent.putExtra("type", type);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDocument();
    }

    /**
     * Bind all views from XML.
     */
    private void initViews() {
        ivImage = findViewById(R.id.ivImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);

        // KTP
        layoutKtp = findViewById(R.id.layoutKtp);
        tvNik = findViewById(R.id.tvNik);
        tvAlamat = findViewById(R.id.tvAlamat);
        btnCopyNik = findViewById(R.id.btnCopyNik);
        btnCopyAlamat = findViewById(R.id.btnCopyAlamat);

        // SIM
        layoutSim = findViewById(R.id.layoutSim);
        tvNomorSim = findViewById(R.id.tvNomorSim);
        tvMasaBerlaku = findViewById(R.id.tvMasaBerlaku);
        btnCopyNomorSim = findViewById(R.id.btnCopyNomorSim);
        btnCopyMasaBerlaku = findViewById(R.id.btnCopyMasaBerlaku);

        // STNK
        layoutStnk = findViewById(R.id.layoutStnk);
        tvNomorPolisi = findViewById(R.id.tvNomorPolisi);
        tvMerkTipe = findViewById(R.id.tvMerkTipe);
        tvJenisModel = findViewById(R.id.tvJenisModel);
        tvTahun = findViewById(R.id.tvTahun);
        tvIsiSilinder = findViewById(R.id.tvIsiSilinder);
        tvNomorRangka = findViewById(R.id.tvNomorRangka);
        tvNomorMesin = findViewById(R.id.tvNomorMesin);
        tvNomorBpkb = findViewById(R.id.tvNomorBpkb);
        tvMasaBerlakuStnk = findViewById(R.id.tvMasaBerlakuStnk);

        btnCopyNomorPolisi = findViewById(R.id.btnCopyNomorPolisi);
        btnCopyMerkTipe = findViewById(R.id.btnCopyMerkTipe);
        btnCopyJenisModel = findViewById(R.id.btnCopyJenisModel);
        btnCopyNomorRangka = findViewById(R.id.btnCopyNomorRangka);
        btnCopyNomorMesin = findViewById(R.id.btnCopyNomorMesin);
        btnCopyNomorBpkb = findViewById(R.id.btnCopyNomorBpkb);

        // NPWP
        layoutNpwp = findViewById(R.id.layoutNpwp);
        tvNpwpNumber = findViewById(R.id.tvNpwpNumber);
        tvNpwpNik = findViewById(R.id.tvNpwpNik);
        tvNpwpNama = findViewById(R.id.tvNpwpNama);
        tvNpwpTanggal = findViewById(R.id.tvNpwpTanggal);
        btnCopyNpwpNumber = findViewById(R.id.btnCopyNpwpNumber);
        btnCopyNpwpNik = findViewById(R.id.btnCopyNpwpNik);

        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        btnDownload = findViewById(R.id.btnDownload);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    /**
     * Initialize Firebase instances.
     */
    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Read document ID and type from Intent.
     */
    private void readIntent() {
        Intent i = getIntent();
        docId = i.getStringExtra("docId");
        type = i.getStringExtra("type");
    }

    /**
     * Fetch document data from Firestore.
     */
    private void loadDocument() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("documents")
                .document(docId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    hideAllLayouts();

                    switch (type) {
                        case "KTP":
                            loadedDocument = snapshot.toObject(KTP.class);
                            showKtp((KTP) loadedDocument);
                            break;
                        case "SIM":
                            loadedDocument = snapshot.toObject(SIM.class);
                            showSim((SIM) loadedDocument);
                            break;
                        case "STNK":
                            loadedDocument = snapshot.toObject(STNK.class);
                            showStnk((STNK) loadedDocument);
                            break;
                        case "NPWP":
                            loadedDocument = snapshot.toObject(NPWP.class);
                            showNpwp((NPWP) loadedDocument);
                            break;
                        case "REMINDER":
                            loadedDocument = snapshot.toObject(ReminderDocument.class);
                            showReminder((ReminderDocument) loadedDocument);
                            break;
                        case "CUSTOM":
                            loadedDocument = snapshot.toObject(CustomDocument.class);
                            showCustom((CustomDocument) loadedDocument);
                            break;
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal load data", Toast.LENGTH_SHORT).show());
    }

    // ===================== SHOW METHODS =====================

    /**
     * Show KTP details and setup copy buttons.
     */
    private void showKtp(KTP ktp) {
        if (ktp == null) return;

        layoutKtp.setVisibility(android.view.View.VISIBLE);

        tvTitle.setText(ktp.getDocument().getTitle());
        tvDesc.setText(ktp.getDocument().getDescription());

        String nik = ktp.getNik();
        String alamat = ktp.getAlamat().toString();

        tvNik.setText("NIK: " + nik);
        tvAlamat.setText("Alamat: " + alamat);

        btnCopyNik.setOnClickListener(v -> copyToClipboard("NIK", nik));
        btnCopyAlamat.setOnClickListener(v -> copyToClipboard("Alamat", alamat));

        imageUrl = ktp.getImageUrl();
        loadImage(imageUrl);
    }

    /**
     * Show SIM details and setup copy buttons.
     */
    private void showSim(SIM sim) {
        if (sim == null) return;

        layoutSim.setVisibility(android.view.View.VISIBLE);

        tvTitle.setText(sim.getDocument().getTitle());
        tvDesc.setText(sim.getDocument().getDescription());

        String nomorSim = sim.getNomorSIM();
        String masaBerlaku = sim.getMasaBerlakuFormatted();

        tvNomorSim.setText("Nomor SIM: " + nomorSim);
        tvMasaBerlaku.setText("Masa Berlaku: " + masaBerlaku);

        btnCopyNomorSim.setOnClickListener(v -> copyToClipboard("Nomor SIM", nomorSim));
        btnCopyMasaBerlaku.setOnClickListener(v -> copyToClipboard("Masa Berlaku", masaBerlaku));

        imageUrl = sim.getImageUrl();
        loadImage(imageUrl);
    }

    /**
     * Show STNK details and setup copy buttons.
     */
    private void showStnk(STNK stnk) {
        if (stnk == null) return;

        layoutStnk.setVisibility(android.view.View.VISIBLE);

        tvTitle.setText(stnk.getDocument().getTitle());
        tvDesc.setText(stnk.getDocument().getDescription());

        tvNomorPolisi.setText("Plat Nomor: " + safe(stnk.getNomorRegistrasi()));
        tvMerkTipe.setText("Merk / Tipe: " + safe(stnk.getMerkTipe()));
        tvJenisModel.setText("Jenis / Model: " + safe(stnk.getJenisModel()));

        tvTahun.setText(
                "Tahun: " + stnk.getTahunPembuatan()
        );

        tvIsiSilinder.setText("Isi Silinder: " + safe(stnk.getIsiSilinder()));
        tvNomorRangka.setText("No. Rangka: " + safe(stnk.getNomorRangka()));
        tvNomorMesin.setText("No. Mesin: " + safe(stnk.getNomorMesin()));
        tvNomorBpkb.setText("No. BPKB: " + safe(stnk.getNomorBpkb()));

        btnCopyNomorPolisi.setOnClickListener(v -> copyToClipboard("Plat Nomor", stnk.getNomorRegistrasi()));
        btnCopyMerkTipe.setOnClickListener(v -> copyToClipboard("Merk / Tipe", stnk.getMerkTipe()));
        btnCopyJenisModel.setOnClickListener(v -> copyToClipboard("Jenis / Model", stnk.getJenisModel()));
        btnCopyNomorRangka.setOnClickListener(v -> copyToClipboard("Nomor Rangka", stnk.getNomorRangka()));
        btnCopyNomorMesin.setOnClickListener(v -> copyToClipboard("Nomor Mesin", stnk.getNomorMesin()));
        btnCopyNomorBpkb.setOnClickListener(v -> copyToClipboard("Nomor BPKB", stnk.getNomorBpkb()));

        tvMasaBerlakuStnk.setText("Masa Berlaku: " + stnk.getMasaBerlakuFormatted());

        imageUrl = stnk.getImageUrl();
        loadImage(imageUrl);
    }

    /**
     * Show NPWP details and setup copy buttons.
     */
    private void showNpwp(NPWP npwp) {
        if (npwp == null) return;

        layoutNpwp.setVisibility(android.view.View.VISIBLE);

        tvTitle.setText(npwp.getDocument().getTitle());
        tvDesc.setText(npwp.getDocument().getDescription());

        tvNpwpNumber.setText("NPWP: " + safe(npwp.getNomorNpwp()));
        tvNpwpNik.setText("NIK: " + safe(npwp.getNik()));
        tvNpwpNama.setText("Nama: " + safe(npwp.getNama()));
        tvNpwpTanggal.setText("Terdaftar: " + npwp.getTanggalTerdaftarFormatted());

        btnCopyNpwpNumber.setOnClickListener(v -> copyToClipboard("NPWP", npwp.getNomorNpwp()));
        btnCopyNpwpNik.setOnClickListener(v -> copyToClipboard("NIK", npwp.getNik()));

        imageUrl = npwp.getImageUrl();
        loadImage(imageUrl);
    }

    /**
     * Show REMINDER document (image + reminder date only)
     */
    private void showReminder(ReminderDocument reminder) {
        if (reminder == null) return;

        tvTitle.setText(reminder.getDocument().getTitle());
        tvDesc.setText(reminder.getDocument().getDescription());

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        String dateText = "Reminder Date: " +
                sdf.format(reminder.getReminderDate());

        tvDesc.append("\n\n" + dateText);

        imageUrl = reminder.getImageUrl();
        loadImage(imageUrl);
    }

    /**
     * Show CUSTOM document (image only)
     */
    private void showCustom(CustomDocument custom) {
        if (custom == null) return;

        tvTitle.setText(custom.getDocument().getTitle());
        tvDesc.setText(custom.getDocument().getDescription());

        imageUrl = custom.getImageUrl();
        loadImage(imageUrl);
    }

    // ===================== IMAGE =====================
    private void loadImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(android.R.color.darker_gray)
                .into(ivImage);
    }

    // ===================== DELETE FLOW =====================
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Document")
                .setMessage("Dokumen ini akan dihapus permanen. Lanjutkan?")
                .setPositiveButton("Delete", (d, w) -> deleteDocument())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void cancelReminderIfNeeded() {
        if (loadedDocument == null) return;

        if (loadedDocument instanceof SIM) {
            ReminderAlarmHelper.cancelReminders(
                    this,
                    ((SIM) loadedDocument).getReminderRequestCodes()
            );
        } else if (loadedDocument instanceof STNK) {
            ReminderAlarmHelper.cancelReminders(
                    this,
                    ((STNK) loadedDocument).getReminderRequestCodes()
            );
        } else if (loadedDocument instanceof ReminderDocument) {
            ReminderAlarmHelper.cancelReminders(
                    this,
                    ((ReminderDocument) loadedDocument).getReminderRequestCodes()
            );
        }
    }

    private void deleteDocument() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // 🔔 CANCEL REMINDER ALARMS FIRST
        cancelReminderIfNeeded();

        db.collection("users")
                .document(uid)
                .collection("documents")
                .document(docId)
                .delete()
                .addOnSuccessListener(v -> deleteImage())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal hapus data", Toast.LENGTH_SHORT).show());
    }

    private void deleteImage() {
        if (imageUrl == null) {
            finish();
            return;
        }

        storage.getReferenceFromUrl(imageUrl)
                .delete()
                .addOnCompleteListener(task -> finish());
    }

    /**
     * Download the currently displayed document image from Firebase Storage URL
     * using Android DownloadManager.
     */
    private void downloadImage() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            // Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(imageUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Document Image");
        request.setDescription("Downloading document image...");
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        );
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        // Save into Downloads folder
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "document_" + System.currentTimeMillis() + ".jpg"
        );

        DownloadManager downloadManager =
                (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    // ===================== UTIL =====================
    private void copyToClipboard(String label, String value) {
        if (value == null || value.isEmpty()) {
            Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value));

        Toast.makeText(this, label + " disalin", Toast.LENGTH_SHORT).show();
    }

    private String safe(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }

    private void hideAllLayouts() {
        layoutKtp.setVisibility(android.view.View.GONE);
        layoutSim.setVisibility(android.view.View.GONE);
        layoutStnk.setVisibility(android.view.View.GONE);
        layoutNpwp.setVisibility(android.view.View.GONE);
    }
}