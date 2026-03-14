package Core.Match;

import Core.Entity.HitboxUtils;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Core.Moba.Units.Tour;
import Core.Moba.Units.Ancient;
import Core.Moba.World.Arena;
import Core.Config;
import java.util.*;

/**
 * Moteur de recherche de chemin (Pathfinding) basé sur l'algorithme A*.
 * Permet aux entités de calculer l'itinéraire le plus court entre deux tuiles 
 * tout en évitant les murs et les structures de l'arène 
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 */
public class PathFinder {
    
    /**
     * Représente une cellule de la grille pour l'algorithme A*.
     * Stocke les coûts G (distance parcourue), H (estimation restante) et F (total).
     */
    private static class Node implements Comparable<Node> {
        int x, y;
        int g, h, f;
        Node parent;
        
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        //Comparaison basée sur le coût F pour la priorité dans l'algorithme
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    private final TileMap tileMap;
    private final CollisionTable collisionTable;
    private Arena arena;
    
    public PathFinder(TileMap tileMap, CollisionTable collisionTable) {
        this.tileMap = tileMap;
        this.collisionTable = collisionTable;
    }
    
    // Définit l'arène pour inclure les bâtiments dans les calculs d'obstacles
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    
    /**
     * Calcule le chemin entre deux points.
     * @param startCol Colonne de départ.
     * @param startRow Ligne de départ.
     * @param targetCol Colonne d'arrivée.
     * @param targetRow Ligne d'arrivée.
     * @return Une liste de coordonnées [x, y] représentant le chemin, ou null si aucun chemin n'est possible.
     */
    public List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow) {
        // Validation des limites
        if (targetCol < 0 || targetCol >= tileMap.getColumns() || targetRow < 0 || targetRow >= tileMap.getRows()) {
            return null;
        }

        // Si la cible est un obstacle, on cherche la tuile marchable la plus proche
        if (hasCollision(targetRow, targetCol)) {
            int[] nearest = findNearestWalkableTile(targetCol, targetRow);
            if (nearest != null) {
                targetCol = nearest[0];
                targetRow = nearest[1];
            } else {
                return null;
            }
        }
        
        if (startCol == targetCol && startRow == targetRow) {
            return null;
        }

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<Node, Node> openMap = new HashMap<>(); // Pour un accès rapide aux nœuds ouverts
        Set<Node> closedList = new HashSet<>();
        
        Node startNode = new Node(startCol, startRow);
        Node targetNode = new Node(targetCol, targetRow);
        
        startNode.g = 0;
        startNode.h = calculateHeuristic(startCol, startRow, targetCol, targetRow);
        startNode.f = startNode.h;
        
        openList.add(startNode);
        openMap.put(startNode, startNode);
        
        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            openMap.remove(currentNode);
            
            // Destination atteinte
            if (currentNode.equals(targetNode)) {
                return reconstructPath(currentNode);
            }
            
            closedList.add(currentNode);
            
            // Exploration des 8 directions voisines
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    
                    int nx = currentNode.x + dx;
                    int ny = currentNode.y + dy;
                    
                    if (nx < 0 || nx >= tileMap.getColumns() || ny < 0 || ny >= tileMap.getRows()) continue;
                    if (hasCollision(ny, nx)) continue;
                    
                    // Empêcher de "couper les coins" sur les murs en diagonale
                    if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
                        if (hasCollision(ny, currentNode.x) || hasCollision(currentNode.y, nx)) {
                            continue;
                        }
                    }
                    
                    Node neighbor = new Node(nx, ny);
                    if (closedList.contains(neighbor)) continue;
                    
                    // Calcul du coût : 10 pour horizontal/vertical, 14 pour diagonale (approx. √2 * 10)
                    int moveCost = (Math.abs(dx) + Math.abs(dy) == 2) ? 14 : 10;
                    int tentativeG = currentNode.g + moveCost;
                    
                    Node existingOpen = openMap.get(neighbor);
                    if (existingOpen == null || tentativeG < existingOpen.g) {
                        if (existingOpen != null) {
                            openList.remove(existingOpen);
                        }
                        
                        neighbor.parent = currentNode;
                        neighbor.g = tentativeG;
                        neighbor.h = calculateHeuristic(nx, ny, targetCol, targetRow);
                        neighbor.f = neighbor.g + neighbor.h;
                        
                        openList.add(neighbor);
                        openMap.put(neighbor, neighbor);
                    }
                }
            }
        }
        return null;
    }
    
    /** Vérifie si une tuile spécifique est bloquée par un mur ou une structure. */
    private boolean hasCollision(int row, int col) {
        if (isWallTile(row, col)) return true;
        if (arena != null) return hasStructureCollision(row, col);
        return false;
    }
    
    private boolean isWallTile(int row, int col) {
        if (row < 0 || row >= tileMap.getRows() || col < 0 || col >= tileMap.getColumns()) return true;
        return collisionTable.hasCollision(tileMap.getTileAt(row, col));
    }
    
    /** Teste si une tuile de la grille est occupée par la hitbox d'une tour ou d'un Ancien. */
    private boolean hasStructureCollision(int row, int col) {
        int tileSize = Config.getTileSize();
        double tilePixelX = col * tileSize;
        double tilePixelY = row * tileSize;
        
        HitboxUtils.Hitbox tileCollisionBox = HitboxUtils.createEntityCollisionBox(tilePixelX, tilePixelY);

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerCollisionBox = HitboxUtils.createTowerCollisionBox(
                tower.position().x(), tower.position().y(), tower.width(), tower.height());
            if (HitboxUtils.aabbIntersects(tileCollisionBox, towerCollisionBox)) return true;
        }
        
        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientCollisionBox = HitboxUtils.createAncientCollisionBox(
                ancient.position().x(), ancient.position().y(), ancient.width(), ancient.height());
            if (HitboxUtils.aabbIntersects(tileCollisionBox, ancientCollisionBox)) return true;
        }
        return false;
    }
    
    /** * Calcule l'estimation de distance (Heuristique). 
     * Utilise la distance diagonale pour un mouvement en 8 directions.
     */
    private int calculateHeuristic(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return 10 * (dx + dy) + (14 - 2 * 10) * Math.min(dx, dy);
    }

    /**
     * Recherche par inondation (BFS) pour trouver la tuile libre la plus proche si la destination est bloquée.
     */
    private int[] findNearestWalkableTile(int targetCol, int targetRow) {
        int maxRadius = 10;
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(new int[]{targetCol, targetRow});
        visited.add(targetCol + "," + targetRow);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            if (!hasCollision(cy, cx)) return current;

            if (Math.abs(cx - targetCol) >= maxRadius || Math.abs(cy - targetRow) >= maxRadius) continue;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = cx + dx;
                    int ny = cy + dy;

                    if (nx >= 0 && nx < tileMap.getColumns() && ny >= 0 && ny < tileMap.getRows()) {
                        String key = nx + "," + ny;
                        if (!visited.contains(key)) {
                            visited.add(key);
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /** Remonte la chaîne des parents pour générer la liste finale de coordonnées. */
    private List<int[]> reconstructPath(Node targetNode) {
        List<int[]> path = new ArrayList<>();
        Node current = targetNode;
        while (current != null) {
            path.add(0, new int[]{current.x, current.y});
            current = current.parent;
        }
        return path;
    }
}