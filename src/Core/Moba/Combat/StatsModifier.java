package Core.Moba.Combat;

/**
 * Représente un ensemble de bonus ou malus à appliquer aux statistiques d'une entité.
 * @param bonusMaxHp      Points de vie supplémentaires.
 * @param bonusMaxMana    Points de mana supplémentaires.
 * @param bonusAttack      Augmentation de la puissance d'attaque.
 * @param bonusDefense     Augmentation de la résistance.
 * @param bonusMoveSpeed   Modification de la vitesse de déplacement.
 */
public record StatsModifier(
        int bonusMaxHp,
        int bonusMaxMana,
        int bonusAttack,
        int bonusDefense,
        double bonusMoveSpeed
) {
    /**
     * Crée un modificateur neutre (valeurs à zéro).
     * Utile pour initialiser des variables ou quand il n'y a pas de bonus.
     * @return Un StatsModifier avec toutes les valeurs à 0.
     */
    public static StatsModifier none() {
        return new StatsModifier(0, 0, 0, 0, 0);
    }
}