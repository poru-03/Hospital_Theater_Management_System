package UI;

import database.PatientDAO;
import database.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    // DAOs
    private UserDAO userDAO;
    private PatientDAO patientDAO;

    // UI Components
    private JLabel lblImage;

    // Custom Colors
    private final Color COLOR_START = new Color(173, 216, 230); // Light Blue
    private final Color COLOR_END = new Color(0, 102, 204); // Strong Blue
    private final Color BTN_GREEN = new Color(0, 153, 76); // Medical Green
    private final Color BTN_HOVER = new Color(0, 128, 64); // Darker Green for Hover

    public LoginFrame() {
        // 1. Initialize Tools
        userDAO = new UserDAO();
        patientDAO = new PatientDAO();

        // 2. Window Setup
        setTitle("Hospital Theater Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // === LEFT PANEL (The Image) ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

        // Load Image
        loadImage();
        leftPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadImage();
            }
        });

        leftPanel.add(lblImage, BorderLayout.CENTER);
        add(leftPanel);

        // === RIGHT PANEL (Gradient + Login) ===
        GradientPanel rightPanel = new GradientPanel(COLOR_START, COLOR_END);
        rightPanel.setLayout(new GridBagLayout());

        // White Card
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(255, 255, 255, 240)); // More opaque
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        cardPanel.setPreferredSize(new Dimension(380, 480)); // Slightly taller

        // Header
        JLabel lblTitle = new JLabel("<html><center>Hospital Theater<br>Management System</center></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_END);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        cardPanel.add(lblTitle, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.addTab("Staff Login", createStaffPanel());
        tabbedPane.addTab("Patient Portal", createPatientPanel());

        cardPanel.add(tabbedPane, BorderLayout.CENTER);
        rightPanel.add(cardPanel);
        add(rightPanel);
    }

    private void loadImage() {
        try {
            // Ensure image is in the project root folder (not inside src)
            ImageIcon icon = new ImageIcon("src/1.jpg");
            if (icon.getIconWidth() > 0) {
                int width = getWidth() / 2 - 40;
                int height = getHeight() - 100;
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
                lblImage.setText("");
            } else {
                lblImage.setText("Image missing: 'src/1.jpg'");
            }
        } catch (Exception e) {
            lblImage.setText("Error loading image");
        }
    }

    // === STAFF PANEL ===
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10)); // Added spacing
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextField txtUser = new JTextField();
        txtUser.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));

        // USE CUSTOM BUTTON CLASS
        StyledButton btnLogin = new StyledButton("SECURE LOGIN", BTN_GREEN);

        panel.add(new JLabel("Staff Credentials"));
        panel.add(txtUser);
        panel.add(txtPass);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            User user = userDAO.login(txtUser.getText().trim(), new String(txtPass.getPassword()).trim());
            if (user != null) {
                dispose();
                openDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
            }
        });
        return panel;
    }

    // === PATIENT PANEL ===
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10)); // Added spacing
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextField txtId = new JTextField();
        txtId.setBorder(BorderFactory.createTitledBorder("Patient ID"));

        JTextField txtPhone = new JTextField();
        txtPhone.setBorder(BorderFactory.createTitledBorder("Phone Number"));

        // USE CUSTOM BUTTON CLASS
        StyledButton btnLogin = new StyledButton("CHECK MY SURGERY", BTN_GREEN);

        panel.add(new JLabel("Patient Verification"));
        panel.add(txtId);
        panel.add(txtPhone);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            if (patientDAO.patientLogin(txtId.getText().trim(), txtPhone.getText().trim())) {
                dispose();
                new PatientDashboard(txtId.getText().trim()).setVisible(true); // Open Dashboard
                JOptionPane.showMessageDialog(this, "Login Success!");
            } else {
                JOptionPane.showMessageDialog(this, "No record found.");
            }
        });
        return panel;
    }

    private void openDashboard(User user) {
        String role = user.getRole();
        if (role.equals("ADMIN"))
            new AdminDashboard(user).setVisible(true);
        else if (role.equals("DOCTOR"))
            new DoctorDashboard(user).setVisible(true);
        else if (role.equals("NURSE"))
            new NurseDashboard(user).setVisible(true);
    }

    // === CUSTOM BUTTON CLASS (Fixes the White Button Issue) ===
    class StyledButton extends JButton {
        private Color baseColor;

        public StyledButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Change color on click/hover logic could go here,
            // but we stick to the base color for simplicity & robustness
            if (getModel().isPressed()) {
                g2.setColor(baseColor.darker());
            } else {
                g2.setColor(baseColor);
            }

            // Draw Rounded Rectangle
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // === GRADIENT PANEL ===
    class GradientPanel extends JPanel {
        private Color start, end;

        public GradientPanel(Color s, Color e) {
            this.start = s;
            this.end = e;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new LoginFrame().setVisible(true);
    }
}