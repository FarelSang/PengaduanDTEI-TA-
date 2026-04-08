package ui;

import service.*;
import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    public UserDashboard() {
        setTitle("User Dashboard");
        setSize(400,300);
        add(new JLabel("INI USER", SwingConstants.CENTER));
    }
}
