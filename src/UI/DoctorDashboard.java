package UI;

import database.BookingDAO;
import model.Booking;
import model.PostOperation;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    // Modern Color Palette
    private final Color COLOR_BG = new Color(241, 245, 249); // slate-100
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // blue-600
    private final Color COLOR_ACCENT = new Color(29, 78, 216); // blue-700
    private final Color COLOR_TEXT = new Color(15, 23, 42); // slate-900
    private final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139); // slate-500
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    public DoctorDashboard(User user) {
        this.currentUser = user;
        this.bookingDAO = new BookingDAO();

        setTitle("Doctor Dashboard - Hospital Theater Management System");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        setContentPane(mainPanel);

        // Header Panel (Flat modern look instead of high gradient)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)), // slate-200 border
            new EmptyBorder(15, 24, 15, 24)
        ));

        // Header Left
        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Doctor Portal");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        JLabel lblWelcome = new JLabel("Welcome back, Dr. " + (user != null ? user.getUsername() : "Doctor"));
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

        // Content Area Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSchedulePanel(), createCompletionPanel());
        splitPane.setDividerLocation(620);
        splitPane.setOpaque(false);
        splitPane.setBorder(new EmptyBorder(24, 24, 24, 24));
        splitPane.setDividerSize(10);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        loadSchedule();
    }

    private JPanel createSchedulePanel() {
        RoundPanel card = new RoundPanel(16, Color.WHITE);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Scheduled Surgeries");
        lblHeader.setFont(FONT_SUBTITLE);
        lblHeader.setForeground(COLOR_TEXT);
        card.add(lblHeader, BorderLayout.NORTH);

        // Custom styled modern table
        String[] columns = { "Booking ID", "Patient ID", "Operation ID", "Date", "Time" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableBookings = new JTable(tableModel);
        tableBookings.setRowHeight(35);
        tableBookings.setFont(FONT_BODY);
        tableBookings.setSelectionBackground(new Color(239, 246, 255)); // blue-50 selection
        tableBookings.setSelectionForeground(COLOR_PRIMARY);
        tableBookings.setShowVerticalLines(false);
        tableBookings.setGridColor(new Color(241, 245, 249));
        tableBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableBookings.getTableHeader().setBackground(new Color(248, 250, 252));
        tableBookings.getTableHeader().setForeground(COLOR_TEXT_LIGHT);
        tableBookings.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        tableBookings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableBookings.getSelectedRow();
                if (row != -1) {
                    lblSelectedBooking.setText("Selected Booking: " + tableModel.getValueAt(row, 0));
                    lblSelectedBooking.setForeground(COLOR_PRIMARY);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableBookings);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true));
        card.add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Schedule");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(COLOR_PRIMARY);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(160, 40));
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.addActionListener(e -> loadSchedule());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefresh);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCompletionPanel() {
        RoundPanel card = new RoundPanel(16, Color.WHITE);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Complete Surgery & Log Post-Op");
        lblHeader.setFont(FONT_SUBTITLE);
        lblHeader.setForeground(COLOR_TEXT);
        card.add(lblHeader, BorderLayout.NORTH);

        JPanel formLayout = new JPanel(new GridBagLayout());
        formLayout.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        lblSelectedBooking = new JLabel("Selected Booking: None");
        lblSelectedBooking.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSelectedBooking.setForeground(new Color(239, 68, 68)); // red-500
        gbc.gridy = 0;
        formLayout.add(lblSelectedBooking, gbc);

        // Post-Op Report ID
        gbc.gridy = 1;
        txtPostOpId = createStyledField("Post-Op Report ID", "e.g., PO-123");
        txtPostOpId.setText("PO100");
        formLayout.add(createFormRow("Report ID", txtPostOpId), gbc);

        // Prescription
        gbc.gridy = 2;
        txtPrescription = createArea("Enter detailed prescription events...");
        txtPrescription.setText("Successful laparoscopic surgery. Prescribed rest for 5 days.");
        formLayout.add(createFormRow("Prescription Summary", new JScrollPane(txtPrescription)), gbc);

        // Medicine
        gbc.gridy = 3;
        txtMedicine = createArea("List medicines to prevent/treat side effects...");
        txtMedicine.setText("Paracetamol 500mg - 3 times daily for 3 days.\nAmoxicillin 500mg - 2 times daily for 5 days.");
        formLayout.add(createFormRow("Medicines Provided", new JScrollPane(txtMedicine)), gbc);

        // Side Effects
        gbc.gridy = 4;
        txtSideEffects = createArea("Enter any potential side effects observed...");
        txtSideEffects.setText("Mild nausea, temporary drowsiness.");
        formLayout.add(createFormRow("Side Effects Notes", new JScrollPane(txtSideEffects)), gbc);

        // Next Date
        gbc.gridy = 5;
        txtNextDate = createStyledField("Next Clinic Date (YYYY-MM-DD)", "e.g., 2026-09-01");
        txtNextDate.setText("2026-09-01");
        formLayout.add(createFormRow("Next Follow-up Date", txtNextDate), gbc);

        card.add(new JScrollPane(formLayout) {
            {
                setBorder(null);
                setOpaque(false);
                getViewport().setOpaque(false);
            }
        }, BorderLayout.CENTER);

        JButton btnComplete = new JButton("Mark Surgery Completed");
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setBackground(new Color(16, 185, 129)); // emerald-500 green
        btnComplete.setFocusPainted(false);
        btnComplete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComplete.setPreferredSize(new Dimension(0, 45));
        btnComplete.putClientProperty("JButton.buttonType", "roundRect");
        btnComplete.addActionListener(e -> completeSurgery());
        card.add(btnComplete, BorderLayout.SOUTH);

        return card;
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
        String presEvent = txtPrescription.getText().trim();
        String sideEff = txtSideEffects.getText().trim();
        String med = txtMedicine.getText().trim();
        String dateStr = txtNextDate.getText().trim();

        if (pOpId.isEmpty() || dateStr.isEmpty()) {
            showError("Please fill all fields (Report ID and Follow-up Date are mandatory).");
            return;
        }

        try {
            Date nextDate = Date.valueOf(dateStr);
            PostOperation postOp = new PostOperation(pOpId, bookingId, presEvent, sideEff, med, nextDate);
            if (bookingDAO.completeSurgery(postOp)) {
                showMessage("Surgery Successfully Completed & Medical Records Logged!");
                loadSchedule();
                clearFields();
                lblSelectedBooking.setText("Selected Booking: None");
            } else {
                showError("Database Error: Failed to complete surgery. Check inputs.");
            }
        } catch (IllegalArgumentException e) {
            showError("Invalid Date Format. Please use YYYY-MM-DD.");
        }
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

    private JTextArea createArea(String placeholder) {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        area.putClientProperty("JComponent.roundRect", true);
        return area;
    }

    private JTextField createStyledField(String label, String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(0, 38));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.roundRect", true);
        return field;
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
}