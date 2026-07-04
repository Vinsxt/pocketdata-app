package com.example.projectskripsi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class PinActivity extends AppCompatActivity {

    // ================= UI =================
    private TextView tvTitle, tvSubtitle;
    private TextInputEditText etOldPin, etPin, etConfirmPin;
    private TextInputLayout tilOldPin, tilPin, tilConfirmPin;
    private MaterialButton btnSubmit;
    private MaterialButton btnForgotPin;

    // ================= STATE =================
    private FirebaseFirestore db;
    private String uid;
    private String storedHash;
    private boolean isChangePinFlow;

    private Mode mode = Mode.CHECK;

    enum Mode {
        CREATE, CHECK, CHANGE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        initViews();
        initFirebase();

        isChangePinFlow = getIntent().getBooleanExtra("change_pin", false);

        checkPinStatus();
        setupActions();
        blockBack();
    }

    // ================= INIT =================

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        etOldPin = findViewById(R.id.etOldPin);
        etPin = findViewById(R.id.etPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);

        tilOldPin = findViewById(R.id.tilOldPin);
        tilPin = findViewById(R.id.tilPin);
        tilConfirmPin = findViewById(R.id.tilConfirmPin);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnForgotPin = findViewById(R.id.btnForgotPin);

        InputFilter[] pinFilter = {new InputFilter.LengthFilter(6)};
        etOldPin.setFilters(pinFilter);
        etPin.setFilters(pinFilter);
        etConfirmPin.setFilters(pinFilter);

        tilOldPin.setVisibility(View.GONE);
        tilConfirmPin.setVisibility(View.GONE);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            forceLogout();
        }
    }

    // ================= MODE HANDLING =================

    private void checkPinStatus() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    storedHash = doc.getString("pin_hash");

                    Log.d("PIN_FLOW", "storedHash=" + storedHash);
                    Log.d("PIN_FLOW", "isChangePinFlow=" + isChangePinFlow);

                    if (storedHash == null) {
                        switchToCreateMode();
                        return;
                    }

                    if (isChangePinFlow) {
                        switchToChangeMode();
                    } else {
                        switchToCheckMode();
                    }
                });
    }

    private void switchToCreateMode() {
        mode = Mode.CREATE;

        tvTitle.setText("Create PIN");
        tvSubtitle.setText("Buat PIN untuk keamanan akun Anda");
        btnSubmit.setText("Save PIN");

        tilOldPin.setVisibility(View.GONE);
        tilConfirmPin.setVisibility(View.VISIBLE);
        btnForgotPin.setVisibility(View.GONE);
    }

    private void switchToCheckMode() {
        mode = Mode.CHECK;

        tvTitle.setText("Enter PIN");
        tvSubtitle.setText("Masukkan PIN untuk melanjutkan");
        btnSubmit.setText("Continue");

        tilOldPin.setVisibility(View.GONE);
        tilConfirmPin.setVisibility(View.GONE);
        btnForgotPin.setVisibility(View.VISIBLE);
    }

    private void switchToChangeMode() {
        mode = Mode.CHANGE;

        tvTitle.setText("Change PIN");
        tvSubtitle.setText("Masukkan PIN lama dan PIN baru");
        btnSubmit.setText("Change PIN");

        tilOldPin.setVisibility(View.VISIBLE);
        tilConfirmPin.setVisibility(View.VISIBLE);
        btnForgotPin.setVisibility(View.GONE);
    }

    // ================= ACTION =================

    private void setupActions() {
        btnSubmit.setOnClickListener(v -> handleSubmit());
        btnForgotPin.setOnClickListener(v -> showForgotPinDialog());
    }

    private void handleSubmit() {
        String pin = etPin.getText().toString().trim();

        if (!pin.matches("\\d{6}")) {
            toast("PIN harus 6 digit angka");
            return;
        }

        switch (mode) {
            case CREATE:
                handleCreate(pin);
                break;
            case CHECK:
                handleCheck(pin);
                break;
            case CHANGE:
                handleChange(pin);
                break;
        }
    }

    // ================= CREATE =================

    private void handleCreate(String pin) {
        String confirm = etConfirmPin.getText().toString().trim();

        if (!pin.equals(confirm)) {
            toast("PIN dan konfirmasi tidak sama");
            etConfirmPin.setText("");
            return;
        }

        savePin(pin, true);
    }

    // ================= CHECK =================

    private void handleCheck(String pin) {
        if (storedHash.equals(sha256(pin))) {
            goHome();
        } else {
            etPin.setText("");
            toast("PIN salah");
        }
    }

    // ================= CHANGE =================

    private void handleChange(String newPin) {
        String oldPin = etOldPin.getText().toString().trim();
        String confirm = etConfirmPin.getText().toString().trim();

        if (!oldPin.matches("\\d{6}")) {
            toast("PIN lama tidak valid");
            return;
        }

        if (!storedHash.equals(sha256(oldPin))) {
            toast("PIN lama salah");
            etOldPin.setText("");
            return;
        }

        if (!newPin.equals(confirm)) {
            toast("PIN baru dan konfirmasi tidak sama");
            etConfirmPin.setText("");
            return;
        }

        if (sha256(newPin).equals(storedHash)) {
            toast("PIN baru tidak boleh sama dengan PIN lama");
            return;
        }

        savePin(newPin, false);
    }

    private void showForgotPinDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Lupa PIN?")
                .setMessage(
                        "Untuk keamanan akun, Anda harus login ulang dengan akun Google untuk membuat PIN baru."
                )
                .setPositiveButton("Lanjutkan", (d, w) -> resetPinFlow())
                .setNegativeButton("Batal", null)
                .show();
    }

    // ================= SAVE =================

    private void savePin(String pin, boolean goHomeAfter) {
        Map<String, Object> data = new HashMap<>();
        data.put("pin_hash", sha256(pin));

        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> {
                    if (goHomeAfter) {
                        goHome();
                    } else {
                        toast("PIN berhasil diganti");
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        toast("Gagal menyimpan PIN"));
    }

    // ================= SECURITY =================

    private void blockBack() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (mode == Mode.CHANGE) {
                            finish();
                        }
                    }
                });
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void resetPinFlow() {
        // Hapus PIN hash
        db.collection("users")
                .document(uid)
                .update("pin_hash", null)
                .addOnSuccessListener(v -> {
                    FirebaseAuth.getInstance().signOut();

                    // balik ke login
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        toast("Gagal reset PIN"));
    }

    // ================= NAV =================

    private void goHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();
    }

    private void forceLogout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}