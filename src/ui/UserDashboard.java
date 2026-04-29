package ui;

import session.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private NavButton btnHome, btnPengaduan, btnProfil;

    public UserDashboard() {
        setTitle("Dashboard User");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ======================
        // 🔝 NAVBAR
        // ======================
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

        // ======================
        // 🔵 AVATAR USER
        // ======================
        String huruf = "U";

        if (Session.username != null && !Session.username.trim().isEmpty()) {
            huruf = Session.username.substring(0, 1).toUpperCase();
        }

        CircleAvatar avatar = new CircleAvatar(huruf);
        avatar.setPreferredSize(new Dimension(50,50));
        avatar.setMaximumSize(new Dimension(50,50));

        navbar.add(logo, BorderLayout.WEST);
        navbar.add(menu, BorderLayout.CENTER);
        navbar.add(avatar, BorderLayout.EAST);

        // ======================
        // 🔄 CARD LAYOUT
        // ======================
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePanel(), "home");
        mainPanel.add(new PengaduanPanel(), "pengaduan");
        mainPanel.add(new ProfilPanel(), "profil");

        // ======================
        // 🔗 NAVIGATION ACTION
        // ======================
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

    // ======================
    // 🔁 SWITCH PAGE
    // ======================
    private void switchPage(String page) {
        cardLayout.show(mainPanel, page);
    }

    // ======================
    // 🎯 ACTIVE NAVBAR
    // ======================
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

    // ======================
    // 🔥 CUSTOM NAV BUTTON
    // ======================
    class NavButton extends JButton {

        private boolean isActive = false;
        private boolean isHover = false;

        public NavButton(String text) {

            super(text);

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(200, 200, 200));
            setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

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

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (isActive) {

                g2.setColor(new Color(70, 130, 180));
                g2.fillRoundRect(
                        0, 0,
                        getWidth(),
                        getHeight(),
                        25, 25
                );

                setForeground(Color.WHITE);

            } else if (isHover) {

                g2.setColor(new Color(70, 130, 180, 80));
                g2.fillRoundRect(
                        0, 0,
                        getWidth(),
                        getHeight(),
                        25, 25
                );

                setForeground(Color.WHITE);

            } else {

                setForeground(new Color(200, 200, 200));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ======================
    // 🔵 AVATAR BULAT
    // ======================
    class CircleAvatar extends JLabel {

        private String text;

        public CircleAvatar(String text) {
            this.text = text;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // bulatan
            g2.setColor(new Color(70, 130, 180));
            g2.fillOval(0, 0, getWidth(), getHeight());

            // huruf
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

            FontMetrics fm = g2.getFontMetrics();

            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(text, x, y);

            g2.dispose();
        }
    }
}