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
<<<<<<< HEAD
// Charger la carte du jeu
=======

>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
    public static GameContext initializeGame(Hero hero, KeyHandler keyHandler, 
                                             MouseHandler mouseHandler, Camera camera) {
        TileMap tileMap = parseTileMap();
        Tile[] tiles = loadTiles();
        CollisionTable collisionTable = buildCollisionTable(tiles);
<<<<<<< HEAD
       // Créer l’arène et les équipes
        Arena arena = createArena(tileMap);
      //  Créer le joueur
=======

        Arena arena = createArena(tileMap);
>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
        Player player = createPlayer(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);

        return new GameContext(tileMap, tiles, collisionTable, player, arena);
    }

<<<<<<< HEAD
    private static TileMap parseTileMap() {// Crée une TileMap à partir du fichier de map
        MapParser mapParser = new MapParser(); // parser pour lire map
=======
    private static TileMap parseTileMap() {
        MapParser mapParser = new MapParser();
>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
        MapParser.MapData mapData = mapParser.parse(Config.getMapFilePath());
        return new TileMap(mapData.tileNumbers(), mapData.columns(), mapData.rows());
    }

<<<<<<< HEAD
    private static Tile[] loadTiles() { // loader pour charger les tiles graphiques
        TileLoader tileLoader = new TileLoader();
        return tileLoader.load(Config.getMapFilePath(), Config.getMaxTiles());
    }
// Creation de la table de collision 
=======
    private static Tile[] loadTiles() {
        TileLoader tileLoader = new TileLoader();
        return tileLoader.load(Config.getMapFilePath(), Config.getMaxTiles());
    }

>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
    private static CollisionTable buildCollisionTable(Tile[] tiles) {
        TileLoader tileLoader = new TileLoader();
        return new CollisionTable(tileLoader.buildCollisionTable(tiles));
    }
<<<<<<< HEAD
// Creation de larena
    private static Arena createArena(TileMap tileMap) {
        Arena arena = new Arena();
        // Création de l’équipe bleue avec base et fontaine
=======

    private static Arena createArena(TileMap tileMap) {
        Arena arena = new Arena();

>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
        Base blueBase = new Base(5000);
        Fontaine blueFontaine = new Fontaine(new Vec2(5, 95), 100, 50);
        Equipe blueTeam = new Equipe("Radiant", TeamColor.BLUE, blueBase, blueFontaine);

<<<<<<< HEAD
        // Création de l’équipe rouge  (pareil)
        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);
        // Initialisation de l’arène avec la map et les équipes
=======
        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);

>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
        arena.initializeFromMap(tileMap, blueTeam, redTeam);

        return arena;
    }

    private static Player createPlayer(KeyHandler keyHandler, MouseHandler mouseHandler,
                                       TileMap tileMap, CollisionTable collisionTable, 
                                       Arena arena, Hero hero) {
<<<<<<< HEAD
    	  // création du joueur avec la map, collisions, arène et héro choisi et l'ajouter 
=======
>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
        Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);
        arena.ajouterUnite(player);
        return player;
    }
}
<<<<<<< HEAD

=======
>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
