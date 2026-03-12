package Engine;

import Core.Database.dao.HeroDAO;
import Core.Database.model.Hero;
import Core.Entity.Direction;
import Engine.Render.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class HeroSelectionPanel extends JPanel {
    
    public interface SelectionListener {
        void onHeroSelected(Hero hero);
    }
    
    private SelectionListener listener;
    private List<Hero> heroes;
    private Hero selectedHero;
    private int selectedIndex = -1;
    
    private Font PIXEL_FONT;
    // ... rest of fields stay the same
    private final Color BACKGROUND_COLOR = new Color(30, 30, 45);
    private final Color CARD_COLOR = new Color(50, 50, 75);
    private final Color CARD_HOVER_COLOR = new Color(70, 70, 100);
    private final Color CARD_SELECTED_COLOR = new Color(90, 120, 180);
    private final Color CARD_SELECTED_BORDER = new Color(255, 215, 0); // Gold border for selected
    private final Color TEXT_COLOR = new Color(240, 240, 240);
    private final Color STAT_COLOR = new Color(120, 220, 120);
    private final Color BORDER_COLOR = new Color(100, 100, 140);
    private final Color BUTTON_COLOR = new Color(60, 150, 60);
    private final Color BUTTON_BORDER = new Color(100, 230, 100);
    private final Color BUTTON_TEXT = new Color(255, 255, 200);
    
    // Responsive card dimensions - smaller to fit many heroes
    private static final int CARD_WIDTH = 160;
    private static final int CARD_HEIGHT = 100;
    private static final int CARD_SPACING = 8;
    private static final int SPRITE_SIZE = 40;
    
    private HeroSpriteCache spriteCache;
    
    public HeroSelectionPanel(Dimension screenSize) {
        // No longer storing fixed dimensions - use getWidth()/getHeight() dynamically
        setPreferredSize(screenSize);
        setBackground(BACKGROUND_COLOR);
        
        // Create pixel-style font
        PIXEL_FONT = new Font("Courier New", Font.BOLD, 14);
        
        // Load heroes from database
        loadHeroes();
        
        // Create sprite cache
        spriteCache = new HeroSpriteCache();
        
        // Setup mouse listener
        setupMouseListener();
    }
    
    public void setSelectionListener(SelectionListener listener) {
        this.listener = listener;
    }
    
    private void handleSelectionConfirm() {
        if (selectedHero != null && listener != null) {
            listener.onHeroSelected(selectedHero);
        }
    }
    
    private void loadHeroes() {
        try {
            HeroDAO heroDAO = new HeroDAO();
            heroes = heroDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            heroes = new ArrayList<>();
        }
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                repaint();
            }
        });
        
        // Keyboard navigation
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }
    
    private void handleKeyPress(int keyCode) {
        int cols = calculateColumns();
        if (heroes.isEmpty()) return;
        
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_UP:
                if (selectedIndex >= cols) {
                    selectedIndex -= cols;
                    selectedHero = heroes.get(selectedIndex);
                    repaint();
                }
                break;
            case java.awt.event.KeyEvent.VK_DOWN:
                if (selectedIndex < heroes.size() - cols) {
                    selectedIndex += cols;
                    selectedHero = heroes.get(selectedIndex);
                    repaint();
                }
                break;
            case java.awt.event.KeyEvent.VK_LEFT:
                if (selectedIndex % cols > 0) {
                    selectedIndex--;
                    selectedHero = heroes.get(selectedIndex);
                    repaint();
                }
                break;
            case java.awt.event.KeyEvent.VK_RIGHT:
                if (selectedIndex % cols < cols - 1 && selectedIndex < heroes.size() - 1) {
                    selectedIndex++;
                    selectedHero = heroes.get(selectedIndex);
                    repaint();
                }
                break;
            case java.awt.event.KeyEvent.VK_ENTER:
                if (selectedHero != null) {
                    handleSelectionConfirm();
                }
                break;
        }
    }
    
    private void handleMouseClick(int mouseX, int mouseY) {
        int cols = calculateColumns();
        int startX = (getWidth() - (cols * CARD_WIDTH + (cols - 1) * CARD_SPACING)) / 2;
        int startY = 80;
        
        for (int i = 0; i < heroes.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cardX = startX + col * (CARD_WIDTH + CARD_SPACING);
            int cardY = startY + row * (CARD_HEIGHT + CARD_SPACING);
            
            if (mouseX >= cardX && mouseX <= cardX + CARD_WIDTH &&
                mouseY >= cardY && mouseY <= cardY + CARD_HEIGHT) {
                selectedIndex = i;
                selectedHero = heroes.get(i);
                repaint();
                System.out.println("Selected hero: " + selectedHero.getName());
                break;
            }
        }
    }
    
    private int calculateColumns() {
        int width = getWidth();
        return Math.max(1, width / (CARD_WIDTH + CARD_SPACING));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Enable antialiasing for cleaner text
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(PIXEL_FONT);
        
        drawTitle(g2);
        drawHeroCards(g2);
        
        if (selectedHero != null) {
            drawSelectButton(g2);
        }
    }
    
    private void drawTitle(Graphics2D g2) {
        String title = "CHOOSE YOUR HERO";
        g2.setColor(new Color(255, 215, 0)); // Gold
        g2.setFont(PIXEL_FONT.deriveFont(Font.BOLD, 28));
        
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (getWidth() - titleWidth) / 2;
        int y = 50;
        
        // Draw title with shadow
        g2.setColor(Color.BLACK);
        g2.drawString(title, x + 2, y + 2);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(title, x, y);
        
        // Draw instruction
        if (selectedHero == null) {
            g2.setColor(new Color(200, 200, 220));
            g2.setFont(PIXEL_FONT.deriveFont(12));
            FontMetrics fm2 = g2.getFontMetrics();
            String instruction = "Click a hero or use arrow keys to select";
            int instX = (getWidth() - fm2.stringWidth(instruction)) / 2;
            g2.drawString(instruction, instX, y + 25);
        } else {
            g2.setColor(new Color(180, 255, 180));
            g2.setFont(PIXEL_FONT.deriveFont(12));
            FontMetrics fm2 = g2.getFontMetrics();
            String selectedText = "Selected: " + selectedHero.getName();
            int selX = (getWidth() - fm2.stringWidth(selectedText)) / 2;
            g2.drawString(selectedText, selX, y + 25);
        }
    }
    
    private void drawHeroCards(Graphics2D g2) {
        int cols = calculateColumns();
        int rows = (int) Math.ceil((double) heroes.size() / cols);
        
        // Calculate total grid height
        int totalGridHeight = rows * (CARD_HEIGHT + CARD_SPACING) - CARD_SPACING;
        
        // Calculate startX to center horizontally
        int startX = (getWidth() - (cols * CARD_WIDTH + (cols - 1) * CARD_SPACING)) / 2;
        
        // Calculate startY: try to center vertically, but leave room for title/button
        int topMargin = 80;
        int bottomMargin = 80;
        int availableHeight = getHeight() - topMargin - bottomMargin;
        int startY;
        if (totalGridHeight <= availableHeight) {
            // Center the grid
            startY = topMargin + (availableHeight - totalGridHeight) / 2;
        } else {
            // Not enough space, start at topMargin
            startY = topMargin;
        }
        
        for (int i = 0; i < heroes.size(); i++) {
            Hero hero = heroes.get(i);
            int col = i % cols;
            int row = i / cols;
            int cardX = startX + col * (CARD_WIDTH + CARD_SPACING);
            int cardY = startY + row * (CARD_HEIGHT + CARD_SPACING);
            
            drawCard(g2, hero, cardX, cardY, i == selectedIndex);
        }
    }
    
    private void drawCard(Graphics2D g2, Hero hero, int x, int y, boolean isSelected) {
        // Card background with subtle gradient effect
        if (isSelected) {
            g2.setColor(CARD_SELECTED_COLOR);
        } else {
            g2.setColor(CARD_HOVER_COLOR);
        }
        g2.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        
        // Card border - thicker and gold for selected
        if (isSelected) {
            g2.setColor(CARD_SELECTED_BORDER);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1));
        }
        g2.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        
        // Draw hero sprite
        BufferedImage sprite = spriteCache.getSprite(hero, Direction.DOWN, 1);
        if (sprite != null) {
            int spriteSize = SPRITE_SIZE;
            int spriteX = x + (CARD_WIDTH - spriteSize) / 2;
            int spriteY = y + 8;
            g2.drawImage(sprite, spriteX, spriteY, spriteSize, spriteSize, null);
        }
        
        // Draw hero name
        String name = hero.getName().toUpperCase();
        g2.setColor(TEXT_COLOR);
        g2.setFont(PIXEL_FONT.deriveFont(Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int nameWidth = fm.stringWidth(name);
        int nameX = x + (CARD_WIDTH - nameWidth) / 2;
        int nameY = y + SPRITE_SIZE + 14;
        g2.drawString(name, nameX, nameY);
        
        // Draw compact stats (HP, ATK, DEF only)
        drawCompactStats(g2, hero, x + 10, nameY + 10);
        
        // Draw short description (2 lines max)
        drawShortDescription(g2, hero, x + 10, nameY + 24, CARD_WIDTH - 20);
        
        // Draw selection indicator
        if (isSelected) {
            drawSelectionIndicator(g2, x, y);
        }
    }
    
    private void drawSelectionIndicator(Graphics2D g2, int x, int y) {
        // Draw a small gold star/check in top-right corner
        int starSize = 8;
        int starX = x + CARD_WIDTH - starSize - 4;
        int starY = y + 4;
        
        g2.setColor(CARD_SELECTED_BORDER);
        g2.fillOval(starX, starY, starSize, starSize);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 6));
        g2.drawString("✓", starX + 2, starY + 6);
    }
    
    private void drawCompactStats(Graphics2D g2, Hero hero, int x, int y) {
        g2.setFont(PIXEL_FONT.deriveFont(10));
        g2.setColor(STAT_COLOR);
        // Line 1: HP and ATK
        g2.drawString("HP:" + hero.getMaxHp(), x, y);
        g2.drawString("ATK:" + hero.getAttack(), x + 70, y);
        // Line 2: DEF
        g2.drawString("DEF:" + hero.getDefense(), x, y + 12);
    }
    
    private void drawShortDescription(Graphics2D g2, Hero hero, int x, int y, int maxWidth) {
        g2.setColor(new Color(180, 180, 200));
        g2.setFont(PIXEL_FONT.deriveFont(9));
        String history = hero.getHistory();
        // Truncate very long text
        if (history.length() > 80) {
            history = history.substring(0, 77) + "...";
        }
        
        // Simple word wrap, max 2 lines
        String[] words = history.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = 11;
        int lines = 0;
        
        for (String word : words) {
            if (lines >= 2) break;
            String testLine = line.length() > 0 ? line + " " + word : word;
            int lineWidth = g2.getFontMetrics().stringWidth(testLine);
            if (lineWidth > maxWidth && line.length() > 0) {
                g2.drawString(line.toString(), x, y + lines * lineHeight);
                line = new StringBuilder(word);
                lines++;
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0 && lines < 2) {
            g2.drawString(line.toString(), x, y + lines * lineHeight);
        }
    }
    
    private void drawSelectButton(Graphics2D g2) {
        String buttonText = "▶ START GAME";
        g2.setFont(PIXEL_FONT.deriveFont(Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int buttonWidth = fm.stringWidth(buttonText) + 50;
        int buttonHeight = 36;
        int buttonX = (getWidth() - buttonWidth) / 2;
        int buttonY = getHeight() - 70;
        
        // Button background with gradient effect
        g2.setColor(BUTTON_COLOR);
        g2.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
        
        // Button border
        g2.setColor(BUTTON_BORDER);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
        
        // Button highlight (top line)
        g2.setColor(new Color(150, 255, 150));
        g2.drawLine(buttonX + 2, buttonY + 2, buttonX + buttonWidth - 3, buttonY + 2);
        
        // Button shadow (bottom line)
        g2.setColor(new Color(40, 100, 40));
        g2.drawLine(buttonX + 2, buttonY + buttonHeight - 2, buttonX + buttonWidth - 3, buttonY + buttonHeight - 2);
        
        // Button text
        g2.setColor(BUTTON_TEXT);
        int textX = buttonX + (buttonWidth - fm.stringWidth(buttonText)) / 2;
        int textY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
        g2.drawString(buttonText, textX, textY);
    }
    
    public Hero getSelectedHero() {
        return selectedHero;
    }
    
    public boolean isHeroSelected() {
        return selectedHero != null;
    }
    
    private class HeroSpriteCache {
        private final java.util.Map<String, BufferedImage> cache = new java.util.HashMap<>();
        
        public BufferedImage getSprite(Hero hero, Direction direction, int frame) {
            // Try to get from cache
            String key = hero.getId() + "_" + direction + "_" + frame;
            BufferedImage cached = cache.get(key);
            if (cached != null) {
                return cached;
            }
            
            // Compose sprite
            BufferedImage composite = composeHeroSprite(hero, direction, frame);
            if (composite != null) {
                cache.put(key, composite);
            }
            return composite;
        }
        
        private BufferedImage composeHeroSprite(Hero hero, Direction direction, int frame) {
            try {
                // Load base character
                BufferedImage base = loadCharacterPart(
                    "src/Resource/Characters/MetroCity/CharacterModel/Character Model.png",
                    hero.getCharacterRow(), direction, frame
                );
                if (base == null) return null;
                
                // Load hair
                BufferedImage hair = loadCharacterPart(
                    "src/Resource/Characters/MetroCity/Hair/Hairs.png",
                    hero.getHairRow(), direction, frame
                );
                
                // Determine if using suit or outfit
                boolean useSuit = hero.getSuitRow() != null;
                BufferedImage outfitOrSuit;
                
                if (useSuit) {
                    outfitOrSuit = loadCharacterPart(
                        "src/Resource/Characters/MetroCity/Outfits/Suit.png",
                        hero.getSuitRow(), direction, frame
                    );
                } else {
                    outfitOrSuit = loadOutfit(hero.getOutfitFile(), direction, frame);
                }
                
                // Composite images in order: base -> outfit/suit -> hair (if no headwear)
                BufferedImage composite = new BufferedImage(
                    base.getWidth(), base.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g = composite.createGraphics();
                g.drawImage(base, 0, 0, null);
                
                if (outfitOrSuit != null) {
                    g.drawImage(outfitOrSuit, 0, 0, null);
                }
                
                // Draw hair only if suit doesn't have headwear
                if (hair != null && (!useSuit || (useSuit && !suitHasHeadwear(hero.getSuitRow())))) {
                    g.drawImage(hair, 0, 0, null);
                }
                
                g.dispose();
                return composite;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        private BufferedImage loadCharacterPart(String path, int row, Direction direction, int frame) {
            try {
                BufferedImage sheet = ImageIO.read(new File(path));
                int spriteSize = 32;
                
                // Bounds checking
                int maxRows = sheet.getHeight() / spriteSize;
                if (row < 0 || row >= maxRows) {
                    System.err.println("Warning: Row " + row + " out of bounds for " + path + " (max rows: " + maxRows + "), using row 0");
                    row = 0; // clamp to 0
                }
                
                int colStart = getColumnOffset(direction);
                int x = (colStart + (frame - 1)) * spriteSize;
                int y = row * spriteSize;
                
                // Additional check for x bounds
                if (x + spriteSize > sheet.getWidth()) {
                    System.err.println("Warning: Column out of bounds for " + path + " at row " + row + ", dir " + direction);
                    return null;
                }
                
                return sheet.getSubimage(x, y, spriteSize, spriteSize);
            } catch (IOException e) {
                System.err.println("Failed to load: " + path + " row " + row);
                return null;
            }
        }
        
        private BufferedImage loadOutfit(String outfitFile, Direction direction, int frame) {
            try {
                // Map the old outfit name to one of the 6 available Outfit#.png files
                String mappedOutfit = mapToAvailableOutfit(outfitFile);
                String path = "src/Resource/Characters/MetroCity/Outfits/" + mappedOutfit;
                BufferedImage sheet = ImageIO.read(new File(path));
                int spriteSize = 32;
                
                // Outfit files have all animations in row 0 (24 columns: 6 per direction)
                int colStart = getColumnOffset(direction);
                int x = (colStart + (frame - 1)) * spriteSize;
                int y = 0;
                
                return sheet.getSubimage(x, y, spriteSize, spriteSize);
            } catch (IOException e) {
                System.err.println("Failed to load outfit: " + outfitFile + " (mapped to " + mapToAvailableOutfit(outfitFile) + ")");
                // Fallback to Outfit1.png
                try {
                    BufferedImage defaultSheet = ImageIO.read(new File("src/Resource/Characters/MetroCity/Outfits/Outfit1.png"));
                    int spriteSize = 32;
                    int colStart = getColumnOffset(direction);
                    int x = (colStart + (frame - 1)) * spriteSize;
                    int y = 0;
                    return defaultSheet.getSubimage(x, y, spriteSize, spriteSize);
                } catch (IOException ex) {
                    System.err.println("Failed to load default outfit");
                    return null;
                }
            }
        }
        
        private String mapToAvailableOutfit(String outfitFile) {
            // There are 6 outfit files: Outfit1.png to Outfit6.png
            // We'll use deterministic hashing to assign each unique outfit name to one of the 6
            if (outfitFile == null) return "Outfit1.png";
            
            // Clean the filename (remove .png if present)
            String cleanName = outfitFile.replace(".png", "").toLowerCase();
            
            // Available outfits
            String[] outfits = {"Outfit1.png", "Outfit2.png", "Outfit3.png", "Outfit4.png", "Outfit5.png", "Outfit6.png"};
            
            // Simple hash-based mapping to get consistent assignment
            int hash = cleanName.hashCode();
            int index = Math.abs(hash) % outfits.length;
            return outfits[index];
        }
        
        private int getColumnOffset(Direction direction) {
            return switch (direction) {
                case DOWN -> 0;
                case RIGHT -> 6;
                case UP -> 12;
                case LEFT -> 18;
            };
        }
        
        private boolean suitHasHeadwear(int suitRow) {
            // Rows 0,1,3 have headwear; row 2 (Formal Suit) doesn't
            return suitRow != 2;
        }
    }
}
