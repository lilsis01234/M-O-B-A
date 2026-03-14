package Core.Moba.Spells;

import Core.Moba.Units.Unite;

import java.util.Objects;
/**
 * Classe pour les sorts
 */

public class Sort {
    private final String nom;
    private final int coutMana;
    private final double cooldownSeconds;

    private int niveau;
    private double cooldownRestant;

    public Sort(String nom, int coutMana, double cooldownSeconds) {
        this.nom = Objects.requireNonNull(nom, "nom");
        if (nom.isBlank()) throw new IllegalArgumentException("nom cannot be blank");
        this.coutMana = Math.max(0, coutMana);
        this.cooldownSeconds = Math.max(0, cooldownSeconds);
        this.niveau = 1;
        this.cooldownRestant = 0;
    }

    public String nom() {
        return nom;
    }

    public int coutMana() {
        return coutMana;
    }

    public double cooldownSeconds() {
        return cooldownSeconds;
    }

    public int niveau() {
        return niveau;
    }

    public void ameliorer() {
        niveau++;
    }

    public boolean estEnCooldown() {
        return cooldownRestant > 0;
    }

    public double cooldownRestant() {
        return cooldownRestant;
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds <= 0) return;
        cooldownRestant = Math.max(0, cooldownRestant - deltaSeconds);
    }

  /**
     * Pour le lancement du sort
     * @param ctx Contexte du sort (qui le lance, vers quelle cible/point).
     * @return true si le sort a été lancé avec succès, false sinon.
     */
    public boolean lancer(SortContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        if (estEnCooldown()) return false;
        if (!ctx.caster().stats().spendMana(coutMana)) return false;
        applyEffect(ctx);
        cooldownRestant = cooldownSeconds;
        return true;
    }

    /**
     * Applique l'effet du sort.
     * utilisée par des classes filles pour créer des sorts variés 
     * @param ctx Contexte d'exécution.
     */
    protected void applyEffect(SortContext ctx) {
        // Effet par défaut : inflige des dégâts de base scalés sur le niveau
        Unite target = ctx.target();
        if (target != null) {
            int dmg = 20 + (niveau - 1) * 10;
            target.subirDegats(dmg);
        }
    }
}

