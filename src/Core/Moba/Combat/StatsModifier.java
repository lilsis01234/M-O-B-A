package Core.Moba.Combat;

public record StatsModifier(
        int bonusMaxHp,
        int bonusMaxMana,
        int bonusAttack,
        int bonusDefense,
        double bonusMoveSpeed
) {
    public static StatsModifier none() {
        return new StatsModifier(0, 0, 0, 0, 0);
    }
}

