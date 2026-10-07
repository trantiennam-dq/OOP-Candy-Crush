package main.ui;

import main.GameManager;
import main.model.Board;
import main.model.Candy;
import main.model.ScoreManager;
import main.logic.SelectionController;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import main.ui.renderer.SpecialCandyRendererRegistry;

public class GamePanel extends JPanel {
    private GameManager gameManager;
    private ScoreManager scoreManager = ScoreManager.getInstance();
    private SelectionController selectionController;
    private Runnable menuCallback;
    public static int CELL_SIZE = 60;
    public static int OFFSET_X = 160;
    public static int OFFSET_Y = 85;

    public GamePanel(GameManager gameManager, SelectionController selectionController) {
        this.gameManager = gameManager;
        this.selectionController = selectionController;
        
        setLayout(new java.awt.BorderLayout());
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 15));
        topPanel.setOpaque(false);
        
        javax.swing.JButton backBtn = new javax.swing.JButton("Back to Levels");
        backBtn.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        backBtn.setBackground(new java.awt.Color(200, 50, 50));
        backBtn.setForeground(java.awt.Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            if (menuCallback != null) menuCallback.run();
        });
        topPanel.add(backBtn);
        add(topPanel, java.awt.BorderLayout.NORTH);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (main.model.LevelManager.getInstance().isGameOver()) {
                    if (menuCallback != null) {
                        menuCallback.run();
                    }
                }
            }
        });
    }

    public void setMenuCallback(Runnable callback) {
        this.menuCallback = callback;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        main.model.LevelManager lm = main.model.LevelManager.getInstance();
        int level = lm.getCurrentLevel();
        String bgName = "background_level" + level + (level == 3 ? ".jpg" : ".png");
        java.awt.Image bg = AssetManager.getInstance().getImage(bgName);
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(new Color(40, 40, 40));
        }

        Board board = gameManager.getBoard();
        if (board != null) {
            int boardWidth = board.getCols() * CELL_SIZE;
            int boardHeight = board.getRows() * CELL_SIZE;
            OFFSET_X = (getWidth() - boardWidth) / 2;
            OFFSET_Y = (getHeight() - boardHeight) / 2 + 20; // +20 for top bar padding
        }

        drawBoard(g);
        drawCandies(g);
        drawAnimations(g);
        drawSelector(g);
        drawScore(g);
    }

    private void drawBoard(Graphics g) {
        Board board = gameManager.getBoard();
        if (board == null)
            return;

        // Draw transparent board background
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(OFFSET_X, OFFSET_Y, board.getCols() * CELL_SIZE, board.getRows() * CELL_SIZE);
        
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                g.setColor(new Color(255, 255, 255, 30));
                g.drawRect(OFFSET_X + c * CELL_SIZE, OFFSET_Y + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawCandies(Graphics g) {
        Board board = gameManager.getBoard();
        if (board == null)
            return;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Candy candy = board.getCandy(r, c);
                if (candy != null) {
                    drawCandy(g, candy);
                }
            }
        }
    }

    private void drawCandy(Graphics g, Candy candy) {
        int x = OFFSET_X + (int) (candy.getVisualX() * CELL_SIZE);
        int y = OFFSET_Y + (int) (candy.getVisualY() * CELL_SIZE);
        int padding = 8;

        g.setColor(candy.getType().getColor());

        g.fillOval(x + padding, y + padding, CELL_SIZE - 2 * padding, CELL_SIZE - 2 * padding);

        // Reflection for glossy look
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(x + padding + 5, y + padding + 5, 15, 10);

        SpecialCandyRendererRegistry.getInstance().render(candy.getSpecialType(), g, x, y, CELL_SIZE, padding);
    }

    private void drawSelector(Graphics g) {
        int r = selectionController.getSelectedRow();
        int c = selectionController.getSelectedCol();
        if (r != -1 && c != -1) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(OFFSET_X + c * CELL_SIZE, OFFSET_Y + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g2d.setStroke(new BasicStroke(1));
        }
    }

    private void drawAnimations(Graphics g) {
        if (gameManager.getAnimationSystem() == null) return;
        
        // Copy list to avoid ConcurrentModificationException during rendering
        java.util.List<main.animation.Animation> animations = new java.util.ArrayList<>(gameManager.getAnimationSystem().getAnimations());
        for (main.animation.Animation anim : animations) {
            if (anim instanceof main.animation.IParticleEffect) {
                main.ui.renderer.ParticleRenderer.render((main.animation.IParticleEffect) anim, g, OFFSET_X, OFFSET_Y, CELL_SIZE);
            }
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        main.model.LevelManager lm = main.model.LevelManager.getInstance();
        g.drawString("Level: " + lm.getCurrentLevel() + " | Moves: " + lm.getMovesLeft(), 10, 25);
        g.drawString("Score: " + scoreManager.getScore() + " / " + lm.getTargetScore(), 10, 50);

        int currentScore = scoreManager.getScore();
        int stars = main.model.LevelManager.calculateStars(currentScore, lm.getTargetScore());
        
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background for stars so it's readable
        int starPanelWidth = 140;
        int starPanelHeight = 45;
        int panelX = (getWidth() - starPanelWidth) / 2;
        int panelY = 10;
        g.setColor(new java.awt.Color(0, 0, 0, 150));
        g.fillRoundRect(panelX, panelY, starPanelWidth, starPanelHeight, 20, 20);
        
        // Draw custom stars
        int cx = getWidth() / 2;
        int cy = panelY + starPanelHeight / 2;
        int spacing = 40;
        
        for (int s = 0; s < 3; s++) {
            int starCx = cx + (s - 1) * spacing;
            boolean isEarned = s < stars;
            g2d.setColor(isEarned ? java.awt.Color.YELLOW : java.awt.Color.GRAY);
            drawStar(g2d, starCx, cy, 16, isEarned);
            if (!isEarned) {
                g2d.setColor(java.awt.Color.LIGHT_GRAY);
                drawStar(g2d, starCx, cy, 16, false); // Outline
            }
        }

        if (lm.isGameOver()) {
            String resultImageName = lm.isGameWon() ? 
                (lm.getCurrentLevel() == 1 ? "result_complete.png" : "result_complete_level" + lm.getCurrentLevel() + ".png") : 
                (lm.getCurrentLevel() == 1 ? "result_lose.png" : "result_lose_level" + lm.getCurrentLevel() + ".png");
            
            java.awt.Image popup = AssetManager.getInstance().getImage(resultImageName);
            if (popup != null) {
                // Dim background
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                // Draw popup centered
                int pw = (int)(getWidth() * 0.8);
                int ph = (int)(getHeight() * 0.8);
                int px = (getWidth() - pw) / 2;
                int py = (getHeight() - ph) / 2;
                g.drawImage(popup, px, py, pw, ph, this);
            } else {
                if (lm.isGameWon()) {
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.GREEN);
                    g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
                    g.drawString("YOU WIN! Click to return", getWidth() / 2 - 150, getHeight() / 2);
                } else {
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.RED);
                    g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
                    g.drawString("GAME OVER! Click to return", getWidth() / 2 - 180, getHeight() / 2);
                }
            }
        } else if (lm.isGameWon() && lm.getMovesLeft() > 0) {
            g.setColor(new Color(255, 105, 180)); // Màu hồng nhạt
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 36));
            g.drawString("SUGAR CRUSH!", getWidth() / 2 - 130, getHeight() / 2);
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
