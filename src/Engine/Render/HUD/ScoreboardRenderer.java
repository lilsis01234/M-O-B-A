package Engine.Render.HUD;

import Core.Entity.Player;
import Core.Moba.World.Arena;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScoreboardRenderer {
    private final Arena arena;
    private final Player player;
    private final int width;
    private long matchStartTime;
    private BufferedImage background;

    public ScoreboardRenderer(Arena arena, Player player, int x, int y, int width, int height) {
        this.arena = arena;
        this.player = player;
        this.width = width;
        this.matchStartTime = System.currentTimeMillis();
        this.background = HUDBackgrounds.getSmallPanelBackground(width, 65);
    }

    public void render(Graphics2D g2, int x, int y) {
        int padding = 3;
        int gap = 2;
        
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        int timerHeight = g2.getFontMetrics().getAscent() + 2;
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        int scoreHeight = g2.getFontMetrics().getAscent() + 2;
        
        int contentHeight = timerHeight + scoreHeight + scoreHeight + gap * 2;
        int totalHeight = padding * 2 + contentHeight;
        
        if (background == null || background.getHeight() != totalHeight) {
            background = HUDBackgrounds.getSmallPanelBackground(width, totalHeight);
        }
        
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, totalHeight);
        
        FlexContainer container = new FlexContainer()
            .setBounds(x, y, width, totalHeight)
            .padding(padding)
            .direction(FlexContainer.FlexDirection.COLUMN)
            .gap(gap);
        container.addItem(0, timerHeight);
        container.addItem(0, scoreHeight);
        container.addItem(0, scoreHeight);
        container.layout();
        
        long elapsed = System.currentTimeMillis() - matchStartTime;
        int minutes = (int) (elapsed / 60000);
        int seconds = (int) ((elapsed % 60000) / 1000);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        Rectangle timerBounds = container.getItem(0).bounds;
        g2.drawString(timerText, timerBounds.x + (timerBounds.width - fm.stringWidth(timerText)) / 2, timerBounds.y + fm.getAscent());

        g2.setColor(new Color(80, 180, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        Rectangle blueBounds = container.getItem(1).bounds;
        g2.drawString("Blue: " + arena.getBlueKills(), blueBounds.x + 2, blueBounds.y + g2.getFontMetrics().getAscent());

        g2.setColor(new Color(255, 80, 80));
        Rectangle redBounds = container.getItem(2).bounds;
        g2.drawString("Red: " + arena.getRedKills(), redBounds.x + 2, redBounds.y + g2.getFontMetrics().getAscent());
    }
    
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}