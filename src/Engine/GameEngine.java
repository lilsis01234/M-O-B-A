package Engine;

import Core.Entity.Player;
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

    private Thread gameThread;
    private boolean running = false;

    public GameEngine(Player player, Camera camera, MouseHandler mouseHandler, Arena arena) {
        this.player = player;
        this.camera = camera;
        this.mouseHandler = mouseHandler;
        this.arena = arena;
    }

    public void start() {
        if (!running) {
            running = true;
            gameThread = new Thread(this::gameLoop);
            gameThread.start();
        }
    }

    public void stop() {
        running = false;
        gameThread = null;
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();

        while (running) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastTime;

            if (elapsedTime >= Config.getNanosecondsPerFrame()) {
                lastTime = currentTime;
                update();
            }
        }
    }

    private void update() {
        updateCamera();
        updateClickEffects();
        player.update();
    }

    private void updateCamera() {
        camera.update(mouseHandler.getCurrentX(), mouseHandler.getCurrentY());
        camera.zoom(mouseHandler.getWheelRotation());
    }

    private void updateClickEffects() {
        if (mouseHandler.hasNewClick()) {
            clickEffects.add(new ClickEffect(mouseHandler.getLastClickWorldX(), mouseHandler.getLastClickWorldY()));
            mouseHandler.clearNewClick();
        }

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
}
