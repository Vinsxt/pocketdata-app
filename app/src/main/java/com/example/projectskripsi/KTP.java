package com.example.projectskripsi;

import java.util.Date;

public class KTP {

    private String nik;
    private String nama;
    private String tempatLahir;
    private Date tanggalLahir;
    private String jenisKelamin;
    private Alamat alamat;
    private String agama;
    private String statusPerkawinan;
    private String pekerjaan;
    private String imageUrl;
    private Document document;

    private String type;

    // 🔥 REQUIRED by Firestore
    public KTP() {}

    public KTP(String nik, String nama, String tempatLahir, Date tanggalLahir,
               String jenisKelamin, Alamat alamat, String agama,
               String statusPerkawinan, String pekerjaan,
               String imageUrl, Document document) {
        this.nik = nik;
        this.nama = nama;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.agama = agama;
        this.statusPerkawinan = statusPerkawinan;
        this.pekerjaan = pekerjaan;
        this.imageUrl = imageUrl;
        this.document = document;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getStatusPerkawinan() {
        return statusPerkawinan;
    }

    public void setStatusPerkawinan(String statusPerkawinan) {
        this.statusPerkawinan = statusPerkawinan;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }

    public Alamat getAlamat() {
        return alamat;
    }

    public void setAlamat(Alamat alamat) {
        this.alamat = alamat;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public Date getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(Date tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }
}
