package Core.Moba.Combat;

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
