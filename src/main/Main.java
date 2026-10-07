package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import main.logic.BasicGravityLogic;
import main.logic.BasicMatchLogic;
import main.logic.BasicSpecialCandyLogic;
import main.logic.IGravityLogic;
import main.logic.IMatchLogic;
import main.logic.ISpecialCandyLogic;
import main.logic.SelectionController;
import main.ui.GamePanel;
import main.ui.StartPanel;
import main.ui.LevelSelectPanel;

public class Main {
    public static void main(String[] args) {
        IMatchLogic matchLogic = new BasicMatchLogic();
        IGravityLogic gravityLogic = new BasicGravityLogic();
        ISpecialCandyLogic specialCandyLogic = new BasicSpecialCandyLogic();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Candy Crush");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            javax.swing.JPanel cardPanel = new javax.swing.JPanel(new java.awt.CardLayout());

            GameManager gameManager = new GameManager(matchLogic, gravityLogic, specialCandyLogic);
            SelectionController selectionController = new SelectionController(gameManager,
                    gameManager.getBoard());

            GamePanel gamePanel = new GamePanel(gameManager, selectionController);
            gameManager.setRepaintCallback(() -> gamePanel.repaint());
            gamePanel.addMouseListener(new main.ui.InputHandler(selectionController));
            gamePanel.addMouseMotionListener(new main.ui.InputHandler(selectionController)); // In case we need motion

            Runnable showLevelSelect = () -> {
                java.awt.CardLayout cl = (java.awt.CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, "LEVEL_SELECT");
            };

            Runnable showStart = () -> {
                java.awt.CardLayout cl = (java.awt.CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, "START");
            };

            StartPanel startPanel = new StartPanel(showLevelSelect);

            LevelSelectPanel levelSelectPanel = new LevelSelectPanel((level) -> {
                main.model.LevelManager.getInstance().startLevel(level);
                gameManager.start();
                java.awt.CardLayout cl = (java.awt.CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, "GAME");
            }, showStart);

            gamePanel.setMenuCallback(showLevelSelect);

            cardPanel.add(startPanel, "START");
            cardPanel.add(levelSelectPanel, "LEVEL_SELECT");
            cardPanel.add(gamePanel, "GAME");

            frame.add(cardPanel);
            frame.setSize(800, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
