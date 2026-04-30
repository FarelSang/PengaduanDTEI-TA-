package ui.ADMIN;

import session.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ui.ADMIN.AdminHomePanel;

public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private MenuButton btnDashboard;
    private MenuButton btnPengaduan;
    private MenuButton btnProfil;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createContent(), BorderLayout.CENTER);

        switchPage("dashboard");
        setActive(btnDashboard);

        setVisible(true);
    }

    // =====================================================
    // SIDEBAR
    // =====================================================
    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(290, 0));
        sidebar.setBackground(new Color(5, 18, 35));

        // ================= TOP =================
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(28, 28, 25, 28));

        JLabel logo = new JLabel("SiPengaduan");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Admin Panel");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(110, 145, 190));

        top.add(logo);
        top.add(Box.createRigidArea(new Dimension(0, 6)));
        top.add(sub);

        // ================= MENU =================
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        btnDashboard = new MenuButton("Dashboard");
        btnPengaduan = new MenuButton("Pengaduan");
        btnProfil = new MenuButton("Profil");

        menu.add(btnDashboard);
        menu.add(Box.createRigidArea(new Dimension(0, 12)));

        menu.add(btnPengaduan);
        menu.add(Box.createRigidArea(new Dimension(0, 12)));

        menu.add(btnProfil);

        // ================= BOTTOM =================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0,
                new Color(20, 40, 65)
        ));

        AvatarLabel avatar = new AvatarLabel(getInitial(Session.username));

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(
                Session.username == null ?
                        "Admin" :
                        Session.username
        );

        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel role = new JLabel("Administrator");
        role.setForeground(new Color(120, 150, 185));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        userText.add(name);
        userText.add(role);

        bottom.add(avatar);
        bottom.add(userText);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        // ================= ACTION =================
        btnDashboard.addActionListener(e -> {
            switchPage("dashboard");
            setActive(btnDashboard);
        });

        btnPengaduan.addActionListener(e -> {
            switchPage("pengaduan");
            setActive(btnPengaduan);
        });

        btnProfil.addActionListener(e -> {
            switchPage("profil");
            setActive(btnProfil);
        });

        return sidebar;
    }

    // =====================================================
    // CONTENT
    // =====================================================
    private JPanel createContent() {

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new AdminHomePanel(), "dashboard");
        contentPanel.add(new AdminPengaduanPanel(), "pengaduan");
        contentPanel.add(
                new ProfilAdminPanel(Session.idUser),
                "profil"
        );
        return contentPanel;
    }

    // =====================================================
    // SWITCH PAGE
    // =====================================================
    private void switchPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    // =====================================================
    // ACTIVE MENU
    // =====================================================
    private void setActive(MenuButton active) {

        MenuButton[] list = {
                btnDashboard,
                btnPengaduan,
                btnProfil
        };

        for (MenuButton btn : list) {
            btn.setActive(false);
        }

        active.setActive(true);
    }

    // =====================================================
    // TEMP PANEL
    // =====================================================
    private JPanel dummyPanel(String text) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(8, 23, 45));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));

        panel.add(lbl);

        return panel;
    }

    // =====================================================
    // GET INITIAL
    // =====================================================
    private String getInitial(String text) {

        if (text == null || text.trim().isEmpty()) {
            return "A";
        }

        return text.substring(0, 1).toUpperCase();
    }

    // =====================================================
    // MENU BUTTON
    // =====================================================
    class MenuButton extends JButton {

        private boolean active = false;
        private boolean hover = false;

        public MenuButton(String text) {

            super(text);

            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(new Color(150, 180, 215));

            setPreferredSize(new Dimension(230, 52));
            setMaximumSize(new Dimension(999, 52));

            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean state) {
            active = state;
            repaint();
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (active) {

                g2.setColor(new Color(36, 76, 122));
                g2.fillRoundRect(
                        0, 0,
                        getWidth(),
                        getHeight(),
                        18, 18
                );

                setForeground(Color.WHITE);

            } else if (hover) {

                g2.setColor(new Color(36, 76, 122, 100));
                g2.fillRoundRect(
                        0, 0,
                        getWidth(),
                        getHeight(),
                        18, 18
                );

                setForeground(Color.WHITE);

            } else {
                setForeground(new Color(150, 180, 215));
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =====================================================
    // AVATAR BULAT
    // =====================================================
    class AvatarLabel extends JLabel {

        private String text;

        public AvatarLabel(String text) {
            this.text = text;
            setPreferredSize(new Dimension(50, 50));
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(35, 82, 130));
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();

            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);

            g2.dispose();
        }
    }
}