package ui;

import config.Koneksi;
import session.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilPanel extends JPanel {

    JTextField txtUsername;
    JPasswordField txtPassword;

    JLabel lblNama;
    JLabel lblTotal;
    JLabel lblProses;
    JLabel lblSelesai;

    CircleAvatar avatar;

    Timer autoRefresh;

    public ProfilPanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(240,242,245));

        add(createContent());

        loadDataUser();
        loadStatPengaduan();
        startAutoRefresh();
    }

    // ==================================================
    // CONTENT
    // ==================================================
    private JPanel createContent() {

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);

        JPanel content = new JPanel(new GridLayout(1,2,25,0));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(1100,520));

        content.add(createProfileCard());
        content.add(createFormCard());

        wrap.add(content);

        return wrap;
    }

    // ==================================================
    // CARD KIRI
    // ==================================================
    private JPanel createProfileCard() {

        RoundedPanel card = new RoundedPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30,25,30,25));

        avatar = new CircleAvatar("U");
        avatar.setPreferredSize(new Dimension(100,100));
        avatar.setMaximumSize(new Dimension(100,100));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNama = new JLabel("-");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNama.setForeground(new Color(30,55,85));
        lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = new JLabel("User");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        role.setForeground(new Color(130,130,130));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel stats = new JPanel(new GridLayout(1,3,14,0));
        stats.setOpaque(false);

        // diperbesar
        stats.setMaximumSize(new Dimension(420,115));
        stats.setPreferredSize(new Dimension(420,115));

        lblTotal = new JLabel("0");
        lblProses = new JLabel("0");
        lblSelesai = new JLabel("0");

        stats.add(createStatCard(lblTotal,"Total",new Color(40,90,180)));
        stats.add(createStatCard(lblProses,"Proses",new Color(184,120,0)));
        stats.add(createStatCard(lblSelesai,"Selesai",new Color(0,140,70)));

        card.add(Box.createVerticalGlue());
        card.add(avatar);
        card.add(Box.createRigidArea(new Dimension(0,18)));
        card.add(lblNama);
        card.add(Box.createRigidArea(new Dimension(0,5)));
        card.add(role);
        card.add(Box.createRigidArea(new Dimension(0,28)));
        card.add(stats);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createStatCard(JLabel angka, String label, Color warna) {

        RoundedPanel p = new RoundedPanel();
        p.setBackground(new Color(245,248,251));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        angka.setFont(new Font("Segoe UI", Font.BOLD, 24));
        angka.setForeground(warna);
        angka.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(angka);
        p.add(Box.createRigidArea(new Dimension(0,6)));
        p.add(l);
        p.add(Box.createVerticalGlue());

        return p;
    }

    // ==================================================
    // CARD KANAN (TETAP)
    // ==================================================
    private JPanel createFormCard() {

        RoundedPanel card = new RoundedPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(35,35,35,35));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Edit Informasi Profil");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(35,60,90));

        txtUsername = modernField("");
        txtPassword = modernPasswordField("");

        top.add(title);
        top.add(Box.createRigidArea(new Dimension(0,25)));
        top.add(formField("Nama Lengkap", txtUsername));
        top.add(Box.createRigidArea(new Dimension(0,22)));

        JPanel passWrap = new JPanel(new BorderLayout(10,0));
        passWrap.setOpaque(false);

        JPanel passFieldWrap = new JPanel(new BorderLayout());
        passFieldWrap.setBackground(Color.WHITE);
        passFieldWrap.setBorder(BorderFactory.createLineBorder(new Color(210,210,210)));
        passFieldWrap.setPreferredSize(new Dimension(100,50));

        txtPassword.setBorder(BorderFactory.createEmptyBorder(0,14,0,10));

        JButton btnShow = new JButton("👁");
        btnShow.setFocusPainted(false);
        btnShow.setBorderPainted(false);
        btnShow.setContentAreaFilled(false);
        btnShow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShow.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        passFieldWrap.add(txtPassword, BorderLayout.CENTER);
        passFieldWrap.add(btnShow, BorderLayout.EAST);

        RoundedButton btnUbah = new RoundedButton("Ubah");
        btnUbah.setPreferredSize(new Dimension(120,50));

        passWrap.add(passFieldWrap, BorderLayout.CENTER);
        passWrap.add(btnUbah, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Password Baru");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(70,110,160));

        p.add(lbl, BorderLayout.NORTH);
        p.add(passWrap, BorderLayout.CENTER);

        top.add(p);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);

        RoundedButton btnLogout = new RoundedButton("Log Out");
        btnLogout.setPreferredSize(new Dimension(180,50));

        bottom.add(btnLogout);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        btnShow.addActionListener(e -> {
            if(txtPassword.getEchoChar() == '*'){
                txtPassword.setEchoChar((char)0);
            } else {
                txtPassword.setEchoChar('*');
            }
        });

        btnUbah.addActionListener(e -> updateProfil());
        btnLogout.addActionListener(e -> logout());

        return card;
    }

    // ==================================================
    // LOAD USER
    // ==================================================
    private void loadDataUser() {

        try {
            Connection conn = Koneksi.getConnection();

            String sql = "SELECT * FROM users WHERE id_user=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, Session.idUser);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                String username = rs.getString("username");

                txtUsername.setText(username);
                lblNama.setText(username);
                avatar.setTextAvatar(username.substring(0,1).toUpperCase());
            }

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // LOAD STATS
    // ==================================================
    private void loadStatPengaduan() {

        try {
            Connection conn = Koneksi.getConnection();

            String sql =
                    "SELECT " +
                    "COUNT(*) total, " +
                    "SUM(CASE WHEN status='diproses' THEN 1 ELSE 0 END) proses, " +
                    "SUM(CASE WHEN status='selesai' THEN 1 ELSE 0 END) selesai " +
                    "FROM pengaduan WHERE id_user=?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, Session.idUser);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                lblTotal.setText(rs.getString("total"));
                lblProses.setText(rs.getString("proses"));
                lblSelesai.setText(rs.getString("selesai"));
            }

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // AUTO REFRESH
    // ==================================================
    private void startAutoRefresh() {

        autoRefresh = new Timer(3000, e -> {
            loadStatPengaduan();
        });

        autoRefresh.start();
    }

    // ==================================================
    // UPDATE PROFIL
    // ==================================================
    private void updateProfil() {

        try {
            Connection conn = Koneksi.getConnection();

            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            String sql;

            if(!password.isEmpty()) {
                sql = "UPDATE users SET username=?, password=? WHERE id_user=?";
            } else {
                sql = "UPDATE users SET username=? WHERE id_user=?";
            }

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);

            if(!password.isEmpty()) {
                pst.setString(2, password);
                pst.setString(3, Session.idUser);
            } else {
                pst.setString(2, Session.idUser);
            }

            pst.executeUpdate();

            Session.username = username;
            lblNama.setText(username);
            avatar.setTextAvatar(username.substring(0,1).toUpperCase());

            JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui");

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // LOGOUT
    // ==================================================
    private void logout() {

        if(autoRefresh != null){
            autoRefresh.stop();
        }

        Session.idUser = null;
        Session.username = null;
        Session.role = null;

        new LoginForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    // ==================================================
    // FIELD
    // ==================================================
    private JTextField modernField(String text) {

        JTextField f = new JTextField(text);
        f.setPreferredSize(new Dimension(100,50));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210)),
                BorderFactory.createEmptyBorder(0,14,0,14)
        ));

        return f;
    }

    private JPasswordField modernPasswordField(String text) {

        JPasswordField f = new JPasswordField(text);
        f.setEchoChar('*');
        f.setPreferredSize(new Dimension(100,50));
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        f.setBorder(BorderFactory.createEmptyBorder());

        return f;
    }

    private JPanel formField(String label, Component field) {

        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI",Font.PLAIN,14));
        l.setForeground(new Color(70,110,160));

        p.add(l,BorderLayout.NORTH);
        p.add(field,BorderLayout.CENTER);

        return p;
    }

    // ==================================================
    // PANEL
    // ==================================================
    class RoundedPanel extends JPanel {

        public RoundedPanel() {
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(new Color(200,210,220));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);

            g2.setColor(getBackground());
            g2.fillRoundRect(1,1,getWidth()-2,getHeight()-2,25,25);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================
    // AVATAR
    // ==================================================
    class CircleAvatar extends JLabel {

        String huruf;

        public CircleAvatar(String huruf) {
            this.huruf = huruf;
        }

        public void setTextAvatar(String t) {
            huruf = t;
            repaint();
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(new Color(28,68,110));
            g2.fillOval(0,0,getWidth(),getHeight());

            g2.setFont(new Font("Segoe UI",Font.BOLD,28));
            FontMetrics fm = g2.getFontMetrics();

            int x = (getWidth()-fm.stringWidth(huruf))/2;
            int y = (getHeight()-fm.getHeight())/2 + fm.getAscent();

            g2.setColor(Color.WHITE);
            g2.drawString(huruf,x,y);

            g2.dispose();
        }
    }

    // ==================================================
    // BUTTON
    // ==================================================
    class RoundedButton extends JButton {

        public RoundedButton(String text) {

            super(text);

            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100,50));
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);

            g2.setColor(new Color(180,180,180));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);

            FontMetrics fm = g2.getFontMetrics();

            int x = (getWidth()-fm.stringWidth(getText()))/2;
            int y = (getHeight()-fm.getHeight())/2 + fm.getAscent();

            g2.drawString(getText(),x,y);

            g2.dispose();
        }
    }
}