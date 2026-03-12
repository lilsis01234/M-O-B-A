package Engine;

import Core.Database.dao.HeroDAO;
import Core.Database.model.Hero;
import Core.Entity.Direction;
import Engine.Render.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private List<Hero> allHeroes;
    private List<Hero> filteredHeroes;
    private Hero selectedHero;
    private int selectedIndex = -1;
    
    // Category filtering
    private String[] categories = {"All", "Force", "Agilité", "Intelligence"};
    private String selectedCategory = "All";
    private int categoryTabHeight = 40;
    private int categoryTabWidth = 100;
    
    // Colors - pixel art theme
    private final Color BACKGROUND = new Color(25, 25, 40);
    private final Color PANEL_BG = new Color(35, 35, 55);
    private final Color CARD_BG = new Color(50, 50, 75);
    private final Color CARD_HOVER = new Color(70, 70, 100);
    private final Color CARD_SELECTED = new Color(90, 130, 200);
    private final Color CARD_BORDER = new Color(100, 100, 140);
    private final Color SELECTED_BORDER = new Color(255, 215, 0); // Gold
    private final Color TEXT_MAIN = new Color(240, 240, 240);
    private final Color TEXT_SECONDARY = new Color(180, 180, 200);
    private final Color STAT_POSITIVE = new Color(120, 230, 120);
    private final Color STAT_NEUTRAL = new Color(200, 200, 120);
    private final Color TAB_BG = new Color(45, 45, 65);
    private final Color TAB_SELECTED = new Color(60, 90, 140);
    private final Color TAB_HOVER = new Color(55, 55, 80);
    
    // Layout
    private final int CARD_WIDTH = 200;
    private final int CARD_HEIGHT = 140;
    private final int CARD_SPACING = 12;
    private final int SPRITE_SIZE = 56;
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 24);
    private final Font FONT_CARD_NAME = new Font("Arial", Font.BOLD, 13);
    private final Font FONT_STAT = new Font("Courier New", Font.BOLD, 11);
    private final Font FONT_DESC = new Font("Arial", Font.PLAIN, 10);
    private final Font FONT_TAB = new Font("Arial", Font.BOLD, 12);
    
    private HeroSpriteCache spriteCache;
    
    public HeroSelectionPanel(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(BACKGROUND);
        setFocusable(true);
        
        spriteCache = new HeroSpriteCache();
        loadHeroes();
        filterHeroes();
        
        setupListeners();
    }
    
    private void loadHeroes() {
        try {
            HeroDAO heroDAO = new HeroDAO();
            allHeroes = heroDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            allHeroes = new ArrayList<>();
        }
    }
    
    private void filterHeroes() {
        filteredHeroes = new ArrayList<>();
        for (Hero hero : allHeroes) {
            if (selectedCategory.equals("All")) {
                filteredHeroes.add(hero);
            } else {
                // Map categoryId to name (simplified)
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
    }
    
    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY(), e.getButton());
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                repaint();
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyNav(e.getKeyCode());
            }
        });
    }
    
    private void handleClick(int x, int y, int button) {
        // Check category tabs first
        if (y <= categoryTabHeight) {
            int tabX = (getWidth() - categories.length * categoryTabWidth) / 2;
            for (int i = 0; i < categories.length; i++) {
                int tabLeft = tabX + i * categoryTabWidth;
                if (x >= tabLeft && x <= tabLeft + categoryTabWidth) {
                    selectedCategory = categories[i];
                    filterHeroes();
                    repaint();
                    return;
                }
            }
            return;
        }
        
        // Check hero cards
        int cols = calculateColumns();
        int cardWidth = CARD_WIDTH;
        int cardHeight = CARD_HEIGHT;
        int spacing = CARD_SPACING;
        
        int totalWidth = cols * cardWidth + (cols - 1) * spacing;
        int startX = (getWidth() - totalWidth) / 2;
        int startY = categoryTabHeight + 60;
        
        int row = (y - startY) / (cardHeight + spacing);
        int col = (x - startX) / (cardWidth + spacing);
        
        int index = row * cols + col;
        if (index >= 0 && index < filteredHeroes.size()) {
            // Left click to select
            if (button == MouseEvent.BUTTON1) {
                selectedIndex = index;
                selectedHero = filteredHeroes.get(index);
                repaint();
            }
        }
    }
    
    private void handleKeyNav(int keyCode) {
        if (filteredHeroes.isEmpty()) return;
        int cols = calculateColumns();
        
        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (selectedIndex >= cols) {
                    selectedIndex -= cols;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (selectedIndex < filteredHeroes.size() - cols) {
                    selectedIndex += cols;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (selectedIndex % cols > 0) {
                    selectedIndex--;
                    selectedHero = filteredHeroes.get(selectedIndex);
                    repaint();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if ((selectedIndex % cols < cols - 1) && (selectedIndex < filteredHeroes.size() - 1)) {
                    selectedIndex++;
                    selectedHero = filteredHeroes.get(selectedIndex);
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
    
    private int calculateColumns() {
        int availableWidth = getWidth() - 60; // margins
        return Math.max(1, availableWidth / (CARD_WIDTH + CARD_SPACING));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawBackground(g2);
        drawTitle(g2);
        drawCategoryTabs(g2);
        drawHeroGrid(g2);
        drawConfirmButton(g2);
    }
    
    private void drawBackground(Graphics2D g2) {
        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND, 0, getHeight(), new Color(15, 15, 25));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Subtle grid pattern
        g2.setColor(new Color(40, 40, 60));
        for (int i = 0; i < getWidth(); i += 40) {
            g2.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += 40) {
            g2.drawLine(0, i, getWidth(), i);
        }
    }
    
    private void drawTitle(Graphics2D g2) {
        String title = "SELECT YOUR HERO";
        g2.setFont(FONT_TITLE);
        g2.setColor(TEXT_MAIN);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        g2.drawString(title, x, 45);
        
        // Underline
        g2.setColor(SELECTED_BORDER);
        g2.drawLine(x, 55, x + fm.stringWidth(title), 55);
    }
    
    private void drawCategoryTabs(Graphics2D g2) {
        int totalWidth = categories.length * categoryTabWidth;
        int startX = (getWidth() - totalWidth) / 2;
        int y = categoryTabHeight - 5;
        
        for (int i = 0; i < categories.length; i++) {
            int x = startX + i * categoryTabWidth;
            boolean isSelected = categories[i].equals(selectedCategory);
            
            // Tab background
            g2.setColor(isSelected ? TAB_SELECTED : TAB_BG);
            g2.fillRoundRect(x, 10, categoryTabWidth - 4, categoryTabHeight - 15, 8, 8);
            
            // Tab border
            g2.setColor(isSelected ? SELECTED_BORDER : CARD_BORDER);
            g2.setStroke(isSelected ? new BasicStroke(2) : new BasicStroke(1));
            g2.drawRoundRect(x, 10, categoryTabWidth - 4, categoryTabHeight - 15, 8, 8);
            
            // Tab text
            g2.setColor(TEXT_MAIN);
            g2.setFont(FONT_TAB);
            FontMetrics fm = g2.getFontMetrics();
            String cat = categories[i];
            int textX = x + (categoryTabWidth - 4 - fm.stringWidth(cat)) / 2;
            g2.drawString(cat, textX, 30);
        }
    }
    
    private void drawHeroGrid(Graphics2D g2) {
        if (filteredHeroes.isEmpty()) {
            g2.setFont(FONT_CARD_NAME);
            g2.setColor(TEXT_SECONDARY);
            String msg = "No heroes in this category";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, x, getHeight() / 2);
            return;
        }
        
        int cols = calculateColumns();
        int cardW = CARD_WIDTH;
        int cardH = CARD_HEIGHT;
        int spacing = CARD_SPACING;
        
        int gridWidth = cols * cardW + (cols - 1) * spacing;
        int startX = (getWidth() - gridWidth) / 2;
        int startY = categoryTabHeight + 60;
        
        int rows = (int) Math.ceil((double) filteredHeroes.size() / cols);
        int gridHeight = rows * (cardH + spacing) - spacing;
        
        // Center grid if fits
        if (gridHeight < getHeight() - startY - 100) {
            startY += (getHeight() - startY - 100 - gridHeight) / 2;
        }
        
        for (int i = 0; i < filteredHeroes.size(); i++) {
            Hero hero = filteredHeroes.get(i);
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cardW + spacing);
            int y = startY + row * (cardH + spacing);
            
            drawCard(g2, hero, x, y, i == selectedIndex);
        }
    }
    
    private void drawCard(Graphics2D g2, Hero hero, int x, int y, boolean selected) {
        // Card shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(x + 3, y + 3, CARD_WIDTH, CARD_HEIGHT, 12, 12);
        
        // Card background
        g2.setColor(selected ? CARD_SELECTED : CARD_HOVER);
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 12, 12);
        
        // Card border
        if (selected) {
            g2.setColor(SELECTED_BORDER);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1));
        }
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 12, 12);
        
        // Sprite
        BufferedImage sprite = spriteCache.getSprite(hero, Direction.DOWN, 1);
        if (sprite != null) {
            int spriteX = x + (CARD_WIDTH - SPRITE_SIZE) / 2;
            int spriteY = y + 8;
            g2.drawImage(sprite, spriteX, spriteY, SPRITE_SIZE, SPRITE_SIZE, null);
            
            // Sprite border
            g2.setColor(selected ? SELECTED_BORDER : new Color(80, 80, 110));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(spriteX - 2, spriteY - 2, SPRITE_SIZE + 4, SPRITE_SIZE + 4, 6, 6);
        }
        
        // Name
        String name = hero.getName().toUpperCase();
        g2.setFont(FONT_CARD_NAME);
        g2.setColor(TEXT_MAIN);
        FontMetrics fm = g2.getFontMetrics();
        int nameW = fm.stringWidth(name);
        int nameX = x + (CARD_WIDTH - nameW) / 2;
        g2.drawString(name, nameX, y + SPRITE_SIZE + 22);
        
        // Stats horizontally (HP | ATK | DEF)
        g2.setFont(FONT_STAT);
        FontMetrics fmStat = g2.getFontMetrics();
        String stats = String.format("HP:%d  ATK:%d  DEF:%d", 
            hero.getMaxHp(), hero.getAttack(), hero.getDefense());
        int statsW = fmStat.stringWidth(stats);
        int statsX = x + (CARD_WIDTH - statsW) / 2;
        g2.setColor(STAT_POSITIVE);
        g2.drawString(stats, statsX, y + SPRITE_SIZE + 38);
        
        // Class/Category indicator
        String classText = getCategoryShort(hero.getCategoryId());
        g2.setFont(FONT_DESC);
        g2.setColor(TEXT_SECONDARY);
        FontMetrics fmDesc = g2.getFontMetrics();
        int classW = fmDesc.stringWidth(classText);
        int classX = x + (CARD_WIDTH - classW) / 2;
        g2.drawString(classText, classX, y + SPRITE_SIZE + 52);
        
        // Short description (2 lines)
        String desc = hero.getHistory();
        if (desc.length() > 60) {
            desc = desc.substring(0, 57) + "...";
        }
        g2.setFont(FONT_DESC);
        drawWrappedString(g2, desc, x + 10, y + SPRITE_SIZE + 62, CARD_WIDTH - 20, 11);
    }
    
    private String getCategoryShort(int catId) {
        return switch (catId) {
            case 1 -> "[FORCE]";
            case 2 -> "[AGILITE]";
            case 3 -> "[INTELLIGENCE]";
            default -> "[UNKNOWN]";
        };
    }
    
    private void drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            String test = line.length() > 0 ? line + " " + word : word;
            int w = g2.getFontMetrics().stringWidth(test);
            if (w > maxWidth && line.length() > 0) {
                g2.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, currentY);
        }
    }
    
    private void drawConfirmButton(Graphics2D g2) {
        if (selectedHero == null) return;
        
        String text = "▶ START GAME WITH " + selectedHero.getName().toUpperCase();
        g2.setFont(FONT_TAB.deriveFont(Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int btnW = fm.stringWidth(text) + 40;
        int btnH = 36;
        int btnX = (getWidth() - btnW) / 2;
        int btnY = getHeight() - 60;
        
        // Button shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(btnX + 3, btnY + 3, btnW, btnH, 10, 10);
        
        // Button bg
        GradientPaint btnGrad = new GradientPaint(btnX, btnY, new Color(50, 150, 50), 
                                                  btnX, btnY + btnH, new Color(30, 100, 30));
        g2.setPaint(btnGrad);
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 10, 10);
        
        // Button border
        g2.setColor(new Color(100, 230, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 10, 10);
        
        // Button text
        g2.setColor(new Color(255, 255, 200));
        int textX = btnX + (btnW - fm.stringWidth(text)) / 2;
        int textY = btnY + (btnH + fm.getAscent()) / 2 - 2;
        g2.drawString(text, textX, textY);
    }
    
    public Hero getSelectedHero() {
        return selectedHero;
    }
    
    public void setSelectionListener(SelectionListener listener) {
        this.listener = listener;
    }
    
    // Sprite cache with Outfit mapping
    private class HeroSpriteCache {
        private final java.util.Map<String, BufferedImage> cache = new java.util.HashMap<>();
        
        public BufferedImage getSprite(Hero hero, Direction direction, int frame) {
            String key = hero.getId() + "_" + direction + "_" + frame;
            BufferedImage cached = cache.get(key);
            if (cached != null) return cached;
            
            BufferedImage composite = composeHeroSprite(hero, direction, frame);
            if (composite != null) {
                cache.put(key, composite);
            }
            return composite;
        }
        
        private BufferedImage composeHeroSprite(Hero hero, Direction direction, int frame) {
            try {
                BufferedImage base = loadPart(
                    "src/Resource/Characters/MetroCity/CharacterModel/Character Model.png",
                    hero.getCharacterRow(), direction, frame
                );
                if (base == null) {
                    base = loadPart(
                        "src/Resource/Characters/MetroCity/CharacterModel/Character Model.png",
                        0, direction, frame
                    );
                }
                
                BufferedImage hair = loadPart(
                    "src/Resource/Characters/MetroCity/Hair/Hairs.png",
                    hero.getHairRow(), direction, frame
                );
                if (hair == null) {
                    hair = loadPart(
                        "src/Resource/Characters/MetroCity/Hair/Hairs.png",
                        0, direction, frame
                    );
                }
                
                String outfitFile = mapOutfit(hero.getOutfitFile());
                BufferedImage outfit = loadOutfit(outfitFile, direction, frame);
                if (outfit == null) outfit = loadOutfit("Outfit1.png", direction, frame);
                
                BufferedImage composite = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = composite.createGraphics();
                g.drawImage(base, 0, 0, null);
                if (outfit != null) g.drawImage(outfit, 0, 0, null);
                if (hair != null) g.drawImage(hair, 0, 0, null);
                g.dispose();
                return composite;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        private BufferedImage loadPart(String path, int row, Direction direction, int frame) {
            try {
                BufferedImage sheet = ImageIO.read(new File(path));
                int spriteSize = 32;
                int maxRows = sheet.getHeight() / spriteSize;
                if (row < 0 || row >= maxRows) row = 0;
                
                int colStart = getColumnOffset(direction);
                int x = (colStart + (frame - 1)) * spriteSize;
                int y = row * spriteSize;
                
                if (x + spriteSize > sheet.getWidth()) return null;
                return sheet.getSubimage(x, y, spriteSize, spriteSize);
            } catch (IOException e) {
                return null;
            }
        }
        
        private BufferedImage loadOutfit(String outfitFile, Direction direction, int frame) {
            try {
                BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/MetroCity/Outfits/" + outfitFile));
                int spriteSize = 32;
                int colStart = getColumnOffset(direction);
                int x = (colStart + (frame - 1)) * spriteSize;
                return sheet.getSubimage(x, 0, spriteSize, spriteSize);
            } catch (IOException e) {
                return null;
            }
        }
        
        private int getColumnOffset(Direction direction) {
            return switch (direction) {
                case DOWN -> 0;
                case RIGHT -> 6;
                case UP -> 12;
                case LEFT -> 18;
            };
        }
        
        private String mapOutfit(String outfitFile) {
            if (outfitFile == null) return "Outfit1.png";
            String clean = outfitFile.replace(".png", "").toLowerCase();
            String[] outfits = {"Outfit1.png","Outfit2.png","Outfit3.png","Outfit4.png","Outfit5.png","Outfit6.png"};
            int hash = Math.abs(clean.hashCode());
            return outfits[hash % outfits.length];
        }
    }
}
