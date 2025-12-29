package game;

import model.characters.*;
import model.characters.Character;
import model.items.*;
import model.exceptions.*;
import java.util.Scanner;
import java.util.ArrayList;
/**
 * המחלקה הראשית של המשחק.
 * מנהלת את זרימת המשחק והאינטראקציה עם השחקן.
 */
public class Game {
    
    private Character player;
    private DungeonMap map;
    private Shop shop;
    private Scanner scanner;
    private boolean gameRunning;
    
    public Game() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = false;
    }
    
    /**
     * מתחיל את המשחק.
     */
    public void start() {
        System.out.println("=================================");
        System.out.println("  Welcome to Dungeon Adventure!");
        System.out.println("=================================\n");
        
        createCharacter();
        initializeMap();
        initializeShop();
        
        gameRunning = true;
        gameLoop();
    }

    private void createCharacter() {
        System.out.print("Enter your character's name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Hero";

        while (true) {
            System.out.println("\nChoose your class:");
            System.out.println("1. Warrior (High Health, High Defense, Rage)");
            System.out.println("2. Mage (High Mana, Spell Power, Magic Shield)");
            System.out.println("3. Archer (Critical Hits, Evasion, Multishot)");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    player = new Warrior(name);
                    System.out.println("Warrior created! Prepare for battle.");
                    return;
                case "2":
                    player = new Mage(name);
                    System.out.println("Mage created! Magic flows through you.");
                    return;
                case "3":
                    player = new Archer(name);
                    System.out.println("Archer created! Strike from the shadows.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void initializeMap() {
        map = new DungeonMap();

        // Create locations
        GameLocation entrance = new GameLocation("entrance", "Dungeon Entrance",
                "The entrance to the dark dungeon. Torches flicker on the walls.", 1);
        GameLocation hallway = new GameLocation("hallway", "Dark Hallway",
                "A long, dark hallway with cobwebs everywhere.", 2);
        GameLocation armory = new GameLocation("armory", "Abandoned Armory",
                "An old armory. Some weapons might still be useful.", 2);
        GameLocation treasury = new GameLocation("treasury", "Treasure Room",
                "Glittering gold and gems catch your eye.", 3);
        GameLocation throneRoom = new GameLocation("throne", "Throne Room",
                "The dark lord awaits on his throne.", 5);

        // Add locations to map
        map.addLocation(entrance);
        map.addLocation(hallway);
        map.addLocation(armory);
        map.addLocation(treasury);
        map.addLocation(throneRoom);

        // Connect locations
        try {
            map.connectLocations("entrance", "hallway");
            map.connectLocations("hallway", "armory");
            map.connectLocations("hallway", "treasury");
            map.connectLocations("treasury", "throne");
        } catch (InvalidActionException e) {
            System.out.println("Error connecting locations: " + e.getMessage());
        }

        // Set special locations
        map.setStartLocation("entrance");
        map.setBossLocation("throne");

        // Add some loot to locations
        armory.addLoot(new Weapon("Rusty Sword", "An old but usable sword", 5, 20,
                Item.ItemRarity.COMMON, 5, 10, Weapon.WeaponType.SWORD));
        treasury.addLoot(new Potion("Health Potion", "Restores 30 HP", 15,
                Item.ItemRarity.COMMON, Potion.PotionType.HEALTH, 30, 1));

        System.out.println("Map initialized with 5 locations.");
    }

    private void initializeShop() {
        shop = new Shop("Village Shop");

        // Add weapons
        shop.addItemToShop(new Weapon("Iron Sword", "A sturdy iron sword", 6, 50,
                Item.ItemRarity.COMMON, 8, 15, Weapon.WeaponType.SWORD), 3);
        shop.addItemToShop(new Weapon("Battle Axe", "A heavy battle axe", 10, 75,
                Item.ItemRarity.UNCOMMON, 12, 20, Weapon.WeaponType.AXE), 2);
        shop.addItemToShop(new Weapon("Oak Staff", "A magical staff", 4, 60,
                Item.ItemRarity.COMMON, 6, 12, Weapon.WeaponType.STAFF), 2);
        shop.addItemToShop(new Weapon("Hunting Bow", "A reliable bow", 3, 55,
                Item.ItemRarity.COMMON, 7, 14, Weapon.WeaponType.BOW), 2);

        // Add armor
        shop.addItemToShop(new Armor("Leather Helmet", "Basic head protection", 2, 30,
                Item.ItemRarity.COMMON, 5, Armor.ArmorSlot.HEAD), 3);
        shop.addItemToShop(new Armor("Chain Mail", "Decent chest protection", 8, 80,
                Item.ItemRarity.UNCOMMON, 15, Armor.ArmorSlot.CHEST), 2);
        shop.addItemToShop(new Armor("Iron Boots", "Sturdy boots", 4, 40,
                Item.ItemRarity.COMMON, 8, Armor.ArmorSlot.BOOTS), 3);

        // Add potions
        shop.addItemToShop(new Potion("Health Potion", "Restores 30 HP", 20,
                Item.ItemRarity.COMMON, Potion.PotionType.HEALTH, 30, 1), 10);
        shop.addItemToShop(new Potion("Mana Potion", "Restores 25 MP", 25,
                Item.ItemRarity.COMMON, Potion.PotionType.MANA, 25, 1), 8);
        shop.addItemToShop(new Potion("Greater Health Potion", "Restores 60 HP", 50,
                Item.ItemRarity.UNCOMMON, Potion.PotionType.HEALTH, 60, 1), 5);

        System.out.println("Shop initialized with " + shop.getTotalItemCount() + " items.");
    }
    
    /**
     * לולאת המשחק הראשית.
     */
    private void gameLoop() {
        while (gameRunning) {
            displayMenu();
            int choice = getPlayerChoice();
            handleChoice(choice);
        }
        
        System.out.println("\nThanks for playing Dungeon Adventure!");
    }
    
    /**
     * מציג את התפריט הראשי.
     */
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View Character");
        System.out.println("2. View Inventory");
        System.out.println("3. Move to Location");
        System.out.println("4. Visit Shop");
        System.out.println("5. Battle");
        System.out.println("6. Save & Quit");
        System.out.print("Choose: ");
    }
    
    /**
     * קורא בחירה מהשחקן.
     */
    private int getPlayerChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }



    private void handleChoice(int choice) {
        switch (choice) {
            case 1:
                viewCharacter();
                break;
            case 2:
                viewInventory();
                break;
            case 3:
                moveToLocation();
                break;
            case 4:
                visitShop();
                break;
            case 5:
                startBattle();
                break;
            case 6:
                gameRunning = false;
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }



    private void viewCharacter() {
        System.out.println("\n=== Character Sheet ===");
        System.out.println(player.toString());
        System.out.println("Defense: " + player.getTotalDefense());
        Weapon w = player.getEquippedWeapon();
        System.out.println("Weapon: " + (w != null ? w.getName() : "None"));
        System.out.println("=======================");
    }

    private void viewInventory() {
        System.out.println("\n=== Inventory ===");
        System.out.println("Items: " + player.getInventorySize() + "/" + player.getMaxInventorySize());

        if (player.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        int i = 1;
        for (Item item : player.getInventory()) {
            System.out.println(i + ". " + item.toString());
            i++;
        }
    }

    private void moveToLocation() {
        GameLocation current = map.getCurrentLocation();
        ArrayList<String> connections = current.getConnectedLocationIds();

        System.out.println("Available locations:");
        for (int i = 0; i < connections.size(); i++) {
            System.out.println((i+1) + ". " + map.getLocation(connections.get(i)).getName());
        }
        System.out.println("0. Cancel");

        int choice = getPlayerChoice();
        if (choice > 0 && choice <= connections.size()) {
            String targetId = connections.get(choice - 1);
            try {
                map.moveTo(targetId); // Может выбросить InvalidActionException
                System.out.println("Moved to " + map.getCurrentLocation().getName());

                // Подбираем лут
                for (Item item : map.getCurrentLocation().collectAllLoot()) {
                    player.addItem(item);
                    System.out.println("Found item: " + item.getName());
                }
            } catch (Exception e) {
                System.out.println("Could not move: " + e.getMessage());
            }
        }
    }
    private void visitShop() {
        // Проверка: магазин доступен только в безопасных зонах (dangerLevel == 0)
        // Если ты хочешь магазин везде, убери это условие.
        if (map.getCurrentLocation().getDangerLevel() > 0) {
            System.out.println("It's too dangerous to set up a shop here!");
            return;
        }

        System.out.println("Welcome to " + shop.getName());
        boolean shopping = true;

        while (shopping) {
            System.out.println("\n--- Shop Menu ---");
            System.out.println("Your Gold: " + player.getGold());
            System.out.println("1. Buy Items");
            System.out.println("2. Sell Items");
            System.out.println("3. Leave");
            System.out.print("Choice: ");

            int choice = getPlayerChoice();

            if (choice == 1) {
                // --- ПОКУПКА ---
                ArrayList<Item> items = shop.getAvailableItems();
                if (items.isEmpty()) {
                    System.out.println("Shop is empty.");
                    continue;
                }

                System.out.println("\nItems for sale:");
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    System.out.println((i + 1) + ". " + item.getName() + " - " + item.getBuyPrice() + " Gold");
                }
                System.out.println("0. Back");

                int buyChoice = getPlayerChoice();
                if (buyChoice > 0 && buyChoice <= items.size()) {
                    try {
                        shop.buyItem(player, items.get(buyChoice - 1).getName());
                        System.out.println("Purchase successful!");
                    } catch (Exception e) {
                        System.out.println("Cannot buy: " + e.getMessage());
                    }
                }

            } else if (choice == 2) {
                // --- ПРОДАЖА ---
                ArrayList<Item> inventory = player.getInventory();
                if (inventory.isEmpty()) {
                    System.out.println("You have nothing to sell.");
                    continue;
                }

                System.out.println("\nYour inventory:");
                for (int i = 0; i < inventory.size(); i++) {
                    Item item = inventory.get(i);
                    System.out.println((i + 1) + ". " + item.getName() + " - Sell for: " + item.getSellPrice() + " Gold");
                }
                System.out.println("0. Back");

                int sellChoice = getPlayerChoice();
                if (sellChoice > 0 && sellChoice <= inventory.size()) {
                    try {
                        shop.sellItem(player, inventory.get(sellChoice - 1).getName());
                        System.out.println("Item sold!");
                    } catch (Exception e) {
                        System.out.println("Cannot sell: " + e.getMessage());
                    }
                }

            } else if (choice == 3) {
                shopping = false;
                System.out.println("You left the shop.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void startBattle() {

        if (map.getCurrentLocation().getDangerLevel() == 0) {
            System.out.println("It's peaceful here. No enemies in sight.");
            return;
        }

        Character enemy;
        if (map.getCurrentLocation().getId().equals(map.getBossLocationId())) {
            enemy = new Warrior("BOSS: Dark Knight");
        } else {
            enemy = new Warrior("Goblin");
        }

        System.out.println("\n!!! A wild " + enemy.getName() + " appears! !!!");


        BattleSystem battle = new BattleSystem(player, enemy);


        while (!battle.isBattleEnded()) {
            System.out.println("\n--- Battle Round ---");
            System.out.println("Player HP: " + player.getCurrentHealth());
            System.out.println("Enemy HP: " + enemy.getCurrentHealth());
            System.out.println("1. Attack");
            System.out.println("2. Use Special Ability");
            System.out.println("3. Use Potion");
            System.out.println("4. Defend");
            System.out.println("5. Flee");
            System.out.print("Action: ");

            int choice = getPlayerChoice();

            try {

                switch (choice) {
                    case 1:
                        battle.queuePlayerAction(BattleAction.ActionType.ATTACK);
                        break;
                    case 2:
                        battle.queuePlayerAction(BattleAction.ActionType.SPECIAL);
                        break;
                    case 3:
                        // Для простоты используем зелье здоровья автоматически
                        battle.queuePlayerItemAction("Health Potion");
                        break;
                    case 4:
                        battle.queuePlayerAction(BattleAction.ActionType.DEFEND);
                        break;
                    case 5:
                        battle.queuePlayerAction(BattleAction.ActionType.FLEE);
                        break;
                    default:
                        System.out.println("You hesitated! (Skipping turn)");
                }


                ArrayList<String> logs = battle.processAllActions();
                for (String log : logs) {
                    System.out.println(log);
                }

            } catch (Exception e) {
                System.out.println("Battle error: " + e.getMessage());
            }
        }


        if (battle.getWinner() == player) {
            System.out.println("\nVICTORY! You defeated " + enemy.getName());

            player.addGold(10 * map.getCurrentLocation().getDangerLevel());
        } else if (battle.getWinner() == enemy) {
            System.out.println("\nDEFEAT! You were knocked out...");

        }
    }
    
    /**
     * נקודת הכניסה למשחק.
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
