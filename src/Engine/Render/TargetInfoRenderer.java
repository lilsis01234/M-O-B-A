package Engine.Render;

import Core.Entity.Player;

import java.awt.*;

public class TargetInfoRenderer {
    private final Player player;
    private final int x, y, width, height;

    public TargetInfoRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        if (!player.hasSelectedTarget()) return;

        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(x, y, width, height, 8, 8);
        g2.setColor(new Color(100, 100, 130));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 8, 8);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("TARGET", x + 8, y + 18);

        var target = player.getSelectedTarget();
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("Type: " + target.getClass().getSimpleName(), x + 8, y + 38);

        int hp = 100;
        int maxHp = 100;
        
        if (target instanceof Core.Moba.Units.Tour) {
            var tour = (Core.Moba.Units.Tour) target;
            hp = tour.stats().hp();
            maxHp = tour.stats().maxHp();
        } else if (target instanceof Core.Moba.Units.Ancient) {
            var ancient = (Core.Moba.Units.Ancient) target;
            hp = ancient.stats().hp();
            maxHp = ancient.stats().maxHp();
        }

        g2.setColor(new Color(200, 50, 50));
        g2.fillRect(x + 8, y + 48, width - 16, 8);
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(x + 8, y + 48, (int) ((double) hp / maxHp * (width - 16)), 8);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString(hp + "/" + maxHp, x + 8, y + 66);
    }
}