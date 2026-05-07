package ui.ADMIN;

import config.Koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminPengaduanPanel extends JPanel {

    private JPanel listPanel;

    private JButton btnSemua;
    private JButton btnPending;
    private JButton btnProses;
    private JButton btnSelesai;
    private JButton btnBelum;

    private JComboBox<String> cbKategori;

    private String filterStatus = "semua";
    private String filterKategori = "semua";
    private boolean filterBelum = false;

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

    private JPanel createTopbar() {

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(new Color(10,28,52));
        top.setBorder(new EmptyBorder(25,35,25,35));

        JLabel title = new JLabel("Semua Pengaduan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Kelola seluruh laporan masyarakat");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(135,160,190));

        top.add(title);
        top.add(Box.createRigidArea(new Dimension(0,5)));
        top.add(sub);

        return top;
    }

    private JScrollPane createContent() {

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(20,35,25,35));

        JPanel filterArea = new JPanel(new BorderLayout());
        filterArea.setOpaque(false);
        filterArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        cbKategori = new JComboBox<>();
        cbKategori.setPreferredSize(new Dimension(210,38));
        cbKategori.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        right.setOpaque(false);

        btnSemua   = createFilterButton("Semua");
        btnPending = createFilterButton("Pending");
        btnProses  = createFilterButton("Proses");
        btnSelesai = createFilterButton("Selesai");
        btnBelum   = createFilterButton("Belum Ditanggapi");

        btnSemua.addActionListener(e -> {
            filterStatus = "semua";
            filterBelum = false;
            setActiveButton(btnSemua);
            loadData();
        });

        btnPending.addActionListener(e -> {
            filterStatus = "pending";
            filterBelum = false;
            setActiveButton(btnPending);
            loadData();
        });

        btnProses.addActionListener(e -> {
            filterStatus = "diproses";
            filterBelum = false;
            setActiveButton(btnProses);
            loadData();
        });

        btnSelesai.addActionListener(e -> {
            filterStatus = "selesai";
            filterBelum = false;
            setActiveButton(btnSelesai);
            loadData();
        });

        btnBelum.addActionListener(e -> {
            filterBelum = true;
            filterStatus = "semua";
            setActiveButton(btnBelum);
            loadData();
        });

        setActiveButton(btnSemua);

        right.add(btnSemua);
        right.add(btnPending);
        right.add(btnProses);
        right.add(btnSelesai);
        right.add(btnBelum);

        filterArea.add(left, BorderLayout.WEST);
        filterArea.add(right, BorderLayout.EAST);

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        wrapper.add(filterArea);
        wrapper.add(Box.createRigidArea(new Dimension(0,20)));
        wrapper.add(listPanel);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(6,20,39));

        return scroll;
    }

    private void loadKategori() {

        cbKategori.removeAllItems();
        cbKategori.addItem("semua");

        try{
            Connection conn = Koneksi.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement("SELECT nama_kategori FROM kategori");

            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                cbKategori.addItem(rs.getString("nama_kategori"));
            }

            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadData() {

        listPanel.removeAll();

        try {

            Connection conn = Koneksi.getConnection();
            
            //subquery
            String sql =
                    "SELECT p.id_pengaduan, p.judul, p.isi, p.tanggal, p.status, " +
                    "p.is_anonim, u.username, k.nama_kategori " +
                    "FROM pengaduan p " +
                    "JOIN users u ON p.id_user=u.id_user " +
                    "JOIN kategori k ON p.id_kategori=k.id_kategori " +
                    "WHERE 1=1 ";
            //TABEL TANGGAPANN
            if(filterBelum){
                sql += "AND p.id_pengaduan NOT IN (SELECT id_pengaduan FROM tanggapan) ";
            }

            if(!filterStatus.equals("semua")){
                sql += "AND p.status=? ";
            }

            if(!filterKategori.equals("semua")){
                sql += "AND k.nama_kategori=? ";
            }

            PreparedStatement pst = conn.prepareStatement(sql);

            int no = 1;

            if(!filterStatus.equals("semua")){
                pst.setString(no++, filterStatus);
            }

            if(!filterKategori.equals("semua")){
                pst.setString(no++, filterKategori);
            }

            ResultSet rs = pst.executeQuery();

            while(rs.next()) {

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

          JButton btnDetail = createRoundedButton("Detail", new Color(72,150,255));
          btnDetail.setPreferredSize(new Dimension(100,36));

          btnDetail.addActionListener(e -> {
              Container parent = getParent();
              while (!(parent.getLayout() instanceof CardLayout)) {
                  parent = parent.getParent();
              }
              JPanel main = (JPanel) parent;
              main.add(new DetailPengaduanPanel(idPengaduan, "pengaduan"),"detail");
              ((CardLayout) main.getLayout()).show(main,"detail");
          });

          right.add(badge);
          right.add(Box.createRigidArea(new Dimension(0,12)));
          right.add(btnDetail);

          card.add(left, BorderLayout.CENTER);
          card.add(right, BorderLayout.EAST);

          return card;
      }

    private JLabel createStatusBadge(String status) {

        Color c = Color.GRAY;

        if(status.equalsIgnoreCase("pending"))
            c = new Color(255,191,0);

        else if(status.equalsIgnoreCase("diproses"))
            c = new Color(72,150,255);

        else if(status.equalsIgnoreCase("selesai"))
            c = new Color(0,220,140);

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

    private JButton createFilterButton(String text){
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void setActiveButton(JButton active){
        JButton[] arr = {btnSemua, btnPending, btnProses, btnSelesai, btnBelum};
        for(JButton b:arr){
            b.setBackground(new Color(18,42,70));
        }
        active.setBackground(new Color(72,150,255));
    }

    private void startRefresh(){
        refreshTimer = new Timer(3000, e -> loadData());
        refreshTimer.start();
    }

    class RoundedPanel extends JPanel {
        private int radius;
        public RoundedPanel(int radius){
            this.radius = radius;
            setOpaque(false);
        }
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
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
        btn.setBorder(new EmptyBorder(8,18,8,18));

        return btn;
    }
}