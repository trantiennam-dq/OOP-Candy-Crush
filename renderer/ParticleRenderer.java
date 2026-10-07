package main.ui.renderer;

import main.animation.IParticleEffect;
import main.animation.Particle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;

public class ParticleRenderer {
    public static void render(IParticleEffect effect, Graphics g, int offsetX, int offsetY, int cellSize) {
        Graphics2D g2d = (Graphics2D) g.create();
        for (Particle p : effect.getParticles()) {
            if (p.life > 0) {
                float alpha = p.life / p.maxLife;
                // Add a small fade-in/fade-out
                if (alpha > 1.0f) alpha = 1.0f;
                if (alpha < 0.0f) alpha = 0.0f;
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(p.color);
                
                int px = offsetX + (int)(p.x * cellSize);
                int py = offsetY + (int)(p.y * cellSize);
                int pSize = (int)(p.size * cellSize);
                
                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(px, py);
                g2d.rotate(Math.toRadians(p.rotation));
                
                // Draw pixel chunk (square centered)
                g2d.fillRect(-pSize / 2, -pSize / 2, pSize, pSize);
                
                g2d.setTransform(oldTransform);
            }
        }
        g2d.dispose();
    }
}
