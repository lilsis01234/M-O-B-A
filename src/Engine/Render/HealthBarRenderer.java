package Engine.Render;

import Core.Moba.Combat.Stats;

import java.awt.Color;
import java.awt.Graphics2D;

public class HealthBarRenderer {

    public void draw(Graphics2D g2, int x, int y, int width, int height, Stats stats) {
        drawBackground(g2, x, y, width, height);
        if (stats != null) {
            drawHealthBar(g2, x, y, width, height, stats);
            drawManaBar(g2, x, y, width, height, stats);
        }
    }

    private void drawBackground(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(x, y, width, height);
    }

    private void drawHealthBar(Graphics2D g2, int x, int y, int width, int height, Stats stats) {
        double hpPercent = (double) stats.hp() / stats.maxHp();
        if (hpPercent > 0) {
            g2.setColor(Color.GREEN);
            g2.fillRect(x, y, (int) (width * hpPercent), height);
        }
    }

    private void drawManaBar(Graphics2D g2, int x, int y, int width, int height, Stats stats) {
        if (stats.maxMana() <= 0) return;
        
        int manaY = y + height + 2;
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(x, manaY, width, height);

        double manaPercent = (double) stats.mana() / stats.maxMana();
        if (manaPercent > 0) {
            g2.setColor(new Color(100, 100, 255));
            g2.fillRect(x, manaY, (int) (width * manaPercent), height);
        }
    }

    public void drawSimple(Graphics2D g2, int x, int y, int width, int height, double hpPercent) {
        g2.setColor(Color.RED);
        g2.fillRect(x, y, width, height);
        
        if (hpPercent > 0) {
            g2.setColor(Color.GREEN);
            g2.fillRect(x, y, (int) (width * hpPercent), height);
        }
    }
}
