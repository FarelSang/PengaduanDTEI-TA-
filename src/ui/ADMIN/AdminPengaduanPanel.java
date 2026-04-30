package ui.ADMIN;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
=========================================================
 PANEL PENGADUAN ADMIN (REVISI)
 - Filter status di area data (kanan atas)
 - ComboBox kategori di kiri atas
 - Judul/pelapor/tanggal rata kiri
 - Tombol Detail
=========================================================
*/

public class AdminPengaduanPanel extends JPanel {

    private JPanel listPanel;

    private JButton btnSemua;
    private JButton btnPending;
    private JButton btnProses;
    private JButton btnSelesai;

    private JComboBox<String> cbKategori;

    private String filterStatus = "semua";
    private String filterKategori = "semua";

    private Timer refreshTimer;

    public AdminPengaduanPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(6,20,39));

        add(createTopbar(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadKategori();
        loadData();
        startRefresh();
    }

    // =====================================================
    // TOPBAR
    // =====================================================
    private JPanel createTopbar() {

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(new Color(10,28,52));
        top.setBorder(new EmptyBorder(25,35,25,35));

        JLabel title = new JLabel("Semua Pengaduan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Kelola seluruh laporan masyarakat");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(135,160,190));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(title);
        top.add(Box.createRigidArea(new Dimension(0,5)));
        top.add(sub);

        return top;
    }

    // =====================================================
    // CONTENT
    // =====================================================
    private JScrollPane createContent() {

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(20,35,25,35));

        // FILTER AREA
        JPanel filterArea = new JPanel(new BorderLayout());
        filterArea.setOpaque(false);
        filterArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // LEFT CATEGORY
        cbKategori = new JComboBox<>();
        cbKategori.setPreferredSize(new Dimension(210,38));
        cbKategori.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbKategori.setFocusable(false);
        cbKategori.setBackground(new Color(18,42,70));
        cbKategori.setForeground(Color.WHITE);

        cbKategori.addActionListener(e -> {
            if(cbKategori.getSelectedItem() != null){
                filterKategori = cbKategori.getSelectedItem().toString();
                loadData();
            }
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        left.setOpaque(false);
        left.add(cbKategori);

        // RIGHT BUTTONS
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        right.setOpaque(false);

        btnSemua   = createFilterButton("Semua");
        btnPending = createFilterButton("Pending");
        btnProses  = createFilterButton("Proses");
        btnSelesai = createFilterButton("Selesai");

        btnSemua.addActionListener(e -> {
            filterStatus = "semua";
            setActiveButton(btnSemua);
            loadData();
        });

        btnPending.addActionListener(e -> {
            filterStatus = "pending";
            setActiveButton(btnPending);
            loadData();
        });

        btnProses.addActionListener(e -> {
            filterStatus = "diproses";
            setActiveButton(btnProses);
            loadData();
        });

        btnSelesai.addActionListener(e -> {
            filterStatus = "selesai";
            setActiveButton(btnSelesai);
            loadData();
        });

        setActiveButton(btnSemua);

        right.add(btnSemua);
        right.add(btnPending);
        right.add(btnProses);
        right.add(btnSelesai);

        filterArea.add(left, BorderLayout.WEST);
        filterArea.add(right, BorderLayout.EAST);

        // LIST DATA
        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        wrapper.add(filterArea);
        wrapper.add(Box.createRigidArea(new Dimension(0,20)));
        wrapper.add(listPanel);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(6,20,39));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // =====================================================
    // LOAD KATEGORI
    // =====================================================
    private void loadKategori() {

        cbKategori.removeAllItems();
        cbKategori.addItem("semua");

        try{
            Connection conn = Koneksi.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(
                            "SELECT nama_kategori FROM kategori ORDER BY nama_kategori"
                    );

            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                cbKategori.addItem(rs.getString("nama_kategori"));
            }

            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // =====================================================
    // LOAD DATA
    // =====================================================
    private void loadData() {

        listPanel.removeAll();

        try {

            Connection conn = Koneksi.getConnection();

            String sql =
                    "SELECT p.id_pengaduan, p.judul, p.isi, p.tanggal, p.status, " +
                    "p.is_anonim, u.username, k.nama_kategori " +
                    "FROM pengaduan p " +
                    "JOIN users u ON p.id_user=u.id_user " +
                    "JOIN kategori k ON p.id_kategori=k.id_kategori " +
                    "WHERE 1=1 ";

            if(!filterStatus.equals("semua")){
                sql += "AND p.status=? ";
            }

            if(!filterKategori.equals("semua")){
                sql += "AND k.nama_kategori=? ";
            }

            sql += "ORDER BY p.tanggal DESC";

            PreparedStatement pst = conn.prepareStatement(sql);

            int no = 1;

            if(!filterStatus.equals("semua")){
                pst.setString(no++, filterStatus);
            }

            if(!filterKategori.equals("semua")){
                pst.setString(no++, filterKategori);
            }

            ResultSet rs = pst.executeQuery();

            boolean ada = false;

            while(rs.next()) {

                ada = true;

                String nama =
                        rs.getInt("is_anonim") == 1
                                ? "Anonim"
                                : rs.getString("username");

                listPanel.add(createCard(
                    rs.getString("id_pengaduan"),
                    rs.getString("judul"),
                    rs.getString("isi"),
                    nama,
                    rs.getString("tanggal"),
                    rs.getString("status")
                ));

                listPanel.add(Box.createRigidArea(new Dimension(0,15)));
            }

            if(!ada){

                JLabel kosong = new JLabel("Tidak ada data pengaduan.");
                kosong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                kosong.setForeground(Color.GRAY);
                kosong.setAlignmentX(Component.CENTER_ALIGNMENT);

                listPanel.add(Box.createRigidArea(new Dimension(0,40)));
                listPanel.add(kosong);
            }

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    // =====================================================
    // CARD
    // =====================================================
        private JPanel createCard(String idPengaduan,
                                  String judul,
                                  String isi,
                                  String pelapor,
                                  String tanggal,
                                  String status){

        RoundedPanel card = new RoundedPanel(24);
        card.setLayout(new BorderLayout(18,0));
        card.setBackground(new Color(11,34,62));
        card.setBorder(new EmptyBorder(20,22,20,22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,180));

        // LEFT
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel("Pelapor: " + pelapor + " • " + tanggal);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(new Color(140,165,190));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(isi);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(new Color(220,225,230));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(lblJudul);
        left.add(Box.createRigidArea(new Dimension(0,6)));
        left.add(info);
        left.add(Box.createRigidArea(new Dimension(0,12)));
        left.add(desc);

        // RIGHT
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel badge = createStatusBadge(status);

       
        JButton btnDetail = new JButton("Detail") {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(getBackground());
                g2.fillRoundRect(
                    0, 0,
                    getWidth(),
                    getHeight(),
                    25, 25
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.setForeground(Color.WHITE);
        btnDetail.setBackground(new Color(72,150,255));
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDetail.setBorder(new EmptyBorder(8,24,8,24));
        btnDetail.setContentAreaFilled(false);
        btnDetail.setOpaque(false);

        // =====================================================
        // AKSI SAAT DIKLIK
        // =====================================================
        btnDetail.addActionListener(e -> {

            Container parent = getParent();          // panel utama CardLayout
            while (!(parent.getLayout() instanceof CardLayout)) {
                parent = parent.getParent();
            }

            JPanel mainPanel = (JPanel) parent;

            mainPanel.add(
                new DetailPengaduanPanel(idPengaduan),
                "detail"
            );

            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "detail");
        });

        right.add(badge);
        right.add(Box.createRigidArea(new Dimension(0,12)));
        right.add(btnDetail);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    // =====================================================
    // STATUS BADGE
    // =====================================================
    private JLabel createStatusBadge(String status) {

        Color c = Color.GRAY;

        if(status.equalsIgnoreCase("pending"))
            c = new Color(255,166,0);

        if(status.equalsIgnoreCase("diproses"))
            c = new Color(72,150,255);

        if(status.equalsIgnoreCase("selesai"))
            c = new Color(0,200,120);

        JLabel lbl = new JLabel(status.toUpperCase());
        lbl.setOpaque(true);
        lbl.setForeground(c);
        lbl.setBackground(new Color(
                c.getRed(),
                c.getGreen(),
                c.getBlue(),
                45
        ));

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setBorder(new EmptyBorder(8,14,8,14));

        return lbl;
    }

    // =====================================================
    // BUTTON
    // =====================================================
    private JButton createFilterButton(String text) {

        JButton btn = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(getBackground());
                g2.fillRoundRect(
                    0, 0,
                    getWidth(),
                    getHeight(),
                    25, 25
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(new Color(170,190,215));
        btn.setBackground(new Color(18,42,70));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(10,18,10,18));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        return btn;
    }

    private void setActiveButton(JButton active) {

        JButton[] arr = {
                btnSemua, btnPending, btnProses, btnSelesai
        };

        for(JButton b : arr){
            b.setBackground(new Color(18,42,70));
            b.setForeground(new Color(170,190,215));
        }

        active.setBackground(new Color(72,150,255));
        active.setForeground(Color.WHITE);
    }

    // =====================================================
    // AUTO REFRESH
    // =====================================================
    private void startRefresh() {

        refreshTimer = new Timer(3000, e -> loadData());
        refreshTimer.start();
    }

    // =====================================================
    // ROUNDED PANEL
    // =====================================================
    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius){
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g){

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());
            g2.fillRoundRect(
                    0,0,getWidth(),getHeight(),
                    radius,radius
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }
}