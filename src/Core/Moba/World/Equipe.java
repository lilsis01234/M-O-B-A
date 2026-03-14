package Core.Moba.World;

import java.util.Objects;

/**
 * Cette classe représente les deux équipes
 * @author RAHARIMANANA Tianantenaina
 * @version 1.0
 */
public final class Equipe {
    private final String nom;
    private final TeamColor couleur;
    private final Base base;
    private final Fontaine fontaine;

    public Equipe(String nom, TeamColor couleur, Base base, Fontaine fontaine) {
        this.nom = Objects.requireNonNull(nom, "nom");
        if (nom.isBlank()) throw new IllegalArgumentException("nom cannot be blank");
        this.couleur = Objects.requireNonNull(couleur, "couleur");
        this.base = Objects.requireNonNull(base, "base");
        this.fontaine = Objects.requireNonNull(fontaine, "fontaine");
    }

    public String nom() {
        return nom;
    }

    public TeamColor couleur() {
        return couleur;
    }

    public Base base() {
        return base;
    }

    public Fontaine fontaine() {
        return fontaine;
    }
}

