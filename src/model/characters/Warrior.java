package model.characters;

/**
 * מחלקה המייצגת לוחם במשחק.
 * יורשת מ-Character.
 * הלוחם מתמחה בהתקפות פיזיות חזקות ובהגנה גבוהה.
 */
public class Warrior extends Character {
    
    private int rage;
    private static final int MAX_RAGE = 100;
    private static final int RAGE_PER_HIT = 10;
    private static final int BERSERK_RAGE_COST = 50;
    
    public Warrior(String name) {
        // לוחם: הרבה חיים, מעט מאנה, כוח גבוה, הגנה גבוהה
        super(name, 150, 30, 15, 10);
        this.rage = 0;
    }
    
    // ============================================================
    // TODO: מימוש מתודות אבסטרקטיות
    // ============================================================

    @Override
    protected void onLevelUp() {
        maxHealth += 20;
        maxMana += 5;
        baseStrength += 3;
        baseDefense += 2;

        currentHealth = maxHealth;
        currentMana = maxMana;
    }

    @Override
    public int calculateAttackDamage() {
        int weaponDamage = (equippedWeapon != null) ? equippedWeapon.calculateDamage() : 0;
        int rageBonus = rage / 10;
        return baseStrength + weaponDamage + rageBonus;
    }

    @Override
    public boolean useSpecialAbility(Character target) {
        if (rage >= BERSERK_RAGE_COST) {
            rage -= BERSERK_RAGE_COST;
            int damage = calculateAttackDamage() * 2;
            target.takeDamage(damage);
            System.out.println(name + " used BERSERK on " + target.getName() + " for " + damage + " damage!");
            return true;
        }
        return false;
    }
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        rage = Math.min(MAX_RAGE, rage + RAGE_PER_HIT);
    }
    
    // ============================================================
    // מתודות ייחודיות ללוחם
    // ============================================================

    public boolean shieldBlock(int incomingDamage) {
        if (useMana(20)) {
            int reducedDamage = (int) (incomingDamage * 0.25);
            super.takeDamage(reducedDamage);
            System.out.println(name + " blocked with shield! Reduced damage to " + reducedDamage);
            return true;
        }
        return false;
    }
    
    // Getters
    public int getRage() {
        return rage;
    }
    
    public int getMaxRage() {
        return MAX_RAGE;
    }
    
    @Override
    public String toString() {
        return "Warrior: " + super.toString() + 
               String.format(" | Rage: %d/%d", rage, MAX_RAGE);
    }
}
