package Engine.Render.HUD;

import Core.Entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ItemBarRenderer {
    private final Player player;
    private int x, y;
    private final int width, height;
    private BufferedImage background;
    private BufferedImage slotBackground;

    public ItemBarRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(Graphics2D g2) {
        if (background == null) {
            background = HUDBackgrounds.getPanelBackground(width, height);
            slotBackground = HUDBackgrounds.getItemSlotBackground(28);
        }
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, height);

        FlexContainer container = new FlexContainer()
            .setBounds(x, y, width, height)
            .padding(4)
            .direction(FlexContainer.FlexDirection.COLUMN)
            .gap(3);
        container.addItem(0, 14);
        container.addItem(1f, 0);
        container.layout();
        
        Rectangle labelBounds = container.getItem(0).bounds;
        Rectangle slotsBounds = container.getItem(1).bounds;
        
        g2.setColor(new Color(200, 200, 220));
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("ITEMS", labelBounds.x + 4, labelBounds.y + 10);

        FlexContainer slotsContainer = new FlexContainer()
            .setBounds(slotsBounds.x, slotsBounds.y, slotsBounds.width, slotsBounds.height)
            .direction(FlexContainer.FlexDirection.ROW)
            .gap(3);
        
        for (int i = 0; i < 6; i++) {
            slotsContainer.addItem(28, 28);
        }
        slotsContainer.layout();

        for (int i = 0; i < 6; i++) {
            Rectangle slotBounds = slotsContainer.getItem(i).bounds;
            
            g2.drawImage(slotBackground, slotBounds.x, slotBounds.y, null);

            g2.setColor(new Color(150, 150, 170));
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            String num = (i + 1) + "";
            g2.drawString(num, slotBounds.x + 3, slotBounds.y + 10);
        }
    }
    
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}