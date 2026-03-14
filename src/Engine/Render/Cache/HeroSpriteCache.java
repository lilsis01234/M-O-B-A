package Engine.Render.Cache;

import Core.Database.model.Hero;
import Core.Entity.Direction;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
 // Cache pour stocker les sprites déjà composés des héros pour un accès rapide
public class HeroSpriteCache {
    private final java.util.Map<String, BufferedImage> cache = new java.util.HashMap<>();

    public BufferedImage getSprite(Hero hero, Direction direction, int frame) {
 // Construire une clé unique basée sur les caractéristiques du héros et la direction
        String key = hero.getCharacterRow() + "_" + hero.getHairRow() + "_" + 
                      hero.getOutfitFile() + "_" + direction + "_" + frame;
         // Composer le sprite si non présent dans le cache
        BufferedImage cached = cache.get(key);
        if (cached != null) return cached;

        BufferedImage composite = composeHeroSprite(hero, direction, frame);
        if (composite != null) {
            cache.put(key, composite);
        }
        return composite;
    }
 // Compose le sprite du héros en combinant les couches
    private BufferedImage composeHeroSprite(Hero hero, Direction direction, int frame) {
        try {
             // Corps de base du personnage
            BufferedImage base = loadPart(
                "src/Resource/Characters/CharacterModel/Character Model.png",
                hero.getCharacterRow(), direction, frame
            );
            if (base == null) { 
                // Repli sur la ligne par défaut si la ligne spécifiée est invalide
                base = loadPart(
                    "src/Resource/Characters/CharacterModel/Character Model.png",
                    0, direction, frame
                );
            }

            // Hair - load from RIGHT column and flip for LEFT direction
            Direction hairDir = direction;
            if (direction == Direction.LEFT) {
                hairDir = Direction.RIGHT;// on charge les cheveux comme si le personnage regardait à d
            }
            BufferedImage hair = loadPart(
                "src/Resource/Characters/Hair/Hairs.png",
                hero.getHairRow(), hairDir, frame
            );
            if (hair == null) {
                hair = loadPart(
                    "src/Resource/Characters/Hair/Hairs.png",
                    0, hairDir, frame
                );
            }
            
            
             // Inversion horizontale pour la direction LEFT
            if (hair != null && direction == Direction.LEFT) {
                hair = flipHorizontal(hair);
            }

            // Tenue (mappée sur une des six tenues standards)
            String outfitFile = mapOutfit(hero.getOutfitFile());
            BufferedImage outfit = loadOutfit(outfitFile, direction, frame);
            if (outfit == null) outfit = loadOutfit("Outfit1.png", direction, frame);

             // Composition finale : base -> tenue -> cheveux
            BufferedImage composite = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = composite.createGraphics();
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
 // Charge une partie d'une feuille de sprite
    private BufferedImage loadPart(String path, int row, Direction direction, int frame) {
        try {
            BufferedImage sheet = ImageIO.read(new File(path));
            int spriteSize = 32;
            int maxRows = sheet.getHeight() / spriteSize;
            if (row < 0 || row >= maxRows) {
                // Essaye de trouver une ligne valide, sinon retourne null
                if (maxRows > 0) row = 0;
                else return null;
            }

            int colStart = getColumnOffset(direction);
            int x = (colStart + (frame - 1)) * spriteSize;
            int y = row * spriteSize;

            if (x + spriteSize > sheet.getWidth()) return null;
            return sheet.getSubimage(x, y, spriteSize, spriteSize);
        } catch (Exception e) {
            return null;
        }
    }
 // Charge la tenue du héros depuis le fichier correspondant
    private BufferedImage loadOutfit(String outfitFile, Direction direction, int frame) {
        try {
            BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/Outfits/" + outfitFile));
            int spriteSize = 32;
            int colStart = getColumnOffset(direction);
            int x = (colStart + (frame - 1)) * spriteSize;
            return sheet.getSubimage(x, 0, spriteSize, spriteSize);
        } catch (Exception e) {
            return null;
        }
    }
 // Retourne l'offset de colonne dans la feuille de sprites selon la direction
    private int getColumnOffset(Direction direction) {
        return switch (direction) {
            case DOWN -> 0;
            case RIGHT -> 6;
            case UP -> 12;
            case LEFT -> 18;
        };
    }

    private String mapOutfit(String outfitFile) {
         // Mappe le fichier de tenue fourni sur l'une des six tenues génériques
        String[] outfits = {"Outfit1.png", "Outfit2.png", "Outfit3.png", "Outfit4.png", "Outfit5.png", "Outfit6.png"};
        if (outfitFile == null) return "Outfit1.png";
        int hash = Math.abs(outfitFile.hashCode());
        return outfits[hash % outfits.length];
    }
    // Retourne une image inversée horizontalement
    private BufferedImage flipHorizontal(BufferedImage src) {
        BufferedImage flipped = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.drawImage(src, src.getWidth(), 0, -src.getWidth(), src.getHeight(), null);
        g.dispose();
        return flipped;
    }
}
