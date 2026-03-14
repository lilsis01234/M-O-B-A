package Engine.Render;

import Core.Entity.HitboxUtils;
import Core.Entity.Player;
import Core.Moba.Units.Ancient;
import Core.Moba.Units.Tour;

import java.awt.*;

public class DebugRenderer {

    private static final Color HITBOX_COLOR = new Color(255, 0, 255, 100);
    private static final Color HITBOX_BORDER = new Color(255, 0, 255, 200);
    private static final Color COLLISION_BOX_COLOR = new Color(255, 255, 0, 100);
    private static final Color COLLISION_BOX_BORDER = new Color(255, 255, 0, 200);
    
    private boolean showHitboxes = true;
    private boolean showCollisionBoxes = true;

    // bascule affichage hitboxes
    public void toggleHitboxes() {
        showHitboxes = !showHitboxes;
    }

    // bascule affichage collision boxes
    public void toggleCollisionBoxes() {
        showCollisionBoxes = !showCollisionBoxes;
    }

    // definir affichage hitboxes
    public void setShowHitboxes(boolean show) {
        this.showHitboxes = show;
    }

    // definir affichage collision boxes
    public void setShowCollisionBoxes(boolean show) {
        this.showCollisionBoxes = show;
    }

    // rendu debug pour joueur, tours et ancients
    public void render(Graphics2D g2, Player player, Iterable<Tour> towers, Iterable<Ancient> ancients) {
        if (!showHitboxes && !showCollisionBoxes) return;

        renderPlayerDebug(g2, player);

        for (Tour tower : towers) {
            renderTowerDebug(g2, tower);
        }

        for (Ancient ancient : ancients) {
            renderAncientDebug(g2, ancient);
        }
    }

    // rendu debug joueur
    private void renderPlayerDebug(Graphics2D g2, Player player) {
        double px = player.getX();
        double py = player.getY();

        if (showHitboxes) {
            HitboxUtils.Hitbox hitbox = HitboxUtils.createEntityHitbox(px, py);
            drawBox(g2, hitbox, HITBOX_COLOR, HITBOX_BORDER, "HIT");
        }

        if (showCollisionBoxes) {
            HitboxUtils.Hitbox collisionBox = HitboxUtils.createEntityCollisionBox(px, py);
            drawBox(g2, collisionBox, COLLISION_BOX_COLOR, COLLISION_BOX_BORDER, "COL");
        }
    }

    // rendu debug tour
    private void renderTowerDebug(Graphics2D g2, Tour tower) {
        int tileSize = Core.Config.getTileSize();
        double px = tower.position().x() * tileSize;
        double py = tower.position().y() * tileSize;
        int width = tower.width();
        int height = tower.height();

        if (showHitboxes) {
            HitboxUtils.Hitbox hitbox = HitboxUtils.createTowerHitbox(
                tower.position().x(), tower.position().y(), width, height);
            drawBox(g2, hitbox, HITBOX_COLOR, HITBOX_BORDER, "HIT");
        }

        if (showCollisionBoxes) {
            HitboxUtils.Hitbox collisionBox = HitboxUtils.createTowerCollisionBox(
                tower.position().x(), tower.position().y(), width, height);
            drawBox(g2, collisionBox, COLLISION_BOX_COLOR, COLLISION_BOX_BORDER, "COL");
        }
    }

    // rendu debug ancient
    private void renderAncientDebug(Graphics2D g2, Ancient ancient) {
        int tileSize = Core.Config.getTileSize();
        double px = ancient.position().x() * tileSize;
        double py = ancient.position().y() * tileSize;
        int width = ancient.width();
        int height = ancient.height();

        if (showHitboxes) {
            HitboxUtils.Hitbox hitbox = HitboxUtils.createAncientHitbox(
                ancient.position().x(), ancient.position().y(), width, height);
            drawBox(g2, hitbox, HITBOX_COLOR, HITBOX_BORDER, "HIT");
        }

        if (showCollisionBoxes) {
            HitboxUtils.Hitbox collisionBox = HitboxUtils.createAncientCollisionBox(
                ancient.position().x(), ancient.position().y(), width, height);
            drawBox(g2, collisionBox, COLLISION_BOX_COLOR, COLLISION_BOX_BORDER, "COL");
        }
    }

    // dessiner un rectangle avec couleur et label
    private void drawBox(Graphics2D g2, HitboxUtils.Hitbox box, Color fill, Color border, String label) {
        int x = (int) box.getLeft();
        int y = (int) box.getTop();
        int w = (int) box.getWidth();
        int h = (int) box.getHeight();

        g2.setColor(fill);
        g2.fillRect(x, y, w, h);
        
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, w, h);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawString(label, x + 2, y + 12);
    }

    // savoir si hitboxes visibles
    public boolean isShowingHitboxes() {
        return showHitboxes;
    }

    // savoir si collision boxes visibles
    public boolean isShowingCollisionBoxes() {
        return showCollisionBoxes;
    }
}