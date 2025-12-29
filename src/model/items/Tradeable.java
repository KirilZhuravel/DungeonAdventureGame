package model.items;

/**
 * ממשק המייצג פריט שניתן לסחור בו.
 * מאפשר קנייה ומכירה של פריטים.
 */
public interface Tradeable {
    
    /**
     * מחזיר את מחיר הקנייה של הפריט.
     * @return מחיר הקנייה במטבעות זהב
     */
    int getBuyPrice();
    
    /**
     * מחזיר את מחיר המכירה של הפריט.
     * @return מחיר המכירה במטבעות זהב
     */
    int getSellPrice();
    
    /**
     * בודק האם הפריט ניתן למכירה.
     * @return true אם ניתן למכור, false אחרת
     */
    boolean isSellable();
}
