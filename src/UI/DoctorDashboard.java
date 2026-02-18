package UI;

import database.BookingDAO;
import model.Booking;
import model.PostOperation;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorDashboard extends JFrame {

    private User currentUser;
    private BookingDAO bookingDAO;
    private JTable tableBookings;
    private DefaultTableModel tableModel;
    private JTextField txtPostOpId;
    private JTextArea txtPrescription;
    private JTextArea txtSideEffects;
    private JTextArea txtMedicine;
    private JTextField txtNextDate;
    private JLabel lblSelectedBooking;

    private final Color COLOR_START = new Color(173, 216, 230);
    private final Color COLOR_END = new Color(0, 102, 204);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public DoctorDashboard(User user) {
        this.currentUser = user;
        this.bookingDAO = new BookingDAO();

        setTitle("Doctor Dashboard - Theater Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel(COLOR_START, COLOR_END);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Doctor Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Doctor: " + (user != null ? user.getUsername() : "Unknown"));
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSchedulePanel(),
                createCompletionPanel());
        splitPane.setDividerLocation(600);
        splitPane.setOpaque(false);
        splitPane.setBorder(new EmptyBorder(10, 20, 20, 20));
        splitPane.setBackground(new Color(0, 0, 0, 0));

        JPanel contentLimit = new JPanel(new BorderLayout());
        contentLimit.setOpaque(false);
        contentLimit.add(splitPane);
        mainPanel.add(contentLimit, BorderLayout.CENTER);

        loadSchedule();
    }

    private JPanel createSchedulePanel() {
        JPanel panel = createGlassPanel();
        panel.setLayout(new BorderLayout());
        JLabel lblHeader = new JLabel("Scheduled Surgeries");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblHeader, BorderLayout.NORTH);

        String[] columns = { "Booking ID", "Patient ID", "Operation ID", "Date", "Time" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBookings = new JTable(tableModel);
        tableBookings.setRowHeight(25);
        tableBookings.setFont(MAIN_FONT);
        tableBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        tableBookings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableBookings.getSelectedRow();
                if (row != -1)
                    lblSelectedBooking.setText("Selected Booking: " + tableModel.getValueAt(row, 0));
            }
        });

        panel.add(new JScrollPane(tableBookings), BorderLayout.CENTER);

        JButton btnRefresh = createActionBtn("Refresh List");
        btnRefresh.setPreferredSize(new Dimension(150, 40));
        btnRefresh.addActionListener(e -> loadSchedule());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCompletionPanel() {
        JPanel panel = createGlassPanel();
        panel.setLayout(new BorderLayout());
        JLabel lblHeader = new JLabel("Complete Surgery");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 1, 10, 10));
        form.setOpaque(false);

        lblSelectedBooking = new JLabel("Selected Booking: None");
        lblSelectedBooking.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSelectedBooking.setForeground(Color.RED);
        form.add(lblSelectedBooking);

        txtPostOpId = createStyledField("Post-Op Report ID");
        txtNextDate = createStyledField("Next Clinic Date (YYYY-MM-DD)");
        txtPrescription = createArea("Prescription");
        txtSideEffects = createArea("Side Effects");
        txtMedicine = createArea("Medicines(For Side Effects)");

        form.add(txtPostOpId);
        form.add(new JScrollPane(txtPrescription));
        form.add(new JScrollPane(txtMedicine));
        form.add(new JScrollPane(txtSideEffects));
        form.add(txtNextDate);

        panel.add(form, BorderLayout.CENTER);

        JButton btnComplete = createActionBtn("Mark Completed");
        btnComplete.addActionListener(e -> completeSurgery());
        panel.add(btnComplete, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSchedule() {
        tableModel.setRowCount(0);
        List<Booking> list = bookingDAO.getBookingsByDoctor(currentUser.getUserId());
        for (Booking b : list)
            tableModel.addRow(new Object[] { b.getBookingId(), b.getPatientId(), b.getOpId(), b.getSurgeryDate(),
                    b.getStartTime() });
    }

    private void completeSurgery() {
        int row = tableBookings.getSelectedRow();
        if (row == -1) {
            showError("Please select a surgery from the list first.");
            return;
        }

        String bookingId = (String) tableModel.getValueAt(row, 0);
        String pOpId = txtPostOpId.getText().trim();
        String presEvent = txtPrescription.getText();
        String sideEff = txtSideEffects.getText();
        String med = txtMedicine.getText();
        String dateStr = txtNextDate.getText().trim();

        if (pOpId.isEmpty() || dateStr.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        try {
            Date nextDate = Date.valueOf(dateStr);
            PostOperation postOp = new PostOperation(pOpId, bookingId, presEvent, sideEff, med, nextDate);
            if (bookingDAO.completeSurgery(postOp)) {
                showMessage("Surgery Completed & Logged!");
                loadSchedule();
                clearFields();
                lblSelectedBooking.setText("Selected Booking: None");
            } else {
                showError("Failed to complete surgery. Check inputs.");
            }
        } catch (IllegalArgumentException e) {
            showError("Invalid Date Format. Use YYYY-MM-DD");
        }
    }

    // === UTILITIES ===
    private JTextArea createArea(String title) {
        JTextArea area = new JTextArea();
        area.setFont(MAIN_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        return area;
    }

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(255, 255, 255, 210));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JTextField createStyledField(String title) {
        JTextField field = new JTextField();
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, MAIN_FONT));
        return field;
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

    private void clearFields() {
        txtPostOpId.setText("");
        txtPrescription.setText("");
        txtSideEffects.setText("");
        txtMedicine.setText("");
        txtNextDate.setText("");
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