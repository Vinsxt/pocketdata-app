package com.example.projectskripsi;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentsAdapter adapter;
    private List<DocumentItem> documentList = new ArrayList<>();

    private static final int REQ_NOTIFICATION = 1001;

    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestNotificationPermissionIfNeeded();

        initFirebase();

        setupGreeting();

        setupLogout();

        setupSettings();

        setupRecyclerView();

        setupFab();

        listenToDocuments();
    }

    /**
     * Initialize Firebase Auth, Credential Manager, Firestore, and Storage instances.
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(getBaseContext());
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Set up the greeting TextView with the user's first name or a default welcome message.
     */
    private void setupGreeting() {
        TextView tvHomeTitle = findViewById(R.id.tv_home_title);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getDisplayName() != null) {
            String fullName = user.getDisplayName(); // contoh: "Vincent Hartono"
            String firstName = fullName.split(" ")[0]; // ambil "Vincent"
            tvHomeTitle.setText("Welcome, " + firstName);
        } else {
            tvHomeTitle.setText("Welcome");
        }
    }

    /**
     * Navigate to SettingsActivity
     */
    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void setupSettings() {
        ImageButton btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> openSettings());
    }

    /**
     * Set up the logout button and its click listener to sign out the user.
     */
    private void setupLogout() {
        ImageButton btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> {
            signOut();
        });
    }

    /**
     * Initialize and configure the RecyclerView and its adapter.
     */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DocumentsAdapter(this, documentList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Set up the Floating Action Button to navigate to CategorySelectActivity on click.
     */
    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            // Toast.makeText(HomeActivity.this, "Add Document Clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, CategorySelectActivity.class);
            startActivity(intent);
        });
    }

    private void listenToDocuments() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .collection("documents")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) {
                        // Toast.makeText(this, "Failed to load documents", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    documentList.clear();

                    for (var docSnap : snapshots.getDocuments()) {

                        String firestoreId = docSnap.getId();
                        String type = docSnap.getString("type");

                        if ("KTP".equals(type)) {

                            KTP ktp = docSnap.toObject(KTP.class);
                            if (ktp != null && ktp.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "KTP", ktp.getDocument())
                                );
                            }

                        } else if ("SIM".equals(type)) {

                            SIM sim = docSnap.toObject(SIM.class);
                            if (sim != null && sim.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "SIM", sim.getDocument())
                                );
                            }

                        } else if ("STNK".equals(type)) {

                            STNK stnk = docSnap.toObject(STNK.class);
                            if (stnk != null && stnk.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "STNK", stnk.getDocument())
                                );
                            }

                        } else if ("NPWP".equals(type)) {

                            NPWP npwp = docSnap.toObject(NPWP.class);
                            if (npwp != null && npwp.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "NPWP", npwp.getDocument())
                                );
                            }

                        } else if ("CUSTOM".equals(type)) {

                            CustomDocument custom = docSnap.toObject(CustomDocument.class);
                            if (custom != null && custom.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "CUSTOM", custom.getDocument())
                                );
                            }

                        } else if ("REMINDER".equals(type)) {

                            ReminderDocument reminder = docSnap.toObject(ReminderDocument.class);
                            if (reminder != null && reminder.getDocument() != null) {
                                documentList.add(
                                        new DocumentItem(firestoreId, "REMINDER", reminder.getDocument())
                                );
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    // [START sign_out]
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // When a user signs out, clear the current user credential state from all credential providers.
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        updateUI(null);
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(TAG, "Couldn't clear user credentials: " + e.getLocalizedMessage());
                        updateUI(null);
                    }
                });
    }
    // [END sign_out]

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIFICATION
                );
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}