package ui.USER;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class DetailPanel extends JPanel {

    private String idPengaduan;

    private JTextField txtJudul;
    private JComboBox<String> cbKategori;
    private JTextArea txtIsi;
    private JTextArea txtTanggapan;

    private JLabel lblStatus;

    public DetailPanel(String idPengaduan) {

        this.idPengaduan = idPengaduan;

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadKategori();
        loadData();
    }

    // ================= HEADER =================
    private JPanel createHeader() {

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(32,93,173));
        top.setBorder(new EmptyBorder(25,30,25,30));

        JLabel title = new JLabel("Detail Pengaduan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton btnBack = new JButton("← Kembali");
        styleButton(btnBack, Color.WHITE, new Color(32,93,173));

        btnBack.addActionListener(e -> {
            Container parent = getParent();
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "home");
        });

        top.add(title, BorderLayout.WEST);
        top.add(btnBack, BorderLayout.EAST);

        return top;
    }

    // ================= CONTENT =================
    private JScrollPane createContent() {

        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(30,30,30,30));

        // ================= CARD =================
        JPanel card = new RoundedPanel(25);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25,25,25,25));

        // ===== TOP =====
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        lblStatus = new JLabel("STATUS");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(new EmptyBorder(8,16,8,16));

        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        statusWrap.setOpaque(false);
        statusWrap.add(lblStatus);

        top.add(statusWrap, BorderLayout.EAST);

        // ===== FORM (JUDUL + KATEGORI SEJAJAR) =====
        JPanel formTop = new JPanel(new GridLayout(1,2,15,0));
        formTop.setOpaque(false);
        formTop.setBorder(new EmptyBorder(15,0,0,0));

        txtJudul = new JTextField();
        txtJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtJudul.setBorder(BorderFactory.createTitledBorder("Judul"));

        cbKategori = new JComboBox<>();
        cbKategori.setBorder(BorderFactory.createTitledBorder("Kategori"));
        cbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        cbKategori.setBackground(Color.WHITE);

        formTop.add(txtJudul);
        formTop.add(cbKategori);

        // ===== ISI =====
        txtIsi = new JTextArea();
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        txtIsi.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtIsi.setBorder(new EmptyBorder(15,15,15,15)); // 🔥 biar gak mentok kiri

        JScrollPane isiScroll = new JScrollPane(txtIsi);
        isiScroll.setBorder(BorderFactory.createTitledBorder("Isi Pengaduan"));
        isiScroll.setPreferredSize(new Dimension(100,180));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        form.add(formTop);
        form.add(Box.createRigidArea(new Dimension(0,15)));
        form.add(isiScroll);

        card.add(top, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        // ================= TANGGAPAN =================
        JPanel tanggapanCard = new RoundedPanel(25);
        tanggapanCard.setLayout(new BorderLayout());
        tanggapanCard.setBackground(new Color(220,230,245)); // lebih tegas tapi tetap soft
        tanggapanCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,215,235)),
                new EmptyBorder(25,25,25,25)
        ));

        JLabel tTitle = new JLabel("Tanggapan Admin");
        tTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        txtTanggapan = new JTextArea();
        txtTanggapan.setEditable(false);
        txtTanggapan.setOpaque(false);
        txtTanggapan.setLineWrap(true);
        txtTanggapan.setWrapStyleWord(true);
        txtTanggapan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtTanggapan.setBorder(new EmptyBorder(10,10,10,10));

        tanggapanCard.add(tTitle, BorderLayout.NORTH);
        tanggapanCard.add(txtTanggapan, BorderLayout.CENTER);

        // ================= BUTTON =================
        JPanel btnArea = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnArea.setOpaque(false);

        JButton btnSave = new JButton("Simpan"){
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52,120,246));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(getText()))/2;
                int y = (getHeight()-fm.getHeight())/2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        btnSave.setPreferredSize(new Dimension(140,45));
        btnSave.setBorder(null);
        btnSave.setContentAreaFilled(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> simpanData());

        btnArea.add(btnSave);

        // ================= WRAP =================
        wrap.add(card);
        wrap.add(Box.createRigidArea(new Dimension(0,20)));
        wrap.add(tanggapanCard);
        wrap.add(Box.createRigidArea(new Dimension(0,20)));
        wrap.add(btnArea);

        JScrollPane scroll = new JScrollPane(wrap);
        scroll.setBorder(null);

        return scroll;
    }

    // ================= LOAD DATA =================
    private void loadData() {
        try {
            Connection conn = Koneksi.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                "SELECT p.*, k.nama_kategori FROM pengaduan p JOIN kategori k ON p.id_kategori=k.id_kategori WHERE id_pengaduan=?"
            );

            pst.setString(1, idPengaduan);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                txtJudul.setText(rs.getString("judul"));
                txtIsi.setText(rs.getString("isi"));
                cbKategori.setSelectedItem(rs.getString("nama_kategori"));
                setStatus(rs.getString("status"));
            }

            PreparedStatement pst2 = conn.prepareStatement(
                "SELECT isi_tanggapan FROM tanggapan WHERE id_pengaduan=?"
            );

            pst2.setString(1, idPengaduan);
            ResultSet rs2 = pst2.executeQuery();

            if(rs2.next()){
                txtTanggapan.setText(rs2.getString("isi_tanggapan"));
            }

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void simpanData() {
        try {
            Connection conn = Koneksi.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                "UPDATE pengaduan SET judul=?, isi=?, id_kategori=? WHERE id_pengaduan=?"
            );

            pst.setString(1, txtJudul.getText());
            pst.setString(2, txtIsi.getText());
            pst.setInt(3, getIdKategori(cbKategori.getSelectedItem().toString()));
            pst.setString(4, idPengaduan);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Berhasil update!");
            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setStatus(String status) {

        lblStatus.setText(status.toUpperCase());

        if(status.equalsIgnoreCase("pending")) {
            lblStatus.setBackground(new Color(255,145,0));
        } else if(status.equalsIgnoreCase("diproses")) {
            lblStatus.setBackground(new Color(0,120,255));
        } else {
            lblStatus.setBackground(new Color(0,170,90));
        }

        lblStatus.setForeground(Color.WHITE);
    }

    private void loadKategori() {
        try {
            Connection conn = Koneksi.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT nama_kategori FROM kategori");

            while(rs.next()){
                cbKategori.addItem(rs.getString("nama_kategori"));
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private int getIdKategori(String nama){
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT id_kategori FROM kategori WHERE nama_kategori=?");
            pst.setString(1, nama);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e){}
        return 0;
    }

    private void styleButton(JButton btn, Color bg, Color fg){
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
    }

    class RoundedPanel extends JPanel {
        int r;
        RoundedPanel(int r){ this.r=r; setOpaque(false); }
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            super.paintComponent(g);
        }
    }
}