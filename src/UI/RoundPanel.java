package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundPanel extends JPanel {
    private int cornerRadius = 15;
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = null;
    private int borderWidth = 1;

    public RoundPanel() {
        setOpaque(false);
    }

    public RoundPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundPanel(int radius, Color bg) {
        this.cornerRadius = radius;
        this.backgroundColor = bg;
        setOpaque(false);
    }

    @Override
    public void setBackground(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        
        // Paint border if set
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(new RoundRectangle2D.Float(
                borderWidth / 2f, 
                borderWidth / 2f, 
                getWidth() - borderWidth, 
                getHeight() - borderWidth, 
                cornerRadius, 
                cornerRadius
            ));
        }
        g2.dispose();
    }
}
