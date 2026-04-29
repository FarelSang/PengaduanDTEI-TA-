package model;

public class Pengaduan {

    private String idPengaduan;
    private String idUser;
    private int idKategori;
    private String judul;
    private String isi;
    private int anonim;

    public String getIdPengaduan() { return idPengaduan; }
    public void setIdPengaduan(String idPengaduan) { this.idPengaduan = idPengaduan; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getIsi() { return isi; }
    public void setIsi(String isi) { this.isi = isi; }

    public int getAnonim() { return anonim; }
    public void setAnonim(int anonim) { this.anonim = anonim; }
}