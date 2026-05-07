package ui.ADMIN;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfilAdminPanel extends JPanel {

    private String idAdmin;

    private JTextField txtNama;
    private JPasswordField txtPassword;

    private JLabel lblNama;

    private JLabel lblTotal;
    private JLabel lblDiproses;
    private JLabel lblSelesai;

    public ProfilAdminPanel(String idAdmin) {

        this.idAdmin = idAdmin;

        setLayout(new BorderLayout());
        setBackground(new Color(5,18,35));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(7,22,43));
        header.setBorder(new EmptyBorder(25,30,25,30));

        JLabel title = new JLabel("Profil Admin");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JScrollPane createContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30,30,30,30));

        content.add(createTopCard());
        content.add(Box.createRigidArea(new Dimension(0,25)));
        content.add(createFormCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(5,18,35));

        return scroll;
    }

    private JPanel createTopCard() {

        RoundedPanel card = new RoundedPanel(28);
        card.setBackground(new Color(10,35,65));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25,30,25,30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        left.setOpaque(false);

        AvatarCircle avatar = new AvatarCircle("A");

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));

        lblNama = new JLabel("Admin");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNama.setForeground(Color.WHITE);

        JLabel role = new JLabel("Super Administrator");
        role.setForeground(new Color(130,160,200));

        textWrap.add(lblNama);
        textWrap.add(role);

        left.add(avatar);
        left.add(textWrap);

        JPanel statWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        statWrap.setOpaque(false);

        lblTotal = createStatBox("Total", "0", new Color(200,210,220));
        lblDiproses = createStatBox("Diproses", "0", new Color(255,200,0));
        lblSelesai = createStatBox("Selesai", "0", new Color(0,220,140));

        statWrap.add(lblTotal.getParent());
        statWrap.add(lblDiproses.getParent());
        statWrap.add(lblSelesai.getParent());

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(statWrap, BorderLayout.SOUTH);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFormCard() {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        RoundedPanel card = new RoundedPanel(28);
        card.setBackground(new Color(10,35,65));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30,30,30,30));
        card.setPreferredSize(new Dimension(550, 360));

        // 🔥 FORM FIX TOTAL
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(8,0,8,0);

        JLabel title = new JLabel("Edit Informasi Akun");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        gbc.gridy = 0;
        form.add(title, gbc);

        gbc.gridy++;
        form.add(createLabel("Nama Admin"), gbc);

        gbc.gridy++;
        txtNama = createField();
        form.add(txtNama, gbc);

        gbc.gridy++;
        form.add(createLabel("Password"), gbc);

        gbc.gridy++;
        txtPassword = createPassword();
        form.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.weighty = 1; // 🔥 ini penting biar ruang kebawah ada
        gbc.anchor = GridBagConstraints.NORTH;

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
        btnWrap.setOpaque(false);

        JButton btnSave = roundedBtn("Simpan", new Color(72,150,255));

        btnSave.addActionListener(e -> simpanProfil());

        btnWrap.add(btnSave);

        form.add(btnWrap, gbc);

        card.add(form, BorderLayout.CENTER);
        outer.add(card);

        return outer;
    }

    private void loadData() {

        try {

            Connection conn = Koneksi.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement("SELECT * FROM users WHERE id_user=?");

            pst.setString(1, idAdmin);
            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                String nama = rs.getString("username");

                txtNama.setText(nama);
                lblNama.setText(nama);
            }

            lblTotal.setText(getCount(conn, "SELECT COUNT(*) FROM pengaduan"));
            lblDiproses.setText(getCount(conn, "SELECT COUNT(*) FROM pengaduan WHERE status='diproses'"));
            lblSelesai.setText(getCount(conn, "SELECT COUNT(*) FROM pengaduan WHERE status='selesai'"));

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getCount(Connection conn, String sql) throws Exception {

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if(rs.next()) return rs.getString(1);
        return "0";
    }

    private void simpanProfil() {

        try {

            Connection conn = Koneksi.getConnection();

            String nama = txtNama.getText();
            String pass = new String(txtPassword.getPassword());

            PreparedStatement update;

            if(pass.isEmpty()) {
                // 🔥 kalau password kosong → update username aja
                update = conn.prepareStatement(
                    "UPDATE users SET username=? WHERE id_user=?"
                );
                update.setString(1, nama);
                update.setString(2, idAdmin);

            } else {
                // 🔥 kalau isi → update dua-duanya
                update = conn.prepareStatement(
                    "UPDATE users SET username=?, password=? WHERE id_user=?"
                );
                update.setString(1, nama);
                update.setString(2, pass);
                update.setString(3, idAdmin);
            }

            update.executeUpdate();

            JOptionPane.showMessageDialog(this, "Berhasil disimpan!");
            loadData();

            // 🔥 kosongin field password lagi biar aman
            txtPassword.setText("");

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private JTextField createField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(100,48));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return txt;
    }

    private JPasswordField createPassword() {
        JPasswordField txt = new JPasswordField();
        txt.setPreferredSize(new Dimension(100,48));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return txt;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(140,170,200));
        return lbl;
    }

    private JLabel createStatBox(String title, String value, Color color) {

        JLabel lbl = new JLabel(value, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(color);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setForeground(new Color(160,180,210));

        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(90,90));
        box.setBackground(new Color(7,25,47));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(Box.createVerticalGlue());
        box.add(lbl);
        box.add(titleLbl);
        box.add(Box.createVerticalGlue());

        return lbl;
    }

    private JButton roundedBtn(String text, Color color) {

        JButton btn = new JButton(text){
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setPreferredSize(new Dimension(150,45));
        btn.setFocusPainted(false);

        return btn;
    }

    class AvatarCircle extends JLabel {

        private String text;

        public AvatarCircle(String text) {
            this.text = text;
            setPreferredSize(new Dimension(80, 80));
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(35, 82, 130));
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 26));

            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, x, y);

            g2.dispose();
        }
    }

    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}