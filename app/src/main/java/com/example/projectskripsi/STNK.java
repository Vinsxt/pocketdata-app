package com.example.projectskripsi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class STNK {

    // =====================
    // DATA UTAMA STNK
    // =====================
    private String nomorRegistrasi;      // Plat nomor (B 1234 ABC)
    private String merkTipe;             // Honda Vario / Toyota Avanza
    private String jenisModel;           // Sepeda Motor / Solo, Mobil Penumpang / Minibus
    private int tahunPembuatan;           // Tahun produksi
    private int tahunPerakitan;           // Tahun perakitan
    private String isiSilinder;           // 150 CC / 1500 CC
    private String nomorRangka;           // VIN
    private String nomorMesin;            // Engine number
    private String nomorBpkb;             // Nomor BPKB

    // =====================
    // META
    // =====================
    private Date masaBerlaku;             // Expired STNK
    private String imageUrl;              // Foto STNK
    private Document document;             // Judul + deskripsi
    private String type;                  // "STNK"
    private int[] reminderOffsets;
    private List<Integer> reminderRequestCodes;

    // =====================
    // EMPTY CONSTRUCTOR (Firestore)
    // =====================
    public STNK() {}

    // =====================
    // FULL CONSTRUCTOR
    // =====================
    public STNK(
            String nomorRegistrasi,
            String merkTipe,
            String jenisModel,
            int tahunPembuatan,
            int tahunPerakitan,
            String isiSilinder,
            String nomorRangka,
            String nomorMesin,
            String nomorBpkb,
            Date masaBerlaku,
            String imageUrl,
            Document document
    ) {
        this.nomorRegistrasi = nomorRegistrasi;
        this.merkTipe = merkTipe;
        this.jenisModel = jenisModel;
        this.tahunPembuatan = tahunPembuatan;
        this.tahunPerakitan = tahunPerakitan;
        this.isiSilinder = isiSilinder;
        this.nomorRangka = nomorRangka;
        this.nomorMesin = nomorMesin;
        this.nomorBpkb = nomorBpkb;
        this.masaBerlaku = masaBerlaku;
        this.imageUrl = imageUrl;
        this.document = document;
        this.type = "STNK";
    }

    // =====================
    // GETTERS
    // =====================
    public String getNomorRegistrasi() { return nomorRegistrasi; }
    public String getMerkTipe() { return merkTipe; }
    public String getJenisModel() { return jenisModel; }
    public int getTahunPembuatan() { return tahunPembuatan; }
    public int getTahunPerakitan() { return tahunPerakitan; }
    public String getIsiSilinder() { return isiSilinder; }
    public String getNomorRangka() { return nomorRangka; }
    public String getNomorMesin() { return nomorMesin; }
    public String getNomorBpkb() { return nomorBpkb; }
    public Date getMasaBerlaku() { return masaBerlaku; }
    public String getImageUrl() { return imageUrl; }
    public Document getDocument() { return document; }
    public String getType() { return type; }

    public List<Integer> getReminderRequestCodes() {
        return reminderRequestCodes;
    }

    // =====================
    // SETTERS (needed)
    // =====================
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDocument(Document document) { this.document = document; }
    public void setType(String type) { this.type = type; }

    public void setReminderRequestCodes(List<Integer> reminderRequestCodes) {
        this.reminderRequestCodes = reminderRequestCodes;
    }

    public String getMasaBerlakuFormatted() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(masaBerlaku);
    }
}
