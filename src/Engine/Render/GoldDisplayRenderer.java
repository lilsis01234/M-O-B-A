package Engine.Render;

import Core.Entity.Player;

import java.awt.*;

public class GoldDisplayRenderer {
    private final Player player;
    private final int x, y, width, height;

    public GoldDisplayRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(255, 200, 0, 100));
        g2.fillRoundRect(x, y, width, height, 4, 4);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String goldText = "" + player.getGold();
        g2.drawString(goldText, x + 8, y + 17);
        
        g2.setColor(new Color(180, 180, 100));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("GOLD", x + width - 35, y + 17);
    }
}