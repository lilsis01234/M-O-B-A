package Engine.Render;

import Core.Config;

import java.awt.*;

public class RenderingUtils {

    public static void drawCenteredImage(Graphics2D g2, Image image, int x, int y, int width, int height) {
        if (image != null) {
            g2.drawImage(image, x - width / 2, y - height / 2, width, height, null);
        }
    }

    public static void drawTextWithOutline(Graphics2D g2, String text, int x, int y, Color textColor, Color outlineColor) {
        g2.setColor(outlineColor);
        g2.drawString(text, x - 1, y);
        g2.drawString(text, x + 1, y);
        g2.drawString(text, x, y - 1);
        g2.drawString(text, x, y + 1);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    public static void drawCenteredText(Graphics2D g2, String text, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, x - textWidth / 2, y);
    }

    public static Color getTeamColor(boolean isBlue) {
        return isBlue ? new Color(0x42, 0x99, 0xe1) : new Color(0xf5, 0x65, 0x65);
    }

    public static void enableAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static int getTileCenterX(int tileX) {
        return tileX * Config.getTileSize() + Config.getTileSize() / 2;
    }

    public static int getTileCenterY(int tileY) {
        return tileY * Config.getTileSize() + Config.getTileSize() / 2;
    }

    private RenderingUtils() {}
}
