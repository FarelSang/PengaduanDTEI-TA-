package ui;

import service.*;
import model.User;
import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public RegisterForm() {
        setTitle("Register");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        JPanel card = new JPanel(new GridLayout(3,1,10,10));
        card.setPreferredSize(new Dimension(300,200));

        txtUser = new JTextField();
        txtPass = new JPasswordField();

        JButton btnRegister = new JButton("Register");

        card.add(txtUser);
        card.add(txtPass);
        card.add(btnRegister);

        panel.add(card);
        add(panel);

        AuthService auth = new AuthServiceImpl();

        btnRegister.addActionListener(e -> {
            User user = new User();

            user.setIdUser(auth.generateId());
            user.setUsername(txtUser.getText());
            user.setPassword(new String(txtPass.getPassword()));
            user.setRole("user"); // 🔥 default user

            if (auth.register(user)) {
                JOptionPane.showMessageDialog(this, "Register berhasil!");
                new LoginForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Register gagal!");
            }
        });

        setVisible(true);
    }
}