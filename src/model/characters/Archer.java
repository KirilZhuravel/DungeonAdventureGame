package model.characters;

/**
 * מחלקה המייצגת קשת במשחק.
 * יורשת מ-Character.
 * הקשת מתמחה בהתקפות מרחוק ובסיכוי לפגיעה קריטית.
 */
public class Archer extends Character {
    
    private double criticalChance;
    private double criticalMultiplier;
    private int arrows;
    private static final int MAX_ARROWS = 30;
    private static final int MULTISHOT_ARROW_COST = 3;
    
    public Archer(String name) {
        // קשת: חיים בינוניים, מאנה בינונית, כוח בינוני, הגנה נמוכה
        super(name, 100, 80, 12, 5);
        this.criticalChance = 0.15; // 15% סיכוי לקריטי
        this.criticalMultiplier = 2.0; // נזק כפול בקריטי
        this.arrows = MAX_ARROWS;
    }
    
    // ============================================================
    // TODO: מימוש מתודות אבסטרקטיות
    // ============================================================

    @Override
    protected void onLevelUp() {
        maxHealth += 12;
        maxMana += 10;
        baseStrength += 2;
        baseDefense += 1;

        criticalChance += 0.02;
        if (criticalChance > 0.5) {
            criticalChance = 0.5;
        }

        arrows = MAX_ARROWS;
        currentHealth = maxHealth;
        currentMana = maxMana;
    }

    @Override
    public int calculateAttackDamage() {
        int damage = baseStrength;
        if (equippedWeapon != null) {
            damage += equippedWeapon.calculateDamage();
        }

        if (Math.random() < criticalChance) {
            damage *= criticalMultiplier;
            System.out.println(name + " landed a CRITICAL HIT!");
        }

        return damage;
    }

    @Override
    public boolean useSpecialAbility(Character target) {
        if (arrows < MULTISHOT_ARROW_COST) {
            System.out.println(name + " doesn't have enough arrows for Multishot!");
            return false;
        }

        arrows -= MULTISHOT_ARROW_COST;
        System.out.println(name + " uses Multishot!");

        for (int i = 0; i < 3; i++) {

            int baseDamage = baseStrength;
            if (equippedWeapon != null) {
                baseDamage += equippedWeapon.calculateDamage();
            }


            int shotDamage = (int) (baseDamage * 0.7);


            if (Math.random() < criticalChance) {
                shotDamage *= criticalMultiplier;
                System.out.println("-> Arrow " + (i + 1) + " CRITICAL!");
            }

            target.takeDamage(shotDamage);
        }

        return true;
    }
    
    // ============================================================
    // TODO: מתודות ייחודיות לקשת
    // ============================================================

    public int shootArrow(Character target) {
        if (arrows <= 0) {
            System.out.println(name + " is out of arrows!");
            return -1;
        }

        arrows--;
        int damage = calculateAttackDamage();
        target.takeDamage(damage);
        return damage;
    }

    public boolean refillArrows() {
        int missingArrows = MAX_ARROWS - arrows;
        if (missingArrows <= 0) {
            return false;
        }

        int cost = missingArrows * 5;
        if (spendGold(cost)) {
            arrows = MAX_ARROWS;
            System.out.println(name + " bought " + missingArrows + " arrows for " + cost + " gold.");
            return true;
        } else {
            System.out.println("Not enough gold to refill arrows (Cost: " + cost + ")");
            return false;
        }
    }
    public boolean evasiveManeuver() {
        if (!useMana(15)) {
            return false;
        }

        double dodgeChance = criticalChance * 1.5;
        if (Math.random() < dodgeChance) {
            System.out.println(name + " performed an evasive maneuver!");
            return true;
        }

        System.out.println(name + " failed to evade.");
        return false;
    }
    
    // Getters
    public double getCriticalChance() {
        return criticalChance;
    }
    
    public double getCriticalMultiplier() {
        return criticalMultiplier;
    }
    
    public int getArrows() {
        return arrows;
    }
    
    public int getMaxArrows() {
        return MAX_ARROWS;
    }
    
    @Override
    public String toString() {
        return "Archer: " + super.toString() + 
               String.format(" | Arrows: %d/%d | Crit: %.0f%%", 
                   arrows, MAX_ARROWS, criticalChance * 100);
    }
}
