package Core.Entity;

/**
 * Utilitaires mathématiques.
 * @author RAHARIMANANA Tianantenaina ZEGHBIB Sonia BOUKIRAT Thafat
 */
public class MathUtils {

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public static int pixelToTileCoord(double pixel, int tileSize) {
        return (int) Math.floor(pixel / tileSize);
    }

    public static double normalize(double value) {
        return value > 0 ? 1.0 : value < 0 ? -1.0 : 0.0;
    }

    public static double[] normalizeVector(double x, double y) {
        double length = Math.sqrt(x * x + y * y);
        if (length == 0) return new double[]{0, 0};
        return new double[]{x / length, y / length};
    }

    public static double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static boolean isNearlyEqual(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    private MathUtils() {}
}
