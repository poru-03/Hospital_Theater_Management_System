package UI;

import database.BookingDAO;
import database.PatientDAO;
import database.PostOperationDAO;
import model.Booking;
import model.Patient;
import model.PostOperation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PatientDashboard extends JFrame {

    private String patientId;
    private PatientDAO patientDAO;
    private BookingDAO bookingDAO;
    private PostOperationDAO postOpDAO;

    // Modern Color Palette
    private final Color COLOR_BG = new Color(241, 245, 249); // slate-100
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // blue-600
    private final Color COLOR_TEXT = new Color(15, 23, 42); // slate-900
    private final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139); // slate-500
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);

    public PatientDashboard(String patientId) {
        this.patientId = patientId;
        this.patientDAO = new PatientDAO();
        this.bookingDAO = new BookingDAO();
        this.postOpDAO = new PostOperationDAO();

        setTitle("My Health Portal - Hospital Theater Management System");
        setSize(520, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        setContentPane(mainPanel);

        Patient p = patientDAO.getPatientById(patientId);
        Booking b = bookingDAO.getBookingByPatient(patientId);

        // Header Panel (Flat modern design)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)), // slate-200
            new EmptyBorder(12, 20, 12, 20)
        ));

        // Header Left
        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Health Portal");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        JLabel lblWelcome = new JLabel("Hello, " + (p != null ? p.getFullName() : "Patient"));
        lblWelcome.setFont(FONT_BODY);
        lblWelcome.setForeground(COLOR_TEXT_LIGHT);
        headerLeft.add(lblTitle);
        headerLeft.add(lblWelcome);

        // Header Right
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        headerRight.setOpaque(false);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(239, 68, 68)); // red-500
        btnLogout.setBackground(new Color(254, 242, 242)); // red-50
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(252, 165, 165), 1, true));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(80, 32));
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        headerRight.add(btnLogout);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Scroll Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (b == null) {
            contentPanel.add(createStatusCard("Status", "No surgeries are currently scheduled.", new Color(100, 116, 139)));
        } else {
            // Surgery Scheduled Card
            RoundPanel statusCard = new RoundPanel(16, Color.WHITE);
            statusCard.setLayout(new GridBagLayout());
            statusCard.setBorder(new EmptyBorder(20, 24, 20, 24));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;

            // Card Header
            JPanel cardHeader = new JPanel(new BorderLayout());
            cardHeader.setOpaque(false);
            JLabel cardTitle = new JLabel("Scheduled Surgery Detail");
            cardTitle.setFont(FONT_SUBTITLE);
            cardTitle.setForeground(COLOR_TEXT);
            
            // Status Badge
            String status = b.getStatus().toUpperCase();
            Color badgeBg = status.equals("COMPLETED") ? new Color(209, 250, 229) : new Color(219, 234, 254);
            Color badgeText = status.equals("COMPLETED") ? new Color(5, 150, 105) : COLOR_PRIMARY;
            JLabel statusBadge = new JLabel("  " + status + "  ");
            statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            statusBadge.setOpaque(true);
            statusBadge.setBackground(badgeBg);
            statusBadge.setForeground(badgeText);
            statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            
            cardHeader.add(cardTitle, BorderLayout.WEST);
            cardHeader.add(statusBadge, BorderLayout.EAST);
            
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 15, 0);
            statusCard.add(cardHeader, gbc);

            // Key-Value rows
            gbc.insets = new Insets(6, 0, 6, 0);
            gbc.gridy = 1;
            statusCard.add(createDataRow("Surgery Date", b.getSurgeryDate().toString()), gbc);
            gbc.gridy = 2;
            statusCard.add(createDataRow("Start Time", b.getStartTime().toString()), gbc);
            gbc.gridy = 3;
            statusCard.add(createDataRow("Assigned Theater ID", b.getTheaterId()), gbc);
            gbc.gridy = 4;
            statusCard.add(createDataRow("Doctor ID", b.getDoctorId()), gbc);

            contentPanel.add(statusCard);

            // Post-Op Medical Report Card
            if ("COMPLETED".equalsIgnoreCase(b.getStatus())) {
                PostOperation report = postOpDAO.getPostOpByBookingId(b.getBookingId());
                if (report != null) {
                    contentPanel.add(Box.createVerticalStrut(20));

                    RoundPanel reportCard = new RoundPanel(16, Color.WHITE);
                    reportCard.setLayout(new GridBagLayout());
                    reportCard.setBorder(new EmptyBorder(20, 24, 20, 24));

                    GridBagConstraints gbcRep = new GridBagConstraints();
                    gbcRep.fill = GridBagConstraints.HORIZONTAL;
                    gbcRep.weightx = 1.0;
                    gbcRep.gridx = 0;

                    JLabel repTitle = new JLabel("Post-Operation Medical Report");
                    repTitle.setFont(FONT_SUBTITLE);
                    repTitle.setForeground(COLOR_PRIMARY);
                    
                    gbcRep.gridy = 0;
                    gbcRep.insets = new Insets(0, 0, 15, 0);
                    reportCard.add(repTitle, gbcRep);

                    gbcRep.insets = new Insets(8, 0, 8, 0);
                    
                    gbcRep.gridy = 1;
                    reportCard.add(createReportSection("Prescription Details", report.getPrescription()), gbcRep);
                    
                    gbcRep.gridy = 2;
                    reportCard.add(createReportSection("Medicines Provided", report.getMedicine()), gbcRep);
                    
                    gbcRep.gridy = 3;
                    reportCard.add(createReportSection("Observed Side Effects", report.getSideEffects()), gbcRep);
                    
                    gbcRep.gridy = 4;
                    reportCard.add(createDataRow("Next Scheduled Clinic Visit", report.getNextClinicDate().toString()), gbcRep);

                    contentPanel.add(reportCard);
                }
            } else {
                contentPanel.add(Box.createVerticalStrut(20));
                contentPanel.add(createStatusCard("Medical Record", "Your post-operation medical record & prescriptions will appear here once the surgery is marked as completed by your doctor.", new Color(59, 130, 246)));
            }
        }

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        mainPanel.add(scroll, BorderLayout.CENTER);
    }

    // === UTILITY LAYOUT BUILDERS ===
    private JPanel createDataRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(COLOR_TEXT_LIGHT);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_BODY);
        lblValue.setForeground(COLOR_TEXT);
        
        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        
        // Add subtle divider
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)
        ));
        return row;
    }

    private JPanel createReportSection(String title, String content) {
        JPanel section = new JPanel(new BorderLayout(4, 4));
        section.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_LIGHT);
        
        JTextPane txtContent = new JTextPane();
        txtContent.setText(content != null && !content.isEmpty() ? content : "None recorded.");
        txtContent.setFont(FONT_BODY);
        txtContent.setForeground(COLOR_TEXT);
        txtContent.setEditable(false);
        txtContent.setOpaque(false);
        txtContent.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        section.add(lblTitle, BorderLayout.NORTH);
        section.add(txtContent, BorderLayout.CENTER);
        
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
            BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));
        return section;
    }

    private RoundPanel createStatusCard(String title, String message, Color titleColor) {
        RoundPanel card = new RoundPanel(16, Color.WHITE);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(titleColor);

        JLabel lblMsg = new JLabel("<html><p style='width: 320px;'>" + message + "</p></html>");
        lblMsg.setFont(FONT_BODY);
        lblMsg.setForeground(COLOR_TEXT_LIGHT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblMsg, BorderLayout.CENTER);
        return card;
    }
}