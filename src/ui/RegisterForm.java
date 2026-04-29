package ui;

import service.*;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public RegisterForm() {
        setTitle("Register - Pengaduan");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(230,230,230));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // 🔲 CARD
        RoundedPanel mainCard = new RoundedPanel(30);
        mainCard.setLayout(new GridLayout(1,2));
        mainCard.setPreferredSize(new Dimension(900,500));
        mainCard.setBackground(Color.WHITE);

        // ======================
        // ⚪ LEFT (FORM)
        // ======================
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12,30,12,30);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Register");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        txtUser = new JTextField();
        txtPass = new JPasswordField();

        styleField(txtUser);
        styleField(txtPass);

        // 👁 SHOW PASSWORD
        JButton btnShow = new JButton("👁");
        btnShow.setBorderPainted(false);
        btnShow.setContentAreaFilled(false);

        btnShow.addActionListener(e -> {
            if (txtPass.getEchoChar() == 0) {
                txtPass.setEchoChar('•');
            } else {
                txtPass.setEchoChar((char)0);
            }
        });

        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setBackground(new Color(245,245,245));
        passWrapper.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        passWrapper.add(txtPass, BorderLayout.CENTER);
        passWrapper.add(btnShow, BorderLayout.EAST);

        // 🔘 BUTTON REGISTER
        JButton btnRegister = new JButton("Daftar"){
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        btnRegister.setBackground(Color.BLACK);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder(14,10,14,10));

        // Hover
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnRegister.setBackground(new Color(50,50,50));
            }
            public void mouseExited(MouseEvent e) {
                btnRegister.setBackground(Color.BLACK);
            }
        });

        JButton btnLogin = new JButton("Sudah punya akun?");
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setForeground(Color.GRAY);

        // Layout
        c.gridx=0; c.gridy=0;
        left.add(title,c);

        c.gridy++;
        left.add(txtUser,c);

        c.gridy++;
        left.add(passWrapper,c);

        c.gridy++;
        left.add(btnRegister,c);

        c.gridy++;
        left.add(btnLogin,c);

        // ======================
        // 🔵 RIGHT (VISUAL)
        // ======================
        JPanel right = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(52,120,246),
                        getWidth(),getHeight(),new Color(0,180,255)
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        right.setLayout(new GridBagLayout());

        JLabel text = new JLabel("Buat akun baru 🚀");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Segoe UI", Font.BOLD, 20));

        right.add(text);

        // ======================
        mainCard.add(left);
        mainCard.add(right);

        ShadowPanel shadow = new ShadowPanel(30);
        shadow.add(mainCard);

        wrapper.add(shadow);
        add(wrapper);

        // 🔗 LOGIC REGISTER
        AuthService auth = new AuthServiceImpl();

        btnRegister.addActionListener(e -> {
            User user = new User();

            user.setIdUser(auth.generateId());
            user.setUsername(txtUser.getText());
            user.setPassword(new String(txtPass.getPassword()));
            user.setRole("user");

            if (auth.register(user)) {
                JOptionPane.showMessageDialog(this, "Register berhasil!");
                new LoginForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Register gagal!");
            }
        });

        btnLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    // 🎨 INPUT STYLE
    private void styleField(JTextField field){
        field.setPreferredSize(new Dimension(200,40));
        field.setBackground(new Color(245,245,245));
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    // 🔵 ROUNDED PANEL
    class RoundedPanel extends JPanel {
        int radius;

        RoundedPanel(int r){
            radius = r;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);

            g2.dispose();
        }
    }

    // 🌫️ SHADOW PANEL
    class ShadowPanel extends JPanel {
        int radius;

        ShadowPanel(int r){
            radius = r;
            setOpaque(false);
            setLayout(new GridBagLayout());
        }

        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0,0,0,40));
            g2.fillRoundRect(8,8,getWidth()-8,getHeight()-8,radius,radius);

            g2.dispose();
        }
    }
}