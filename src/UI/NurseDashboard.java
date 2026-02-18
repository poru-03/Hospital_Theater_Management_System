package UI;

import database.BookingDAO;
import database.NurseDAO;
import database.PatientDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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

    private final Color COLOR_START = new Color(173, 216, 230);
    private final Color COLOR_END = new Color(0, 102, 204);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public NurseDashboard(User user) {
        this.currentUser = user;
        this.nurseDAO = new NurseDAO();
        this.patientDAO = new PatientDAO();
        this.bookingDAO = new BookingDAO();

        setTitle("Nurse Dashboard - Theater Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel(COLOR_START, COLOR_END);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Nurse Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Logged in as: " + (user != null ? user.getUsername() : "Nurse"));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUser.setForeground(new Color(230, 240, 255));

        // FIXED: Styled Logout
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

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(230, 240, 255));

        loadDropdownData();

        tabbedPane.addTab("Register Patient", createPatientPanel());
        tabbedPane.addTab("Book Surgery", createBookingPanel());

        JPanel contentLimit = new JPanel(new BorderLayout());
        contentLimit.setOpaque(false);
        contentLimit.setBorder(new EmptyBorder(10, 20, 20, 20));
        contentLimit.add(tabbedPane);
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

        styleComboBox(cmbOpPatient, "Operation Type");
        styleComboBox(cmbOpBooking, "Operation Type");
        styleComboBox(cmbDoctor, "Assign Doctor");
        styleComboBox(cmbTheater, "Assign Theater");
    }

    private JPanel createPatientPanel() {
        JPanel panel = createGlassPanel();
        JTextField txtId = createStyledField("Patient ID");
        JTextField txtName = createStyledField("Full Name");
        JTextField txtAge = createStyledField("Age");
        JTextField txtContact = createStyledField("Contact Number");
        String[] genders = { "Male", "Female", "Other" };
        JComboBox<String> cmbGender = new JComboBox<>(genders);
        styleComboBox(cmbGender, "Gender");

        JButton btnRegister = createActionBtn("Register Patient");
        btnRegister.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String ageStr = txtAge.getText().trim();
                String contact = txtContact.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                Operation selectedOp = (Operation) cmbOpPatient.getSelectedItem();

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
                    showError("Invalid Contact Number.");
                    return;
                }

                Patient p = new Patient(id, name, age, contact, gender, selectedOp.getOpId(), currentUser.getUserId());
                if (patientDAO.registerPatient(p)) {
                    showMessage("Patient Registered Successfully!");
                    clearFields(txtId, txtName, txtAge, txtContact);
                } else {
                    showError("Registration Failed. ID might exist.");
                }
            } catch (NumberFormatException ex) {
                showError("Age must be a number.");
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setOpaque(false);
        form.add(txtId);
        form.add(txtName);
        form.add(txtAge);
        form.add(txtContact);
        form.add(cmbGender);
        form.add(cmbOpPatient);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnRegister, BorderLayout.SOUTH);
        return wrapInContainer(panel);
    }

    private JPanel createBookingPanel() {
        JPanel panel = createGlassPanel();
        JTextField txtBookingId = createStyledField("Booking ID");
        JTextField txtPatientId = createStyledField("Patient ID");
        JTextField txtDate = createStyledField("Date (YYYY-MM-DD)");
        JTextField txtTime = createStyledField("Time (HH:MM:SS)");

        JButton btnBook = createActionBtn("Schedule Surgery");
        btnBook.addActionListener(e -> {
            try {
                String bId = txtBookingId.getText().trim();
                String pId = txtPatientId.getText().trim();
                Doctor doc = (Doctor) cmbDoctor.getSelectedItem();
                Theater theater = (Theater) cmbTheater.getSelectedItem();
                Operation op = (Operation) cmbOpBooking.getSelectedItem();

                if (doc == null || theater == null || op == null) {
                    showError("Please select Doctor, Theater, and Operation.");
                    return;
                }
                Date sqlDate = Date.valueOf(txtDate.getText().trim());
                Time sqlTime = Time.valueOf(txtTime.getText().trim());

                Booking booking = new Booking(bId, pId, doc.getDoctorId(), theater.getTheaterId(), op.getOpId(),
                        sqlDate, sqlTime, "SCHEDULED");
                if (bookingDAO.addBooking(booking)) {
                    showMessage("Surgery Scheduled Successfully!");
                    clearFields(txtBookingId, txtPatientId, txtDate, txtTime);
                } else {
                    showError("Booking Failed. Doctor/Theater might be busy.");
                }
            } catch (IllegalArgumentException ex) {
                showError("Invalid Date/Time Format.");
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setOpaque(false);
        form.add(txtBookingId);
        form.add(txtPatientId);
        form.add(txtDate);
        form.add(txtTime);
        form.add(cmbDoctor);
        form.add(cmbTheater);
        form.add(cmbOpBooking);
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnBook, BorderLayout.SOUTH);
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

    private void styleComboBox(JComboBox<?> box, String title) {
        box.setFont(MAIN_FONT);
        box.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        box.setBackground(new Color(230, 240, 255));
    }

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