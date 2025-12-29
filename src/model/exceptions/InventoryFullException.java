package model.exceptions;

/**
 * Exception המתרחש כאשר מנסים להוסיף פריט למלאי מלא.
 */
public class InventoryFullException extends Exception {
    
    private final int maxCapacity;
    private final String itemName;
    
    public InventoryFullException(String itemName, int maxCapacity) {
        super("Cannot add item '" + itemName + "' - inventory is full (max: " + maxCapacity + ")");
        this.itemName = itemName;
        this.maxCapacity = maxCapacity;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public String getItemName() {
        return itemName;
    }
}
