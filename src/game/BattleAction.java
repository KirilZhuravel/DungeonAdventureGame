package game;

import model.characters.Character;

/**
 * מחלקה המייצגת פעולת קרב.
 * משמשת בתור הפעולות של BattleSystem.
 */
public class BattleAction {
    
    public enum ActionType {
        ATTACK("Basic Attack"),
        SPECIAL("Special Ability"),
        USE_ITEM("Use Item"),
        DEFEND("Defend"),
        FLEE("Flee");
        
        private final String displayName;
        
        ActionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private Character actor;
    private Character target;
    private ActionType actionType;
    private String itemName; // אם הפעולה היא USE_ITEM
    private int priority; // עדיפות לביצוע (גבוה יותר = קודם)
    
    public BattleAction(Character actor, Character target, ActionType actionType) {
        this.actor = actor;
        this.target = target;
        this.actionType = actionType;
        this.itemName = null;
        this.priority = calculateDefaultPriority();
    }
    
    public BattleAction(Character actor, Character target, 
                        ActionType actionType, String itemName) {
        this(actor, target, actionType);
        this.itemName = itemName;
    }
    
    /**
     * מחשב עדיפות ברירת מחדל לפי סוג הפעולה.
     */
    private int calculateDefaultPriority() {
        switch (actionType) {
            case FLEE: return 100;     // בריחה קודמת
            case DEFEND: return 80;    // הגנה שנייה
            case USE_ITEM: return 60;  // שימוש בפריט שלישי
            case SPECIAL: return 40;   // יכולת מיוחדת רביעית
            case ATTACK: return 20;    // התקפה אחרונה
            default: return 0;
        }
    }
    
    // Getters & Setters
    public Character getActor() {
        return actor;
    }
    
    public Character getTarget() {
        return target;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        String actionStr = String.format("%s -> %s: %s",
            actor.getName(), target.getName(), actionType.getDisplayName());
        if (itemName != null) {
            actionStr += " (" + itemName + ")";
        }
        return actionStr;
    }
}
