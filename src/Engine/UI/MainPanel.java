package Engine.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    public interface MainMenuListener {
        void onStartGame();
        void onSettings();
        void onExit();
    }

    private MainMenuListener listener;
    private List<MenuButton> buttons = new ArrayList<>();
    private int selectedIndex = 0;

    private final Color BACKGROUND_DARK = new Color(20, 20, 30);
    private final Color BACKGROUND_LIGHT = new Color(30, 30, 45);
    private final Color ACCENT = new Color(180, 140, 90);
    private final Color ACCENT_BRIGHT = new Color(220, 180, 120);
    private final Color TEXT_MAIN = new Color(240, 230, 200);
    private final Color TEXT_DIM = new Color(160, 150, 130);
    private final Color BUTTON_BG = new Color(40, 35, 50);
    private final Color BUTTON_HOVER = new Color(60, 50, 70);
    private final Color BUTTON_BORDER = new Color(100, 90, 70);

    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 48);
    private final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 20);
    private final Font VERSION_FONT = new Font("Monospaced", Font.PLAIN, 12);

    private String gameTitle = "MOBA";
    private String version = "v0.0.1";
    private String[] menuItems = {"START GAME", "SETTINGS", "EXIT"};

    private Rectangle titleBounds;
    private Rectangle menuBounds;
    private Rectangle footerBounds;

    public MainPanel(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(BACKGROUND_DARK);
        setFocusable(true);
        setLayout(null);

        setupInputListeners();
    }

    private void setupInputListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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
                }
            }
        });
    }
    
    public void handleMouseClick(int x, int y) {
        handleClick(x, y);
    }
    
    public void handleMouseMove(int x, int y, java.awt.Component comp) {
        updateHover(x, y);
        if (isAnyButtonHovered()) {
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    public void handleMouseExit(java.awt.Component comp) {
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        repaint();
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
            case 0 -> listener.onStartGame();
            case 1 -> listener.onSettings();
            case 2 -> listener.onExit();
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

    public void setMenuListener(MainMenuListener listener) {
        this.listener = listener;
    }
    
    public boolean isAnyButtonHovered() {
        for (MenuButton btn : buttons) {
            if (btn.hovered) return true;
        }
        return false;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        calculateLayout();
    }

    private void calculateLayout() {
        int w = getWidth();
        int h = getHeight();

        titleBounds = new Rectangle(0, h / 4, w, 100);
        menuBounds = new Rectangle(0, h / 2 - 50, w, 150);
        footerBounds = new Rectangle(0, h - 40, w, 40);

        buttons.clear();
        int btnWidth = 220;
        int btnHeight = 50;
        int spacing = 15;
        int totalHeight = menuItems.length * btnHeight + (menuItems.length - 1) * spacing;
        int startY = menuBounds.y + (menuBounds.height - totalHeight) / 2;

        for (int i = 0; i < menuItems.length; i++) {
            int x = (w - btnWidth) / 2;
            int y = startY + i * (btnHeight + spacing);
            buttons.add(new MenuButton(menuItems[i], new Rectangle(x, y, btnWidth, btnHeight)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawBackground(g2);
        drawTitle(g2);
        drawMenuButtons(g2);
        drawFooter(g2);
    }

    private void drawBackground(Graphics2D g2) {
        GradientPaint gradient = new GradientPaint(
            0, 0, BACKGROUND_DARK,
            0, getHeight(), new Color(10, 10, 20)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(40, 35, 50, 100));
        int lineSpacing = 60;
        for (int y = 0; y < getHeight(); y += lineSpacing) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawTitle(Graphics2D g2) {
        if (titleBounds == null) return;

        g2.setColor(ACCENT);
        g2.setFont(TITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();

        int x = (getWidth() - fm.stringWidth(gameTitle)) / 2;
        int y = titleBounds.y + fm.getAscent() + 20;
        g2.drawString(gameTitle, x, y);

        g2.setColor(ACCENT_BRIGHT);
        g2.setStroke(new BasicStroke(2));
        int underlineY = y + 5;
        g2.drawLine(x - 20, underlineY, x + fm.stringWidth(gameTitle) + 20, underlineY);
    }

    private void drawMenuButtons(Graphics2D g2) {
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton btn = buttons.get(i);
            boolean isHovered = btn.hovered;

            drawPixelButton(g2, btn.bounds, btn.text, isHovered);
        }
    }

    private void drawPixelButton(Graphics2D g2, Rectangle bounds, String text, boolean hovered) {
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

    private void drawFooter(Graphics2D g2) {
        if (footerBounds == null) return;

        g2.setColor(TEXT_DIM);
        g2.setFont(VERSION_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(version)) / 2;
        int y = footerBounds.y + fm.getAscent() + 10;
        g2.drawString(version, x, y);
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
