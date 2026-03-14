package Engine.Render.HUD;

import Core.Entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GoldDisplayRenderer {
    private final Player player;
    private final int width;
    private BufferedImage background;

    public GoldDisplayRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.width = width;
    }

    public void render(Graphics2D g2, int x, int y) {
        int padding = 3;
        int gap = 8;
        
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        int contentHeight = g2.getFontMetrics().getAscent() + 2;
        int totalHeight = padding * 2 + contentHeight;
        
        if (background == null || background.getHeight() != totalHeight) {
            background = HUDBackgrounds.getSmallPanelBackground(width, totalHeight);
        }
        
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, totalHeight);
        
        FlexContainer container = new FlexContainer()
            .setBounds(x, y, width, totalHeight)
            .padding(padding)
            .direction(FlexContainer.FlexDirection.ROW)
            .gap(gap)
            .justifyContent(FlexContainer.JustifyContent.SPACE_BETWEEN);
        container.addItem(0, contentHeight);
        container.addItem(0, contentHeight);
        container.layout();
        
        Rectangle goldBounds = container.getItem(0).bounds;
        Rectangle labelBounds = container.getItem(1).bounds;
        
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("" + player.getGold(), goldBounds.x, goldBounds.y + g2.getFontMetrics().getAscent());
        
        g2.setColor(new Color(220, 220, 150));
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("GOLD", labelBounds.x + labelBounds.width - 35, labelBounds.y + g2.getFontMetrics().getAscent());
    }
    
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}