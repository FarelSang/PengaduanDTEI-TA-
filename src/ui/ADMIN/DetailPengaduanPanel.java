package ui.ADMIN;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailPengaduanPanel extends JPanel {

    private String idPengaduan;
    private String fromPage;

    private JLabel lblJudul;
    private JLabel lblKategori;
    private JLabel lblTanggal;
    private JLabel lblPelapor;
    private JLabel lblStatus;

    private JTextArea txtIsi;
    private JTextArea txtTanggapan;

    private JComboBox<String> cbStatus;

    public DetailPengaduanPanel(String idPengaduan, String fromPage){

        this.idPengaduan = idPengaduan;
        this.fromPage = fromPage;

        setLayout(new BorderLayout());
        setBackground(new Color(6,20,39));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        updateDibaca();
        loadData();
    }

    // =====================================================
    // HEADER
    // =====================================================
    private JPanel createHeader() {

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(10,28,52));
        top.setBorder(new EmptyBorder(25,35,25,35));

        JLabel title = new JLabel("Detail Pengaduan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JButton btnBack = createRoundedButton(
                "← Kembali",
                new Color(72,150,255)
        );

        btnBack.addActionListener(e -> {

            Container parent = getParent();

            if(parent.getLayout() instanceof CardLayout) {

                CardLayout cl =
                        (CardLayout) parent.getLayout();

                cl.show(parent, fromPage);
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        right.setOpaque(false);
        right.add(btnBack);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    // =====================================================
    // CONTENT
    // =====================================================
    private JScrollPane createContent() {

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(30,35,30,35));

        // =====================================================
        // DETAIL CARD
        // =====================================================

        RoundedPanel detailCard = new RoundedPanel(28);
        detailCard.setBackground(new Color(11,34,62));
        detailCard.setLayout(new BorderLayout());
        detailCard.setBorder(new EmptyBorder(28,28,28,28));
        detailCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,340));

        // =====================================================
        // TOP AREA
        // =====================================================

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);

        // LEFT CONTENT
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        // JUDUL
        JPanel judulWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        judulWrap.setOpaque(false);
        judulWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblJudul = new JLabel("-");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblJudul.setForeground(Color.WHITE);

        judulWrap.add(lblJudul);

        left.add(judulWrap);
        left.add(Box.createRigidArea(new Dimension(0,18)));

        // INFO BADGES
        JPanel infoWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        infoWrap.setOpaque(false);

        lblKategori = createInfoBadge("");
        lblTanggal  = createInfoBadge("");
        lblPelapor  = createInfoBadge("");

        infoWrap.add(lblKategori);
        infoWrap.add(lblTanggal);
        infoWrap.add(lblPelapor);

        left.add(infoWrap);

        // STATUS
        lblStatus = new JLabel("PENDING");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(new EmptyBorder(10,18,10,18));

        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        statusWrap.setOpaque(false);
        statusWrap.add(lblStatus);

        topArea.add(left, BorderLayout.WEST);
        topArea.add(statusWrap, BorderLayout.EAST);

        // =====================================================
        // ISI
        // =====================================================

        txtIsi = new JTextArea();
        txtIsi.setEditable(false);
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        txtIsi.setOpaque(false);
        txtIsi.setForeground(new Color(235,240,245));
        txtIsi.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txtIsi.setBorder(new EmptyBorder(18,18,18,18));

        RoundedPanel isiWrap = new RoundedPanel(20);
        isiWrap.setBackground(new Color(7,25,47));
        isiWrap.setLayout(new BorderLayout());

        isiWrap.add(txtIsi, BorderLayout.CENTER);

        JPanel contentWrap = new JPanel(new BorderLayout());
        contentWrap.setOpaque(false);
        contentWrap.setBorder(new EmptyBorder(26,0,0,0));
        contentWrap.add(isiWrap, BorderLayout.CENTER);

        detailCard.add(topArea, BorderLayout.NORTH);
        detailCard.add(contentWrap, BorderLayout.CENTER);

        // =====================================================
        // TANGGAPAN CARD
        // =====================================================

        RoundedPanel tanggapanCard = new RoundedPanel(28);
        tanggapanCard.setBackground(new Color(11,34,62));
        tanggapanCard.setLayout(new BoxLayout(tanggapanCard, BoxLayout.Y_AXIS));
        tanggapanCard.setBorder(new EmptyBorder(28,28,28,28));
        tanggapanCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,430));

        JLabel titleTanggapan = new JLabel("Tanggapan Admin");
        titleTanggapan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleTanggapan.setForeground(Color.WHITE);
        titleTanggapan.setAlignmentX(Component.CENTER_ALIGNMENT);

        tanggapanCard.add(titleTanggapan);
        tanggapanCard.add(Box.createRigidArea(new Dimension(0,24)));

        // TEXTAREA
        txtTanggapan = new JTextArea();
        txtTanggapan.setLineWrap(true);
        txtTanggapan.setWrapStyleWord(true);
        txtTanggapan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTanggapan.setForeground(Color.WHITE);
        txtTanggapan.setBackground(new Color(7,25,47));
        txtTanggapan.setCaretColor(Color.WHITE);
        txtTanggapan.setBorder(new EmptyBorder(18,18,18,18));

        JScrollPane sp = new JScrollPane(txtTanggapan);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(100,190));

        tanggapanCard.add(sp);

        tanggapanCard.add(Box.createRigidArea(new Dimension(0,24)));

        // =====================================================
        // ACTION AREA
        // =====================================================

        JPanel actionArea = new JPanel(new BorderLayout());
        actionArea.setOpaque(false);

        // LEFT
        JPanel leftAction = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        leftAction.setOpaque(false);

        cbStatus = new JComboBox<>(new String[]{
                "pending",
                "diproses",
                "selesai"
        });

        cbStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbStatus.setFocusable(false);
        cbStatus.setPreferredSize(new Dimension(170,42));
        cbStatus.setBackground(new Color(18,42,70));
        cbStatus.setForeground(Color.WHITE);

        leftAction.add(cbStatus);

        // RIGHT
        JPanel rightAction = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));
        rightAction.setOpaque(false);

        JButton btnSimpan = createRoundedButton(
                "Simpan & Kirim",
                new Color(72,150,255)
        );

        btnSimpan.setPreferredSize(new Dimension(165,42));

        // =====================================================
        // ACTION SIMPAN
        // =====================================================

        btnSimpan.addActionListener(e -> simpanTanggapan());

        rightAction.add(btnSimpan);

        actionArea.add(leftAction, BorderLayout.WEST);
        actionArea.add(rightAction, BorderLayout.EAST);

        tanggapanCard.add(actionArea);

        // =====================================================

        wrapper.add(detailCard);
        wrapper.add(Box.createRigidArea(new Dimension(0,28)));
        wrapper.add(tanggapanCard);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(6,20,39));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // =====================================================
    // SIMPAN TANGGAPAN
    // =====================================================
    private void simpanTanggapan() {

        try {

            Connection conn = Koneksi.getConnection();

            String isiTanggapan = txtTanggapan.getText().trim();
            String statusBaru = cbStatus.getSelectedItem().toString();

            // =================================================
            // UPDATE STATUS PENGADUAN
            // =================================================

            PreparedStatement updateStatus =
                    conn.prepareStatement(
                            "UPDATE pengaduan SET status=? WHERE id_pengaduan=?"
                    );

            updateStatus.setString(1, statusBaru);
            updateStatus.setString(2, idPengaduan);

            updateStatus.executeUpdate();

            // =================================================
            // CEK APAKAH ADA ISI TANGGAPAN
            // =================================================

            if(!isiTanggapan.isEmpty()) {

                // =============================================
                // CEK APAKAH SUDAH ADA TANGGAPAN
                // =============================================

                PreparedStatement cekTanggapan =
                        conn.prepareStatement(
                                "SELECT id_tanggapan FROM tanggapan " +
                                "WHERE id_pengaduan=?"
                        );

                cekTanggapan.setString(1, idPengaduan);

                ResultSet rs = cekTanggapan.executeQuery();

                String tanggal =
                        new SimpleDateFormat("yyyy-MM-dd")
                                .format(new Date());

                // =============================================
                // JIKA SUDAH ADA → UPDATE
                // =============================================

                if(rs.next()) {

                    String idTanggapan =
                            rs.getString("id_tanggapan");

                    PreparedStatement updateTanggapan =
                            conn.prepareStatement(
                                    "UPDATE tanggapan " +
                                    "SET isi_tanggapan=?, tanggal=? " +
                                    "WHERE id_tanggapan=?"
                            );

                    updateTanggapan.setString(1, isiTanggapan);
                    updateTanggapan.setString(2, tanggal);
                    updateTanggapan.setString(3, idTanggapan);

                    updateTanggapan.executeUpdate();

                }

                // =============================================
                // JIKA BELUM ADA → INSERT
                // =============================================

                else {

                    String idTanggapan = "TNG001";

                    PreparedStatement cekId =
                            conn.prepareStatement(
                                    "SELECT id_tanggapan FROM tanggapan " +
                                    "ORDER BY id_tanggapan DESC LIMIT 1"
                            );

                    ResultSet rsId = cekId.executeQuery();

                    if(rsId.next()) {

                        String lastId =
                                rsId.getString("id_tanggapan");

                        int nomor =
                                Integer.parseInt(
                                        lastId.substring(3)
                                ) + 1;

                        idTanggapan =
                                String.format("TNG%03d", nomor);
                    }

                    PreparedStatement insert =
                            conn.prepareStatement(
                                    "INSERT INTO tanggapan " +
                                    "(id_tanggapan, id_pengaduan, id_user, isi_tanggapan, tanggal) " +
                                    "VALUES (?, ?, ?, ?, ?)"
                            );

                    insert.setString(1, idTanggapan);
                    insert.setString(2, idPengaduan);

                    // ID ADMIN
                    insert.setString(3, "USR001");

                    insert.setString(4, isiTanggapan);
                    insert.setString(5, tanggal);

                    insert.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Data berhasil disimpan!"
            );

            conn.close();

            // =================================================
            // KEMBALI KE HALAMAN PENGADUAN
            // =================================================

            Container parent = getParent();

            if(parent.getLayout() instanceof CardLayout) {

                CardLayout cl =
                        (CardLayout) parent.getLayout();

                cl.show(parent, "pengaduan");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =====================================================
    // LOAD DATA
    // =====================================================
    private void loadData() {

        try {

            Connection conn = Koneksi.getConnection();

            // =================================================
            // LOAD DATA PENGADUAN
            // =================================================

            String sql =
                    "SELECT p.*, u.username, k.nama_kategori " +
                    "FROM pengaduan p " +
                    "JOIN users u ON p.id_user=u.id_user " +
                    "JOIN kategori k ON p.id_kategori=k.id_kategori " +
                    "WHERE p.id_pengaduan=?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idPengaduan);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                String nama =
                        rs.getInt("is_anonim") == 1
                                ? "Anonim"
                                : rs.getString("username");

                String status = rs.getString("status");

                lblJudul.setText(rs.getString("judul"));

                lblKategori.setText(
                        "Kategori : " +
                        rs.getString("nama_kategori")
                );

                lblTanggal.setText(
                        "Tanggal : " +
                        rs.getString("tanggal")
                );

                lblPelapor.setText(
                        "Pelapor : " + nama
                );

                txtIsi.setText(
                        rs.getString("isi")
                );

                // =============================================
                // STATUS
                // =============================================

                lblStatus.setText(status.toUpperCase());

                if(status.equalsIgnoreCase("pending")) {

                    lblStatus.setForeground(new Color(255,191,0));
                    lblStatus.setBackground(new Color(58,48,18));

                }
                else if(status.equalsIgnoreCase("diproses")) {

                    lblStatus.setForeground(new Color(72,150,255));
                    lblStatus.setBackground(new Color(20,45,75));

                }
                else {

                    lblStatus.setForeground(new Color(0,220,140));
                    lblStatus.setBackground(new Color(15,60,45));
                }

                cbStatus.setSelectedItem(status);
            }

            // =================================================
            // LOAD TANGGAPAN
            // =================================================

            PreparedStatement pstTanggapan =
                    conn.prepareStatement(
                            "SELECT isi_tanggapan " +
                            "FROM tanggapan " +
                            "WHERE id_pengaduan=? " +
                            "ORDER BY id_tanggapan DESC LIMIT 1"
                    );

            pstTanggapan.setString(1, idPengaduan);

            ResultSet rsTanggapan = pstTanggapan.executeQuery();

            if(rsTanggapan.next()) {

                txtTanggapan.setText(
                        rsTanggapan.getString("isi_tanggapan")
                );

            } else {

                txtTanggapan.setText("");
            }

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =====================================================
    // UPDATE DIBACA
    // =====================================================
    private void updateDibaca() {

        try {

            Connection conn = Koneksi.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(
                            "UPDATE pengaduan SET is_dibaca=1 WHERE id_pengaduan=?"
                    );

            pst.setString(1, idPengaduan);
            pst.executeUpdate();

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =====================================================
    // INFO BADGE
    // =====================================================
    private JLabel createInfoBadge(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setOpaque(true);
        lbl.setForeground(new Color(220,228,235));
        lbl.setBackground(new Color(18,42,70));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setBorder(new EmptyBorder(10,16,10,16));

        return lbl;
    }

    // =====================================================
    // ROUNDED BUTTON
    // =====================================================
    private JButton createRoundedButton(String text, Color bg) {

        JButton btn = new JButton(text){

            @Override
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
                        18,
                        18
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(11,22,11,22));

        return btn;
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