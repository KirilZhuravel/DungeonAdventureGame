package model.characters;

import model.items.Weapon;
import model.items.Armor;
import model.items.Item;
import model.exceptions.InventoryFullException;
import model.exceptions.ItemNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * מחלקה אבסטרקטית המייצגת דמות במשחק.
 * מממשת את ממשק Attackable.
 * כל סוגי הדמויות (Warrior, Mage, Archer) יורשים ממחלקה זו.
 */
public abstract class Character implements Attackable {
    
    // Basic stats
    protected String name;
    protected int level;
    protected int experience;
    protected int gold;
    
    // Health & Mana
    protected int currentHealth;
    protected int maxHealth;
    protected int currentMana;
    protected int maxMana;
    
    // Combat stats
    protected int baseStrength;
    protected int baseDefense;
    
    // Equipment - HashMap מ-slot לשריון
    protected HashMap<Armor.ArmorSlot, Armor> equippedArmor;
    protected Weapon equippedWeapon;
    
    // Inventory - ArrayList של פריטים + Stack לפריטים אחרונים שהשתמשנו בהם
    protected ArrayList<Item> inventory;
    protected Stack<Item> recentlyUsedItems;
    protected final int maxInventorySize;
    
    // Constants
    protected static final int EXPERIENCE_PER_LEVEL = 100;
    protected static final int DEFAULT_INVENTORY_SIZE = 20;
    
    public Character(String name, int maxHealth, int maxMana, 
                     int baseStrength, int baseDefense) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.gold = 0;
        
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        
        this.baseStrength = baseStrength;
        this.baseDefense = baseDefense;
        
        this.equippedArmor = new HashMap<>();
        this.equippedWeapon = null;
        
        this.inventory = new ArrayList<>();
        this.recentlyUsedItems = new Stack<>();
        this.maxInventorySize = DEFAULT_INVENTORY_SIZE;
    }
    
    // ============================================================
    // TODO: מימוש ממשק Attackable
    // ============================================================

    @Override
    public void takeDamage(int damage) {
        int defense = getTotalDefense();
        // הנזק לא יכול להיות שלילי (לא מרפאים מאויב חלש)
        int finalDamage = Math.max(0, damage - defense);

        this.currentHealth -= finalDamage;

        // הבריאות לא תרד מתחת ל-0
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
    }

    @Override
    public boolean isAlive() {
        return currentHealth > 0;
    }
    
    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
    
    // ============================================================
    // TODO: ניהול מלאי (Inventory Management)
    // ============================================================

    public void addItem(Item item) throws InventoryFullException {
        if (inventory.size() >= maxInventorySize) {
            throw new InventoryFullException(item.getName(), maxInventorySize);
        }
        inventory.add(item);
    }

    public Item removeItem(String itemName) throws ItemNotFoundException {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                inventory.remove(item);
                return item;
            }
        }
        throw new ItemNotFoundException("Item '" + itemName + "' not found in inventory.");
    }

    public <T extends Item> ArrayList<T> findItemsByType(Class<T> itemClass) {
        ArrayList<T> result = new ArrayList<>();
        for (Item item : inventory) {
            if (itemClass.isInstance(item)) {
                result.add(itemClass.cast(item));
            }
        }
        return result;
    }

    public HashMap<Item.ItemRarity, ArrayList<Item>> getItemsByRarity() {
        HashMap<Item.ItemRarity, ArrayList<Item>> map = new HashMap<>();

        for (Item item : inventory) {
            Item.ItemRarity rarity = item.getRarity();
            map.putIfAbsent(rarity, new ArrayList<>());
            map.get(rarity).add(item);
        }

        return map;
    }
    
    // ============================================================
    // TODO: ציוד (Equipment)
    // ============================================================

    public void equipWeapon(Weapon weapon) throws ItemNotFoundException, InventoryFullException {
        if (!inventory.contains(weapon)) {
            throw new ItemNotFoundException("Weapon not found in inventory");
        }

        inventory.remove(weapon);
        if (equippedWeapon != null) {
            inventory.add(equippedWeapon);
        }
        equippedWeapon = weapon;
    }

    public void equipArmor(Armor armor) throws ItemNotFoundException, InventoryFullException {
        if (!inventory.contains(armor)) {
            throw new ItemNotFoundException("Armor not found in inventory");
        }

        Armor.ArmorSlot slot = armor.getSlot();

        inventory.remove(armor);


        if (equippedArmor.containsKey(slot)) {
            Armor oldArmor = equippedArmor.get(slot);
            inventory.add(oldArmor);
        }


        equippedArmor.put(slot, armor);
    }
    public int getTotalDefense() {
        int totalDefense = baseDefense;

        for (Armor armor : equippedArmor.values()) {
            totalDefense += armor.getDefense();
        }

        return totalDefense;
    }
    
    // ============================================================
    // TODO: ריפוי ומאנה
    // ============================================================

    public void heal(int amount) {
        if (amount > 0) {
            this.currentHealth += amount;

            if (this.currentHealth > this.maxHealth) {
                this.currentHealth = this.maxHealth;
            }
        }
    }

    public void restoreMana(int amount) {
        currentMana += amount;
        if (currentMana > maxMana) {
            currentMana = maxMana;
        }
    }

    public boolean useMana(int amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }
    
    // ============================================================
    // TODO: ניסיון ורמות (Experience & Leveling)
    // ============================================================

    public void gainExperience(int amount) {
        experience += amount;

        // לולאה למקרה שעולים כמה רמות בבת אחת
        while (experience >= EXPERIENCE_PER_LEVEL) {
            experience -= EXPERIENCE_PER_LEVEL;
            level++;

            // שיפור נתונים בסיסי בעליית רמה
            maxHealth += 10;
            currentHealth = maxHealth;
            maxMana += 5;
            currentMana = maxMana;
            baseStrength += 2;
            baseDefense += 1;

            // קריאה לפונקציה הספציפית של הדמות
            onLevelUp();
        }
    }
    
    /**
     * מתודה אבסטרקטית שנקראת כאשר הדמות עולה רמה.
     * כל סוג דמות מגדיר מה קורה כשעולים רמה.
     */
    protected abstract void onLevelUp();
    
    /**
     * מתודה אבסטרקטית לחישוב נזק התקפה.
     * כל סוג דמות מחשב נזק בצורה שונה.
     * 
     * @return נזק ההתקפה
     */
    public abstract int calculateAttackDamage();
    
    /**
     * מתודה אבסטרקטית לביצוע יכולת מיוחדת.
     * כל סוג דמות יש לו יכולת מיוחדת.
     * 
     * @param target היעד של היכולת
     * @return true אם היכולת בוצעה בהצלחה
     */
    public abstract boolean useSpecialAbility(Character target);
    
    // ============================================================
    // Recently Used Items Stack
    // ============================================================
    
    /**
     * מוסיף פריט לסטאק הפריטים האחרונים.
     * @param item הפריט שנעשה בו שימוש
     */
    public void pushRecentlyUsed(Item item) {
        recentlyUsedItems.push(item);
    }
    
    /**
     * מחזיר את הפריט האחרון שנעשה בו שימוש.
     * @return הפריט האחרון, או null אם הסטאק ריק
     */
    public Item popRecentlyUsed() {
        if (recentlyUsedItems.isEmpty()) {
            return null;
        }
        return recentlyUsedItems.pop();
    }
    
    /**
     * מציץ לפריט האחרון בלי להסיר אותו.
     * @return הפריט האחרון, או null אם הסטאק ריק
     */
    public Item peekRecentlyUsed() {
        if (recentlyUsedItems.isEmpty()) {
            return null;
        }
        return recentlyUsedItems.peek();
    }
    
    // ============================================================
    // Getters & Setters
    // ============================================================
    
    public String getName() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getGold() {
        return gold;
    }
    
    public void addGold(int amount) {
        this.gold += amount;
    }
    
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
    
    public int getCurrentMana() {
        return currentMana;
    }
    
    public int getMaxMana() {
        return maxMana;
    }
    
    public int getBaseStrength() {
        return baseStrength;
    }
    
    public int getBaseDefense() {
        return baseDefense;
    }
    
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
    
    public HashMap<Armor.ArmorSlot, Armor> getEquippedArmor() {
        return new HashMap<>(equippedArmor);
    }
    
    public ArrayList<Item> getInventory() {
        return new ArrayList<>(inventory);
    }
    
    public int getInventorySize() {
        return inventory.size();
    }
    
    public int getMaxInventorySize() {
        return maxInventorySize;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Level %d) - HP: %d/%d, Mana: %d/%d, Gold: %d",
            name, level, currentHealth, maxHealth, currentMana, maxMana, gold);
    }
}
