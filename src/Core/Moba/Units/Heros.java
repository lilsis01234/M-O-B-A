package Core.Moba.Units;

import Core.Moba.Combat.Stats;
import Core.Moba.Items.Equipement;
import Core.Moba.Spells.Sort;
import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Heros extends Unite {
    private final String nom;
    private final Equipe equipe;

    private int niveau;
    private int xp;
    private int gold;

    private final List<Sort> sorts;
    private final List<Equipement> inventaire;

    private final RecallState recall;
    private final RespawnTimer respawn;

    public Heros(String nom, Equipe equipe, Vec2 position, Stats stats) {
        super(position, stats);
        this.nom = Objects.requireNonNull(nom, "nom");
        if (nom.isBlank()) throw new IllegalArgumentException("nom cannot be blank");
        this.equipe = Objects.requireNonNull(equipe, "equipe");
        this.niveau = 1;
        this.xp = 0;
        this.gold = 0;
        this.sorts = new ArrayList<>(3);
        this.inventaire = new ArrayList<>(6);
        this.recall = new RecallState(4.0);
        this.respawn = new RespawnTimer();
    }

    public String nom() {
        return nom;
    }

    public Equipe equipe() {
        return equipe;
    }

    public int niveau() {
        return niveau;
    }

    public int xp() {
        return xp;
    }

    public int gold() {
        return gold;
    }

    public List<Sort> sorts() {
        return Collections.unmodifiableList(sorts);
    }

    public List<Equipement> inventaire() {
        return Collections.unmodifiableList(inventaire);
    }

    public RecallState recall() {
        return recall;
    }

    public RespawnTimer respawn() {
        return respawn;
    }

    public void update(double deltaSeconds) {
        for (Sort s : sorts) {
            s.update(deltaSeconds);
        }
        recall.update(deltaSeconds);
        respawn.update(deltaSeconds);
    }

    public boolean ajouterSort(Sort sort) {
        Objects.requireNonNull(sort, "sort");
        if (sorts.size() >= 3) return false;
        return sorts.add(sort);
    }

    public void gagnerGold(int amount) {
        gold += Math.max(0, amount);
    }

    public void gagnerXp(int amount) {
        xp += Math.max(0, amount);
        while (xp >= xpPourNiveauSuivant()) {
            xp -= xpPourNiveauSuivant();
            niveau++;
        }
    }

    public int xpPourNiveauSuivant() {
        return 100 + (niveau - 1) * 50;
    }

    public boolean acheter(Equipement item) {
        Objects.requireNonNull(item, "item");
        if (inventaire.size() >= 6) return false;
        if (gold < item.prix()) return false;
        gold -= item.prix();
        inventaire.add(item);
        stats().applyModifier(item.bonus());
        return true;
    }

    public boolean fusionnerEquipements(String nomFinal, int prixFinal, Equipement a, Equipement b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (!inventaire.contains(a) || !inventaire.contains(b)) return false;

        Equipement fused = Equipement.fusionner(nomFinal, prixFinal, a.bonus(), List.of(a, b));
        inventaire.remove(a);
        inventaire.remove(b);
        inventaire.add(fused);
        stats().applyModifier(fused.bonus());
        return true;
    }

    public void demarrerRecall() {
        recall.demarrerRecall();
    }

    public void appliquerRecallSiTermine() {
        if (!recall.isRecalling()) return;
        if (!recall.estTermine()) return;
        setPosition(equipe.fontaine().position());
        recall.annulerRecall();
    }

    public void mourir(double delaiReapparitionSecondes) {
        if (estMorte()) {
            respawn.start(delaiReapparitionSecondes);
        }
    }
}

