package Engine.Render;

import Core.Config;
import Core.Entity.Player;

import java.awt.*;

public class UIRenderer {

    private final Player player;

    public UIRenderer(Player player) {
        this.player = player;
    }

    public void render(Graphics2D g2) {
        drawFPS(g2);
        drawPlayerStatus(g2);
    }

    private void drawFPS(Graphics2D g2) {
        g2.setColor(Color.white);
        String fpsText = "FPS: " + (int) (1_000_000_000.0 / Config.getNanosecondsPerFrame());
        g2.drawString(fpsText, 10, 20);
    }

    private void drawPlayerStatus(Graphics2D g2) {
        if (!player.isAlive()) {
            drawRespawnTimer(g2);
        }

        if (player.isInFountain()) {
            drawFountainStatus(g2);
        }

        if (player.isOnEnemyWood()) {
            drawEnemyTerritoryWarning(g2);
        }
    }

    private void drawRespawnTimer(Graphics2D g2) {
        double timeLeft = player.getRespawnTimeRemaining();
        g2.setColor(Color.RED);
        String text = String.format("Respawn in: %.1f s", timeLeft);
        g2.drawString(text, 10, 40);
    }

    private void drawFountainStatus(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        g2.drawString("In Fountain - Healing/Mana Regen", 10, 80);
    }

    private void drawEnemyTerritoryWarning(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawString("ENEMY TERRITORY - Taking Damage!", 10, 100);
    }
}
