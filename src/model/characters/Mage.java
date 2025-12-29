package model.characters;

/**
 * מחלקה המייצגת קוסם במשחק.
 * יורשת מ-Character.
 * הקוסם מתמחה בכישופים ובשימוש במאנה.
 */
public class Mage extends Character {
    
    private int spellPower;
    private static final int FIREBALL_MANA_COST = 25;
    private static final int HEAL_MANA_COST = 30;
    
    public Mage(String name) {
        // קוסם: מעט חיים, הרבה מאנה, כוח נמוך, הגנה נמוכה
        super(name, 80, 150, 5, 3);
        this.spellPower = 20;
    }
    
    // ============================================================
    // TODO: מימוש מתודות אבסטרקטיות
    // ============================================================

    @Override
    protected void onLevelUp() {
        maxHealth += 8;
        maxMana += 25;
        baseStrength += 1;
        baseDefense += 1;
        spellPower += 5;

        currentHealth = maxHealth;
        currentMana = maxMana;
    }

    @Override
    public int calculateAttackDamage() {
        int damage = baseStrength;
        if (equippedWeapon != null) {
            damage += equippedWeapon.calculateDamage();
        }
        return damage;
    }

    @Override
    public boolean useSpecialAbility(Character target) {
        if (currentMana < FIREBALL_MANA_COST) {
            return false;
        }

        useMana(FIREBALL_MANA_COST);
        int damage = (int) Math.ceil(spellPower * 1.5);
        target.takeDamage(damage);
        return true;
    }
    
    // ============================================================
    // TODO: מתודות ייחודיות לקוסם
    // ============================================================

    public boolean castHeal() {
        if (currentMana < HEAL_MANA_COST) {
            return false;
        }

        useMana(HEAL_MANA_COST);
        heal(spellPower);
        return true;
    }

    public int castManaShield(int incomingDamage) {
        int maxAbsorb = currentMana * 2;

        if (maxAbsorb >= incomingDamage) {
            int manaNeeded = (int) Math.ceil(incomingDamage / 2.0);
            useMana(manaNeeded);
            return 0;
        } else {
            int damageReduced = maxAbsorb;
            currentMana = 0;
            return incomingDamage - damageReduced;
        }
    }

    public int calculateSpellDamage(double multiplier) {
        return (int) Math.ceil(spellPower * multiplier);
    }
    
    // Getters
    public int getSpellPower() {
        return spellPower;
    }
    
    @Override
    public String toString() {
        return "Mage: " + super.toString() + 
               String.format(" | Spell Power: %d", spellPower);
    }
}
