package Core.Moba.World;

import Core.Moba.Units.Tour;
import Core.Moba.Units.CoreBase;
import Core.Moba.Units.Unite;
import Core.Tile.TileMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
/**
 * Cette classe est responsable du scan de la carte pour placer les entités,
 * de la définition des voies (Lanes) et du suivi du score global (Kills).
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia 
 * @version 2.0
 */

public final class Arena {
    private final Map<Voie, List<Vec2>> lanesWaypoints;
    private final List<Vec2> jungleCampPositions;
    private final List<Tour> tours;
    private final List<CoreBase> coreBases;
    private final List<Object> unites;
    private Vec2 blueCoreBasePos;
    private Vec2 redCoreBasePos;
    
    private int blueKills = 0;
    private int redKills = 0;

    public Arena() {
        lanesWaypoints = new EnumMap<>(Voie.class);
        for (Voie v : Voie.values()) {
            lanesWaypoints.put(v, new ArrayList<>());
        }
        jungleCampPositions = new ArrayList<>();
        tours = new ArrayList<>();
        coreBases = new ArrayList<>();
        unites = new ArrayList<>();
    }

    public void initializeFromMap(TileMap map, Equipe blueTeam, Equipe redTeam) {
        tours.clear();
        coreBases.clear();
        boolean[][] visited = new boolean[map.getRows()][map.getColumns()];

        // Scan map for markers
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getColumns(); col++) {
                if (visited[row][col]) continue;

                int tileId = map.getTileAt(row, col);
                if (tileId < 20 || tileId > 23) continue;

                // Find cluster dimensions
                int w = 0;
                while (col + w < map.getColumns() && map.getTileAt(row, col + w) == tileId) {
                    w++;
                }
                int h = 0;
                while (row + h < map.getRows() && map.getTileAt(row + h, col) == tileId) {
                    h++;
                }

                // Mark cluster as visited and REPLACE markers with ground tiles in map
                 int groundId = 18; // Default Grass
                 if (tileId == 22 || tileId == 23) {
                     groundId = 18; // Grass for CoreBases (no wood floor)
                 } else {
                     groundId = 3; // Sand for Towers
                 }

                for (int r = row; r < row + h; r++) {
                    for (int c = col; c < col + w; c++) {
                        visited[r][c] = true;
                        map.setTileAt(r, c, groundId);
                    }
                }

                Vec2 pos = new Vec2(col, row);
                switch (tileId) {
                         case 20 -> { // Blue Tower
                             addTour(new Tour(blueTeam, pos, 1000, 50, 20, 450, 3, determineLane(pos, true), w, h));
                         }
                         case 21 -> { // Red Tower
                             addTour(new Tour(redTeam, pos, 1000, 50, 20, 450, 3, determineLane(pos, false), w, h));
                         }
                    case 22 -> { // Blue CoreBase
                        blueCoreBasePos = pos;
                        coreBases.add(new CoreBase(blueTeam, pos, 5000, 100, w, h));
                    }
                    case 23 -> { // Red CoreBase
                        redCoreBasePos = pos;
                        coreBases.add(new CoreBase(redTeam, pos, 5000, 100, w, h));
                    }
                }
            }
        }
        
        // After all towers are added, we can assign Tiers properly
        for (Tour t : tours) {
            Vec2 coreBasePos = (t.equipe().couleur() == TeamColor.BLUE) ? blueCoreBasePos : redCoreBasePos;
            if (coreBasePos != null) {
                t.setTier(determineTier(t.position(), coreBasePos));
            }
        }
    }

    /**
     * Pour déterminer sur quelle voie (TOP, MID, BOT) se situe une coordonnée
     */
    private Voie determineLane(Vec2 pos, boolean isBlue) {
        double x = pos.x();
        double y = pos.y();
        
        if (isBlue) {
            if (x < 30 && y > 30) return Voie.TOP;
            if (y > 70 && x > 30) return Voie.BOT;
            return Voie.MID;
        } else {
            if (y < 30 && x < 70) return Voie.TOP;
            if (x > 70 && y > 30) return Voie.BOT;
            return Voie.MID;
        }
    }

    private int determineTier(Vec2 pos, Vec2 coreBasePos) {
        if (coreBasePos == null) return 3;
        double dist = pos.distanceTo(coreBasePos);
        if (dist < 20) return 1; // Base towers
        if (dist < 50) return 2; // Mid lane towers
        return 3; // Outer towers
    }

    public void initializeTowers(Equipe blueTeam, Equipe redTeam) {
        // This method is now legacy or can be used for hardcoded fallbacks
    }

    public Vec2 getBlueCoreBase() { return blueCoreBasePos; }
    public Vec2 getRedCoreBase() { return redCoreBasePos; }

    public List<Vec2> voieWaypoints(Voie voie) {
        return lanesWaypoints.get(voie);
    }

    public List<Vec2> jungleCampPositions() {
        return jungleCampPositions;
    }

    public List<Tour> tours() {
        return tours;
    }

    public List<CoreBase> coreBases() {
        return coreBases;
    }

    public void addTour(Tour tour) {
        tours.add(tour);
    }

    public List<Object> unites() {
        return unites;
    }

    public void ajouterUnite(Object unite) {
        unites.add(unite);
    }

    public void retirerUnite(Object unite) {
        unites.remove(unite);
    }
    
    // Gestion du Score
    public void recordKill(TeamColor teamColor) {
        if (teamColor == TeamColor.BLUE) {
            blueKills++;
        } else {
            redKills++;
        }
    }
    
    public int getBlueKills() {
        return blueKills;
    }
    
    public int getRedKills() {
        return redKills;
    }
}
