package com.example.projectskripsi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NPWP {

    private String nomorNpwp;
    private String nik;
    private String nama;
    private Date tanggalTerdaftar;

    private String imageUrl;
    private Document document;
    private String type;

    // empty constructor untuk Firestore
    public NPWP() {}

    public NPWP(
            String nomorNpwp,
            String nik,
            String nama,
            Date tanggalTerdaftar,
            String imageUrl,
            Document document
    ) {
        this.nomorNpwp = nomorNpwp;
        this.nik = nik;
        this.nama = nama;
        this.tanggalTerdaftar = tanggalTerdaftar;
        this.imageUrl = imageUrl;
        this.document = document;
        this.type = "NPWP";
    }

    // ================= GETTERS =================

    public String getNomorNpwp() {
        return nomorNpwp;
    }

    public String getNik() {
        return nik;
    }

    public String getNama() {
        return nama;
    }

    public Date getTanggalTerdaftar() {
        return tanggalTerdaftar;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Document getDocument() {
        return document;
    }

    public String getType() {
        return type;
    }

    // ================= SETTERS =================

    public void setNomorNpwp(String nomorNpwp) {
        this.nomorNpwp = nomorNpwp;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTanggalTerdaftar(Date tanggalTerdaftar) {
        this.tanggalTerdaftar = tanggalTerdaftar;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTanggalTerdaftarFormatted() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(tanggalTerdaftar);
    }
}