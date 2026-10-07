package main.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class StartPanel extends JPanel {
    private Image bgImage;
    private Runnable onPlayClicked;

    public StartPanel(Runnable onPlayClicked) {
        this.onPlayClicked = onPlayClicked;
        this.bgImage = AssetManager.getInstance().getImage("background_start.png");
        
        // Add mouse listener to detect clicks on the "Play" button area
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (getPlayButtonBounds().contains(e.getPoint())) {
                    if (onPlayClicked != null) {
                        onPlayClicked.run();
                    }
                }
            }
        });

        // Add mouse motion listener to change cursor on hover
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (getPlayButtonBounds().contains(e.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private Rectangle getPlayButtonBounds() {
        // The play button is roughly in the center horizontally, lower half vertically.
        // Let's estimate relative to current panel size.
        int w = getWidth();
        int h = getHeight();
        int btnWidth = w * 3 / 10; // 30% of width
        int btnHeight = h * 2 / 10; // 20% of height
        int btnX = (w - btnWidth) / 2;
        int btnY = (h - btnHeight) * 3 / 4; // 75% down
        return new Rectangle(btnX, btnY, btnWidth, btnHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        // Optional: draw debug rect
        // g.setColor(new java.awt.Color(255, 0, 0, 100));
        // Rectangle r = getPlayButtonBounds();
        // g.fillRect(r.x, r.y, r.width, r.height);
    }
}
