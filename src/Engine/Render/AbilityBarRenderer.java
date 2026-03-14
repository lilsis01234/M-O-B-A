package Engine.Render;

import Core.Entity.Player;

import java.awt.*;

public class AbilityBarRenderer {
    private final Player player;
    private final int x, y, width, height;

    public AbilityBarRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        int slotSize = 55;
        int slotCount = 4;
        int spacing = (width - slotCount * slotSize) / (slotCount + 1);
        int startX = x + spacing;

        for (int i = 0; i < slotCount; i++) {
            int slotX = startX + i * (slotSize + spacing);
            int slotY = y + 8;
            
            g2.setColor(new Color(40, 40, 60));
            g2.fillRoundRect(slotX, slotY, slotSize, slotSize, 6, 6);
            g2.setColor(new Color(100, 100, 130));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(slotX, slotY, slotSize, slotSize, 6, 6);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String key = (i + 1) + "";
            g2.drawString(key, slotX + 4, slotY + 16);

            if (i < 3 && player.getHero() != null) {
                var spells = player.getHero().getSpells();
                if (i < spells.size()) {
                    var spell = spells.get(i);
                    g2.setColor(new Color(180, 180, 200));
                    g2.setFont(new Font("Arial", Font.PLAIN, 8));
                    String name = spell.getName().length() > 7 ? spell.getName().substring(0, 7) : spell.getName();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(name, slotX + (slotSize - fm.stringWidth(name)) / 2, slotY + 45);
                }
            }
        }
    }
}