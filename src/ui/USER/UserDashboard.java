package ui.USER;

import session.Session;
import ui.LoginForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ui.LoginForm;

public class UserDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private NavButton btnHome, btnPengaduan, btnProfil;

    public UserDashboard() {

        setTitle("Dashboard User");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(33, 74, 110));
        navbar.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        JLabel logo = new JLabel("SiPengaduan");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        menu.setOpaque(false);

        btnHome = new NavButton("Beranda");
        btnPengaduan = new NavButton("Pengaduan");
        btnProfil = new NavButton("Profil");

        menu.add(btnHome);
        menu.add(btnPengaduan);
        menu.add(btnProfil);

        // =====================================================
        // AVATAR
        // =====================================================

        String huruf = "U";

        if (Session.username != null &&
                !Session.username.trim().isEmpty()) {

            huruf =
                    Session.username
                            .substring(0, 1)
                            .toUpperCase();
        }

        CircleAvatar avatar = new CircleAvatar(huruf);

        avatar.setPreferredSize(new Dimension(50,50));
        avatar.setMaximumSize(new Dimension(50,50));

        // =====================================================
        // RIGHT AREA (AVATAR + LOGOUT)
        // =====================================================

        JPanel rightArea =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        14,
                        0
                ));

        rightArea.setOpaque(false);

        NavButton btnLogout = new NavButton("Logout");

        btnLogout.setPreferredSize(
                new Dimension(120, 42)
        );

        btnLogout.addActionListener(e -> logout());

        rightArea.add(btnLogout);
        rightArea.add(avatar);

        // =====================================================

        navbar.add(logo, BorderLayout.WEST);
        navbar.add(menu, BorderLayout.CENTER);
        navbar.add(rightArea, BorderLayout.EAST);

        // =====================================================
        // MAIN PANEL
        // =====================================================

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePanel(), "home");
        mainPanel.add(new PengaduanPanel(), "pengaduan");
        mainPanel.add(new ProfilPanel(), "profil");

        // =====================================================
        // ACTION
        // =====================================================

        btnHome.addActionListener(e -> {
            switchPage("home");
            setActive(btnHome);
        });

        btnPengaduan.addActionListener(e -> {
            switchPage("pengaduan");
            setActive(btnPengaduan);
        });

        btnProfil.addActionListener(e -> {
            switchPage("profil");
            setActive(btnProfil);
        });

        setActive(btnHome);

        add(navbar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void logout() {

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Yakin ingin logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if(confirm == JOptionPane.YES_OPTION) {

            // HAPUS SESSION
            Session.idUser = null;
            Session.username = null;
            Session.role = null;

            // KEMBALI KE LOGIN
            dispose();

            new LoginForm();
        }
    }

    // =====================================================
    // SWITCH PAGE
    // =====================================================

    private void switchPage(String page) {
        cardLayout.show(mainPanel, page);
    }

    // =====================================================
    // ACTIVE MENU
    // =====================================================

    private void setActive(NavButton activeBtn) {

        NavButton[] buttons = {
                btnHome,
                btnPengaduan,
                btnProfil
        };

        for (NavButton btn : buttons) {
            btn.setActive(false);
        }

        activeBtn.setActive(true);
    }

    // =====================================================
    // NAV BUTTON
    // =====================================================

    class NavButton extends JButton {

        private boolean isActive = false;
        private boolean isHover = false;

        public NavButton(String text) {

            super(text);

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(200, 200, 200));

            setBorder(
                    BorderFactory.createEmptyBorder(
                            10,
                            25,
                            10,
                            25
                    )
            );

            setContentAreaFilled(false);
            setFocusPainted(false);

            setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (isActive) {

                g2.setColor(
                        new Color(70, 130, 180)
                );

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        25,
                        25
                );

                setForeground(Color.WHITE);

            }
            else if (isHover) {

                g2.setColor(
                        new Color(
                                70,
                                130,
                                180,
                                80
                        )
                );

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        25,
                        25
                );

                setForeground(Color.WHITE);

            }
            else {

                setForeground(
                        new Color(200, 200, 200)
                );
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =====================================================
    // AVATAR
    // =====================================================

    class CircleAvatar extends JLabel {

        private String text;

        public CircleAvatar(String text) {

            this.text = text;

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // BULATAN
            g2.setColor(
                    new Color(70, 130, 180)
            );

            g2.fillOval(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            // HURUF
            g2.setColor(Color.WHITE);

            g2.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            18
                    )
            );

            FontMetrics fm =
                    g2.getFontMetrics();

            int x =
                    (getWidth() -
                            fm.stringWidth(text)) / 2;

            int y =
                    ((getHeight() -
                            fm.getHeight()) / 2)
                            + fm.getAscent();

            g2.drawString(text, x, y);

            g2.dispose();
        }
    }
}