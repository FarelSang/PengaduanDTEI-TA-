package service;

import config.Koneksi;
import model.User;
import java.sql.*;

public class AuthServiceImpl implements AuthService {

    @Override
    public String login(String username, String password) {
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("role"); // 🔥 ambil role
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        try {
            Connection conn = Koneksi.getConnection();

            String sql = "INSERT INTO users VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, user.getIdUser());
            pst.setString(2, user.getUsername());
            pst.setString(3, user.getPassword());
            pst.setString(4, user.getRole());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String generateId() {
        String id = "USR001";
        try {
            Connection conn = Koneksi.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(
                "SELECT id_user FROM users ORDER BY id_user DESC LIMIT 1"
            );

            if (rs.next()) {
                String last = rs.getString("id_user");
                int num = Integer.parseInt(last.substring(3)) + 1;
                id = String.format("USR%03d", num);
            }

        } catch (Exception e) {
            System.out.println("ID error: " + e.getMessage());
        }
        return id;
    }
}