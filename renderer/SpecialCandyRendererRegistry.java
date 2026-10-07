package main.ui.renderer;

import main.model.enums.SpecialType;
import java.awt.Color;
import java.awt.Graphics;
import java.util.EnumMap;
import java.util.Map;

public class SpecialCandyRendererRegistry {
    private static SpecialCandyRendererRegistry instance;
    private final Map<SpecialType, ISpecialEffectRenderer> renderers = new EnumMap<>(SpecialType.class);

    private SpecialCandyRendererRegistry() {
        renderers.put(SpecialType.STRIPED_HORIZONTAL, (g, x, y, cellSize, padding) -> {
            g.setColor(Color.WHITE);
            g.fillRect(x + padding, y + cellSize / 2 - 2, cellSize - 2 * padding, 4);
        });

        renderers.put(SpecialType.STRIPED_VERTICAL, (g, x, y, cellSize, padding) -> {
            g.setColor(Color.WHITE);
            g.fillRect(x + cellSize / 2 - 2, y + padding, 4, cellSize - 2 * padding);
        });

        renderers.put(SpecialType.WRAPPED, (g, x, y, cellSize, padding) -> {
            g.setColor(Color.WHITE);
            g.drawRect(x + padding + 5, y + padding + 5, cellSize - 2 * padding - 10, cellSize - 2 * padding - 10);
            g.drawRect(x + padding + 7, y + padding + 7, cellSize - 2 * padding - 14, cellSize - 2 * padding - 14);
        });
        renderers.put(SpecialType.COLOR_BOMB, (g, x, y, cellSize, padding) -> {
            g.setColor(Color.BLACK);
            g.fillOval(x + padding + 5, y + padding + 5, cellSize - 2 * padding - 10, cellSize - 2 * padding - 10);
        });
    }

    public static SpecialCandyRendererRegistry getInstance() {
        if (instance == null) {
            instance = new SpecialCandyRendererRegistry();
        }
        return instance;
    }

    public void render(SpecialType type, Graphics g, int x, int y, int cellSize, int padding) {
        ISpecialEffectRenderer renderer = renderers.get(type);
        if (renderer != null) {
            renderer.draw(g, x, y, cellSize, padding);
        }
    }
    
    public void registerRenderer(SpecialType type, ISpecialEffectRenderer renderer) {
        renderers.put(type, renderer);
    }
}
