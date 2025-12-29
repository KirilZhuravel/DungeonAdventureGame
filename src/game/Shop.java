package game;

import model.characters.Character;
import model.items.Item;
import model.items.Weapon;
import model.items.Armor;
import model.items.Potion;
import model.exceptions.InventoryFullException;
import model.exceptions.ItemNotFoundException;
import model.exceptions.InsufficientGoldException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * מחלקה המייצגת חנות במשחק.
 * מאפשרת קנייה ומכירה של פריטים.
 */
public class Shop {
    
    private String name;
    private ArrayList<Item> inventory;
    private HashMap<String, Integer> stock; // מיפוי שם פריט לכמות במלאי
    
    public Shop(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.stock = new HashMap<>();
    }
    
    // ============================================================
    // TODO: ניהול מלאי החנות
    // ============================================================

    public void addItemToShop(Item item, int quantity) {

        boolean exists = false;
        for (Item i : inventory) {
            if (i.getName().equals(item.getName())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            inventory.add(item);
        }


        int currentStock = stock.getOrDefault(item.getName(), 0);
        stock.put(item.getName(), currentStock + quantity);
    }

    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> available = new ArrayList<>();
        for (Item item : inventory) {
            if (getItemStock(item.getName()) > 0) {
                available.add(item);
            }
        }
        return available;
    }

    public ArrayList<Item> getItemsByCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();
        String catLower = category.toLowerCase();

        for (Item item : inventory) {
            // אם אין במלאי, לא נציג (אופציונלי, תלוי בדרישות)
            if (getItemStock(item.getName()) <= 0) continue;

            if (catLower.equals("weapon") && item instanceof Weapon) {
                result.add(item);
            } else if (catLower.equals("armor") && item instanceof Armor) {
                result.add(item);
            } else if (catLower.equals("potion") && item instanceof Potion) {
                result.add(item);
            }
        }
        return result;
    }
    
    // ============================================================
    // TODO: קנייה ומכירה
    // ============================================================

    public Item buyItem(Character customer, String itemName)
            throws ItemNotFoundException, InsufficientGoldException,
            InventoryFullException {
        // Find the item
        Item itemToBuy = null;
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                itemToBuy = item;
                break;
            }
        }

        if (itemToBuy == null || stock.getOrDefault(itemName, 0) <= 0) {
            throw new ItemNotFoundException(itemName);
        }

        int price = itemToBuy.getBuyPrice();
        if (customer.getGold() < price) {
            throw new InsufficientGoldException(price, customer.getGold());
        }

        // Perform transaction
        customer.spendGold(price);
        customer.addItem(itemToBuy);
        stock.put(itemName, stock.get(itemName) - 1);

        return itemToBuy;
    }

    public int sellItem(Character seller, String itemName)
            throws ItemNotFoundException {

        Item itemToSell = seller.removeItem(itemName);

        if (!itemToSell.isSellable()) {

            try {
                seller.addItem(itemToSell);
            } catch (InventoryFullException e) {

            }
            throw new ItemNotFoundException(itemName);
        }

        int sellPrice = itemToSell.getSellPrice();
        seller.addGold(sellPrice);


        addItemToShop(itemToSell, 1);

        return sellPrice;
    }

    public int getItemStock(String itemName) {
        return stock.getOrDefault(itemName, 0);
    }
    public int getTotalValue() {
        int totalValue = 0;
        for (Item item : inventory) {
            totalValue += item.getBuyPrice() * getItemStock(item.getName());
        }
        return totalValue;
    }
    // ============================================================
    // TODO: דוחות (שימוש ב-HashMap)
    // ============================================================

    public HashMap<String, String> getInventoryReport() {
        HashMap<String, String> report = new HashMap<>();
        for (Item item : inventory) {
            report.put(item.getName(), "Price: " + item.getBuyPrice() + " | Stock: " + getItemStock(item.getName()));
        }
        return report;
    }

    public HashMap<String, int[]> getPriceComparison() {
        HashMap<String, int[]> comparison = new HashMap<>();
        for (Item item : inventory) {
            comparison.put(item.getName(), new int[]{item.getBuyPrice(), item.getSellPrice()});
        }
        return comparison;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public int getUniqueItemCount() {
        return inventory.size();
    }
    
    public int getTotalItemCount() {
        int total = 0;
        for (int count : stock.values()) {
            total += count;
        }
        return total;
    }
    
    @Override
    public String toString() {
        return String.format("Shop: %s | Items: %d unique, %d total",
            name, getUniqueItemCount(), getTotalItemCount());
    }
}
