package Engine.Render.HUD;

import java.awt.*;

public class PauseButtonRenderer {

    private int x;
    private int y;
    private final int width = 32;
    private final int height = 32;
    private boolean hovered = false;

    private final Color BUTTON_BG = new Color(40, 35, 50, 220);
    private final Color BUTTON_HOVER = new Color(60, 50, 70, 230);
    private final Color BUTTON_BORDER = new Color(100, 90, 70);
    private final Color ICON_COLOR = new Color(220, 200, 160);
    private final Color ICON_HOVER = new Color(255, 230, 180);

    public PauseButtonRenderer() {
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        Color bgColor = hovered ? BUTTON_HOVER : BUTTON_BG;
        g2.setColor(bgColor);
        g2.fillRect(x, y, width, height);

        g2.setColor(BUTTON_BORDER);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width - 1, height - 1);

        drawPauseIcon(g2);
    }

    private void drawPauseIcon(Graphics2D g2) {
        Color iconColor = hovered ? ICON_HOVER : ICON_COLOR;
        g2.setColor(iconColor);

        int barWidth = 4;
        int barHeight = 14;
        int barSpacing = 4;
        int totalWidth = barWidth * 2 + barSpacing;
        int startX = x + (width - totalWidth) / 2;
        int startY = y + (height - barHeight) / 2;

        g2.fillRect(startX, startY, barWidth, barHeight);
        g2.fillRect(startX + barWidth + barSpacing, startY, barWidth, barHeight);
    }

    public boolean handleClick(int clickX, int clickY) {
        return clickX >= x && clickX <= x + width &&
               clickY >= y && clickY <= y + height;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void updateHover(int mouseX, int mouseY) {
        this.hovered = mouseX >= x && mouseX <= x + width &&
                       mouseY >= y && mouseY <= y + height;
    }
    
    public void resetHover() {
        this.hovered = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
