package Core.Moba.Units;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.World.Equipe;
import Core.Moba.World.TeamColor;
import Core.Moba.World.Vec2;

import java.util.List;
import java.util.Objects;

/**
 * Système d'intelligence artificielle qui contrôle la Tour.
 * Gère la détection des ennemis, la priorité de ciblage (Minions avant Héros),
 * et l'augmentation progressive des dégâts lors d'attaques consécutives.
 */

public final class TowerAI {
    private final Tour tour;
    private Object cible;
    private double cooldownAttaque;
    private double tempsDerniereAttaque;
    private int hitsConsecutifs;
     private static final double COOLDOWN_BASE = 3.0;
    private static final double TEMPS_AGGRO = 5.0;
    private double timerAggro;

    public TowerAI(Tour tour) {
        this.tour = Objects.requireNonNull(tour, "tour");
        this.cooldownAttaque = COOLDOWN_BASE;
        this.tempsDerniereAttaque = 0;
        this.hitsConsecutifs = 0;
        this.timerAggro = 0;
    }

    public Object cible() {
        return cible;
    }

    /**
     * Calcule les dégâts infligés
     * Plus la tour tire sur la même cible, plus le multiplicateur augmente (+10% par coup)
     * @return Dégâts finaux après hits consécutifs
     */

    public int calculerDegats() {
        int baseDegats = tour.stats().attack();
        double multiplicateur = 1.0 + (hitsConsecutifs * 0.1);
        hitsConsecutifs++;
        return (int) (baseDegats * multiplicateur);
    }

    public void resetHitsConsecutifs() {
        hitsConsecutifs = 0;
    }

    public void mettreAJour(double deltaSeconds, List<Object> unites) {
        if (tour.estMorte()) {
            cible = null;
            return;
        }

        timerAggro -= deltaSeconds;

        boolean cibleValide = false;
        if (cible != null) {
            if (cible instanceof Unite unite) {
                cibleValide = !unite.estMorte() && estDansPortee(cible);
            } else if (cible instanceof Player player) {
                cibleValide = !player.estMorte() && estDansPortee(cible);
            }
        }

        if (timerAggro > 0 && cibleValide) {
            return;
        }

        if (cibleValide) {
            return;
        }

        timerAggro = 0;
        cible = null;
        chercherCible(unites);
    }
    
    public boolean peutAttaquer() {
        if (cible == null) return false;
        if (cible instanceof Unite unite) {
            return !unite.estMorte() && estDansPortee(cible);
        } else if (cible instanceof Player player) {
            return !player.estMorte() && estDansPortee(cible);
        }
        return false;
    }
    
    public boolean doitAttaquer(double deltaSeconds) {
        // Update cooldown
        if (tempsDerniereAttaque > 0) {
            tempsDerniereAttaque -= deltaSeconds;
        }
        
        // If target is invalid, reset to idle if we're in attack animation
        if (cible == null || 
            (cible instanceof Unite unite && unite.estMorte()) ||
            (cible instanceof Player player && player.estMorte())) {
            if (tour.getCurrentFrame() > 13) {
                tour.setIdle();
                tour.resetAttackReady();
            }
            return false;
        }
        
        // If attack animation is complete, signal to fire
        if (tour.isAttackReady()) {
            return true;
        }
        
        // If tower is idle and ready to attack (cooldown expired), start attack animation
        if (tempsDerniereAttaque <= 0 && peutAttaquer()) {
            int frame = tour.getCurrentFrame();
            if (frame >= 6 && frame <= 13) { // Idle frame range
                tour.startAttackAnimation();
                tempsDerniereAttaque = cooldownAttaque;
            }
        }
        
        return false;
    }

    /**
     * Algorithme de recherche de cible avec priorités :
     * 1. Minions ennemis les plus proches (pour protéger les héros)
     * 2. Héros ennemis si aucun minion n'est présent
     */

    private void chercherCible(List<Object> unites) {
        Object cibleCreep = null;
        Object cibleHeros = null;
        double distMinCreep = Double.MAX_VALUE;
        double distMinHeros = Double.MAX_VALUE;

        for (Object unite : unites) {
            if (unite == tour) continue;
            
            Equipe equipe = getEquipe(unite);
            if (equipe == null) continue;
            if (equipe == tour.equipe()) continue;
            
            boolean estMort = estMort(unite);
            if (estMort) continue;

            if (!estDansPortee(unite)) continue;

            double dist = distanceVers(unite);

            if (unite instanceof Minion || unite instanceof Creep) {
                if (dist < distMinCreep) {
                    distMinCreep = dist;
                    cibleCreep = unite;
                }
            } else if (unite instanceof Heros || unite instanceof Player) {
                if (dist < distMinHeros) {
                    distMinHeros = dist;
                    cibleHeros = unite;
                }
            }
        }

        boolean backdoor = verifBackdoor();

        if (!backdoor && cibleCreep != null) {
            cible = cibleCreep;
            System.out.println("Tour " + tour + " cible un creep");
            return;
        }

        if (cibleHeros != null) {
            cible = cibleHeros;
            System.out.println("Tour " + tour + " cible un hero/player! Distance: " + distMinHeros);
            return;
        }

        if (cibleCreep != null) {
            cible = cibleCreep;
        }
    }

    private boolean verifBackdoor() {
        return false;
    }

    private boolean estDansPortee(Object unite) {
        if (unite == null) return false;
        double dist = distanceVers(unite);
        return dist <= tour.portee();
    }

    private double distanceVers(Object unite) {
        if (unite == null) return Double.MAX_VALUE;
        int tileSize = Config.getTileSize();
        Vec2 posTourTiles = tour.position();
        Vec2 posTour = new Vec2(posTourTiles.x() * tileSize, posTourTiles.y() * tileSize);
        Vec2 posCible = getPosition(unite);
        if (posCible == null) return Double.MAX_VALUE;
        double dx = posCible.x() - posTour.x();
        double dy = posCible.y() - posTour.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Equipe getEquipe(Object obj) {
        if (obj instanceof Unite unite) {
            return unite.equipe();
        } else if (obj instanceof Player player) {
            return player.equipe();
        }
        return null;
    }

    private Vec2 getPosition(Object obj) {
        int tileSize = Config.getTileSize();
        if (obj instanceof Unite unite) {
            Vec2 pos = unite.position();
            return new Vec2(pos.x() * tileSize, pos.y() * tileSize);
        } else if (obj instanceof Player player) {
            return new Vec2(player.getX(), player.getY());
        }
        return null;
    }

    private boolean estMort(Object obj) {
        if (obj instanceof Unite unite) {
            return unite.estMorte();
        } else if (obj instanceof Player player) {
            return player.estMorte();
        }
        return true;
    }
}
