package Engine.Render;

import Core.Entity.Player;
import Core.Moba.World.Arena;

import java.awt.*;

public class ScoreboardRenderer {
    private final Arena arena;
    private final Player player;
    private final int x, y, width, height;
    private long matchStartTime;

    public ScoreboardRenderer(Arena arena, Player player, int x, int y, int width, int height) {
        this.arena = arena;
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.matchStartTime = System.currentTimeMillis();
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 6, 6);

        long elapsed = System.currentTimeMillis() - matchStartTime;
        int minutes = (int) (elapsed / 60000);
        int seconds = (int) ((elapsed % 60000) / 1000);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(timerText, x + (width - fm.stringWidth(timerText)) / 2, y + 18);

        g2.setColor(new Color(80, 180, 255));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        String blueScore = "Blue: " + arena.getBlueKills();
        g2.drawString(blueScore, x + 8, y + 38);

        g2.setColor(new Color(255, 80, 80));
        String redScore = "Red: " + arena.getRedKills();
        g2.drawString(redScore, x + 8, y + 52);
    }
}