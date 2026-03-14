package Engine.Render.HUD;

import Core.Entity.Player;

import java.awt.*;

public class BuffRenderer {
    private final Player player;
    private final int x, y, width, height;

    public BuffRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 180));
        g2.fillRoundRect(x, y, width, height, 6, 6);
        
        g2.setColor(new Color(180, 180, 200));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("BUFFS", x + 8, y + 12);
        
        if (player.isInFountain()) {
            g2.setColor(Color.CYAN);
            g2.fillRect(x + 8, y + 18, 16, 16);
            g2.setFont(new Font("Arial", Font.PLAIN, 7));
            g2.drawString("HEAL", x + 10, y + 28);
        }
        
        if (player.isOnEnemyWood()) {
            g2.setColor(new Color(200, 50, 50));
            g2.fillRect(x + 30, y + 18, 16, 16);
            g2.setFont(new Font("Arial", Font.PLAIN, 7));
            g2.drawString("DMG", x + 32, y + 28);
        }
    }
}