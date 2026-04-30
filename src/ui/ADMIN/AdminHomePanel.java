package ui.ADMIN;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/*
=========================================================
 HALAMAN HOME ADMIN
 - Modern Dark UI
 - Statistik realtime dari database
 - List pengaduan terbaru
 - Auto refresh
=========================================================
*/

public class AdminHomePanel extends JPanel {

    private JLabel lblTotal, lblProses, lblSelesai;
    private JPanel tablePanel;

    private Timer refreshTimer;

    public AdminHomePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(6, 20, 39));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadStatistik();
        loadPengaduan();

        startAutoRefresh();
    }

    // ======================================================
    // HEADER
    // ======================================================
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 28, 52));
        header.setBorder(new EmptyBorder(28, 35, 28, 35));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard Pengaduan 2026");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Selamat datang, Admin");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(130, 160, 200));

        left.add(sub);
        left.add(Box.createRigidArea(new Dimension(0, 8)));
        left.add(title);

        Locale localeID = new Locale("id", "ID");

        String hariTanggal = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", localeID)
        );

        JLabel date = new JLabel(hariTanggal);
        date.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        date.setForeground(new Color(120, 145, 180));

                header.add(left, BorderLayout.WEST);
                header.add(date, BorderLayout.EAST);

        return header;
    }

    // ======================================================
    // CONTENT
    // ======================================================
    private JScrollPane createContent() {

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel statWrap = new JPanel(new GridLayout(1, 3, 22, 0));
        statWrap.setOpaque(false);
        statWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        lblTotal = new JLabel("0");
        lblProses = new JLabel("0");
        lblSelesai = new JLabel("0");

        statWrap.add(createStatCard(
                "Total Pengaduan",
                lblTotal,
                "Seluruh laporan masuk",
                "Semua status",
                new Color(72, 150, 255)
        ));

        statWrap.add(createStatCard(
                "Sedang Diproses",
                lblProses,
                "Ditangani tim",
                "Dalam proses",
                new Color(255, 166, 0)
        ));

        statWrap.add(createStatCard(
                "Selesai",
                lblSelesai,
                "Telah ditindaklanjuti",
                "Pengaduan selesai",
                new Color(0, 200, 120)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        top.setBorder(new EmptyBorder(28, 0, 15, 0));

        JLabel title = new JLabel("Daftar Pengaduan Terbaru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        top.add(title, BorderLayout.WEST);

        tablePanel = new JPanel();
        tablePanel.setOpaque(false);
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        main.add(statWrap);
        main.add(top);
        main.add(tablePanel);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(6, 20, 39));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ======================================================
    // CARD STAT
    // ======================================================
    private JPanel createStatCard(String title,
                                  JLabel angka,
                                  String sub,
                                  String badge,
                                  Color color) {

        RoundedPanel card = new RoundedPanel(26);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(11, 34, 62));
        card.setBorder(new EmptyBorder(25, 28, 25, 28));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(135, 165, 205));

        angka.setFont(new Font("Segoe UI", Font.BOLD, 52));
        angka.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(100, 130, 170));

        JLabel tag = new JLabel(" " + badge + " ");
        tag.setOpaque(true);
        tag.setBackground(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                45
        ));
        tag.setForeground(color);
        tag.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tag.setBorder(new EmptyBorder(7, 10, 7, 10));

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(angka);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(tag);

        return card;
    }

    // ======================================================
    // LOAD STATISTIK
    // ======================================================
    private void loadStatistik() {

        try {

            Connection conn = Koneksi.getConnection();

            PreparedStatement pst1 =
                    conn.prepareStatement("SELECT COUNT(*) FROM pengaduan");
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) lblTotal.setText(rs1.getString(1));

            PreparedStatement pst2 =
                    conn.prepareStatement(
                            "SELECT COUNT(*) FROM pengaduan WHERE status='diproses'"
                    );
            ResultSet rs2 = pst2.executeQuery();

            if (rs2.next()) lblProses.setText(rs2.getString(1));

            PreparedStatement pst3 =
                    conn.prepareStatement(
                            "SELECT COUNT(*) FROM pengaduan WHERE status='selesai'"
                    );
            ResultSet rs3 = pst3.executeQuery();

            if (rs3.next()) lblSelesai.setText(rs3.getString(1));

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // LOAD TABLE
    // ======================================================
    private void loadPengaduan() {

        tablePanel.removeAll();

        addHeaderRow();

        try {

            Connection conn = Koneksi.getConnection();

            String sql =
                    "SELECT p.judul, p.tanggal, p.status, p.is_anonim, u.username " +
                    "FROM pengaduan p " +
                    "JOIN users u ON p.id_user=u.id_user " +
                    "ORDER BY p.tanggal DESC LIMIT 6";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String pelapor;

                if(rs.getInt("is_anonim") == 1){
                    pelapor = "Anonim";
                }else{
                    pelapor = rs.getString("username");
                }

                tablePanel.add(createRow(
                        rs.getString("judul"),
                        pelapor,
                        rs.getString("tanggal"),
                        rs.getString("status")
                ));
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    // ======================================================
    // HEADER TABLE
    // ======================================================
    private void addHeaderRow() {

        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        row.add(head("Judul"));
        row.add(head("Pelapor"));
        row.add(head("Tanggal"));
        row.add(head("Status"));
        row.add(head("Aksi"));

        tablePanel.add(row);
        tablePanel.add(line());
    }

    private JLabel head(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(new Color(120, 150, 185));

        return lbl;
    }

    // ======================================================
    // ROW DATA
    // ======================================================
    private JPanel createRow(String judul,
                             String user,
                             String tanggal,
                             String status) {

        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        row.add(cell(judul));
        row.add(cell(user));
        row.add(cell(tanggal));
        row.add(statusBadge(status));

        JButton btn = new JButton("Detail");
        btn.setFocusPainted(false);
        btn.setBackground(new Color(38, 88, 155));
        btn.setForeground(Color.WHITE);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        row.add(btn);

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        wrap.add(row);
        wrap.add(line());

        return wrap;
    }

    private JLabel cell(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(Color.WHITE);

        return lbl;
    }

    private JPanel statusBadge(String status) {

        Color c = new Color(130,130,130);

        if(status.equalsIgnoreCase("pending"))
            c = new Color(255,166,0);

        if(status.equalsIgnoreCase("diproses"))
            c = new Color(72,150,255);

        if(status.equalsIgnoreCase("selesai"))
            c = new Color(0,200,120);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        wrap.setOpaque(false);

        JLabel lbl = new JLabel(status);
        lbl.setOpaque(true);
        lbl.setForeground(c);
        lbl.setBackground(new Color(
                c.getRed(),
                c.getGreen(),
                c.getBlue(),
                40
        ));

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(new EmptyBorder(6,12,6,12));

        wrap.add(lbl);

        return wrap;
    }

    private JPanel line() {

        JPanel p = new JPanel();
        p.setBackground(new Color(20, 45, 75));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(1,1));

        return p;
    }

    // ======================================================
    // AUTO REFRESH
    // ======================================================
    private void startAutoRefresh() {

        refreshTimer = new Timer(3000, e -> {
            loadStatistik();
            loadPengaduan();
        });

        refreshTimer.start();
    }

    // ======================================================
    // PANEL BULAT
    // ======================================================
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
                    0,
                    0,
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