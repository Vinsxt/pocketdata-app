package com.example.projectskripsi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SIM {

    private String jenisSIM;
    private String golonganDarah;
    private String nomorSIM;
    private String nama;
    private String tempatLahir;
    private Date tanggalLahir;
    private String jenisKelamin;
    private String pekerjaan;
    private String poldaPenerbit;
    private Date masaBerlaku;
    private String imageUrl;
    private Document document;
    private int[] reminderOffsets;
    private List<Integer> reminderRequestCodes;

    private String type;

    // Empty constructor for firestore
    public SIM() {}

    public SIM(String jenisSIM, String golonganDarah, String nomorSIM, String nama, String tempatLahir, Date tanggalLahir, String jenisKelamin, String pekerjaan, String poldaPenerbit, Date masaBerlaku, String imageUrl, Document document) {
        this.jenisSIM = jenisSIM;
        this.golonganDarah = golonganDarah;
        this.nomorSIM = nomorSIM;
        this.nama = nama;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.pekerjaan = pekerjaan;
        this.poldaPenerbit = poldaPenerbit;
        this.masaBerlaku = masaBerlaku;
        this.imageUrl = imageUrl;
        this.document = document;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJenisSIM() {
        return jenisSIM;
    }

    public void setJenisSIM(String jenisSIM) {
        this.jenisSIM = jenisSIM;
    }

    public String getGolonganDarah() {
        return golonganDarah;
    }

    public void setGolonganDarah(String golonganDarah) {
        this.golonganDarah = golonganDarah;
    }

    public String getNomorSIM() {
        return nomorSIM;
    }

    public void setNomorSIM(String nomorSIM) {
        this.nomorSIM = nomorSIM;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public Date getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(Date tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getPoldaPenerbit() {
        return poldaPenerbit;
    }

    public void setPoldaPenerbit(String poldaPenerbit) {
        this.poldaPenerbit = poldaPenerbit;
    }

    public Date getMasaBerlaku() {
        return masaBerlaku;
    }

    public void setMasaBerlaku(Date masaBerlaku) {
        this.masaBerlaku = masaBerlaku;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }


    public String getMasaBerlakuFormatted() {
        if (masaBerlaku == null) return "-";

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        return sdf.format(masaBerlaku);
    }

    public List<Integer> getReminderRequestCodes() {
        return reminderRequestCodes;
    }

    public void setReminderRequestCodes(List<Integer> reminderRequestCodes) {
        this.reminderRequestCodes = reminderRequestCodes;
    }
}
