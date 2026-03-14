package Engine.Render.HUD;

import Core.Entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TargetInfoRenderer {
    private final Player player;///joueur selectionne la cible
    private final int width, height;//dmension du panneau 
    private BufferedImage background;//img fond

    public TargetInfoRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y) {
        // Position is passed in render()
    }

    public void render(Graphics2D g2, int x, int y) {
        //position generer dans render
        if (!player.hasSelectedTarget()) return;

        if (background == null) {
            background = HUDBackgrounds.getPanelBackground(width, height);
        }
        
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, height);
  // container vertical pour organiser titres type et barre hp
        FlexContainer container = new FlexContainer()
            .setBounds(x, y, width, height)
            .padding(8)
            .direction(FlexContainer.FlexDirection.COLUMN)
            .gap(4);
        container.addItem(0, 20);
        container.addItem(0, 18);
        container.addItem(0, 14);
        container.addItem(0, 16);
        container.layout();

        Rectangle titleBounds = container.getItem(0).bounds;
        Rectangle typeBounds = container.getItem(1).bounds;
        Rectangle hpBarBounds = container.getItem(2).bounds;
        Rectangle hpTextBounds = container.getItem(3).bounds;
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString("TARGET", titleBounds.x, titleBounds.y + g2.getFontMetrics().getAscent());

        var target = player.getSelectedTarget();
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Type: " + target.getClass().getSimpleName(), typeBounds.x, typeBounds.y + g2.getFontMetrics().getAscent());

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
        g2.fillRect(hpBarBounds.x, hpBarBounds.y, hpBarBounds.width, hpBarBounds.height);
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(hpBarBounds.x, hpBarBounds.y, (int) ((double) hp / maxHp * hpBarBounds.width), hpBarBounds.height);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString(hp + "/" + maxHp, hpTextBounds.x, hpTextBounds.y + g2.getFontMetrics().getAscent());
    }
    //bordure 3d du panneau
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}