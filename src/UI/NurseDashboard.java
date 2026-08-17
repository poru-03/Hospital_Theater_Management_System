package UI;

import database.BookingDAO;
import database.NurseDAO;
import database.PatientDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class NurseDashboard extends JFrame {

    private User currentUser;
    private NurseDAO nurseDAO;
    private PatientDAO patientDAO;
    private BookingDAO bookingDAO;
    private JTabbedPane tabbedPane;
    private JComboBox<Operation> cmbOpPatient;
    private JComboBox<Operation> cmbOpBooking;
    private JComboBox<Doctor> cmbDoctor;
    private JComboBox<Theater> cmbTheater;

    // Modern Color Palette
    private final Color COLOR_BG = new Color(241, 245, 249); // slate-100
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // blue-600
    private final Color COLOR_TEXT = new Color(15, 23, 42); // slate-900
    private final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139); // slate-500
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    public NurseDashboard(User user) {
        this.currentUser = user;
        this.nurseDAO = new NurseDAO();
        this.patientDAO = new PatientDAO();
        this.bookingDAO = new BookingDAO();

        setTitle("Nurse Dashboard - Hospital Theater Management System");
        setSize(950, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        setContentPane(mainPanel);

        // Header Panel (Same flat modern look)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)), // slate-200
            new EmptyBorder(15, 24, 15, 24)
        ));

        // Header Left
        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Nurse Portal");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        JLabel lblWelcome = new JLabel("Logged in as: Nurse " + (user != null ? user.getUsername() : "Nurse"));
        lblWelcome.setFont(FONT_BODY);
        lblWelcome.setForeground(COLOR_TEXT_LIGHT);
        headerLeft.add(lblTitle);
        headerLeft.add(lblWelcome);

        // Header Right
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        headerRight.setOpaque(false);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(239, 68, 68)); // red-500
        btnLogout.setBackground(new Color(254, 242, 242)); // red-50
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(252, 165, 165), 1, true));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(90, 35));
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        headerRight.add(btnLogout);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane (using modern styling options)
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 40);

        loadDropdownData();

        tabbedPane.addTab("Register Patient", createPatientPanel());
        tabbedPane.addTab("Book Surgery", createBookingPanel());

        JPanel contentLimit = new JPanel(new BorderLayout());
        contentLimit.setOpaque(false);
        contentLimit.setBorder(new EmptyBorder(24, 24, 24, 24));
        contentLimit.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(contentLimit, BorderLayout.CENTER);
    }

    private void loadDropdownData() {
        List<Operation> ops = nurseDAO.getAllOperations();
        List<Doctor> docs = nurseDAO.getAllDoctors();
        List<Theater> theaters = nurseDAO.getAllTheaters();

        cmbOpPatient = new JComboBox<>(ops.toArray(new Operation[0]));
        cmbOpBooking = new JComboBox<>(ops.toArray(new Operation[0]));
        cmbDoctor = new JComboBox<>(docs.toArray(new Doctor[0]));
        cmbTheater = new JComboBox<>(theaters.toArray(new Theater[0]));

        styleComboBox(cmbOpPatient);
        styleComboBox(cmbOpBooking);
        styleComboBox(cmbDoctor);
        styleComboBox(cmbTheater);
    }

    private JPanel createPatientPanel() {
        RoundPanel card = new RoundPanel(16, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 40, 35, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 0.5;

        // Title within card
        JLabel lblHeader = new JLabel("Register New Patient");
        lblHeader.setFont(FONT_SUBTITLE);
        lblHeader.setForeground(COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 15, 20, 15);
        card.add(lblHeader, gbc);

        // Inputs
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 15, 8, 15);

        JTextField txtId = createStyledField("e.g., P100");
        txtId.setText("P101");
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(createFormRow("Patient ID", txtId), gbc);

        JTextField txtName = createStyledField("e.g., John Doe");
        txtName.setText("Jane Smith");
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(createFormRow("Full Name", txtName), gbc);

        JTextField txtAge = createStyledField("e.g., 45");
        txtAge.setText("32");
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(createFormRow("Age", txtAge), gbc);

        JTextField txtContact = createStyledField("e.g., 0771234567");
        txtContact.setText("0779876543");
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(createFormRow("Contact Number", txtContact), gbc);

        String[] genders = { "Male", "Female", "Other" };
        JComboBox<String> cmbGender = new JComboBox<>(genders);
        styleComboBox(cmbGender);
        gbc.gridx = 0; gbc.gridy = 3;
        card.add(createFormRow("Gender", cmbGender), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        card.add(createFormRow("Surgical Operation Type", cmbOpPatient), gbc);

        // Action Button
        JButton btnRegister = new JButton("Register Patient Record");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(COLOR_PRIMARY);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(0, 45));
        btnRegister.putClientProperty("JButton.buttonType", "roundRect");
        btnRegister.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String ageStr = txtAge.getText().trim();
                String contact = txtContact.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                Operation selectedOp = (Operation) cmbOpPatient.getSelectedItem();

                if (id.isEmpty() || name.isEmpty() || ageStr.isEmpty() || contact.isEmpty()) {
                    showError("Please fill in all details.");
                    return;
                }
                if (selectedOp == null) {
                    showError("Please select an Operation type.");
                    return;
                }
                int age = Integer.parseInt(ageStr);
                if (!Patient.isValidAge(age)) {
                    showError("Invalid Age (0-120).");
                    return;
                }
                if (!Patient.isValidContact(contact)) {
                    showError("Invalid Contact Number. (Must start with 0, have 10 digits).");
                    return;
                }

                Patient p = new Patient(id, name, age, contact, gender, selectedOp.getOpId(), currentUser.getUserId());
                if (patientDAO.registerPatient(p)) {
                    showMessage("Patient successfully registered!");
                    clearFields(txtId, txtName, txtAge, txtContact);
                } else {
                    showError("Failed to register. Patient ID might already exist.");
                }
            } catch (NumberFormatException ex) {
                showError("Age must be a valid number.");
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 0, 15);
        card.add(btnRegister, gbc);

        return wrapInContainer(card);
    }

    private JPanel createBookingPanel() {
        RoundPanel card = new RoundPanel(16, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 40, 35, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 0.5;

        // Title within card
        JLabel lblHeader = new JLabel("Schedule a New Surgery");
        lblHeader.setFont(FONT_SUBTITLE);
        lblHeader.setForeground(COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 15, 20, 15);
        card.add(lblHeader, gbc);

        // Inputs
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 15, 8, 15);

        JTextField txtBookingId = createStyledField("e.g., B100");
        txtBookingId.setText("B101");
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(createFormRow("Booking ID", txtBookingId), gbc);

        JTextField txtPatientId = createStyledField("e.g., P100");
        txtPatientId.setText("P101");
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(createFormRow("Patient ID", txtPatientId), gbc);

        JTextField txtDate = createStyledField("e.g., 2026-08-30");
        txtDate.setText("2026-08-25");
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(createFormRow("Date (YYYY-MM-DD)", txtDate), gbc);

        JTextField txtTime = createStyledField("e.g., 09:30:00");
        txtTime.setText("10:30:00");
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(createFormRow("Time (HH:MM:SS)", txtTime), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(createFormRow("Assign Doctor", cmbDoctor), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        card.add(createFormRow("Assign Operating Theater", cmbTheater), gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(createFormRow("Operation Type", cmbOpBooking), gbc);

        // Action Button
        JButton btnBook = new JButton("Confirm & Schedule Booking");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.setForeground(Color.WHITE);
        btnBook.setBackground(new Color(16, 185, 129)); // green-500
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(0, 45));
        btnBook.putClientProperty("JButton.buttonType", "roundRect");
        btnBook.addActionListener(e -> {
            try {
                String bId = txtBookingId.getText().trim();
                String pId = txtPatientId.getText().trim();
                Doctor doc = (Doctor) cmbDoctor.getSelectedItem();
                Theater theater = (Theater) cmbTheater.getSelectedItem();
                Operation op = (Operation) cmbOpBooking.getSelectedItem();

                if (bId.isEmpty() || pId.isEmpty() || txtDate.getText().isEmpty() || txtTime.getText().isEmpty()) {
                    showError("Please fill in all booking fields.");
                    return;
                }
                if (doc == null || theater == null || op == null) {
                    showError("Please select a Doctor, Theater, and Operation Type.");
                    return;
                }

                Date sqlDate = Date.valueOf(txtDate.getText().trim());
                Time sqlTime = Time.valueOf(txtTime.getText().trim());

                Booking booking = new Booking(bId, pId, doc.getDoctorId(), theater.getTheaterId(), op.getOpId(),
                        sqlDate, sqlTime, "SCHEDULED");

                if (bookingDAO.addBooking(booking)) {
                    showMessage("Surgery successfully scheduled!");
                    clearFields(txtBookingId, txtPatientId, txtDate, txtTime);
                } else {
                    showError("Failed to schedule. Doctor/Theater might be busy at this slot or Patient ID is invalid.");
                }
            } catch (IllegalArgumentException ex) {
                showError("Invalid Date or Time Format. Use YYYY-MM-DD and HH:MM:SS.");
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 0, 15);
        card.add(btnBook, gbc);

        return wrapInContainer(card);
    }

    // === UTILITY LAYOUT BUILDERS ===
    private JPanel createFormRow(String labelText, JComponent inputComponent) {
        JPanel row = new JPanel(new BorderLayout(5, 5));
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_LIGHT);
        row.add(label, BorderLayout.NORTH);
        row.add(inputComponent, BorderLayout.CENTER);
        return row;
    }

    private JPanel wrapInContainer(JPanel inner) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(inner);
        return wrapper;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(280, 38));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.roundRect", true);
        return field;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setPreferredSize(new Dimension(280, 38));
        box.setBackground(Color.WHITE);
        box.putClientProperty("JComponent.roundRect", true);
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
}