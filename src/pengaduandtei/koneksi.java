package pengaduandtei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Farel Sang Putra
 */
public class koneksi {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://192.168.1.5:3306/db_project?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "project";
            String pass = "12345";

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Koneksi berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}
