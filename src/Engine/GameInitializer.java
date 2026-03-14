package Engine;

import Core.Config;
import Core.Database.model.Hero;
import Core.Entity.CollisionDetector;
import Core.Entity.PathFollower;
import Core.Entity.Player;
import Core.Moba.Units.Tour;
import Core.Moba.World.*;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Engine.Input.KeyHandler;
import Engine.Input.MouseHandler;
import Engine.Render.Camera;
import Engine.Tile.MapParser;
import Engine.Tile.Tile;
import Engine.Tile.TileLoader;

public class GameInitializer {

    public record GameContext(
        TileMap tileMap,
        Tile[] tiles,
        CollisionTable collisionTable,
        Player player,
        Arena arena
    ) {}
// Charger la carte du jeu
    public static GameContext initializeGame(Hero hero, KeyHandler keyHandler, 
                                             MouseHandler mouseHandler, Camera camera) {
        TileMap tileMap = parseTileMap();
        Tile[] tiles = loadTiles();
        CollisionTable collisionTable = buildCollisionTable(tiles);
       // Créer l’arène et les équipes
        Arena arena = createArena(tileMap);
      //  Créer le joueur
        Player player = createPlayer(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);

        return new GameContext(tileMap, tiles, collisionTable, player, arena);
    }

    private static TileMap parseTileMap() {// Crée une TileMap à partir du fichier de map
        MapParser mapParser = new MapParser(); // parser pour lire map
        MapParser.MapData mapData = mapParser.parse(Config.getMapFilePath());
        return new TileMap(mapData.tileNumbers(), mapData.columns(), mapData.rows());
    }

    private static Tile[] loadTiles() { // loader pour charger les tiles graphiques
        TileLoader tileLoader = new TileLoader();
        return tileLoader.load(Config.getMapFilePath(), Config.getMaxTiles());
    }
// Creation de la table de collision 
    private static CollisionTable buildCollisionTable(Tile[] tiles) {
        TileLoader tileLoader = new TileLoader();
        return new CollisionTable(tileLoader.buildCollisionTable(tiles));
    }
// Creation de larena
    private static Arena createArena(TileMap tileMap) {
        Arena arena = new Arena();
        // Création de l’équipe bleue avec base et fontaine
        Base blueBase = new Base(5000);
        Fontaine blueFontaine = new Fontaine(new Vec2(5, 95), 100, 50);
        Equipe blueTeam = new Equipe("Radiant", TeamColor.BLUE, blueBase, blueFontaine);

        // Création de l’équipe rouge  (pareil)
        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);
        // Initialisation de l’arène avec la map et les équipes
        arena.initializeFromMap(tileMap, blueTeam, redTeam);

        return arena;
    }

    private static Player createPlayer(KeyHandler keyHandler, MouseHandler mouseHandler,
                                       TileMap tileMap, CollisionTable collisionTable, 
                                       Arena arena, Hero hero) {
    	  // création du joueur avec la map, collisions, arène et héro choisi et l'ajouter 
        Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);
        arena.ajouterUnite(player);
        return player;
    }
}

