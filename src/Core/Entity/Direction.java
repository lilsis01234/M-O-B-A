package Core.Entity;

/**
 * Directions cardinales du jeu.
 */
public enum Direction {
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right");
    
    private final String name;
    
    Direction(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static Direction fromString(String str) {
        for (Direction dir : values()) {
            if (dir.name.equals(str)) {
                return dir;
            }
        }
        return DOWN;
    }
    
    public static Direction fromDelta(int dx, int dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? RIGHT : LEFT;
        } else if (Math.abs(dy) > Math.abs(dx)) {
            return dy > 0 ? DOWN : UP;
        } else if (dx != 0) {
            return dx > 0 ? RIGHT : LEFT;
        }
        return DOWN;
    }
}
