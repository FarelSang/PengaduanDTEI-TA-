package ui;

import service.*;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginForm() {
        setTitle("Aplikasi Pengaduan Masyarakat");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔵 PANEL KIRI (Branding)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0, 102, 204));
        leftPanel.setLayout(new GridBagLayout());

        JLabel title = new JLabel("PENGADUAN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JLabel subtitle = new JLabel("Laporkan masalah dengan mudah");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0,10)));
        textPanel.add(subtitle);

        leftPanel.add(textPanel);

        // ⚪ PANEL KANAN (Form Login)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 300));
        card.setBackground(Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 20, 10, 20);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogin.setHorizontalAlignment(JLabel.CENTER);

        txtUser = new JTextField();
        txtPass = new JPasswordField();

        styleField(txtUser, "Username");
        styleField(txtPass, "Password");

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        JButton btnRegister = new JButton("Belum punya akun?");
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setForeground(new Color(0, 102, 204));

        c.gridx = 0;
        c.gridy = 0;
        card.add(lblLogin, c);

        c.gridy++;
        card.add(txtUser, c);

        c.gridy++;
        card.add(txtPass, c);

        c.gridy++;
        card.add(btnLogin, c);

        c.gridy++;
        card.add(btnRegister, c);

        rightPanel.add(card);

        // 🧩 Split layout (responsive)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(600);
        split.setEnabled(false);

        add(split, BorderLayout.CENTER);

        // 🔗 LOGIC
        AuthService auth = new AuthServiceImpl();

        btnLogin.addActionListener(e -> {
            String role = auth.login(
                txtUser.getText(),
                new String(txtPass.getPassword())
            );

            if (role != null) {
                JOptionPane.showMessageDialog(this, "Login berhasil!");

                if (role.equals("admin")) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new UserDashboard().setVisible(true);
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal!");
            }
        });

        btnRegister.addActionListener(e -> {
            new RegisterForm().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    // 🎨 Styling input
    private void styleField(JTextField field, String placeholder) {
        field.setPreferredSize(new Dimension(200, 40));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
    }
}