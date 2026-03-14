package Core.Entity;

import Core.Config;

/**
 * Utilitaires pour la manipulation et la création de collision 
 * 
 */
public class HitboxUtils {

    /** Ratio de la hauteur totale utilisé pour la collision au sol des entités (pieds). */
    public static final double COLLISION_BOX_BOTTOM_RATIO = 0.15;
    /** Marge intérieure par défaut pour éviter que les hitboxes ne collent trop aux murs. */
    public static final double HITBOX_INSET = 2.0;
    /** Largeur relative des tours pour la collision. */
    public static final double TOWER_COLLISION_WIDTH_RATIO = 0.35;
    /** Hauteur relative de la base des tours pour la collision au sol. */
    public static final double TOWER_COLLISION_HEIGHT_RATIO = 0.25;
    /** Hauteur relative totale de la tour pour recevoir des dégâts. */
    public static final double TOWER_HITBOX_HEIGHT_RATIO = 0.75;

    /**
     * Classe interne de collision rectangulaire simple
     */
    public static class Hitbox {
        private double x, y, width, height;

        public Hitbox(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }

        public double getLeft() { return x; }
        public double getTop() { return y; }
        public double getRight() { return x + width; }
        public double getBottom() { return y + height; }

        public double getCenterX() { return x + width / 2; }
        public double getCenterY() { return y + height / 2; }

        /** @return Une nouvelle Hitbox réduite de l'épaisseur 'amount' sur chaque bord. */
        public Hitbox inset(double amount) {
            return new Hitbox(x + amount, y + amount, width - 2 * amount, height - 2 * amount);
        }

        /** @return Une nouvelle Hitbox décalée selon les vecteurs dx et dy. */
        public Hitbox translate(double dx, double dy) {
            return new Hitbox(x + dx, y + dy, width, height);
        }
    }

    /**
     * Test d'intersection AABB (Axis-Aligned Bounding Box) entre deux boîtes
     * @return true si les deux boîtes se chevauchent.
     */
    public static boolean aabbIntersects(Hitbox a, Hitbox b) {
        return a.getLeft() < b.getRight() &&
               a.getRight() > b.getLeft() &&
               a.getTop() < b.getBottom() &&
               a.getBottom() > b.getTop();
    }

    /**
     * Vérifie si une boîte est entièrement contenue dans une autre.
     */
    public static boolean aabbContains(Hitbox container, Hitbox contained) {
        return contained.getLeft() >= container.getLeft() &&
               contained.getRight() <= container.getRight() &&
               contained.getTop() >= container.getTop() &&
               contained.getBottom() <= container.getBottom();
    }

    /**
     * Vérifie si un point (px, py) se trouve à l'intérieur de la hitbox.
     */
    public static boolean pointInHitbox(double px, double py, Hitbox hitbox) {
        return px >= hitbox.getLeft() && px < hitbox.getRight() &&
               py >= hitbox.getTop() && py < hitbox.getBottom();
    }

    /**
     * Crée une hitbox "complète" pour une entité (utilisée pour les interactions/dégâts).
     * @param topLeftX Position X du sprite.
     * @param topLeftY Position Y du sprite.
     * @return Hitbox centrée sur le personnage.
     */
    public static Hitbox createEntityHitbox(double topLeftX, double topLeftY) {
        int tileSize = Config.getTileSize();
        double inset = HITBOX_INSET;
        double fullWidth = tileSize - 2 * inset;
        double fullHeight = tileSize - 2 * inset;
        
        double hitboxWidth = fullWidth * 0.5;
        double hitboxHeight = fullHeight;
        
        double offsetX = (fullWidth - hitboxWidth) / 2;
        
        return new Hitbox(
            topLeftX + inset + offsetX,
            topLeftY + inset,
            hitboxWidth,
            hitboxHeight
        );
    }

    /**
     * Surcharge permettant de spécifier une marge personnalisée.
     */
    public static Hitbox createEntityHitbox(double topLeftX, double topLeftY, double inset) {
        int tileSize = Config.getTileSize();
        double fullWidth = tileSize - 2 * inset;
        double fullHeight = tileSize - 2 * inset;
        
        double hitboxWidth = fullWidth * 0.5;
        double hitboxHeight = fullHeight;
        
        double offsetX = (fullWidth - hitboxWidth) / 2;
        
        return new Hitbox(
            topLeftX + inset + offsetX,
            topLeftY + inset,
            hitboxWidth,
            hitboxHeight
        );
    }

    /**
     * Crée une hitbox réduite située aux "pieds" de l'entité.
     * Utilisée pour tester les collisions avec les murs et le décor.
     */
    public static Hitbox createEntityCollisionBox(double topLeftX, double topLeftY) {
        int tileSize = Config.getTileSize();
        double inset = HITBOX_INSET;
        double fullWidth = tileSize - 2 * inset;
        double fullHeight = tileSize - 2 * inset;
        
        double collisionWidth = fullWidth * 0.5;
        double collisionHeight = fullHeight * COLLISION_BOX_BOTTOM_RATIO;
        
        double offsetX = (fullWidth - collisionWidth) / 2;
        
        return new Hitbox(
            topLeftX + inset + offsetX,
            topLeftY + tileSize - inset - collisionHeight,
            collisionWidth,
            collisionHeight
        );
    }

    /**
     * Crée la boîte de collision au sol pour une Tour.
     */
    public static Hitbox createTowerCollisionBox(double towerX, double towerY, int width, int height) {
        int tileSize = Config.getTileSize();
        double pixelX = towerX * tileSize;
        double pixelY = towerY * tileSize;
        
        double spriteWidth = width * tileSize;
        double spriteHeight = height * tileSize;
        
        double collisionWidth = spriteWidth * TOWER_COLLISION_WIDTH_RATIO;
        double collisionHeight = spriteHeight * TOWER_COLLISION_HEIGHT_RATIO;
        
        double offsetX = (spriteWidth - collisionWidth) / 2;
        
        return new Hitbox(
            pixelX + offsetX,
            pixelY + spriteHeight - collisionHeight,
            collisionWidth,
            collisionHeight
        );
    }

    /**
     * Crée la boîte de réception de dégâts pour une Tour.
     */
    public static Hitbox createTowerHitbox(double towerX, double towerY, int width, int height) {
        int tileSize = Config.getTileSize();
        double pixelX = towerX * tileSize;
        double pixelY = towerY * tileSize;
        
        double spriteWidth = width * tileSize;
        double spriteHeight = height * tileSize;
        
        double hitboxWidth = spriteWidth * TOWER_COLLISION_WIDTH_RATIO;
        double hitboxHeight = spriteHeight * TOWER_HITBOX_HEIGHT_RATIO;
        
        double offsetX = (spriteWidth - hitboxWidth) / 2;
        
        return new Hitbox(
            pixelX + offsetX,
            pixelY + spriteHeight - hitboxHeight,
            hitboxWidth,
            hitboxHeight
        );
    }

    /** Boîte de collision au sol pour l'Ancien (Base). */
    public static Hitbox createAncientCollisionBox(double ancientX, double ancientY, int width, int height) {
        return createTowerCollisionBox(ancientX, ancientY, width, height);
    }

    /** Boîte de réception de dégâts pour l'Ancien (Base). */
    public static Hitbox createAncientHitbox(double ancientX, double ancientY, int width, int height) {
        return createTowerHitbox(ancientX, ancientY, width, height);
    }

    private HitboxUtils() {}
}