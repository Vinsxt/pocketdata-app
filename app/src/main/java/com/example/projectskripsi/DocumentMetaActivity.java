package com.example.projectskripsi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

public class DocumentMetaActivity extends AppCompatActivity {

    // ================= UI =================
    private TextInputEditText etTitle, etDescription;
    private MaterialButton btnSave;
    private View layoutLoading;
    private TextView tvLoadingStatus;

    // ================= Firebase =================
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // ================= Data =================
    private String documentType;
    private Uri imageUri;

    private KTP ktp;
    private SIM sim;
    private STNK stnk;
    private NPWP npwp;
    private Date reminderDate;
    private int[] reminderOffsets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_meta);

        initViews();
        initFirebase();
        parseIntent();

        btnSave.setOnClickListener(v -> startUploadFlow());
    }

    // ================= UI =================

    /**
     * Initialize UI components.
     */
    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        layoutLoading = findViewById(R.id.layoutLoading);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);
    }

    // ================= Firebase =================

    /**
     * Initialize Firebase instances.
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // ================= Intent Parsing =================

    /**
     * Parse incoming Intent data and extract document details.
     */
    private void parseIntent() {
        Intent i = getIntent();

        documentType = i.getStringExtra("document_type");

        String imageUriString = i.getStringExtra("image_uri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
        }

        if (documentType == null || imageUri == null) {
            // toast("Invalid document data");
            finish();
            return;
        }

        switch (documentType) {
            case "KTP":
                extractKtp(i);
                break;
            case "SIM":
                extractSim(i);
                break;
            case "STNK":
                extractStnk(i);
                break;
            case "NPWP":
                extractNpwp(i);
                break;

            case "CUSTOM":
                // tidak perlu extract apa-apa
                break;

            case "REMINDER":
                extractReminder(i);
                break;

            default:
                // toast("Invalid document data");
                finish();
                break;
        }
    }

    // ================= Extractors =================

    /**
     * Extract KTP document data from Intent.
     */
    private void extractKtp(Intent i) {
        Alamat alamat = new Alamat(
                i.getStringExtra("alamat_jalan"),
                "-",
                i.getStringExtra("kelurahan"),
                i.getStringExtra("kecamatan"),
                i.getStringExtra("kota"),
                i.getStringExtra("provinsi")
        );

        ktp = new KTP(
                i.getStringExtra("nik"),
                i.getStringExtra("nama"),
                i.getStringExtra("tempat_lahir"),
                new Date(i.getLongExtra("tanggal_lahir", 0)),
                i.getStringExtra("gender"),
                alamat,
                i.getStringExtra("agama"),
                i.getStringExtra("status"),
                i.getStringExtra("pekerjaan"),
                null,
                null
        );
    }

    /**
     * Extract SIM document data from Intent.
     */
    private void extractSim(Intent i) {
        sim = new SIM(
                i.getStringExtra("jenis_sim"),
                i.getStringExtra("golongan_darah"),
                i.getStringExtra("nomor_sim"),
                i.getStringExtra("nama"),
                i.getStringExtra("tempat_lahir"),
                new Date(i.getLongExtra("tanggal_lahir", 0)),
                i.getStringExtra("gender"),
                i.getStringExtra("pekerjaan"),
                i.getStringExtra("polda"),
                new Date(i.getLongExtra("masa_berlaku", 0)),
                null,
                null
        );
    }

    /**
     * Extract STNK document data from Intent.
     */
    private void extractStnk(Intent i) {
        int tahunPembuatan = i.getIntExtra("tahun_pembuatan", 0);
        int tahunPerakitan = i.getIntExtra("tahun_perakitan", 0);

        stnk = new STNK(
                i.getStringExtra("nomor_polisi"),
                i.getStringExtra("merk_tipe"),
                i.getStringExtra("jenis_model"),
                tahunPembuatan,
                tahunPerakitan,
                i.getStringExtra("isi_silinder"),
                i.getStringExtra("nomor_rangka"),
                i.getStringExtra("nomor_mesin"),
                i.getStringExtra("nomor_bpkb"),
                new Date(i.getLongExtra("masa_berlaku", 0)),
                null,
                null
        );
    }

    /**
     * Extract NPWP document data from Intent.
     */
    private void extractNpwp(Intent i) {
        npwp = new NPWP(
                i.getStringExtra("nomor_npwp"),
                i.getStringExtra("nik"),
                i.getStringExtra("nama"),
                new Date(i.getLongExtra("tanggal_terdaftar", 0)),
                null,
                null
        );
    }

    /**
     * Extract Reminder document data from Intent.
     */
    private void extractReminder(Intent i) {
        reminderDate = new Date(i.getLongExtra("reminder_date", 0));
        reminderOffsets = i.getIntArrayExtra("reminder_offsets");

        if (reminderDate.getTime() == 0 || reminderOffsets == null) {
            // toast("Invalid reminder data");
            finish();
        }
    }

    // ================= Save Flow =================

    /**
     * Start the upload process for the document image and metadata.
     */
    private void startUploadFlow() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || imageUri == null) return;

        String ownerKey;
        String prefix;

        switch (documentType) {
            case "KTP":
                ownerKey = ktp.getNama();
                prefix = "ktp";
                break;
            case "SIM":
                ownerKey = sim.getNama();
                prefix = "sim";
                break;
            case "STNK":
                ownerKey = stnk.getNomorRegistrasi();
                prefix = "stnk";
                break;
            case "NPWP":
                ownerKey = npwp.getNomorNpwp();
                prefix = "npwp";
                break;

            case "CUSTOM":
                ownerKey = etTitle.getText().toString();
                prefix = "custom";
                break;

            case "REMINDER":
                ownerKey = etTitle.getText().toString();
                prefix = "reminder";
                break;

            default:
                // toast("Unknown document type");
                return;
        }

        String safeName = sanitizeFileName(ownerKey);

        layoutLoading.setVisibility(View.VISIBLE);
        tvLoadingStatus.setText("Uploading image...");

        StorageReference ref = storage.getReference()
                .child("documents")
                .child(user.getUid())
                .child(prefix + "_" + safeName + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                attachMetaAndSave(uri.toString())))
                .addOnFailureListener(e -> showError(e.getMessage()));
    }

    /**
     * Attach metadata to the document and save it to Firestore.
     * @param imageUrl The URL of the uploaded image.
     */
    private void attachMetaAndSave(String imageUrl) {
        tvLoadingStatus.setText("Saving data...");

        Document document = new Document(
                etTitle.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                new Date().toString(),
                imageUrl
        );

        switch (documentType) {
            case "KTP":
                ktp.setImageUrl(imageUrl);
                ktp.setDocument(document);
                ktp.setType("KTP");
                saveToFirestore(ktp);
                break;
            case "SIM":
                sim.setImageUrl(imageUrl);
                sim.setDocument(document);
                sim.setType("SIM");
                saveToFirestore(sim);

                // 🔔 SIM REMINDER
                ReminderAlarmHelper.scheduleSimReminders(
                        this,
                        sim.getMasaBerlaku().getTime(),
                        sim.getNama()
                );
                break;
            case "STNK":
                stnk.setImageUrl(imageUrl);
                stnk.setDocument(document);
                stnk.setType("STNK");
                saveToFirestore(stnk);
                break;
            case "NPWP":
                npwp.setImageUrl(imageUrl);
                npwp.setDocument(document);
                npwp.setType("NPWP");
                saveToFirestore(npwp);
                break;

            case "CUSTOM": {
                CustomDocument custom = new CustomDocument(
                        imageUrl,
                        document
                );

                saveToFirestore(custom);
                break;
            }

            case "REMINDER": {
                ReminderDocument reminder = new ReminderDocument(
                        reminderDate,
                        imageUrl,
                        document
                );

                saveToFirestore(reminder);

                // 🔔 SET REMINDER NOTIFICATION (NORMAL + TEST FALLBACK)
                List<Integer> requestCodes =
                        ReminderAlarmHelper.scheduleGenericReminders(
                                this,
                                reminderDate.getTime(),
                                reminderOffsets,
                                "Reminder Dokumen",
                                etTitle.getText().toString()
                        );

                // 🔥 Jika semua offset ke-skip (deadline hari ini / sudah lewat)
                // munculkan reminder test (langsung muncul)
                if (requestCodes.isEmpty()) {
                    ReminderAlarmHelper.scheduleImmediateReminder(
                            this,
                            etTitle.getText().toString(),
                            etDescription.getText().toString()
                    );
                }
                break;
            }
        }
    }

    /**
     * Save the document data to Firestore under the current user.
     * @param data The document data object.
     */
    private void saveToFirestore(Object data) {
        db.collection("users")
                .document(mAuth.getUid())
                .collection("documents")
                .add(data)
                .addOnSuccessListener(v -> finishToHome())
                .addOnFailureListener(e -> showError(e.getMessage()));
    }

    // ================= Helpers =================

    /**
     * Sanitize a string to be a safe filename.
     * @param input The input string.
     * @return A sanitized filename string.
     */
    private String sanitizeFileName(String input) {
        return input
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
    }

    /**
     * Display an error message and hide loading UI.
     * @param msg The error message.
     */
    private void showError(String msg) {
        layoutLoading.setVisibility(View.GONE);
        // toast(msg);
    }

    /**
     * Navigate to HomeActivity and finish current activity stack.
     */
    private void finishToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();
    }

    /**
     * Show a short Toast message.
     * @param msg The message to display.
     */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}