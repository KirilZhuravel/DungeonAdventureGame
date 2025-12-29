package model.items;

import model.characters.Character;

/**
 * ממשק המייצג פריט שניתן להשתמש בו.
 * פריטים כמו שיקויים מממשים ממשק זה.
 */
public interface Usable {
    
    /**
     * משתמש בפריט על דמות מסוימת.
     * @param target הדמות עליה משתמשים בפריט
     * @return true אם השימוש הצליח, false אחרת
     */
    boolean use(Character target);
    
    /**
     * בודק האם ניתן להשתמש בפריט על דמות מסוימת.
     * @param target הדמות לבדיקה
     * @return true אם ניתן להשתמש, false אחרת
     */
    boolean canUse(Character target);
    
    /**
     * מחזיר את מספר השימושים שנותרו בפריט.
     * @return מספר השימושים שנותרו
     */
    int getRemainingUses();
}
