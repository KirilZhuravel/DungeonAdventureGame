package model.items;

/**
 * מחלקה בסיסית המייצגת פריט במשחק.
 * כל סוגי הפריטים (נשק, שריון, שיקוי) יורשים ממחלקה זו.
 */
public abstract class Item implements Tradeable {
    
    protected String name;
    protected String description;
    protected int weight;
    protected int basePrice;
    protected ItemRarity rarity;
    
    /**
     * enum המייצג את רמת הנדירות של הפריט
     */
    public enum ItemRarity {
        COMMON(1.0),
        UNCOMMON(1.5),
        RARE(2.5),
        EPIC(4.0),
        LEGENDARY(10.0);
        
        private final double priceMultiplier;
        
        ItemRarity(double priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
        }
        
        public double getPriceMultiplier() {
            return priceMultiplier;
        }
    }
    
    public Item(String name, String description, int weight, int basePrice, ItemRarity rarity) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.basePrice = basePrice;
        this.rarity = rarity;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public ItemRarity getRarity() {
        return rarity;
    }
    

    @Override
    public int getBuyPrice() {
        double price = basePrice * rarity.getPriceMultiplier();
        return (int) Math.ceil(price);
    }

    @Override
    public int getSellPrice() {
        double sellPrice = getBuyPrice() * 0.5;
        return (int) Math.ceil(sellPrice);
    }

    @Override
    public boolean isSellable() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Weight: %d)", 
            rarity, name, description, weight);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.name) && rarity == item.rarity;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + rarity.hashCode();
    }
}
