package Engine.Render.HUD;

import Core.Entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AbilityBarRenderer {
    private final Player player;
    private int x, y;
    private final int width, height;
        // Images pour le fond de la barre et des slots
    private BufferedImage background;
    private BufferedImage slotBackground;
 // Constructeur qui initialise la barre pour un joueur donné
    public AbilityBarRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
     // Permet de changer la position de la barre
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
 // Méthode principale de rendu de la barre de compétences
    public void render(Graphics2D g2) {
        if (background == null) {
            background = HUDBackgrounds.getPanelBackground(width, height);
            slotBackground = HUDBackgrounds.getAbilitySlotBackground(46);
        }
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, height);
 // Création d'un container flexible pour les slots
        FlexContainer container = new FlexContainer()
            .setBounds(x, y, width, height)
            .padding(4)
            .direction(FlexContainer.FlexDirection.ROW)
            .gap(8)
            .justifyContent(FlexContainer.JustifyContent.CENTER);
        
        for (int i = 0; i < 4; i++) {
            container.addItem(46, 46);
        }
        container.layout();
  // Parcours chaque slot pour dessiner fond, raccourci clavier et nom du sort
        for (int i = 0; i < 4; i++) {
            Rectangle slotBounds = container.getItem(i).bounds;
            
            g2.drawImage(slotBackground, slotBounds.x, slotBounds.y, null);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String key = (i + 1) + "";
            g2.drawString(key, slotBounds.x + 5, slotBounds.y + 20);
 // Affichage du nom du sort si le héros existe et qu'il a un sort 
            if (i < 3 && player.getHero() != null) {
                var spells = player.getHero().getSpells();
                if (i < spells.size()) {
                    var spell = spells.get(i);
                    g2.setColor(new Color(200, 200, 220));
                    g2.setFont(new Font("Arial", Font.PLAIN, 8));
                    String name = spell.getName().length() > 7 ? spell.getName().substring(0, 7) : spell.getName();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(name, slotBounds.x + (slotBounds.width - fm.stringWidth(name)) / 2, slotBounds.y + 45);
                }
            }
        }
    }
     // Dessine une bordure à double ligne autour de la barre
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}