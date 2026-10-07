package main.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class LevelSelectPanel extends JPanel {
    private Image bgImage;
    private Consumer<Integer> onLevelSelected;
    private Runnable onBack;

    public LevelSelectPanel(Consumer<Integer> onLevelSelected, Runnable onBack) {
        this.onLevelSelected = onLevelSelected;
        this.onBack = onBack;
        this.bgImage = AssetManager.getInstance().getImage("background_level_select.png");
        
        setLayout(new java.awt.BorderLayout());
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 15));
        topPanel.setOpaque(false);
        
        javax.swing.JButton backBtn = new javax.swing.JButton("Back to Menu");
        backBtn.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        backBtn.setBackground(new java.awt.Color(200, 50, 50));
        backBtn.setForeground(java.awt.Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        topPanel.add(backBtn);
        add(topPanel, java.awt.BorderLayout.NORTH);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 1; i <= 3; i++) {
                    if (getLevelButtonBounds(i).contains(e.getPoint())) {
                        if (onLevelSelected != null) {
                            onLevelSelected.accept(i);
                        }
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean hovering = false;
                for (int i = 1; i <= 3; i++) {
                    if (getLevelButtonBounds(i).contains(e.getPoint())) {
                        hovering = true;
                        break;
                    }
                }
                setCursor(hovering ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });
    }

    private Rectangle getLevelButtonBounds(int level) {
        int w = getWidth();
        int h = getHeight();
        int btnWidth = w / 5;
        int btnHeight = h / 5;
        int spacing = w / 10;
        
        // Center horizontally
        int totalWidth = (btnWidth * 3) + (spacing * 2);
        int startX = (w - totalWidth) / 2;
        int btnY = h / 2; // Roughly center vertically
        
        int btnX = startX + (level - 1) * (btnWidth + spacing);
        return new Rectangle(btnX, btnY, btnWidth, btnHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        main.model.LevelManager lm = main.model.LevelManager.getInstance();
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 1; i <= 3; i++) {
            int score = lm.getHighScore(i);
            int target = lm.getTargetScoreForLevel(i);
            int stars = main.model.LevelManager.calculateStars(score, target);
            
            Rectangle r = getLevelButtonBounds(i);
            
            // Draw score
            String scoreText = "Score: " + score;
            java.awt.FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(scoreText);
            
            g.setColor(new java.awt.Color(0, 0, 0, 150));
            g.fillRect(r.x + (r.width - textWidth) / 2 - 10, r.y - 45, textWidth + 20, 30);
            
            g.setColor(java.awt.Color.WHITE);
            g.drawString(scoreText, r.x + (r.width - textWidth) / 2, r.y - 23);
            
            // Cover background stars
            int starsY = r.y + r.height + 5;
            g.setColor(new java.awt.Color(0, 0, 0, 180));
            g.fillRoundRect(r.x - 10, starsY, r.width + 20, 40, 20, 20);
            
            // Draw custom stars
            int cx = r.x + r.width / 2;
            int cy = starsY + 20;
            int spacing = 35;
            
            for (int s = 0; s < 3; s++) {
                int starCx = cx + (s - 1) * spacing;
                boolean isEarned = s < stars;
                g2d.setColor(isEarned ? java.awt.Color.YELLOW : java.awt.Color.GRAY);
                drawStar(g2d, starCx, cy, 14, isEarned);
                if (!isEarned) {
                    g2d.setColor(java.awt.Color.LIGHT_GRAY);
                    drawStar(g2d, starCx, cy, 14, false); // Draw outline for empty star
                }
            }
        }
    }

    private void drawStar(java.awt.Graphics2D g2d, int cx, int cy, int radius, boolean filled) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        double angle = -Math.PI / 2;
        for (int i = 0; i < 10; i++) {
            double rad = (i % 2 == 0) ? radius : radius / 2.5;
            xPoints[i] = cx + (int) (Math.cos(angle) * rad);
            yPoints[i] = cy + (int) (Math.sin(angle) * rad);
            angle += Math.PI / 5;
        }
        if (filled) {
            g2d.fillPolygon(xPoints, yPoints, 10);
        } else {
            g2d.setStroke(new java.awt.BasicStroke(2f));
            g2d.drawPolygon(xPoints, yPoints, 10);
        }
    }
}
