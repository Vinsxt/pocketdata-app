package com.example.projectskripsi;

public class Alamat {
    private String nomorRumah;
    private String rtRw;
    private String kelDesa;
    private String kecamatan;
    private String kota;
    private String provinsi;

    public Alamat() {
        // Required no-argument constructor for Firestore
    }

    public Alamat(String nomorRumah, String rtRw, String kelDesa, String kecamatan, String kota, String provinsi) {
        this.nomorRumah = nomorRumah;
        this.rtRw = rtRw;
        this.kelDesa = kelDesa;
        this.kecamatan = kecamatan;
        this.kota = kota;
        this.provinsi = provinsi;
    }

    // Public getters and setters
    public String getNomorRumah() { return nomorRumah; }
    public void setNomorRumah(String nomorRumah) { this.nomorRumah = nomorRumah; }

    public String getRtRw() { return rtRw; }
    public void setRtRw(String rtRw) { this.rtRw = rtRw; }

    public String getKelDesa() { return kelDesa; }
    public void setKelDesa(String kelDesa) { this.kelDesa = kelDesa; }

    public String getKecamatan() { return kecamatan; }
    public void setKecamatan(String kecamatan) { this.kecamatan = kecamatan; }

    public String getKota() { return kota; }
    public void setKota(String kota) { this.kota = kota; }

    public String getProvinsi() { return provinsi; }
    public void setProvinsi(String provinsi) { this.provinsi = provinsi; }

    // toString function
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (nomorRumah != null && !nomorRumah.isEmpty()) {
            sb.append(nomorRumah);
        }

        if (rtRw != null && !rtRw.isEmpty()) {
            sb.append(", RT/RW ").append(rtRw);
        }

        if (kelDesa != null && !kelDesa.isEmpty()) {
            sb.append(", ").append(kelDesa);
        }

        if (kecamatan != null && !kecamatan.isEmpty()) {
            sb.append(", Kec. ").append(kecamatan);
        }

        if (kota != null && !kota.isEmpty()) {
            sb.append(", ").append(kota);
        }

        if (provinsi != null && !provinsi.isEmpty()) {
            sb.append(", ").append(provinsi);
        }

        return sb.toString();
    }
}
