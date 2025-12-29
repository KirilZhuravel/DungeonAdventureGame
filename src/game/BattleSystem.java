package game;

import model.characters.Character;
import model.items.Item;
import model.items.Potion;
import model.exceptions.InvalidActionException;
import model.exceptions.ItemNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Comparator;

/**
 * מערכת הקרב של המשחק.
 * משתמשת ב-Queue לניהול תור הפעולות.
 */
public class BattleSystem {
    
    private Character player;
    private Character enemy;
    private Queue<BattleAction> actionQueue;
    private ArrayList<String> battleLog;
    private boolean battleEnded;
    private Character winner;
    
    public BattleSystem(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
        this.actionQueue = new LinkedList<>();
        this.battleLog = new ArrayList<>();
        this.battleEnded = false;
        this.winner = null;
        
        logMessage("Battle started: " + player.getName() + " vs " + enemy.getName());
    }
    
    // ============================================================
    // TODO: ניהול תור פעולות (Queue Management)
    // ============================================================

    // ============================================================

    public void queueAction(BattleAction action) throws InvalidActionException {
        if (battleEnded) {
            // Updated to use 2 parameters based on your example
            throw new InvalidActionException("Battle", "Battle has already ended.");
        }
        actionQueue.add(action);
    }

    public void queuePlayerAction(BattleAction.ActionType actionType)
            throws InvalidActionException {
        BattleAction action = new BattleAction(player, enemy, actionType);
        queueAction(action);
    }

    public void queuePlayerItemAction(String itemName) throws InvalidActionException {
        BattleAction action = new BattleAction(player, enemy, BattleAction.ActionType.USE_ITEM, itemName);
        queueAction(action);
    }
    public BattleAction generateEnemyAction() {
        double roll = Math.random();
        BattleAction.ActionType actionType;

        if (roll < 0.60) {
            actionType = BattleAction.ActionType.ATTACK;
        } else if (roll < 0.85) {
            actionType = BattleAction.ActionType.SPECIAL;
        } else {
            actionType = BattleAction.ActionType.DEFEND;
        }

        return new BattleAction(enemy, player, actionType);
    }

    public String processNextAction() {
        if (actionQueue.isEmpty()) {
            return null;
        }

        BattleAction action = actionQueue.poll();
        String resultMessage = "";

        try {
            switch (action.getActionType()) {
                case ATTACK:
                    int damage = executeAttack(action.getActor(), action.getTarget());
                    resultMessage = action.getActor().getName() + " attacked for " + damage + " damage.";
                    break;
                case SPECIAL:
                    boolean success = executeSpecialAbility(action.getActor(), action.getTarget());
                    resultMessage = action.getActor().getName() + (success ? " used special ability!" : " failed special ability.");
                    break;
                case DEFEND:
                    executeDefend(action.getActor());
                    resultMessage = action.getActor().getName() + " is defending.";
                    break;
                case USE_ITEM:
                    boolean used = executeUseItem(action.getActor(), action.getItemName());
                    resultMessage = action.getActor().getName() + (used ? " used " + action.getItemName() : " failed to use item.");
                    break;
                case FLEE:
                    boolean fled = executeFlee(action.getActor());
                    resultMessage = action.getActor().getName() + (fled ? " fled the battle!" : " failed to flee.");
                    if (fled) {
                        battleEnded = true;
                        winner = action.getActor() == player ? enemy : player; // Technically no winner, but battle ends
                    }
                    break;
            }
        } catch (ItemNotFoundException e) {
            resultMessage = "Error: " + e.getMessage();
        }

        logMessage(resultMessage);
        checkBattleEnd();

        return resultMessage;
    }

    public ArrayList<String> processAllActions() {
        ArrayList<String> results = new ArrayList<>();

        // Add enemy action if queue only has player action (simplified turn logic)
        if (!battleEnded && actionQueue.size() == 1 && actionQueue.peek().getActor() == player) {
            actionQueue.add(generateEnemyAction());
        }

        // Sort actions to determine order
        ArrayList<BattleAction> actionsList = new ArrayList<>(actionQueue);
        sortActionsByPriority(actionsList);
        actionQueue.clear();
        actionQueue.addAll(actionsList);

        while (!actionQueue.isEmpty() && !battleEnded) {
            String res = processNextAction();
            if (res != null) results.add(res);
        }

        return results;
    }
    
    // ============================================================
    // TODO: ביצוע פעולות (Action Execution)
    // ============================================================

    private int executeAttack(Character attacker, Character defender) {
        int damage = attacker.calculateAttackDamage();
        defender.takeDamage(damage);
        return damage;
    }

    private boolean executeSpecialAbility(Character actor, Character target) {
        return actor.useSpecialAbility(target);
    }

    private boolean executeUseItem(Character actor, String itemName) throws ItemNotFoundException {
        try {
            Item item = actor.removeItem(itemName);
            if (item instanceof Potion) {
                boolean used = ((Potion) item).use(actor);
                if (used) {
                    actor.pushRecentlyUsed(item);
                    return true;
                }
            }
            // If not usable or failed, return to inventory
            try {
                actor.addItem(item);
            } catch (Exception e) { /* Ignore full inventory here */ }
            return false;
        } catch (ItemNotFoundException e) {
            throw e;
        }
    }

    private void executeDefend(Character defender) {
        logMessage(defender.getName() + " takes defensive stance.");
        // Note: Actual damage reduction logic would be in Character.takeDamage()
        // or handled by a temporary status effect system.
    }

    private boolean executeFlee(Character fleeing) {
        double baseChance = 0.30;
        int levelDiff = fleeing.getLevel() - (fleeing == player ? enemy.getLevel() : player.getLevel());
        double chance = baseChance + (levelDiff * 0.05);

        return Math.random() < chance;
    }
    
    // ============================================================
    // TODO: בדיקת סיום קרב
    // ============================================================

    private void checkBattleEnd() {
        if (!player.isAlive()) {
            battleEnded = true;
            winner = enemy;
            logMessage(player.getName() + " was defeated!");
        } else if (!enemy.isAlive()) {
            battleEnded = true;
            winner = player;
            logMessage(enemy.getName() + " was defeated!");

            // Loot logic could go here
            int goldReward = enemy.getLevel() * 10;
            player.addGold(goldReward);
            player.gainExperience(enemy.getLevel() * 20);
            logMessage(player.getName() + " gained " + goldReward + " gold.");
        }
    }
    
    // ============================================================
    // TODO: מיון פעולות לפי עדיפות (שימוש במחלקה אנונימית)
    // ============================================================

    public void sortActionsByPriority(ArrayList<BattleAction> actions) {
        actions.sort(new Comparator<BattleAction>() {
            @Override
            public int compare(BattleAction a1, BattleAction a2) {
                // Higher priority first (descending order)
                return Integer.compare(a2.getPriority(), a1.getPriority());
            }
        });
    }

    public ArrayList<BattleAction> getActionsFilteredBy(ArrayList<BattleAction> actions, ActionFilter filter) {
        ArrayList<BattleAction> result = new ArrayList<>();
        for (BattleAction action : actions) {
            if (filter.test(action)) {
                result.add(action);
            }
        }
        return result;
    }
    
    /**
     * ממשק פונקציונלי לסינון פעולות.
     */
    public interface ActionFilter {
        boolean test(BattleAction action);
    }
    
    // ============================================================
    // Utility Methods
    // ============================================================
    
    private void logMessage(String message) {
        battleLog.add(message);
        System.out.println(message);
    }
    
    // Getters
    public Character getPlayer() {
        return player;
    }
    
    public Character getEnemy() {
        return enemy;
    }
    
    public boolean isBattleEnded() {
        return battleEnded;
    }
    
    public Character getWinner() {
        return winner;
    }
    
    public ArrayList<String> getBattleLog() {
        return new ArrayList<>(battleLog);
    }
    
    public int getQueueSize() {
        return actionQueue.size();
    }
    
    public boolean isQueueEmpty() {
        return actionQueue.isEmpty();
    }
}
