package Core.Moba.Items;

import Core.Moba.Combat.StatsModifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe pour les équipements
 */
public class Equipement {
    private final String nom;
    private final int prix;
    private final EquipementTier tier;
    private final StatsModifier bonus;
    private final List<String> composants;

    public Equipement(String nom, int prix, EquipementTier tier, StatsModifier bonus, List<String> composants) {
        this.nom = Objects.requireNonNull(nom, "nom");
        if (nom.isBlank()) throw new IllegalArgumentException("nom cannot be blank");
        this.prix = Math.max(0, prix);
        this.tier = Objects.requireNonNull(tier, "tier");
        this.bonus = bonus == null ? StatsModifier.none() : bonus;
        // liste non modifiable pour protéger les données
        this.composants = composants == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(composants));
    }

    public String nom() { return nom; }
    public int prix() { return prix; }
    public EquipementTier tier() { return tier; }
    public StatsModifier bonus() { return bonus; }
    public List<String> composants() { return composants; }

    /**
     * Fusion d'équipements
     * Permet de créer un équipement à partir d'une liste d'objets
     * @param nomFinal Nom du nouvel objet créé.
     * @param prixFinal Prix de vente de l'objet fini.
     * @param bonusFinal Statistiques cumulées de l'objet fini.
     * @param items Liste des équipements consommés pour la fusion.
     * @return Un nouvel équipement de tier FINAL
     */
    public static Equipement fusionner(String nomFinal, int prixFinal, StatsModifier bonusFinal, List<Equipement> items) {
        Objects.requireNonNull(nomFinal, "nomFinal");
        if (nomFinal.isBlank()) throw new IllegalArgumentException("nomFinal cannot be blank");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items cannot be empty");

        List<String> names = new ArrayList<>();
        for (Equipement e : items) {
            if (e != null) names.add(e.nom());
        }
        return new Equipement(nomFinal, Math.max(0, prixFinal), EquipementTier.FINAL,
                bonusFinal == null ? StatsModifier.none() : bonusFinal, names);
    }
}