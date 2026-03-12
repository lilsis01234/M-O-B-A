package Core.Entity;

import Core.Config;

/**
 * Utilitaires pour les hitboxes (boites de collision).
 */
public class HitboxUtils {

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

        public Hitbox inset(double amount) {
            return new Hitbox(x + amount, y + amount, width - 2 * amount, height - 2 * amount);
        }

        public Hitbox translate(double dx, double dy) {
            return new Hitbox(x + dx, y + dy, width, height);
        }
    }

    /**
     * Teste si deux hitboxes se chevauchent.
     */
    public static boolean aabbIntersects(Hitbox a, Hitbox b) {
        return a.getLeft() < b.getRight() &&
               a.getRight() > b.getLeft() &&
               a.getTop() < b.getBottom() &&
               a.getBottom() > b.getTop();
    }

    public static boolean aabbContains(Hitbox container, Hitbox contained) {
        return contained.getLeft() >= container.getLeft() &&
               contained.getRight() <= container.getRight() &&
               contained.getTop() >= container.getTop() &&
               contained.getBottom() <= container.getBottom();
    }

    public static boolean pointInHitbox(double px, double py, Hitbox hitbox) {
        return px >= hitbox.getLeft() && px < hitbox.getRight() &&
               py >= hitbox.getTop() && py < hitbox.getBottom();
    }

    public static Hitbox createEntityHitbox(double topLeftX, double topLeftY) {
        int tileSize = Config.getTileSize();
        double inset = 6.0;
        return new Hitbox(
            topLeftX + inset,
            topLeftY + inset,
            tileSize - 2 * inset,
            tileSize - 2 * inset
        );
    }

    public static Hitbox createEntityHitbox(double topLeftX, double topLeftY, double inset) {
        int tileSize = Config.getTileSize();
        return new Hitbox(
            topLeftX + inset,
            topLeftY + inset,
            tileSize - 2 * inset,
            tileSize - 2 * inset
        );
    }

    private HitboxUtils() {}
}
