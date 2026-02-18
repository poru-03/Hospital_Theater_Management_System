package UI;

import database.AdminDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; // Import needed for rounded corners

public class AdminDashboard extends JFrame {

    private User currentUser;
    private AdminDAO adminDAO;
    private JTabbedPane tabbedPane;

    // Gradient Colors
    private final Color COLOR_START = new Color(173, 216, 230); // Light Blue
    private final Color COLOR_END = new Color(0, 102, 204); // Strong Blue
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.adminDAO = new AdminDAO();

        setTitle("Admin Dashboard - Theater Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Container with Gradient
        GradientPanel mainPanel = new GradientPanel(COLOR_START, COLOR_END);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        // User Label & Logout Button
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Welcome, " + (user != null ? user.getUsername() : "Admin"));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUser.setForeground(new Color(230, 240, 255));

        // FIXED: Use StyledButton for Logout (Red)
        JButton btnLogout = new StyledButton("Logout", new Color(220, 53, 69), 12);
        btnLogout.setPreferredSize(new Dimension(80, 30));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        userPanel.add(lblUser);
        userPanel.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(230, 240, 255));

        // Add Tabs
        tabbedPane.addTab("Register Doctor", createDoctorPanel());
        tabbedPane.addTab("Register Nurse", createNursePanel());
        tabbedPane.addTab("Manage Operations", createOperationPanel());
        tabbedPane.addTab("Manage Theaters", createTheaterPanel());

        JPanel contentLimit = new JPanel(new BorderLayout());
        contentLimit.setOpaque(false);
        contentLimit.setBorder(new EmptyBorder(10, 20, 20, 20));
        contentLimit.add(tabbedPane);

        mainPanel.add(contentLimit, BorderLayout.CENTER);
    }

    // === PANELS (Logic Unchanged) ===

    private JPanel createDoctorPanel() {
        JPanel panel = createGlassPanel();
        JTextField txtId = createStyledField("Doctor ID");
        JTextField txtName = createStyledField("Full Name");
        JTextField txtUser = createStyledField("Username");
        JPasswordField txtPass = createStyledPasswordField("Password");
        JTextField txtContact = createStyledField("Contact Number");

        String[] specializations = { "General Surgeon", "Cardiologist", "Neurologist", "Anesthesiologist",
                "Orthopedic Surgeon", "Pediatrician" };
        JComboBox<String> cmbSpec = new JComboBox<>(specializations);
        cmbSpec.setBorder(BorderFactory.createTitledBorder(null, "Specialization", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        cmbSpec.setBackground(new Color(230, 240, 255));

        JButton btnAdd = createActionBtn("Register Doctor");
        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (adminDAO.addDoctor(new Doctor(id, txtUser.getText().trim(), new String(txtPass.getPassword()),
                    txtName.getText().trim(), txtContact.getText().trim(), (String) cmbSpec.getSelectedItem()))) {
                showMessage("Doctor Added Successfully!");
                clearFields(txtId, txtName, txtUser, txtPass, txtContact);
            } else {
                showError("Failed to add Doctor. Check ID.");
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setOpaque(false);
        form.add(txtId);
        form.add(txtName);
        form.add(txtUser);
        form.add(txtPass);
        form.add(txtContact);
        form.add(cmbSpec);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return wrapInContainer(panel);
    }

    private JPanel createNursePanel() {
        JPanel panel = createGlassPanel();
        JTextField txtId = createStyledField("Nurse ID");
        JTextField txtName = createStyledField("Full Name");
        JTextField txtUser = createStyledField("Username");
        JPasswordField txtPass = createStyledPasswordField("Password");
        JTextField txtContact = createStyledField("Contact Number");

        JButton btnAdd = createActionBtn("Register Nurse");
        btnAdd.addActionListener(e -> {
            if (adminDAO.addNurse(new Nurse(txtId.getText().trim(), txtUser.getText().trim(),
                    new String(txtPass.getPassword()), txtName.getText().trim(), txtContact.getText().trim()))) {
                showMessage("Nurse Added Successfully!");
                clearFields(txtId, txtName, txtUser, txtPass, txtContact);
            } else {
                showError("Failed to add Nurse.");
            }
        });

        JPanel form = new JPanel(new GridLayout(3, 2, 15, 15));
        form.setOpaque(false);
        form.add(txtId);
        form.add(txtName);
        form.add(txtUser);
        form.add(txtPass);
        form.add(txtContact);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return wrapInContainer(panel);
    }

    private JPanel createOperationPanel() {
        JPanel panel = createGlassPanel();
        JTextField txtId = createStyledField("Operation ID");
        JTextField txtName = createStyledField("Operation Name");
        JTextArea txtDesc = new JTextArea(5, 20);
        txtDesc.setFont(MAIN_FONT);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(BorderFactory.createTitledBorder(null, "Description", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));

        JButton btnAdd = createActionBtn("Add Operation Type");
        btnAdd.addActionListener(e -> {
            if (adminDAO.addOperation(
                    new Operation(txtId.getText().trim(), txtName.getText().trim(), txtDesc.getText().trim()))) {
                showMessage("Operation Type Added!");
                txtId.setText("");
                txtName.setText("");
                txtDesc.setText("");
            } else {
                showError("Failed to add Operation.");
            }
        });

        JPanel form = new JPanel(new GridLayout(3, 1, 15, 15));
        form.setOpaque(false);
        form.add(txtId);
        form.add(txtName);
        form.add(scrollDesc);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return wrapInContainer(panel);
    }

    private JPanel createTheaterPanel() {
        JPanel panel = createGlassPanel();
        JTextField txtId = createStyledField("Theater ID");
        JTextField txtName = createStyledField("Theater Name (e.g., OR-1)");

        JButton btnAdd = createActionBtn("Add Theater");
        btnAdd.addActionListener(e -> {
            if (adminDAO.addTheater(new Theater(txtId.getText().trim(), txtName.getText().trim()))) {
                showMessage("Theater Added!");
                txtId.setText("");
                txtName.setText("");
            } else {
                showError("Failed to add Theater.");
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 1, 15, 15));
        form.setOpaque(false);
        form.add(txtId);
        form.add(txtName);
        form.add(Box.createGlue());
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return wrapInContainer(panel);
    }

    // === UTILITIES ===

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(255, 255, 255, 210));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        return panel;
    }

    private JPanel wrapInContainer(JPanel inner) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(inner);
        return wrapper;
    }

    private JTextField createStyledField(String title) {
        JTextField field = new JTextField();
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        return field;
    }

    private JPasswordField createStyledPasswordField(String title) {
        JPasswordField field = new JPasswordField();
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        return field;
    }

    // FIXED: Now returns a StyledButton
    private JButton createActionBtn(String text) {
        StyledButton btn = new StyledButton(text, COLOR_END, 14);
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields)
            f.setText("");
    }

    // === CUSTOM BUTTON CLASS (Fixes White Button Issue) ===
    class StyledButton extends JButton {
        private Color baseColor;

        public StyledButton(String text, Color color, int fontSize) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed())
                g2.setColor(baseColor.darker());
            else
                g2.setColor(baseColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class GradientPanel extends JPanel {
        private Color startColor, endColor;

        public GradientPanel(Color start, Color end) {
            this.startColor = start;
            this.endColor = end;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}