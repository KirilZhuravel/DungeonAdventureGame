package model.characters;

/**
 * ממשק המייצג יצור שניתן לתקוף אותו.
 * כל הדמויות והמפלצות מממשות ממשק זה.
 */
public interface Attackable {
    
    /**
     * מקבל נזק מהתקפה.
     * @param damage כמות הנזק
     */
    void takeDamage(int damage);
    
    /**
     * בודק האם היצור עדיין בחיים.
     * @return true אם בחיים, false אם מת
     */
    boolean isAlive();
    
    /**
     * מחזיר את כמות נקודות החיים הנוכחיות.
     * @return נקודות חיים נוכחיות
     */
    int getCurrentHealth();
    
    /**
     * מחזיר את כמות נקודות החיים המקסימליות.
     * @return נקודות חיים מקסימליות
     */
    int getMaxHealth();
}
