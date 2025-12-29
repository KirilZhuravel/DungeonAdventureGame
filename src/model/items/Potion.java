package model.items;

import model.characters.Character;

/**
 * מחלקה המייצגת שיקוי במשחק.
 * יורשת מ-Item וגם מממשת את Usable.
 */
public class Potion extends Item implements Usable {
    
    private PotionType potionType;
    private int potency;
    private int remainingUses;
    private final int maxUses;
    
    /**
     * סוגי שיקויים במשחק
     */
    public enum PotionType {
        HEALTH("Restores health points"),
        MANA("Restores mana points"),
        STRENGTH("Temporarily increases strength"),
        DEFENSE("Temporarily increases defense");
        
        private final String effect;
        
        PotionType(String effect) {
            this.effect = effect;
        }
        
        public String getEffect() {
            return effect;
        }
    }
    
    public Potion(String name, String description, int basePrice, ItemRarity rarity,
                  PotionType potionType, int potency, int maxUses) {
        super(name, description, 1, basePrice, rarity); // Potions weigh 1
        this.potionType = potionType;
        this.potency = potency;
        this.maxUses = maxUses;
        this.remainingUses = maxUses;
    }
    
    // ============================================================
    // TODO: מימוש ממשק Usable
    // ============================================================

    @Override
    public boolean use(Character target) {
        if (!canUse(target)) {
            return false;
        }

        if (potionType == PotionType.HEALTH) {
            target.heal(potency);
        } else if (potionType == PotionType.MANA) {
            target.restoreMana(potency);
        }

        remainingUses--;
        return true;
    }

    @Override
    public boolean canUse(Character target) {
        if (remainingUses <= 0) {
            return false;
        }

        if (potionType == PotionType.HEALTH) {
            return target.getCurrentHealth() < target.getMaxHealth();
        } else if (potionType == PotionType.MANA) {
            return target.getCurrentMana() < target.getMaxMana();
        }

        return true;
    }
    @Override
    public int getRemainingUses() {
        return remainingUses;
    }

    @Override
    public boolean isSellable() {
        return remainingUses == maxUses;
    }
    
    // Getters
    public PotionType getPotionType() {
        return potionType;
    }
    
    public int getPotency() {
        return potency;
    }
    
    public int getMaxUses() {
        return maxUses;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Type: %s | Potency: %d | Uses: %d/%d",
            super.toString(), potionType, potency, remainingUses, maxUses);
    }
}
