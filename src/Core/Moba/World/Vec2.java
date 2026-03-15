package Core.Moba.World;

/**
 * Cette classe représente un vecteur à deux dimensions utilisé pour les positions et les calculs physiques
 * @author RAHARIMANANA Tianantenaina
 * @version 1.0
 */
public record Vec2(double x, double y) {
    
    /**
     * Additionne deux vecteurs
     * @param other Le vecteur à ajouter
     * @return Un nouveau vecteur résultant de l'addition
     */
    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    /**
     * Soustrait un vecteur d'un autre
     * @param other Le vecteur à soustraire
     * @return Un nouveau vecteur résultant de la soustraction
     */
    public Vec2 sub(Vec2 other) {
        return new Vec2(x - other.x, y - other.y);
    }

    /**
     * Calcule la longueur (norme) du vecteur
     * @return La longueur du vecteur
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Calcule la distance entre deux points
     * @param other Le point de destination
     * @return La distance euclidienne entre les deux points
     */
    public double distanceTo(Vec2 other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    /**
     * Normalise le vecteur pour qu'il ait une longueur de 1 tout en gardant sa direction
     * @return Un vecteur unitaire (de longueur 1.0)
     */
    public Vec2 normalized() {
        double len = length();
        if (len == 0) return new Vec2(0, 0);
        return new Vec2(x / len, y / len);
    }
}
