package ui.USER;

import config.Koneksi;
import model.Pengaduan;
import service.PengaduanService;
import session.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class PengaduanPanel extends JPanel {

    JTextField txtJudul;
    JComboBox<String> kategori;
    JTextArea txtDesk;
    JCheckBox anonim;

    public PengaduanPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(240,242,245));

        // ======================
        // HEADER
        // ======================
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(52,120,180));
        header.setBorder(BorderFactory.createEmptyBorder(28,60,28,60));

        JLabel title = new JLabel("Buat Pengaduan Baru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Isi formulir berikut dengan lengkap dan jelas");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(220,220,220));

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0,8)));
        header.add(sub);

        // ======================
        // CARD FORM
        // ======================
        RoundedPanel card = new RoundedPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28,28,28,28));

        txtJudul = createField("Contoh: Kerusakan AC Ruang Kelas 5B");

        kategori = new JComboBox<>();
        styleCombo(kategori);
        loadKategori();

        txtDesk = new JTextArea();
        txtDesk.setLineWrap(true);
        txtDesk.setWrapStyleWord(true);
        txtDesk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesk.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        setPlaceholderArea(txtDesk,
                "Jelaskan masalah secara detail: kondisi, dampak, dan waktu kejadian...");

        JScrollPane scroll = new JScrollPane(txtDesk);
        scroll.setPreferredSize(new Dimension(100,160));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(215,215,215)));

        anonim = new JCheckBox("Kirim sebagai anonim");
        anonim.setOpaque(false);
        anonim.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        anonim.setForeground(new Color(70,70,70));
        anonim.setAlignmentX(Component.LEFT_ALIGNMENT);

        // tombol
        RoundedButton btnBatal = new RoundedButton("Batal", false);
        RoundedButton btnKirim = new RoundedButton("Kirim Pengaduan", true);

        btnBatal.addActionListener(e -> resetForm());
        btnKirim.addActionListener(e -> simpanPengaduan());

        JPanel btnRow = new JPanel(new GridLayout(1,2,18,0));
        btnRow.setOpaque(false);
        btnRow.add(btnBatal);
        btnRow.add(btnKirim);

        // add component
        card.add(wrapField("Judul Pengaduan *", txtJudul));
        card.add(Box.createRigidArea(new Dimension(0,18)));

        card.add(wrapField("Kategori *", kategori));
        card.add(Box.createRigidArea(new Dimension(0,18)));

        card.add(wrapField("Deskripsi Pengaduan *", scroll));
        card.add(Box.createRigidArea(new Dimension(0,15)));

        card.add(anonim);
        card.add(Box.createRigidArea(new Dimension(0,28)));

        card.add(btnRow);

        // wrapper tengah
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(28,35,28,35));
        wrap.add(card, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(wrap, BorderLayout.CENTER);
    }

    // ======================
    // LOAD KATEGORI
    // ======================
    private void loadKategori() {

        try {
            Connection conn = Koneksi.getConnection();
            //Tabel kategori
            String sql = "SELECT * FROM kategori";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            kategori.removeAllItems();
            kategori.addItem("Pilih Kategori");

            while(rs.next()) {
                kategori.addItem(rs.getString("nama_kategori"));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ======================
    // AMBIL ID KATEGORI
    // ======================
    private int getIdKategori(String nama) {

        int id = 0;

        try {
            Connection conn = Koneksi.getConnection();

            String sql = "SELECT id_kategori FROM kategori WHERE nama_kategori=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nama);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                id = rs.getInt("id_kategori");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    // ======================
    // SIMPAN DATA
    // ======================
    private void simpanPengaduan() {

        String judul = txtJudul.getText().trim();
        String isi = txtDesk.getText().trim();
        String namaKategori = kategori.getSelectedItem().toString();

        if(judul.isEmpty()
                || judul.equals("Contoh: Kerusakan AC Ruang Kelas 5B")
                || namaKategori.equals("Pilih Kategori")
                || isi.isEmpty()
                || isi.equals("Jelaskan masalah secara detail: kondisi, dampak, dan waktu kejadian...")) {

            JOptionPane.showMessageDialog(this,
                    "Lengkapi semua data!");
            return;
        }

        PengaduanService service = new PengaduanService();

        Pengaduan p = new Pengaduan();

        p.setIdPengaduan(service.generateId());
        p.setIdUser(Session.idUser);
        p.setIdKategori(getIdKategori(namaKategori));
        p.setJudul(judul);
        p.setIsi(isi);
        p.setAnonim(anonim.isSelected() ? 1 : 0);

        if(service.simpan(p)) {

            JOptionPane.showMessageDialog(this,
                    "Pengaduan berhasil dikirim!");

            resetForm();

        } else {

            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data!");
        }
    }

    // ======================
    // RESET FORM
    // ======================
    private void resetForm() {

        txtJudul.setText("Contoh: Kerusakan AC Ruang Kelas 5B");
        txtJudul.setForeground(Color.GRAY);

        kategori.setSelectedIndex(0);

        txtDesk.setText("Jelaskan masalah secara detail: kondisi, dampak, dan waktu kejadian...");
        txtDesk.setForeground(Color.GRAY);

        anonim.setSelected(false);
    }

    // ======================
    // TEXT FIELD
    // ======================
    private JTextField createField(String placeholder) {

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(100,52));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215,215,215)),
                BorderFactory.createEmptyBorder(0,15,0,15)
        ));

        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    // ======================
    // PLACEHOLDER TEXTAREA
    // ======================
    private void setPlaceholderArea(JTextArea area, String text) {

        area.setText(text);
        area.setForeground(Color.GRAY);

        area.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                if(area.getText().equals(text)) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if(area.getText().trim().isEmpty()) {
                    area.setText(text);
                    area.setForeground(Color.GRAY);
                }
            }
        });
    }

    // ======================
    // STYLE COMBO
    // ======================
    private void styleCombo(JComboBox<String> combo) {

        combo.setPreferredSize(new Dimension(100,52));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(215,215,215)));
    }

    // ======================
    // LABEL + FIELD
    // ======================
    private JPanel wrapField(String label, Component field) {

        JPanel p = new JPanel(new BorderLayout(0,7));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(70,110,160));

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    // ======================
    // PANEL BULAT
    // ======================
    class RoundedPanel extends JPanel {

        public RoundedPanel() {
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(190,205,220));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);

            g2.setColor(getBackground());
            g2.fillRoundRect(1,1,getWidth()-2,getHeight()-2,25,25);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ======================
    // BUTTON
    // ======================
    class RoundedButton extends JButton {

        boolean primary;

        public RoundedButton(String text, boolean primary) {

            super(text);
            this.primary = primary;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100,52));
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if(primary) {
                g2.setColor(new Color(52,120,246));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);

                g2.setColor(new Color(180,180,180));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);

                g2.setColor(Color.BLACK);
            }

            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth()-fm.stringWidth(getText()))/2;
            int y = (getHeight()-fm.getHeight())/2 + fm.getAscent();

            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }
}