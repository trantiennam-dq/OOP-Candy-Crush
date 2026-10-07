package main.ui.renderer;

import java.awt.Graphics;

public interface ISpecialEffectRenderer {
    void draw(Graphics g, int x, int y, int cellSize, int padding);
}
