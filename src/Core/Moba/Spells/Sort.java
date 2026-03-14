package Core.Moba.Spells;

import Core.Moba.Units.Unite;

import java.util.Objects;

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
     * Core-only placeholder: applyEffect can be implemented later per spell type.
     */
    public boolean lancer(SortContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        if (estEnCooldown()) return false;
        if (!ctx.caster().stats().spendMana(coutMana)) return false;
        applyEffect(ctx);
        cooldownRestant = cooldownSeconds;
        return true;
    }

    protected void applyEffect(SortContext ctx) {
        // Default: basic nuke for demo purposes (scales with level).
        Unite target = ctx.target();
        if (target != null) {
            int dmg = 20 + (niveau - 1) * 10;
            target.subirDegats(dmg);
        }
    }
}

