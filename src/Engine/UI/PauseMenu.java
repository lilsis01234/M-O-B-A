package Engine.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PauseMenu extends JPanel {

    public interface PauseMenuListener {
        void onResume();
        void onReturnToMain();
        void onSettings();
    }

    private PauseMenuListener listener;
    private List<MenuButton> buttons = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean isMenuVisible = false;  // Use different name to avoid confusion with Swing's visibility

    private final Color OVERLAY_BG = new Color(0, 0, 0, 180);
    private final Color MENU_BG = new Color(25, 25, 35);
    private final Color MENU_BORDER = new Color(80, 70, 60);
    private final Color ACCENT = new Color(180, 140, 90);
    private final Color ACCENT_BRIGHT = new Color(220, 180, 120);
    private final Color TEXT_MAIN = new Color(240, 230, 200);
    private final Color TEXT_DIM = new Color(160, 150, 130);
    private final Color BUTTON_BG = new Color(40, 35, 50);
    private final Color BUTTON_HOVER = new Color(60, 50, 70);
    private final Color BUTTON_BORDER = new Color(100, 90, 70);

    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 32);
    private final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 18);

    private String title = "PAUSE";
    private String[] menuItems = {"CONTINUER", "PARAMETRE", "RETOUR A L'ACCUEIL"};

    private Rectangle menuContainerBounds;
    private Rectangle titleBounds;
    private int lastMouseX = 0;
    private int lastMouseY = 0;

    public PauseMenu() {
        setOpaque(false);
        setFocusable(false);
        setLayout(null);
        setVisible(true);  // Keep Swing visibility true to be in container, but won't draw
        isMenuVisible = false;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isMenuVisible) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        navigateSelection(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        navigateSelection(1);
                        break;
                    case KeyEvent.VK_ENTER:
                        handleSelection();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        resume();
                        break;
                }
            }
        });
    }
    
    public void handleMouseClick(int x, int y) {
        if (isMenuVisible) {
            handleClick(x, y);
        }
    }
    
    public void handleMouseMove(int x, int y) {
        lastMouseX = x;
        lastMouseY = y;
        if (isMenuVisible) {
            updateHover(x, y);
        }
    }

    public void setPauseMenuListener(PauseMenuListener listener) {
        this.listener = listener;
    }

    public void show(int screenWidth, int screenHeight) {
        isMenuVisible = true;
        selectedIndex = 0;
        calculateLayout(screenWidth, screenHeight);
        requestFocusInWindow();
        repaint();
    }
    
    public void showAt(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        isMenuVisible = true;
        selectedIndex = 0;
        calculateLayout(screenWidth, screenHeight);
        updateHover(mouseX, mouseY);
        requestFocusInWindow();
        repaint();
    }

    public void hide() {
        isMenuVisible = false;
        for (MenuButton btn : buttons) {
            btn.hovered = false;
        }
        repaint();
    }

    public boolean isPauseMenuVisible() {
        return isMenuVisible;
    }
    
    public boolean isAnyButtonHovered() {
        for (MenuButton btn : buttons) {
            if (btn.hovered) return true;
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return isMenuVisible;
    }

    @Override
    public boolean contains(int x, int y) {
        return isMenuVisible && super.contains(x, y);
    }

    private void navigateSelection(int direction) {
        selectedIndex += direction;
        if (selectedIndex < 0) selectedIndex = menuItems.length - 1;
        if (selectedIndex >= menuItems.length) selectedIndex = 0;
        repaint();
    }

    private void handleSelection() {
        if (listener == null) return;

        switch (selectedIndex) {
            case 0 -> resume();
            case 1 -> {
                resume();
                listener.onSettings();
            }
            case 2 -> {
                resume();
                listener.onReturnToMain();
            }
        }
    }

    private void resume() {
        hide();
        if (listener != null) {
            listener.onResume();
        }
    }

    private void handleClick(int x, int y) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).bounds.contains(x, y)) {
                selectedIndex = i;
                handleSelection();
                return;
            }
        }
    }

    private void updateHover(int x, int y) {
        boolean changed = false;
        for (int i = 0; i < buttons.size(); i++) {
            boolean wasHovered = buttons.get(i).hovered;
            buttons.get(i).hovered = buttons.get(i).bounds.contains(x, y);
            if (wasHovered != buttons.get(i).hovered) changed = true;
        }
        if (changed) repaint();
    }

    public int getLastMouseX() {
        return lastMouseX;
    }
    
    public int getLastMouseY() {
        return lastMouseY;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (isMenuVisible) {
            calculateLayout(getWidth(), getHeight());
        }
    }

    private void calculateLayout(int w, int h) {
        if (w <= 0 || h <= 0) {
            w = getWidth();
            h = getHeight();
        }

        int menuWidth = 300;
        int menuHeight = 250;
        int menuX = (w - menuWidth) / 2;
        int menuY = (h - menuHeight) / 2;
        menuContainerBounds = new Rectangle(menuX, menuY, menuWidth, menuHeight);

        titleBounds = new Rectangle(menuX, menuY + 15, menuWidth, 50);

        buttons.clear();
        int btnWidth = 240;
        int btnHeight = 45;
        int spacing = 12;
        int totalHeight = menuItems.length * btnHeight + (menuItems.length - 1) * spacing;
        int startY = menuY + 70;

        for (int i = 0; i < menuItems.length; i++) {
            int x = menuX + (menuWidth - btnWidth) / 2;
            int y = startY + i * (btnHeight + spacing);
            buttons.add(new MenuButton(menuItems[i], new Rectangle(x, y, btnWidth, btnHeight)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isMenuVisible) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawOverlay(g2);
        drawMenuPanel(g2);
    }

    private void drawOverlay(Graphics2D g2) {
        g2.setColor(OVERLAY_BG);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawMenuPanel(Graphics2D g2) {
        if (menuContainerBounds == null) return;

        int x = menuContainerBounds.x;
        int y = menuContainerBounds.y;
        int w = menuContainerBounds.width;
        int h = menuContainerBounds.height;

        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(x + 6, y + 6, w, h);

        g2.setColor(MENU_BG);
        g2.fillRect(x, y, w, h);

        g2.setColor(MENU_BORDER);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, w - 1, h - 1);

        drawTitle(g2);
        drawButtons(g2);
    }

    private void drawTitle(Graphics2D g2) {
        if (titleBounds == null) return;

        g2.setColor(ACCENT);
        g2.setFont(TITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = titleBounds.x + (titleBounds.width - fm.stringWidth(title)) / 2;
        int textY = titleBounds.y + fm.getAscent() + 5;
        g2.drawString(title, textX, textY);

        g2.setColor(MENU_BORDER);
        g2.setStroke(new BasicStroke(1));
        int lineY = titleBounds.y + titleBounds.height - 5;
        g2.drawLine(titleBounds.x + 20, lineY, titleBounds.x + titleBounds.width - 20, lineY);
    }

    private void drawButtons(Graphics2D g2) {
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton btn = buttons.get(i);
            boolean isSelected = (i == selectedIndex);
            boolean isHovered = btn.hovered;

            drawPixelButton(g2, btn.bounds, btn.text, isSelected, isHovered);
        }
    }

    private void drawPixelButton(Graphics2D g2, Rectangle bounds, String text, boolean selected, boolean hovered) {
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        if (hovered) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(x + 4, y + 4, w, h);
        }

        Color bgColor = hovered ? BUTTON_HOVER : BUTTON_BG;
        g2.setColor(bgColor);
        g2.fillRect(x, y, w, h);

        g2.setColor(hovered ? ACCENT : BUTTON_BORDER);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, w - 1, h - 1);

        g2.setColor(hovered ? TEXT_MAIN : TEXT_DIM);
        g2.setFont(BUTTON_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (w - fm.stringWidth(text)) / 2;
        int textY = y + (h + fm.getAscent()) / 2 - 2;
        g2.drawString(text, textX, textY);
    }

    private static class MenuButton {
        String text;
        Rectangle bounds;
        boolean hovered;

        MenuButton(String text, Rectangle bounds) {
            this.text = text;
            this.bounds = bounds;
            this.hovered = false;
        }
    }
}
