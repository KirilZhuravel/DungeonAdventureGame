package model.exceptions;

/**
 * Exception המתרחש כאשר מחפשים פריט שלא קיים במלאי.
 */
public class ItemNotFoundException extends Exception {
    
    private final String itemName;
    
    public ItemNotFoundException(String itemName) {
        super("Item not found: " + itemName);
        this.itemName = itemName;
    }
    
    public String getItemName() {
        return itemName;
    }
}
