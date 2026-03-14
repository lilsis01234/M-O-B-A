package Engine;

import Core.Entity.Player;
import Core.Moba.Units.Tour;
import Core.Moba.Units.TowerProjectile;
import Core.Moba.Units.Unite;
import Core.Moba.World.Arena;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Engine.Input.KeyHandler;
import Engine.Input.MouseHandler;
import Engine.Render.Camera;
import Engine.Render.ClickEffect;
import Core.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine {

    private final Player player;
    private final Camera camera;
    private final MouseHandler mouseHandler;
    private final Arena arena;
    private final List<ClickEffect> clickEffects = new ArrayList<>();
    private final List<TowerProjectile> tousProjectiles = new ArrayList<>();

    private Thread gameThread;
    private boolean running = false;

    public GameEngine(Player player, Camera camera, MouseHandler mouseHandler, Arena arena) {
        this.player = player;
        this.camera = camera;
        this.mouseHandler = mouseHandler;
        this.arena = arena;
    }

    public void start() {
        if (!running) { // verifié si le jeu n'est pas deja lancé //
            running = true;// active le moteur 
            gameThread = new Thread(this::gameLoop); // création d'un nouveau thread qui s'occupera de gamelop
            gameThread.start();
        }
    }

    public void stop() { // arrete le moteur du jeu 
        running = false;
        gameThread = null;
    }

    private void gameLoop() { //boucle principal 
        long lastTime = System.nanoTime(); // pour stocker le temps de la derniere frame

        while (running) {
            long currentTime = System.nanoTime();// recupere le temp actuel
            long elapsedTime = currentTime - lastTime;// calcul du temps ecoulé 

            if (elapsedTime >= Config.getNanosecondsPerFrame()) { // verifi si accez de temps est passé pour faire une nouvelle frame
                lastTime = currentTime;
                update();
            }
        }
    }
//mise a jour general
    private void update() {
        double deltaSeconds = 1.0 / 60.0; // temps entre chaque frame 
        
        if (player.justRespawned()) { // vérifie si le joueur vient de respawn (aprés la mort)

            camera.centerOn((float) player.getX(), (float) player.getY()); // centrer la cam sur le joueur 
            player.clearJustRespawned(); // desactiver le signe (reset) de respawn
        }
        //mise a jour de : 
        camera.updatePlayerPosition((float) player.getX(), (float) player.getY());// La position du player envers la cam
        updateCamera();// de la camera 
        updateClickEffects();//des effets en cliquant 
        updateTowers(deltaSeconds);// les tours
        player.update();// joueur (ses mouvements ainsi que actions)
    }

    private void updateTowers(double deltaSeconds) {// met à jour toutes les tours et leurs attaques

        List<Object> unites = arena.unites();
     
        for (Tour tour : arena.tours()) {
            tour.ai().mettreAJour(deltaSeconds, unites);// met à jour  l IA de la tour
            // Si la tour doit attaquer alors calculer les degats  et recup la cible 
            if (tour.ai().doitAttaquer(deltaSeconds)) {
                int degats = tour.ai().calculerDegats();
                Object cible = tour.ai().cible();
                if (cible != null) {
                	 // affiche dans la console que la tour attaque
                    System.out.println("Tour " + tour.equipe().couleur() + " at " + tour.position() + " firing at " + cible.getClass().getSimpleName());
                     TowerProjectile projectile = new TowerProjectile(tour, cible, degats);//vers cible
                     tour.ajouterProjectile(projectile);
                     tousProjectiles.add(projectile);
                     // Réinitialise le drapeau d’attaque de la tour après le tir, l’animation continue jusqu’à la frame 20 puis revient automatiquement à l’état idle
                     tour.resetAttackReady();
                }
            }
            
            tour.mettreAJourProjectiles(deltaSeconds);
        }
        
        tousProjectiles.removeIf(p -> p.aFini());
    }

    private void updateCamera() {
        boolean isMovingWithWASD = player.isMovingWithWASD();
        if (isMovingWithWASD) {//  verifier le suivi de la camera
            camera.setFollowPlayer(true);
        } else {
            camera.setFollowPlayer(false);
        }
        camera.update(mouseHandler.getCurrentX(), mouseHandler.getCurrentY());
        camera.zoom(mouseHandler.getWheelRotation());//zoom selon la molette
    }

    private void updateClickEffects() {
        if (mouseHandler.hasNewClick()) { // crée un effet visuel à la position du clic
        	//effet visuel à la position du clic
            clickEffects.add(new ClickEffect(mouseHandler.getLastClickWorldX(), mouseHandler.getLastClickWorldY()));
            mouseHandler.clearNewClick();
        }
     // Met à jour et supprime les effets visuels déclenchés par les clics de souris
        Iterator<ClickEffect> it = clickEffects.iterator();
        while (it.hasNext()) {
            ClickEffect effect = it.next();
            effect.update();
            if (effect.isDead()) {
                it.remove();
            }
        }
    }

    public List<ClickEffect> getClickEffects() {
        return clickEffects;
    }

    public List<TowerProjectile> getProjectiles() {
        return tousProjectiles;
    }
 // centre MANUELLEMENT la caméra sur le joueur 
    public void centerCameraOnPlayer() {
        camera.centerOn((float) player.getX(), (float) player.getY());
    }
}

