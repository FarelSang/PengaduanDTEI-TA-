package ui;

import service.*;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400,300);
        add(new JLabel("INI ADMIN", SwingConstants.CENTER));
    }
}