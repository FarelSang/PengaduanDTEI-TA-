package ui;

import config.Koneksi;
import session.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HomePanel extends JPanel {

    private JPanel listPanel;
    private JTextField txtCari;
    private Timer refreshTimer;

    public HomePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadData();
        startAutoRefresh();
    }

    // ==================================================
    // AUTO REFRESH
    // ==================================================
    private void startAutoRefresh() {

        refreshTimer = new Timer(3000, e -> loadData());
        refreshTimer.start();
    }

    public void stopAutoRefresh() {
        if(refreshTimer != null){
            refreshTimer.stop();
        }
    }

    // ==================================================
    // HEADER
    // ==================================================
    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(32,93,173));
        header.setBorder(new EmptyBorder(28,35,28,35));

        JLabel title = new JLabel("Halo, " + Session.username);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Pantau semua pengaduan Anda dengan mudah");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(225,235,245));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0,4)));
        header.add(sub);
        header.add(Box.createRigidArea(new Dimension(0,18)));

        JPanel searchPanel = new JPanel(new BorderLayout(10,0));
        searchPanel.setOpaque(false);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel searchWrap = new RoundedPanel(18);
        searchWrap.setLayout(new BorderLayout());
        searchWrap.setBackground(Color.WHITE);
        searchWrap.setBorder(new EmptyBorder(0,15,0,0));

        txtCari = new JTextField("Cari pengaduan...");
        txtCari.setBorder(null);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCari.setForeground(Color.GRAY);

        txtCari.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(txtCari.getText().equals("Cari pengaduan...")){
                    txtCari.setText("");
                    txtCari.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if(txtCari.getText().trim().isEmpty()){
                    txtCari.setText("Cari pengaduan...");
                    txtCari.setForeground(Color.GRAY);
                }
            }
        });

        txtCari.addActionListener(e -> loadData());

        JButton btnCari = new JButton("Cari");
        btnCari.setPreferredSize(new Dimension(90,48));
        btnCari.setBackground(new Color(24,75,145));
        btnCari.setForeground(Color.WHITE);
        btnCari.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCari.setFocusPainted(false);
        btnCari.setBorder(null);
        btnCari.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCari.addActionListener(e -> loadData());

        searchWrap.add(txtCari, BorderLayout.CENTER);

        searchPanel.add(searchWrap, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);

        header.add(searchPanel);

        return header;
    }

    // ==================================================
    // CONTENT
    // ==================================================
    private JScrollPane createContent() {

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(22,30,14,30));

        JLabel title = new JLabel("Pengaduan Terbaru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(35,35,35));

        top.add(title, BorderLayout.WEST);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(0,22,22,22));

        main.add(top, BorderLayout.NORTH);
        main.add(listPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245,247,250));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ==================================================
    // LOAD DATA
    // ==================================================
    private void loadData() {

        listPanel.removeAll();

        try {

            Connection conn = Koneksi.getConnection();

            String keyword = txtCari.getText().trim();

            String sql =
                "SELECT p.judul, p.isi, p.tanggal, p.status, k.nama_kategori " +
                "FROM pengaduan p " +
                "JOIN kategori k ON p.id_kategori = k.id_kategori " +
                "WHERE p.id_user=? ";

            if(!keyword.equals("") &&
               !keyword.equals("Cari pengaduan...")) {

                sql += "AND p.judul LIKE ? ";
            }

            sql += "ORDER BY p.tanggal DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, Session.idUser);

            if(!keyword.equals("") &&
               !keyword.equals("Cari pengaduan...")) {

                pst.setString(2, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            boolean ada = false;

            while(rs.next()) {

                ada = true;

                listPanel.add(createCard(
                    rs.getString("judul"),
                    rs.getString("isi"),
                    rs.getString("nama_kategori"),
                    rs.getString("tanggal"),
                    rs.getString("status")
                ));

                listPanel.add(Box.createRigidArea(new Dimension(0,14)));
            }

            if(!ada){

                JLabel kosong = new JLabel("Belum ada pengaduan.");
                kosong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                kosong.setForeground(Color.GRAY);
                kosong.setAlignmentX(Component.CENTER_ALIGNMENT);

                listPanel.add(Box.createRigidArea(new Dimension(0,30)));
                listPanel.add(kosong);
            }

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    // ==================================================
    // CARD
    // ==================================================
    private JPanel createCard(String judul,
                              String isi,
                              String kategori,
                              String tanggal,
                              String status) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE,175));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel card = new RoundedPanel(22);
        card.setLayout(new BorderLayout(18,0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20,22,20,22));

        // LEFT
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblJudul.setForeground(new Color(25,25,25));
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfo = new JLabel(kategori + " • " + tanggal);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(130,130,130));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(isi);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setOpaque(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(new Color(80,80,80));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(lblJudul);
        left.add(Box.createRigidArea(new Dimension(0,6)));
        left.add(lblInfo);
        left.add(Box.createRigidArea(new Dimension(0,12)));
        left.add(desc);

        // RIGHT
        Color c = getStatusColor(status);

        JLabel badge = new JLabel(status.toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(c);
        badge.setOpaque(true);
        badge.setBackground(new Color(
            c.getRed(),
            c.getGreen(),
            c.getBlue(),
            35
        ));
        badge.setBorder(new EmptyBorder(7,14,7,14));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        right.setOpaque(false);
        right.add(badge);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.NORTH);

        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    // ==================================================
    // STATUS COLOR
    // ==================================================
    private Color getStatusColor(String status) {

        if(status.equalsIgnoreCase("pending"))
            return new Color(255,145,0);

        if(status.equalsIgnoreCase("diproses"))
            return new Color(0,120,255);

        if(status.equalsIgnoreCase("selesai"))
            return new Color(0,170,90);

        return Color.GRAY;
    }

    // ==================================================
    // ROUNDED PANEL
    // ==================================================
    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());
            g2.fillRoundRect(
                0,0,
                getWidth(),
                getHeight(),
                radius,
                radius
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }
}