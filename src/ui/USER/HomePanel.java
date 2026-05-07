package ui.USER;

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

    private JComboBox<String> filterBox;
    private String mode = "Semua";

    public HomePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadData();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(3000, e -> loadData());
        refreshTimer.start();
    }

    // ================= HEADER =================
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

        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);

        header.add(searchPanel);

        return header;
    }

    // ================= CONTENT =================
    private JScrollPane createContent() {

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(22,30,14,30));

        JLabel title = new JLabel("Pengaduan Terbaru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(35,35,35));

        // 🔥 FILTER TANPA NGUBAH LAYOUT
        filterBox = new JComboBox<>(new String[]{"Semua", "Punya Saya"});
        filterBox.setPreferredSize(new Dimension(150,35));

        filterBox.addActionListener(e -> {
            mode = filterBox.getSelectedItem().toString();
            loadData();
        });

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        rightTop.setOpaque(false);
        rightTop.add(filterBox);

        top.add(title, BorderLayout.WEST);
        top.add(rightTop, BorderLayout.EAST);

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

    // ================= LOAD DATA =================
    private void loadData() {

        listPanel.removeAll();

        try {

            Connection conn = Koneksi.getConnection();

            String keyword = txtCari.getText().trim();
            
            //relasi & join
            String sql =
                "SELECT p.id_pengaduan, p.id_user, p.judul, p.isi, p.tanggal, p.status, k.nama_kategori " +
                "FROM pengaduan p " +
                "JOIN kategori k ON p.id_kategori = k.id_kategori " +
                "WHERE 1=1 ";

            if(mode.equals("Punya Saya")){
                sql += "AND p.id_user=? ";
            }

            if(!keyword.equals("") &&
               !keyword.equals("Cari pengaduan...")) {
                sql += "AND p.judul LIKE ? ";
            }

            sql += "ORDER BY p.tanggal DESC";

            PreparedStatement pst = conn.prepareStatement(sql);

            int i = 1;

            if(mode.equals("Punya Saya")){
                pst.setString(i++, Session.idUser);
            }

            if(!keyword.equals("") &&
               !keyword.equals("Cari pengaduan...")) {
                pst.setString(i++, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while(rs.next()) {

                boolean isMine = rs.getString("id_user").equals(Session.idUser);

                listPanel.add(createCard(
                        rs.getString("id_pengaduan"),
                        rs.getString("judul"),
                        rs.getString("isi"),
                        rs.getString("nama_kategori"),
                        rs.getString("tanggal"),
                        rs.getString("status"),
                        isMine
                ));

                listPanel.add(Box.createRigidArea(new Dimension(0,14)));
            }

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    // ================= CARD =================
    private JPanel createCard(String idPengaduan,
                              String judul,
                              String isi,
                              String kategori,
                              String tanggal,
                              String status,
                              boolean isMine) {

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
        left.setAlignmentX(Component.LEFT_ALIGNMENT); // penting

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT); // 🔥

        JLabel lblInfo = new JLabel(kategori + " • " + tanggal);
        lblInfo.setForeground(new Color(130,130,130));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT); // 🔥

        JTextArea desc = new JTextArea(isi);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT); // 🔥

        left.add(lblJudul);
        left.add(Box.createRigidArea(new Dimension(0,6)));
        left.add(lblInfo);
        left.add(Box.createRigidArea(new Dimension(0,12)));
        left.add(desc);

        // RIGHT
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        right.setOpaque(false);

        // tombol detail SEKARANG SELALU ADA
        JButton btn = new JButton("Detail"){
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

        btn.setPreferredSize(new Dimension(110,40));
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {

            Container parent = getParent();

            while(!(parent.getLayout() instanceof CardLayout)){
                parent = parent.getParent();
            }

            parent.add(new DetailPanel(idPengaduan), "detail");

            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "detail");
        });

        right.add(btn);
        
        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.NORTH);

        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    // ================= ROUNDED =================
    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}