package Engine;

import Engine.Render.Cache.HeroSpriteCache;
import Core.Database.JsonDataProvider;
import Core.Database.JsonDataProviderFactory;
import Core.Database.model.Hero;
import Core.Entity.Direction;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeroSelectionPanel extends JPanel implements MouseWheelListener {
    
public interface SelectionListener {
    void onHeroSelected(Hero hero);
    void onGoBack();
}
    
    private SelectionListener listener;
    private List<Hero> allHeroes;
    private List<Hero> filteredHeroes;
    private Hero selectedHero;
    private int selectedIndex = -1;
    
    // Category filtering
    private String[] categories = {"All", "Force", "Agilité", "Intelligence"};
    private String selectedCategory = "All";
    
    // Colors
    private final Color BACKGROUND = new Color(25, 25, 40);
    private final Color HEADER_BG = new Color(35, 35, 55, 230);
    private final Color CARD_BG = new Color(50, 50, 75);
    private final Color CARD_HOVER = new Color(70, 70, 100);
    private final Color CARD_SELECTED = new Color(90, 130, 200);
    private final Color CARD_BORDER = new Color(100, 100, 140);
    private final Color SELECTED_BORDER = new Color(255, 215, 0);
    private final Color TEXT_MAIN = new Color(240, 240, 240);
    private final Color TEXT_SECONDARY = new Color(180, 180, 200);
    private final Color STAT_POSITIVE = new Color(120, 230, 120);
    private final Color TAB_BG = new Color(45, 45, 65);
    private final Color TAB_SELECTED = new Color(60, 90, 140);
    private final Color TAB_HOVER = new Color(55, 55, 80);
    private final Color SCROLLBAR_BG = new Color(80, 80, 110, 200);
    private final Color SCROLLBAR_THUMB = new Color(150, 150, 190);
    
    // Layout zones (flexible with padding)
    private final int HEADER_HEIGHT = 140;
    private final int FOOTER_HEIGHT = 100;
    private final int VERTICAL_PADDING = 20;
    private final int HORIZONTAL_PADDING = 30;
    
    // Category tabs
    private final int TAB_HEIGHT = 30;
    private final int TAB_WIDTH = 120;
    private final int TAB_SPACING = 8;
    private final int TAB_Y_OFFSET = 60;
    
    // Card dimensions
    private int cardWidth = 200;
    private int cardHeight = 170;
    private int cardSpacing = 20;
    private final int SPRITE_SIZE = 56;
    
    // Fonts
    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private final Font FONT_CARD_NAME = new Font("SansSerif", Font.BOLD, 13);
    private final Font FONT_STAT = new Font("Monospaced", Font.BOLD, 11);
    private final Font FONT_DESC = new Font("SansSerif", Font.PLAIN, 10);
    private final Font FONT_TAB = new Font("SansSerif", Font.BOLD, 12);
    
    // Scrolling
    private int scrollY = 0;
    private final int SCROLL_SPEED = 60;
    
    // Layout bounds (computed)
    private Rectangle headerBounds;
    private Rectangle contentBounds;
    private Rectangle footerBounds;
    
    private HeroSpriteCache spriteCache;
    
    public HeroSelectionPanel(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(BACKGROUND);
        setFocusable(true);
        setOpaque(true);
        setVisible(true);
        
        spriteCache = new HeroSpriteCache();
        loadHeroes();
        filterHeroes();
        setupListeners();
    }
    
    private void loadHeroes() {
        try {
            JsonDataProvider dataProvider = JsonDataProviderFactory.create();
            allHeroes = dataProvider.getAllHeroes();
            System.out.println("DEBUG: Loaded " + allHeroes.size() + " heroes");
        } catch (IOException e) {
            e.printStackTrace();
            allHeroes = new ArrayList<>();
        }
    }
    
    public void handleMouseClick(int x, int y, int button) {
        handleClick(x, y, button);
    }
    
    public void handleMouseMove(int x, int y, java.awt.Component comp) {
        updateCursor(x, y);
        repaint();
    }
    
    public void handleMouseWheel(int rotation) {
        if (contentBounds == null) return;
        
        int maxScroll = calculateMaxScroll();
        scrollY += rotation * SCROLL_SPEED;
        if (scrollY < 0) scrollY = 0;
        if (maxScroll > 0 && scrollY > maxScroll) scrollY = maxScroll;
        repaint();
    }
    
    public void handleMouseExit(java.awt.Component comp) {
        comp.setCursor(Cursor.getDefaultCursor());
    }
    
    private void filterHeroes() {
        filteredHeroes = new ArrayList<>();
        for (Hero hero : allHeroes) {
            if (selectedCategory.equals("All")) {
                filteredHeroes.add(hero);
            } else {
                String heroCategory = switch (hero.getCategoryId()) {
                    case 1 -> "Force";
                    case 2 -> "Agilité";
                    case 3 -> "Intelligence";
                    default -> "Force";
                };
                if (heroCategory.equals(selectedCategory)) {
                    filteredHeroes.add(hero);
                }
            }
        }
        selectedIndex = -1;
        selectedHero = null;
        scrollY = 0;
    }
    
    private void setupListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyNav(e.getKeyCode());
            }
        });
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (contentBounds == null) return;
        
        int maxScroll = calculateMaxScroll();
        scrollY += e.getWheelRotation() * SCROLL_SPEED;
        if (scrollY < 0) scrollY = 0;
        if (maxScroll > 0 && scrollY > maxScroll) scrollY = maxScroll;
        repaint();
    }
    
    private void updateCursor(int x, int y) {
        if (headerBounds == null || footerBounds == null || contentBounds == null) {
            calculateLayoutBounds();
        }
        
        int backBtnW = 130;
        int backBtnH = 36;
        int backBtnX = 30;
        int backBtnY = footerBounds.y + (footerBounds.height - backBtnH) / 2;
        
        if (footerBounds != null && footerBounds.contains(x, y) && 
            x >= backBtnX && x <= backBtnX + backBtnW && y >= backBtnY && y <= backBtnY + backBtnH) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }
        
        // Check header tabs - only if inside header bounds
        if (headerBounds != null && headerBounds.contains(x, y)) {
            if (y >= headerBounds.height - TAB_Y_OFFSET && y <= headerBounds.height - TAB_Y_OFFSET + TAB_HEIGHT) {
                int totalTabWidth = categories.length * TAB_WIDTH + (categories.length - 1) * TAB_SPACING;
                int startX = headerBounds.x + (headerBounds.width - totalTabWidth) / 2;
                int tabY = headerBounds.y + headerBounds.height - TAB_Y_OFFSET;
                
                for (int i = 0; i < categories.length; i++) {
                    int tabLeft = startX + i * (TAB_WIDTH + TAB_SPACING);
                    if (x >= tabLeft && x <= tabLeft + TAB_WIDTH) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
            }
        }
        
        // Check confirm button (only if in footer bounds)
        if (footerBounds != null && footerBounds.contains(x, y) && selectedHero != null) {
            String btnText = "▶ START GAME WITH " + selectedHero.getName().toUpperCase();
            Font btnFont = FONT_TAB.deriveFont(Font.BOLD, 16);
            FontMetrics fm = getFontMetrics(btnFont);
            int btnW = Math.min(getWidth() - 60 - 170, fm.stringWidth(btnText) + 50);
            int btnH = 44;
            int btnX = (getWidth() - btnW) / 2;
            int btnY = footerBounds.y + (footerBounds.height - btnH) / 2;
            
            if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        
        // Check hero cards only in content bounds
        if (contentBounds != null && contentBounds.contains(x, y) && !filteredHeroes.isEmpty()) {
            int cols = calculateColumns();
            int totalWidth = cols * cardWidth + (cols - 1) * cardSpacing;
            int startX = contentBounds.x + (contentBounds.width - totalWidth) / 2;
            
            int maxScroll = calculateMaxScroll();
            int effectiveY = (int) (y - contentBounds.y + scrollY);
            
            int row = effectiveY / (cardHeight + cardSpacing);
            int col = (x - startX) / (cardWidth + cardSpacing);
            
            if (col >= 0 && col < cols && row >= 0) {
                int index = row * cols + col;
                if (index >= 0 && index < filteredHeroes.size()) {
                    int cardX = startX + col * (cardWidth + cardSpacing);
                    int cardY = contentBounds.y + effectiveY - (effectiveY % (cardHeight + cardSpacing)) - scrollY;
                    if (x >= cardX && x <= cardX + cardWidth && y >= cardY && y <= cardY + cardHeight) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
            }
        }
        
        setCursor(Cursor.getDefaultCursor());
    }
    
    private void handleClick(int x, int y, int button) {
        if (button != MouseEvent.BUTTON1) return;
        
        // Ensure layout bounds are calculated (they're calculated in paintComponent)
        if (headerBounds == null || footerBounds == null || contentBounds == null) {
            calculateLayoutBounds();
        }
        
        // Check back button (in footer bounds)
        if (footerBounds != null) {
            String backText = "◀ BACK TO MENU";
            Font btnFont = FONT_TAB.deriveFont(Font.BOLD, 14);
            FontMetrics fm = getFontMetrics(btnFont);
            int backBtnW = fm.stringWidth(backText) + 40;
            int backBtnH = 36;
            int backBtnX = 30;
            int backBtnY = footerBounds.y + (footerBounds.height - backBtnH) / 2;
            
            if (x >= backBtnX && x <= backBtnX + backBtnW && y >= backBtnY && y <= backBtnY + backBtnH) {
                if (listener != null) {
                    listener.onGoBack();
                }
                return;
            }
        }
        
        // Check tabs (in header bounds)
        if (headerBounds != null && headerBounds.contains(x, y)) {
            String backText = "◀ BACK TO MENU";
            Font btnFont = FONT_TAB.deriveFont(Font.BOLD, 14);
            FontMetrics fm = getFontMetrics(btnFont);
            int backBtnW = fm.stringWidth(backText) + 40;
            int backBtnH = 36;
            int backBtnX = 30;
            int backBtnY = footerBounds.y + (footerBounds.height - backBtnH) / 2;
            
            if (x >= backBtnX && x <= backBtnX + backBtnW && y >= backBtnY && y <= backBtnY + backBtnH) {
                if (listener != null) {
                    listener.onGoBack();
                }
                return;
            }
        }
        
        // Check tabs (in header bounds)
        if (headerBounds != null && headerBounds.contains(x, y)) {
            if (y >= headerBounds.height - TAB_Y_OFFSET && y <= headerBounds.height - TAB_Y_OFFSET + TAB_HEIGHT) {
                int totalTabWidth = categories.length * TAB_WIDTH + (categories.length - 1) * TAB_SPACING;
                int startX = headerBounds.x + (headerBounds.width - totalTabWidth) / 2;
                int tabY = headerBounds.y + headerBounds.height - TAB_Y_OFFSET;
                
                for (int i = 0; i < categories.length; i++) {
                    int tabLeft = startX + i * (TAB_WIDTH + TAB_SPACING);
                    if (x >= tabLeft && x <= tabLeft + TAB_WIDTH) {
                        selectedCategory = categories[i];
                        scrollY = 0;
                        filterHeroes();
                        repaint();
                        return;
                    }
                }
            }
        }
        
        // Check confirm button (in footer bounds)
        if (footerBounds != null && footerBounds.contains(x, y) && selectedHero != null) {
            String btnText = "▶ START GAME WITH " + selectedHero.getName().toUpperCase();
            Font btnFont = FONT_TAB.deriveFont(Font.BOLD, 16);
            FontMetrics fm = getFontMetrics(btnFont);
            int btnW = Math.min(getWidth() - 60 - 170, fm.stringWidth(btnText) + 50);
            int btnH = 44;
            int btnX = (getWidth() - btnW) / 2;
            int btnY = footerBounds.y + (footerBounds.height - btnH) / 2;
            
            if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
                if (listener != null) {
                    listener.onHeroSelected(selectedHero);
                }
                return;
            }
        }
        
        // Check hero cards (only in content bounds)
        if (contentBounds == null || filteredHeroes.isEmpty()) return;
        
        if (!contentBounds.contains(x, y)) return;
        
        int cols = calculateColumns();
        int totalWidth = cols * cardWidth + (cols - 1) * cardSpacing;
        int startX = contentBounds.x + (contentBounds.width - totalWidth) / 2;
        
        int effectiveY = (int) (y - contentBounds.y + scrollY);
        int row = effectiveY / (cardHeight + cardSpacing);
        int col = (x - startX) / (cardWidth + cardSpacing);
        
        if (col >= 0 && col < cols && row >= 0) {
            int index = row * cols + col;
            if (index >= 0 && index < filteredHeroes.size()) {
                int cardX = startX + col * (cardWidth + cardSpacing);
                int cardY = contentBounds.y + effectiveY - (effectiveY % (cardHeight + cardSpacing)) - scrollY;
                if (x >= cardX && x <= cardX + cardWidth && y >= cardY && y <= cardY + cardHeight) {
                    selectedIndex = index;
                    selectedHero = filteredHeroes.get(index);
                    repaint();
                }
            }
        }
    }
    
    private void handleKeyNav(int keyCode) {
        if (filteredHeroes.isEmpty()) return;
        
        int cols = calculateColumns();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (selectedIndex - cols >= 0) {
                    selectedIndex -= cols;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    ensureVisible(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (selectedIndex + cols < filteredHeroes.size()) {
                    selectedIndex += cols;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    ensureVisible(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (selectedIndex > 0) {
                    selectedIndex--;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    ensureVisible(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (selectedIndex < filteredHeroes.size() - 1) {
                    selectedIndex++;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    ensureVisible(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_ENTER:
                if (selectedHero != null && listener != null) {
                    listener.onHeroSelected(selectedHero);
                }
                break;
        }
    }
    
    private void ensureVisible(int index) {
        if (contentBounds == null) return;
        
        int cols = calculateColumns();
        int cardTotalHeight = cardHeight + cardSpacing;
        
        int row = index / cols;
        int visibleRows = contentBounds.height / cardTotalHeight;
        int firstVisibleRow = scrollY / cardTotalHeight;
        int lastVisibleRow = firstVisibleRow + visibleRows;
        
        if (row < firstVisibleRow) {
            scrollY = row * cardTotalHeight;
        } else if (row >= lastVisibleRow) {
            scrollY = (row - visibleRows + 1) * cardTotalHeight;
        }
        
        int maxScroll = calculateMaxScroll();
        if (scrollY > maxScroll) scrollY = maxScroll;
        if (scrollY < 0) scrollY = 0;
    }
    
    private void calculateLayoutBounds() {
        int w = getWidth();
        int h = getHeight();
        
        // Header: full width at top
        headerBounds = new Rectangle(0, 0, w, HEADER_HEIGHT);
        
        // Footer: full width at bottom
        footerBounds = new Rectangle(0, h - FOOTER_HEIGHT, w, FOOTER_HEIGHT);
        
        // Content: between header and footer with padding
        int contentY = HEADER_HEIGHT + VERTICAL_PADDING;
        int contentHeight = h - HEADER_HEIGHT - FOOTER_HEIGHT - VERTICAL_PADDING * 2;
        contentBounds = new Rectangle(HORIZONTAL_PADDING, contentY, w - HORIZONTAL_PADDING * 2, contentHeight);
    }
    
    private int calculateColumns() {
        if (contentBounds == null) return 1;
        int availableWidth = contentBounds.width - 40; // Extra margin
        return Math.max(1, availableWidth / (cardWidth + cardSpacing));
    }
    
    private int calculateMaxScroll() {
        if (filteredHeroes.isEmpty() || contentBounds == null) return 0;
        
        int cols = calculateColumns();
        int rows = (int) Math.ceil((double) filteredHeroes.size() / cols);
        int cardTotalHeight = cardHeight + cardSpacing;
        int gridHeight = rows * cardTotalHeight - cardSpacing;
        
        if (gridHeight <= contentBounds.height) return 0;
        return gridHeight - contentBounds.height;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Calculate layout bounds
        calculateLayoutBounds();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background
        drawBackground(g2);
        
        // Draw header area with clipping
        drawHeader(g2);
        
        // Draw content area with clipping
        drawHeroGrid(g2);
        
        // Draw footer area
        drawFooter(g2);
    }
    
    private void drawBackground(Graphics2D g2) {
        GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND, 0, getHeight(), new Color(15, 15, 25));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawHeader(Graphics2D g2) {
        if (headerBounds == null) return;
        
        // Save original clip
        Shape originalClip = g2.getClip();
        
        // Set clipping to header bounds
        g2.setClip(headerBounds);
        
        // Header background with gradient
        GradientPaint headerGradient = new GradientPaint(
            0, headerBounds.y, HEADER_BG,
            0, headerBounds.y + headerBounds.height, new Color(25, 25, 40, 200)
        );
        g2.setPaint(headerGradient);
        g2.fillRect(headerBounds.x, headerBounds.y, headerBounds.width, headerBounds.height);
        
        // Header bottom separator
        g2.setColor(new Color(100, 100, 140));
        g2.drawLine(headerBounds.x, headerBounds.y + headerBounds.height - 1, 
                   headerBounds.x + headerBounds.width, headerBounds.y + headerBounds.height - 1);
        
        // Title - centered in header
        g2.setColor(TEXT_MAIN);
        g2.setFont(FONT_TITLE);
        FontMetrics fm = g2.getFontMetrics();
        String title = "SELECT YOUR HERO";
        int titleX = headerBounds.x + (headerBounds.width - fm.stringWidth(title)) / 2;
        int titleY = headerBounds.y + 40;
        g2.drawString(title, titleX, titleY);
        
        // Draw tabs
        drawCategoryTabs(g2);
        
        // Restore original clip
        g2.setClip(originalClip);
    }
    
    private void drawCategoryTabs(Graphics2D g2) {
        if (headerBounds == null) return;
        
        Point mouse = getMousePosition();
        boolean mouseInHeader = mouse != null && headerBounds.contains(mouse);
        
        int totalTabWidth = categories.length * TAB_WIDTH + (categories.length - 1) * TAB_SPACING;
        int startX = headerBounds.x + (headerBounds.width - totalTabWidth) / 2;
        int tabY = headerBounds.y + headerBounds.height - TAB_Y_OFFSET;
        
        for (int i = 0; i < categories.length; i++) {
            int x = startX + i * (TAB_WIDTH + TAB_SPACING);
            boolean isSelected = categories[i].equals(selectedCategory);
            boolean isHover = mouseInHeader && 
                mouse.x >= x && mouse.x <= x + TAB_WIDTH &&
                mouse.y >= tabY && mouse.y <= tabY + TAB_HEIGHT;
            
            Color bgColor = isSelected ? TAB_SELECTED : (isHover ? TAB_HOVER : TAB_BG);
            g2.setColor(bgColor);
            g2.fillRoundRect(x, tabY, TAB_WIDTH, TAB_HEIGHT, 8, 8);
            
            g2.setColor(isSelected ? SELECTED_BORDER : CARD_BORDER);
            g2.setStroke(isSelected ? new BasicStroke(2) : new BasicStroke(1));
            g2.drawRoundRect(x, tabY, TAB_WIDTH, TAB_HEIGHT, 8, 8);
            
            g2.setColor(TEXT_MAIN);
            g2.setFont(FONT_TAB);
            FontMetrics fm = g2.getFontMetrics();
            String cat = categories[i];
            int textX = x + (TAB_WIDTH - fm.stringWidth(cat)) / 2;
            int textY = tabY + 20;
            g2.drawString(cat, textX, textY);
        }
    }
    
    private void drawHeroGrid(Graphics2D g2) {
        if (contentBounds == null || filteredHeroes.isEmpty()) return;
        
        // Save original clip
        Shape originalClip = g2.getClip();
        
        // Set clipping to content bounds (prevents drawing outside this area)
        g2.setClip(contentBounds);
        
        int cols = calculateColumns();
        int totalWidth = cols * cardWidth + (cols - 1) * cardSpacing;
        int startX = contentBounds.x + (contentBounds.width - totalWidth) / 2;
        
        int rows = (int) Math.ceil((double) filteredHeroes.size() / cols);
        int cardTotalHeight = cardHeight + cardSpacing;
        int gridHeight = rows * cardTotalHeight - cardSpacing;
        boolean scrolling = gridHeight > contentBounds.height;
        
        // Draw scrollbar if needed (outside clipping region since it's on the edge)
        if (scrolling) {
            int scrollbarX = contentBounds.x + contentBounds.width - 12;
            int scrollbarY = contentBounds.y;
            int scrollbarHeight = contentBounds.height;
            
            g2.setColor(SCROLLBAR_BG);
            g2.fillRect(scrollbarX, scrollbarY, 6, scrollbarHeight);
            
            int maxScroll = calculateMaxScroll();
            if (maxScroll > 0) {
                float thumbHeight = Math.max(30, (float)((double)contentBounds.height / gridHeight * scrollbarHeight));
                float thumbY = scrollbarY + (float)scrollY / maxScroll * (scrollbarHeight - thumbHeight);
                g2.setColor(SCROLLBAR_THUMB);
                g2.fillRect(scrollbarX, (int)thumbY, 6, (int)thumbHeight);
            }
        }
        
        // Determine visible range using proper formula
        int firstRow = scrolling ? (int)Math.floor((double)scrollY / cardTotalHeight) : 0;
        int visibleRows = contentBounds.height / cardTotalHeight + 1;
        int lastRow = Math.min(rows, firstRow + visibleRows);
        
        // Draw cards
        for (int row = firstRow; row < lastRow; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                if (index >= filteredHeroes.size()) break;
                
                int x = startX + col * (cardWidth + cardSpacing);
                int y = contentBounds.y + row * cardTotalHeight - scrollY;
                
                // Skip if not in visible area (extra check)
                if (y + cardHeight < contentBounds.y || y > contentBounds.y + contentBounds.height) {
                    continue;
                }
                
                drawCard(g2, filteredHeroes.get(index), x, y, index == selectedIndex);
            }
        }
        
        // Restore original clip
        g2.setClip(originalClip);
    }
    
    private void drawCard(Graphics2D g2, Hero hero, int x, int y, boolean selected) {
        // Card shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(x + 4, y + 4, cardWidth, cardHeight, 12, 12);
        
        // Card background
        g2.setColor(selected ? CARD_SELECTED : CARD_BG);
        g2.fillRoundRect(x, y, cardWidth, cardHeight, 12, 12);
        
        // Card border
        g2.setColor(selected ? SELECTED_BORDER : CARD_BORDER);
        g2.setStroke(selected ? new BasicStroke(2) : new BasicStroke(1));
        g2.drawRoundRect(x, y, cardWidth, cardHeight, 12, 12);
        
        // Sprite
        BufferedImage sprite = spriteCache.getSprite(hero, Direction.DOWN, 1);
        if (sprite != null) {
            int spriteSize = 48;
            int spriteX = x + (cardWidth - spriteSize) / 2;
            int spriteY = y + 12;
            g2.drawImage(sprite, spriteX, spriteY, spriteSize, spriteSize, null);
        }
        
        // Hero name
        g2.setColor(TEXT_MAIN);
        g2.setFont(FONT_CARD_NAME);
        FontMetrics fm = g2.getFontMetrics();
        String name = hero.getName();
        int nameX = x + (cardWidth - fm.stringWidth(name)) / 2;
        int nameY = y + 72;
        g2.drawString(name, nameX, nameY);
        
        // Stats row
        g2.setFont(FONT_STAT);
        fm = g2.getFontMetrics();
        int statsY = nameY + 22;
        int statsX = x + 15;
        int colWidth = (cardWidth - 30) / 3;
        
        String hpText = "HP: " + hero.getMaxHp();
        String atkText = "ATK: " + hero.getAttack();
        String defText = "DEF: " + hero.getDefense();
        
        g2.setColor(STAT_POSITIVE);
        g2.drawString(hpText, statsX, statsY);
        g2.drawString(atkText, statsX + colWidth, statsY);
        g2.drawString(defText, statsX + colWidth * 2, statsY);
        
        // Description
        g2.setFont(FONT_DESC);
        g2.setColor(TEXT_SECONDARY);
        String desc = hero.getHistory();
        if (desc != null && !desc.isEmpty()) {
            desc = desc.length() > 100 ? desc.substring(0, 100) + "..." : desc;
            drawWrappedString(g2, desc, x + 12, statsY + 18, cardWidth - 24, 12);
        }
        
        // Category badge
        String category = switch (hero.getCategoryId()) {
            case 1 -> "FORCE";
            case 2 -> "AGILITE";
            case 3 -> "INTELLIGENCE";
            default -> "FORCE";
        };
        g2.setFont(FONT_TAB.deriveFont(Font.BOLD, 10));
        fm = g2.getFontMetrics();
        int catW = fm.stringWidth(category) + 12;
        int catX = x + cardWidth - catW - 8;
        int catY = y + 10;
        
        g2.setColor(new Color(80, 80, 120, 200));
        g2.fillRoundRect(catX, catY, catW, 18, 6, 6);
        g2.setColor(TEXT_MAIN);
        g2.drawString(category, catX + 6, catY + 13);
    }
    
    private void drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String testLine = line + word + " ";
            if (fm.stringWidth(testLine) > maxWidth && line.length() > 0) {
                g2.drawString(line.toString().trim(), x, y);
                line = new StringBuilder(word + " ");
                y += lineHeight;
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString().trim(), x, y);
        }
    }
    
    private void drawFooter(Graphics2D g2) {
        if (footerBounds == null) return;
        
        // Footer background
        GradientPaint footerGradient = new GradientPaint(
            0, footerBounds.y, new Color(35, 35, 55),
            0, footerBounds.y + footerBounds.height, new Color(25, 25, 40)
        );
        g2.setPaint(footerGradient);
        g2.fillRect(footerBounds.x, footerBounds.y, footerBounds.width, footerBounds.height);
        
        // Footer top separator
        g2.setColor(new Color(100, 100, 140));
        g2.drawLine(footerBounds.x, footerBounds.y, footerBounds.x + footerBounds.width, footerBounds.y);
        
        // Back button (always visible, left side)
        String backText = "◀ BACK TO MENU";
        Font backFont = FONT_TAB.deriveFont(Font.BOLD, 14);
        FontMetrics fmBack = getFontMetrics(backFont);
        int backBtnW = fmBack.stringWidth(backText) + 40;
        int backBtnH = 36;
        int backBtnX = 30;
        int backBtnY = footerBounds.y + (footerBounds.height - backBtnH) / 2;
        
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(backBtnX + 3, backBtnY + 3, backBtnW, backBtnH, 10, 10);
        
        g2.setColor(new Color(150, 100, 100));
        g2.fillRoundRect(backBtnX, backBtnY, backBtnW, backBtnH, 10, 10);
        
        g2.setColor(new Color(200, 140, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(backBtnX, backBtnY, backBtnW, backBtnH, 10, 10);
        
        g2.setColor(new Color(255, 230, 230));
        g2.setFont(backFont);
        fmBack = getFontMetrics(backFont);
        int textX = backBtnX + (backBtnW - fmBack.stringWidth(backText)) / 2;
        int textY = backBtnY + (backBtnH + fmBack.getAscent()) / 2 - 2;
        g2.drawString(backText, textX, textY);
        
        // Start button
        if (selectedHero != null) {
            String btnText = "▶ START GAME WITH " + selectedHero.getName().toUpperCase();
            Font btnFont = FONT_TAB.deriveFont(Font.BOLD, 16);
            FontMetrics fm = getFontMetrics(btnFont);
            int btnW = Math.min(getWidth() - 60 - backBtnW - 60, fm.stringWidth(btnText) + 50);
            int btnH = 44;
            int btnX = (getWidth() - btnW) / 2;
            int btnY = footerBounds.y + (footerBounds.height - btnH) / 2;
            
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(btnX + 4, btnY + 4, btnW, btnH, 12, 12);
            
            GradientPaint btnGrad = new GradientPaint(btnX, btnY, new Color(60, 180, 60),
                    btnX, btnY + btnH, new Color(40, 120, 40));
            g2.setPaint(btnGrad);
            g2.fillRoundRect(btnX, btnY, btnW, btnH, 12, 12);
            
            g2.setColor(new Color(120, 255, 120));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(btnX, btnY, btnW, btnH, 12, 12);
            
            g2.setColor(new Color(255, 255, 220));
            g2.setFont(btnFont);
            fm = getFontMetrics(btnFont);
            int textX2 = btnX + (btnW - fm.stringWidth(btnText)) / 2;
            int textY2 = btnY + (btnH + fm.getAscent()) / 2 - 2;
            g2.drawString(btnText, textX2, textY2);
        }
    }
    
    public Hero getSelectedHero() {
        return selectedHero;
    }
    
    public void setSelectionListener(SelectionListener listener) {
        this.listener = listener;
    }
}