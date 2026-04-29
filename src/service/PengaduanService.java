package service;

import config.Koneksi;
import model.Pengaduan;

import java.sql.*;

public class PengaduanService {

    public String generateId() {

        String id = "PNG001";

        try {
            Connection conn = Koneksi.getConnection();

            String sql = "SELECT id_pengaduan FROM pengaduan ORDER BY id_pengaduan DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if(rs.next()){

                String last = rs.getString("id_pengaduan");
                int nomor = Integer.parseInt(last.substring(3)) + 1;

                id = String.format("PNG%03d", nomor);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return id;
    }

    public boolean simpan(Pengaduan p){

        try{
            Connection conn = Koneksi.getConnection();

            String sql = "INSERT INTO pengaduan VALUES (?,?,?,?,?,NOW(),'pending',?,0)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, p.getIdPengaduan());
            pst.setString(2, p.getIdUser());
            pst.setInt(3, p.getIdKategori());
            pst.setString(4, p.getJudul());
            pst.setString(5, p.getIsi());
            pst.setInt(6, p.getAnonim());

            pst.executeUpdate();

            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}