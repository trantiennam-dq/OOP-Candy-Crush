package main.ui;

import main.logic.SelectionController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputHandler extends MouseAdapter {
    private SelectionController selectionController;

    public InputHandler(SelectionController selectionController) {
        this.selectionController = selectionController;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (main.model.LevelManager.getInstance().isGameOver()) {
            return;
        }
        int col = (e.getX() - GamePanel.OFFSET_X) / GamePanel.CELL_SIZE;
        int row = (e.getY() - GamePanel.OFFSET_Y) / GamePanel.CELL_SIZE;
        
        selectionController.selectCandy(row, col);
    }
}
