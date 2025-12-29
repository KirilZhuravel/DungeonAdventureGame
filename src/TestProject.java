import model.characters.*;
import model.items.*;
import model.exceptions.*;
import game.*;
import utils.GameUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;

/**
 * מחלקת בדיקות לפרויקט.
 * השתמשו במחלקה זו כדי לבדוק את המימוש שלכם.
 * 
 * הוראות הרצה:
 * 1. הידור: javac -d out src/**\/*.java
 * 2. הרצה: java -cp out TestProject
 */
public class TestProject {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   Dungeon Adventure - Tests");
        System.out.println("=================================\n");
        
        // הרצת כל הבדיקות
        testItems();
        testCharacterBasics();
        testInventoryManagement();
        testEquipment();
        testBattleSystem();
        testShop();
        testDungeonMap();
        testSortingAndFiltering();
        
        // סיכום
        System.out.println("\n=================================");
        System.out.printf("Tests: %d/%d passed (%.1f%%)\n", 
            testsPassed, testsRun, (testsPassed * 100.0 / testsRun));
        System.out.println("=================================");
    }
    
    // ============================================================
    // בדיקות פריטים
    // ============================================================
    
    private static void testItems() {
        System.out.println("\n--- Testing Items ---");
        
        // Test Weapon
        Weapon sword = new Weapon("Iron Sword", "A basic sword", 5, 100,
            Item.ItemRarity.COMMON, 10, 20, Weapon.WeaponType.SWORD);
        
        test("Weapon creation", sword.getName().equals("Iron Sword"));
        test("Weapon damage range", 
            sword.getMinDamage() == 10 && sword.getMaxDamage() == 20);
        
        try {
            int damage = sword.calculateDamage();
            test("Weapon calculateDamage", damage >= 10 && damage <= 20);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Weapon calculateDamage");
        }
        
        try {
            double avg = sword.getAverageDamage();
            test("Weapon averageDamage", avg == 15.0);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Weapon averageDamage");
        }
        
        // Test Armor
        Armor helmet = new Armor("Steel Helmet", "Protects your head", 3, 80,
            Item.ItemRarity.UNCOMMON, 15, Armor.ArmorSlot.HEAD);
        
        test("Armor creation", helmet.getDefense() == 15);
        test("Armor slot", helmet.getSlot() == Armor.ArmorSlot.HEAD);
        
        try {
            double reduction = helmet.calculateDamageReduction();
            test("Armor damageReduction", reduction >= 0 && reduction <= 0.75);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Armor damageReduction");
        }
        
        // Test Potion
        Potion healPotion = new Potion("Health Potion", "Restores health", 50,
            Item.ItemRarity.COMMON, Potion.PotionType.HEALTH, 30, 3);
        
        test("Potion creation", healPotion.getPotency() == 30);
        test("Potion uses", healPotion.getRemainingUses() == 3);
        
        // Test Tradeable interface
        try {
            int buyPrice = sword.getBuyPrice();
            int sellPrice = sword.getSellPrice();
            test("Item getBuyPrice", buyPrice == 100); // COMMON = 1.0 multiplier
            test("Item getSellPrice", sellPrice == 50); // 50% of buy price
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Item Tradeable methods");
        }
    }
    
    // ============================================================
    // בדיקות דמויות
    // ============================================================
    
    private static void testCharacterBasics() {
        System.out.println("\n--- Testing Characters ---");
        
        Warrior warrior = new Warrior("TestWarrior");
        Mage mage = new Mage("TestMage");
        Archer archer = new Archer("TestArcher");
        
        // Test creation
        test("Warrior creation", warrior.getName().equals("TestWarrior"));
        test("Warrior high HP", warrior.getMaxHealth() == 150);
        
        test("Mage creation", mage.getName().equals("TestMage"));
        test("Mage high mana", mage.getMaxMana() == 150);
        
        test("Archer creation", archer.getName().equals("TestArcher"));
        
        // Test isAlive
        try {
            test("Character isAlive", warrior.isAlive());
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character isAlive");
        }
        
        // Test heal
        try {
            warrior.takeDamage(50); // First damage the warrior
            warrior.heal(30);
            test("Character heal", 
                warrior.getCurrentHealth() == warrior.getMaxHealth() - 20);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character heal/takeDamage");
        }
        
        // Test experience and leveling
        try {
            int startLevel = warrior.getLevel();
            warrior.gainExperience(100);
            test("Character leveling", warrior.getLevel() == startLevel + 1);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character gainExperience");
        }
    }
    
    // ============================================================
    // בדיקות ניהול מלאי
    // ============================================================
    
    private static void testInventoryManagement() {
        System.out.println("\n--- Testing Inventory ---");
        
        Warrior warrior = new Warrior("InvTest");
        
        Weapon sword = new Weapon("Test Sword", "Test", 5, 100,
            Item.ItemRarity.COMMON, 10, 20, Weapon.WeaponType.SWORD);
        Potion potion = new Potion("Test Potion", "Test", 50,
            Item.ItemRarity.COMMON, Potion.PotionType.HEALTH, 30, 3);
        
        // Test addItem
        try {
            warrior.addItem(sword);
            warrior.addItem(potion);
            test("addItem", warrior.getInventorySize() == 2);
        } catch (InventoryFullException e) {
            test("addItem (unexpected full)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character addItem");
        }
        
        // Test removeItem
        try {
            Item removed = warrior.removeItem("Test Sword");
            test("removeItem", removed != null && removed.getName().equals("Test Sword"));
            test("removeItem size", warrior.getInventorySize() == 1);
        } catch (ItemNotFoundException e) {
            test("removeItem (item not found)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character removeItem");
        }
        
        // Test findItemsByType
        try {
            warrior.addItem(sword);
            ArrayList<Weapon> weapons = warrior.findItemsByType(Weapon.class);
            test("findItemsByType", weapons.size() == 1);
        } catch (Exception e) {
            testNotImplemented("Character findItemsByType");
        }
        
        // Test getItemsByRarity
        try {
            HashMap<Item.ItemRarity, ArrayList<Item>> byRarity = 
                warrior.getItemsByRarity();
            test("getItemsByRarity", byRarity.containsKey(Item.ItemRarity.COMMON));
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Character getItemsByRarity");
        }
    }
    
    // ============================================================
    // בדיקות ציוד
    // ============================================================
    
    private static void testEquipment() {
        System.out.println("\n--- Testing Equipment ---");
        
        Warrior warrior = new Warrior("EquipTest");
        
        Weapon sword = new Weapon("Battle Sword", "Test", 5, 100,
            Item.ItemRarity.COMMON, 15, 25, Weapon.WeaponType.SWORD);
        Armor helmet = new Armor("Iron Helmet", "Test", 3, 80,
            Item.ItemRarity.COMMON, 10, Armor.ArmorSlot.HEAD);
        
        try {
            // Add items to inventory first
            warrior.addItem(sword);
            warrior.addItem(helmet);
            
            // Test equipWeapon
            warrior.equipWeapon(sword);
            test("equipWeapon", warrior.getEquippedWeapon() != null);
            test("equipWeapon removed from inventory", 
                warrior.getInventorySize() == 1);
            
            // Test equipArmor
            warrior.equipArmor(helmet);
            test("equipArmor", 
                warrior.getEquippedArmor().containsKey(Armor.ArmorSlot.HEAD));
            
            // Test getTotalDefense
            int defense = warrior.getTotalDefense();
            test("getTotalDefense", defense >= warrior.getBaseDefense());
            
        } catch (InventoryFullException e) {
            test("Equipment (inventory full)", false);
        } catch (ItemNotFoundException e) {
            test("Equipment (item not found)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Equipment methods");
        }
    }
    
    // ============================================================
    // בדיקות מערכת קרב
    // ============================================================
    
    private static void testBattleSystem() {
        System.out.println("\n--- Testing Battle System ---");
        
        Warrior player = new Warrior("Hero");
        Mage enemy = new Mage("Dark Mage");
        
        BattleSystem battle = new BattleSystem(player, enemy);
        
        test("BattleSystem creation", !battle.isBattleEnded());
        test("BattleSystem queue empty", battle.isQueueEmpty());
        
        // Test queueAction
        try {
            battle.queuePlayerAction(BattleAction.ActionType.ATTACK);
            test("queueAction", battle.getQueueSize() == 1);
        } catch (InvalidActionException e) {
            test("queueAction (invalid)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("BattleSystem queueAction");
        }
        
        // Test generateEnemyAction
        try {
            BattleAction enemyAction = battle.generateEnemyAction();
            test("generateEnemyAction", enemyAction != null);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("BattleSystem generateEnemyAction");
        }
        
        // Test sortActionsByPriority with anonymous class
        try {
            ArrayList<BattleAction> actions = new ArrayList<>();
            actions.add(new BattleAction(player, enemy, BattleAction.ActionType.ATTACK));
            actions.add(new BattleAction(player, enemy, BattleAction.ActionType.FLEE));
            actions.add(new BattleAction(player, enemy, BattleAction.ActionType.DEFEND));
            
            battle.sortActionsByPriority(actions);
            // After sorting, FLEE should be first (highest priority)
            test("sortActionsByPriority", 
                actions.get(0).getActionType() == BattleAction.ActionType.FLEE);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("BattleSystem sortActionsByPriority");
        }
    }
    
    // ============================================================
    // בדיקות חנות
    // ============================================================
    
    private static void testShop() {
        System.out.println("\n--- Testing Shop ---");
        
        Shop shop = new Shop("Test Shop");
        
        Weapon sword = new Weapon("Shop Sword", "For sale", 5, 100,
            Item.ItemRarity.COMMON, 10, 20, Weapon.WeaponType.SWORD);
        Potion potion = new Potion("Shop Potion", "For sale", 30,
            Item.ItemRarity.COMMON, Potion.PotionType.HEALTH, 25, 1);
        
        // Test addItemToShop
        try {
            shop.addItemToShop(sword, 5);
            shop.addItemToShop(potion, 10);
            test("addItemToShop", shop.getUniqueItemCount() == 2);
            test("Shop total items", shop.getTotalItemCount() == 15);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Shop addItemToShop");
        }
        
        // Test getItemStock
        try {
            int stock = shop.getItemStock("Shop Sword");
            test("getItemStock", stock == 5);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("Shop getItemStock");
        }
        
        // Test buyItem
        Warrior buyer = new Warrior("Buyer");
        buyer.addGold(200);
        
        try {
            Item bought = shop.buyItem(buyer, "Shop Sword");
            test("buyItem success", bought != null);
            test("buyItem gold spent", buyer.getGold() < 200);
            test("buyItem stock reduced", shop.getItemStock("Shop Sword") == 4);
        } catch (Exception e) {
            if (e instanceof UnsupportedOperationException) {
                testNotImplemented("Shop buyItem");
            } else {
                test("buyItem exception: " + e.getMessage(), false);
            }
        }
    }
    
    // ============================================================
    // בדיקות מפה
    // ============================================================
    
    private static void testDungeonMap() {
        System.out.println("\n--- Testing Dungeon Map ---");
        
        DungeonMap map = new DungeonMap();
        
        GameLocation entrance = new GameLocation("entrance", "Cave Entrance", 
            "A dark cave entrance", 1);
        GameLocation hall = new GameLocation("hall", "Main Hall",
            "A large hall", 2);
        GameLocation treasure = new GameLocation("treasure", "Treasure Room",
            "Full of gold", 3);
        
        // Test addLocation
        try {
            map.addLocation(entrance);
            map.addLocation(hall);
            map.addLocation(treasure);
            map.setStartLocation("entrance");
            
            test("addLocation", map.getTotalLocations() == 3);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("DungeonMap addLocation");
        }
        
        // Test connectLocations
        try {
            map.connectLocations("entrance", "hall");
            map.connectLocations("hall", "treasure");
            test("connectLocations", true);
        } catch (InvalidActionException e) {
            test("connectLocations (invalid)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("DungeonMap connectLocations");
        }
        
        // Test moveTo
        try {
            map.moveTo("hall");
            test("moveTo", map.getCurrentLocationId().equals("hall"));
        } catch (InvalidActionException e) {
            test("moveTo (invalid)", false);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("DungeonMap moveTo");
        }
        
        // Test getAccessibleLocations
        try {
            ArrayList<GameLocation> accessible = map.getAccessibleLocations();
            test("getAccessibleLocations", accessible.size() == 2); // entrance & treasure
        } catch (UnsupportedOperationException e) {
            testNotImplemented("DungeonMap getAccessibleLocations");
        }
        
        // Test getExplorationProgress
        try {
            double progress = map.getExplorationProgress();
            test("getExplorationProgress", progress > 0 && progress <= 1.0);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("DungeonMap getExplorationProgress");
        }
    }
    
    // ============================================================
    // בדיקות מיון וסינון
    // ============================================================
    
    private static void testSortingAndFiltering() {
        System.out.println("\n--- Testing Sorting & Filtering ---");
        
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Weapon("Expensive Sword", "", 5, 500,
            Item.ItemRarity.RARE, 20, 30, Weapon.WeaponType.SWORD));
        items.add(new Weapon("Cheap Dagger", "", 2, 50,
            Item.ItemRarity.COMMON, 5, 10, Weapon.WeaponType.DAGGER));
        items.add(new Weapon("Medium Axe", "", 8, 200,
            Item.ItemRarity.UNCOMMON, 15, 25, Weapon.WeaponType.AXE));
        
        // Test sortItemsByPrice
        try {
            GameUtils.sortItemsByPrice(items);
            test("sortItemsByPrice", 
                items.get(0).getName().equals("Cheap Dagger"));
        } catch (UnsupportedOperationException e) {
            testNotImplemented("GameUtils sortItemsByPrice");
        }
        
        // Test sortItemsByRarity
        try {
            GameUtils.sortItemsByRarity(items);
            test("sortItemsByRarity", 
                items.get(0).getRarity() == Item.ItemRarity.COMMON);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("GameUtils sortItemsByRarity");
        }
        
        // Test filterAffordableItems
        try {
            ArrayList<Item> affordable = GameUtils.filterAffordableItems(items, 100);
            test("filterAffordableItems", affordable.size() == 1);
        } catch (UnsupportedOperationException e) {
            testNotImplemented("GameUtils filterAffordableItems");
        }
        
        // Test filterByRarity
        try {
            ArrayList<Item> rareItems = GameUtils.filterByRarity(items, 
                Item.ItemRarity.UNCOMMON);
            test("filterByRarity", rareItems.size() == 2); // UNCOMMON and RARE
        } catch (UnsupportedOperationException e) {
            testNotImplemented("GameUtils filterByRarity");
        }
        
        // Test findBestItem with anonymous Comparator
        try {
            Item mostExpensive = GameUtils.findBestItem(items, new Comparator<Item>() {
                @Override
                public int compare(Item i1, Item i2) {
                    return i1.getBuyPrice() - i2.getBuyPrice();
                }
            });
            test("findBestItem", 
                mostExpensive != null && mostExpensive.getName().equals("Expensive Sword"));
        } catch (UnsupportedOperationException e) {
            testNotImplemented("GameUtils findBestItem");
        }
    }
    
    // ============================================================
    // Utility Methods
    // ============================================================
    
    private static void test(String name, boolean passed) {
        testsRun++;
        if (passed) {
            testsPassed++;
            System.out.println("  ✓ " + name);
        } else {
            System.out.println("  ✗ " + name + " - FAILED");
        }
    }
    
    private static void testNotImplemented(String name) {
        testsRun++;
        System.out.println("  ○ " + name + " - NOT IMPLEMENTED");
    }
}
