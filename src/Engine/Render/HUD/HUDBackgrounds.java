package Engine.Render.HUD;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HUDBackgrounds {

    private static BufferedImage panelBg;
    private static BufferedImage smallPanelBg;
    private static BufferedImage abilitySlotBg;
    private static BufferedImage itemSlotBg;
    private static boolean initialized = false;

    private static void initialize() {
        if (initialized) return;
        
        try {
            panelBg = ImageIO.read(new File("src/Resource/HUD/panel.png"));
            smallPanelBg = ImageIO.read(new File("src/Resource/HUD/small_panel.png"));
            abilitySlotBg = ImageIO.read(new File("src/Resource/HUD/ability_slot.png"));
            itemSlotBg = ImageIO.read(new File("src/Resource/HUD/item_slot.png"));
        } catch (IOException e) {
            panelBg = createPanelBackground(200, 140);
            smallPanelBg = createSmallPanelBackground(140, 60);
            abilitySlotBg = createAbilitySlotBackground(46);
            itemSlotBg = createItemSlotBackground(28);
        }
        initialized = true;
    }

    public static BufferedImage getPanelBackground(int width, int height) {
        initialize();
        return scaleImage(panelBg, width, height);
    }

    public static BufferedImage getSmallPanelBackground(int width, int height) {
        initialize();
        return scaleImage(smallPanelBg, width, height);
    }

    public static BufferedImage getAbilitySlotBackground(int size) {
        initialize();
        return scaleImage(abilitySlotBg, size, size);
    }

    public static BufferedImage getItemSlotBackground(int size) {
        initialize();
        return scaleImage(itemSlotBg, size, size);
    }

    private static BufferedImage scaleImage(BufferedImage src, int width, int height) {
        if (src == null) {
            return createFallbackPanel(width, height);
        }
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dst.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return dst;
    }

    private static BufferedImage createFallbackPanel(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return img;
    }

    public static BufferedImage createPanelBackground(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2.setColor(new Color(30, 30, 40, 230));
        g2.fillRect(2, 2, width - 4, height - 4);
        
        g2.setColor(new Color(50, 50, 70));
        g2.drawRect(0, 0, width - 1, height - 1);
        
        g2.setColor(new Color(70, 70, 90));
        g2.drawRect(1, 1, width - 3, height - 3);
        
        g2.setColor(new Color(25, 25, 35));
        g2.drawRect(2, 2, width - 5, height - 5);
        
        for (int i = 4; i < width - 4; i += 8) {
            for (int j = 4; j < height - 4; j += 8) {
                if ((i + j) % 16 == 0) {
                    g2.setColor(new Color(35, 35, 45, 50));
                    g2.fillRect(i, j, 4, 4);
                }
            }
        }
        
        g2.dispose();
        return img;
    }

    public static BufferedImage createSmallPanelBackground(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2.setColor(new Color(25, 25, 35, 220));
        g2.fillRect(2, 2, width - 4, height - 4);
        
        g2.setColor(new Color(50, 50, 70));
        g2.drawRect(0, 0, width - 1, height - 1);
        
        g2.setColor(new Color(60, 60, 80));
        g2.drawRect(1, 1, width - 3, height - 3);
        
        g2.dispose();
        return img;
    }

    public static BufferedImage createAbilitySlotBackground(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2.setColor(new Color(40, 40, 60));
        g2.fillRect(0, 0, size, size);
        
        g2.setColor(new Color(70, 70, 90));
        g2.drawRect(0, 0, size - 1, size - 1);
        
        g2.setColor(new Color(50, 50, 70));
        g2.drawRect(1, 1, size - 3, size - 3);
        
        g2.setColor(new Color(30, 30, 50));
        g2.drawRect(2, 2, size - 5, size - 5);
        
        g2.dispose();
        return img;
    }

    public static BufferedImage createItemSlotBackground(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2.setColor(new Color(50, 50, 70));
        g2.fillRect(0, 0, size, size);
        
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(0, 0, size - 1, size - 1);
        
        g2.setColor(new Color(60, 60, 80));
        g2.drawRect(1, 1, size - 3, size - 3);
        
        g2.dispose();
        return img;
    }
}
