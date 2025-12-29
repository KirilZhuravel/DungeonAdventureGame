package model.exceptions;

/**
 * Exception המתרחש כאשר מנסים לבצע פעולה לא חוקית במשחק.
 */
public class InvalidActionException extends Exception {
    
    private final String actionName;
    private final String reason;
    
    public InvalidActionException(String actionName, String reason) {
        super("Invalid action '" + actionName + "': " + reason);
        this.actionName = actionName;
        this.reason = reason;
    }
    
    public String getActionName() {
        return actionName;
    }
    
    public String getReason() {
        return reason;
    }
}
