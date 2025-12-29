package model.exceptions;

/**
 * Exception המתרחש כאשר אין מספיק זהב לביצוע פעולה.
 */
public class InsufficientGoldException extends Exception {
    
    private final int required;
    private final int available;
    
    public InsufficientGoldException(int required, int available) {
        super("Insufficient gold: required " + required + ", available " + available);
        this.required = required;
        this.available = available;
    }
    
    public int getRequired() {
        return required;
    }
    
    public int getAvailable() {
        return available;
    }
}
