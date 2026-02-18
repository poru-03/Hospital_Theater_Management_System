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
import java.awt.geom.RoundRectangle2D; // Needed

public class PatientDashboard extends JFrame {

    private String patientId;
    private PatientDAO patientDAO;
    private BookingDAO bookingDAO;
    private PostOperationDAO postOpDAO;

    private final Color COLOR_START = new Color(173, 216, 230);
    private final Color COLOR_END = new Color(0, 102, 204);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public PatientDashboard(String patientId) {
        this.patientId = patientId;
        this.patientDAO = new PatientDAO();
        this.bookingDAO = new BookingDAO();
        this.postOpDAO = new PostOperationDAO();

        setTitle("My Health Portal - Theater Management System");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel(COLOR_START, COLOR_END);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        Patient p = patientDAO.getPatientById(patientId);
        Booking b = bookingDAO.getBookingByPatient(patientId);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Health Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Hello, " + (p != null ? p.getFullName() : "Patient"));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        if (b == null) {
            contentPanel.add(createCard("Status", "No surgeries scheduled."));
        } else {
            JPanel statusCard = createGlassPanel();
            statusCard.setLayout(new GridLayout(4, 1, 5, 5));
            addLabeledValue(statusCard, "Surgery Date", b.getSurgeryDate().toString());
            addLabeledValue(statusCard, "Start Time", b.getStartTime().toString());
            addLabeledValue(statusCard, "Status", b.getStatus());
            contentPanel.add(wrapInContainer(statusCard));

            if ("COMPLETED".equalsIgnoreCase(b.getStatus())) {
                PostOperation report = postOpDAO.getPostOpByBookingId(b.getBookingId());
                if (report != null) {
                    JPanel reportCard = createGlassPanel();
                    reportCard.setLayout(new GridLayout(4, 1, 5, 5));
                    addLabeledValue(reportCard, "Prescription", report.getPrescription());
                    addLabeledValue(reportCard, "Medicines(For Side Effects)", report.getMedicine());
                    addLabeledValue(reportCard, "Side Effects", report.getSideEffects());
                    addLabeledValue(reportCard, "Next Clinic Visit", report.getNextClinicDate().toString());

                    JLabel lblReportTitle = new JLabel("Post-Operation Report");
                    lblReportTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    lblReportTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
                    JPanel titleWrap = new JPanel(new BorderLayout());
                    titleWrap.setOpaque(false);
                    titleWrap.add(lblReportTitle, BorderLayout.CENTER);

                    contentPanel.add(titleWrap);
                    contentPanel.add(wrapInContainer(reportCard));
                }
            } else {
                contentPanel.add(createCard("Info", "Medical report will appear here after surgery."));
            }
        }

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        mainPanel.add(scroll, BorderLayout.CENTER);
    }

    private void addLabeledValue(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel("<html><b>" + label + ":</b> " + value + "</html>");
        lbl.setFont(MAIN_FONT);
        panel.add(lbl);
    }

    private JPanel createCard(String title, String content) {
        JPanel card = createGlassPanel();
        card.add(new JLabel("<html><h3>" + title + "</h3><p>" + content + "</p></html>"));
        return wrapInContainer(card);
    }

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        return panel;
    }

    private JPanel wrapInContainer(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(inner, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
        return wrapper;
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