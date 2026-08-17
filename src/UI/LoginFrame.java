package UI;

import database.PatientDAO;
import database.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private UserDAO userDAO;
    private PatientDAO patientDAO;

    // Modern Color Palette
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // blue-600
    private final Color COLOR_BG_START = new Color(30, 58, 138); // blue-900 (deep)
    private final Color COLOR_BG_END = new Color(17, 24, 39); // slate-900 (very deep)
    private final Color COLOR_RIGHT_BG = new Color(248, 250, 252); // slate-50 (clean backdrop)
    private final Color COLOR_TEXT_LIGHT = new Color(148, 163, 184); // slate-400
    private final Color COLOR_FEATURE_TEXT = new Color(219, 234, 254); // blue-100
    private final Color BTN_GREEN = new Color(16, 185, 129); // emerald-500
    private final Color BTN_BLUE = new Color(37, 99, 235); // blue-600

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_FEATURE = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    public LoginFrame() {
        // 1. Initialize Tools
        userDAO = new UserDAO();
        patientDAO = new PatientDAO();

        // 2. Window Setup
        setTitle("Hospital Theater Management System");
        setSize(980, 620);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Supporting full screen and resize!

        // Main Split Grid (50/50 landscape)
        JPanel mainSplit = new JPanel(new GridLayout(1, 2));
        setContentPane(mainSplit);

        // =============================================================
        // LEFT HALF: Dynamic Branding Panel (Gradient + Infographics)
        // =============================================================
        GradientPanel leftPanel = new GradientPanel(COLOR_BG_START, COLOR_BG_END);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Container to group left text content
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        leftContainer.setOpaque(false);

        // Vector Logo
        LogoPanel logo = new LogoPanel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Main Title
        JLabel lblTitle = new JLabel("Hospital Portal");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(20, 0, 5, 0));

        // Subtitle
        JLabel lblSubtitle = new JLabel("Surgical Theater Operations Management");
        lblSubtitle.setFont(FONT_SUBTITLE);
        lblSubtitle.setForeground(COLOR_TEXT_LIGHT);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitle.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Separator Line
        JPanel separator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), 2);
            }
        };
        separator.setMaximumSize(new Dimension(300, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // System Features Checklist
        JPanel featureList = new JPanel();
        featureList.setLayout(new BoxLayout(featureList, BoxLayout.Y_AXIS));
        featureList.setOpaque(false);
        featureList.setBorder(new EmptyBorder(30, 0, 0, 0));
        featureList.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] features = {
            "✓  Real-time Surgery Scheduling",
            "✓  Sterile Theater Room Monitoring",
            "✓  Interactive Doctor Post-Op Logging",
            "✓  Direct Patient Health Portal Access",
            "✓  Multi-Role Security Clearances"
        };

        for (String feat : features) {
            JLabel lblFeat = new JLabel(feat);
            lblFeat.setFont(FONT_FEATURE);
            lblFeat.setForeground(COLOR_FEATURE_TEXT);
            lblFeat.setBorder(new EmptyBorder(6, 0, 6, 0));
            featureList.add(lblFeat);
        }

        leftContainer.add(logo);
        leftContainer.add(lblTitle);
        leftContainer.add(lblSubtitle);
        leftContainer.add(separator);
        leftContainer.add(featureList);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.anchor = GridBagConstraints.WEST;
        leftPanel.add(leftContainer, gbcLeft);
        mainSplit.add(leftPanel);

        // =============================================================
        // RIGHT HALF: Centered Login Card Panel
        // =============================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(COLOR_RIGHT_BG);

        // Floating glassmorphism Card
        RoundPanel card = new RoundPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(390, 480));

        // Card Header
        JPanel cardHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        cardHeader.setOpaque(false);
        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(COLOR_BG_END);
        JLabel lblHelp = new JLabel("Select your clearance type below to sign in:");
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHelp.setForeground(COLOR_TEXT_LIGHT);
        cardHeader.add(lblWelcome);
        cardHeader.add(lblHelp);
        card.add(cardHeader, BorderLayout.NORTH);

        // Form Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 36);

        tabbedPane.addTab("Staff Portal", createStaffPanel());
        tabbedPane.addTab("Patient Portal", createPatientPanel());
        card.add(tabbedPane, BorderLayout.CENTER);

        // GridBagConstraints to center the card dynamically during resizing/fullscreen
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0; gbcRight.gridy = 0;
        rightPanel.add(card, gbcRight);
        mainSplit.add(rightPanel);
    }

    // === STAFF PANEL ===
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JTextField txtUser = createStyledField("Username", "nurse1");
        JPasswordField txtPass = createStyledPasswordField("Password", "123");

        JButton btnLogin = new JButton("Authorize Security Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(BTN_BLUE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");

        panel.add(createFormRow("Username", txtUser));
        panel.add(createFormRow("Password", txtPass));
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            User user = userDAO.login(txtUser.getText().trim(), new String(txtPass.getPassword()).trim());
            if (user != null) {
                dispose();
                openDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Authorization Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    // === PATIENT PANEL ===
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JTextField txtId = createStyledField("Patient ID", "P100");
        JTextField txtPhone = createStyledField("Phone Number", "0771234567");

        JButton btnLogin = new JButton("Open Medical Portal");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(BTN_GREEN);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");

        panel.add(createFormRow("Patient ID", txtId));
        panel.add(createFormRow("Phone Number", txtPhone));
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            if (patientDAO.patientLogin(txtId.getText().trim(), txtPhone.getText().trim())) {
                dispose();
                new PatientDashboard(txtId.getText().trim()).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Patient ID or Phone Number not matched in record.", "Access Denied", JOptionPane.ERROR_MESSAGE);
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

    // === UTILITY LAYOUT BUILDERS ===
    private JPanel createFormRow(String labelText, JComponent inputComponent) {
        JPanel row = new JPanel(new BorderLayout(4, 4));
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(100, 116, 139));
        row.add(label, BorderLayout.NORTH);
        row.add(inputComponent, BorderLayout.CENTER);
        return row;
    }

    private JTextField createStyledField(String placeholder, String demoText) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(0, 38));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.roundRect", true);
        if (demoText != null) {
            field.setText(demoText);
        }
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder, String demoText) {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(0, 38));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.roundRect", true);
        if (demoText != null) {
            field.setText(demoText);
        }
        return field;
    }

    // === VECTOR BRANDING LOGO ===
    class LogoPanel extends JPanel {
        public LogoPanel() {
            setPreferredSize(new Dimension(70, 70));
            setMaximumSize(new Dimension(70, 70));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 5;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Translucent white outer background ring
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillOval(x, y, size, size);

            // Cross coordinates
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int len = size / 2;
            int thick = size / 6;

            // Fill cross
            g2.setColor(Color.WHITE);
            g2.fillRect(cx - len / 2, cy - thick / 2, len, thick);
            g2.fillRect(cx - thick / 2, cy - len / 2, thick, len);

            // Circular stroke ring
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(x + 3, y + 3, size - 6, size - 6);

            g2.dispose();
        }
    }

    // === GRADIENT COMPONENT ===
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
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
        new LoginFrame().setVisible(true);
    }
}