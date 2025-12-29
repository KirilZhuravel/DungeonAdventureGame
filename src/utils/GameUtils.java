package utils;

import model.characters.Character;
import model.items.Item;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * מחלקת עזר עם פונקציות שימושיות למשחק.
 * כאן נתרגל שימוש במחלקות אנונימיות ו-Comparator.
 */
public class GameUtils {
    
    // ============================================================
    // TODO: מיון פריטים (שימוש במחלקות אנונימיות)
    // ============================================================

    public static void sortItemsByPrice(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                return Integer.compare(i1.getBuyPrice(), i2.getBuyPrice());
            }
        });
    }

    public static void sortItemsByPriceDescending(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                // Reverse order: i2 compared to i1
                return Integer.compare(i2.getBuyPrice(), i1.getBuyPrice());
            }
        });
    }

    public static void sortItemsByRarity(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                // Compare using the ordinal value of the enum
                return Integer.compare(i1.getRarity().ordinal(), i2.getRarity().ordinal());
            }
        });
    }

    public static void sortItemsByName(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                return i1.getName().compareTo(i2.getName());
            }
        });
    }

    public static void sortItemsByWeight(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                return Integer.compare(i1.getWeight(), i2.getWeight());
            }
        });
    }
    
    // ============================================================
    // TODO: מיון דמויות
    // ============================================================

    public static void sortCharactersByHealth(ArrayList<Character> characters) {
        characters.sort(new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                return Integer.compare(c1.getCurrentHealth(), c2.getCurrentHealth());
            }
        });
    }

    public static void sortCharactersByLevel(ArrayList<Character> characters) {
        characters.sort(new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                // Reverse order: Highest level first
                return Integer.compare(c2.getLevel(), c1.getLevel());
            }
        });
    }
    
    // ============================================================
    // TODO: סינון (Filtering)
    // ============================================================
    
    /**
     * ממשק פונקציונלי לסינון פריטים.
     */
    public interface ItemFilter {
        boolean accept(Item item);
    }

    public static ArrayList<Item> filterItems(ArrayList<Item> items, ItemFilter filter) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (filter.accept(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static ArrayList<Item> filterAffordableItems(ArrayList<Item> items, int playerGold) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                return item.getBuyPrice() <= playerGold;
            }
        });
    }

    public static ArrayList<Item> filterByRarity(ArrayList<Item> items, Item.ItemRarity minRarity) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                // Check if item's rarity is equal to or higher (greater ordinal) than minRarity
                return item.getRarity().ordinal() >= minRarity.ordinal();
            }
        });
    }

    public static ArrayList<Item> filterLightItems(ArrayList<Item> items, int maxWeight) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                return item.getWeight() <= maxWeight;
            }
        });
    }
    
    // ============================================================
    // TODO: פונקציות עזר נוספות
    // ============================================================

    public static Item findBestItem(ArrayList<Item> items, Comparator<Item> comparator) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        Item bestItem = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            Item current = items.get(i);
            // If current is "greater than" bestItem according to comparator
            if (comparator.compare(current, bestItem) > 0) {
                bestItem = current;
            }
        }
        return bestItem;
    }

    public static int calculateTotalWeight(ArrayList<Item> items) {
        int totalWeight = 0;
        for (Item item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    public static int calculateTotalValue(ArrayList<Item> items) {
        int totalValue = 0;
        for (Item item : items) {
            totalValue += item.getSellPrice();
        }
        return totalValue;
    }
}
