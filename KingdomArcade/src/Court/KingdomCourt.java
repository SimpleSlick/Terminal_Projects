package Court;

import java.io.IOException;
import java.util.Scanner;

import ENUMS.slotOperation;
import Game.GameState;
import SaveState.RegistryManagement;

public class KingdomCourt {
    private final GameState gameState;
    private final EventManager manager;
    private final RegistryManagement registry;
    private final Scanner scanner = new Scanner(System.in);

    // --- FIXED: Game structure ---
    public static final int MAX_CHAPTERS = 7;
    public static final int DAYS_PER_CHAPTER = 30; // 7 chapters * 5 days = 35 days total
    // If you want 30 days total, use DAYS_PER_CHAPTER = 4 and handle chapter 7 specially.
    // Don't use MAX_DAYS = 30 anymore, it conflicts with chapters.

    public KingdomCourt(GameState state) {
        this.gameState = state;
        this.manager = new EventManager(gameState);
        this.registry = new RegistryManagement(gameState);
    }

    public void kingdomDisplay() {
        System.out.println("=".repeat(30));
        System.out.println(" ".repeat(8) + "KINGDOM ARCADE");
        System.out.println("=".repeat(30));

        System.out.println("-".repeat(30));
        System.out.println(" ".repeat(8) + "KINGDOM STATUS");
        System.out.println("-".repeat(30));

        System.out.println();
        System.out.printf("%-25s%-25s%n", "DAY: " + gameState.currentDay, "YEAR: " + gameState.currentYear);
        System.out.printf("%-25s%-25s%n", "CHAPTER: " + gameState.currentChapter + " / " + MAX_CHAPTERS, "DIFFICULTY: " + gameState.gameDifficulty);

        System.out.println();
        System.out.printf("%-25s%-25s%n", "GOLD: " + String.format("%,d", gameState.gold), "TREASURY: " + String.format("%,d", gameState.treasury));
        System.out.printf("%-25s%-25s%n", "WOOD: " + String.format("%,d", gameState.wood), "STONE: " + String.format("%,d", gameState.stone));
        System.out.printf("%-25s%-25s%n", "IRON: " + String.format("%,d", gameState.iron), "STRENGTH: " + String.format("%,d", gameState.kingdomStrength));
        System.out.printf("%-25s%-25s%n", "FOOD: " + String.format("%,d", gameState.food), "POPULATION: " + String.format("%,d", gameState.population));
        System.out.printf("%-25s%-25s%n", "ARMY: " + String.format("%,d", gameState.army), "WEAPONS: " + String.format("%,d", gameState.weapons));

        System.out.println();
        System.out.printf("%-25s%-25s%n", "HAPPINESS: " + gameState.happiness + "%", "STABILITY: " + gameState.stability + "%");
        System.out.printf("%-25s%-25s%n", "CRIME RATE: " + gameState.crimeRate + "%", "UNEMPLOYMENT: " + gameState.Unemployment + "%");
        System.out.printf("%-25s%-25s%n", "PUBLIC APPROVAL: " + gameState.publicApproval + "%", "MILITARY MORALE: " + gameState.militaryMorale + "%");
        System.out.printf("%-25s%-25s%n", "FAITH: " + gameState.faith + "%", "REPUTATION: " + gameState.kingdomReputation + "%");

        if (gameState.currentPhase != null && gameState.currentPhase != ENUMS.ActivityType.NONE) {
            System.out.println();
            System.out.println("ACTIVE PHASE: " + gameState.currentPhase + " (" + gameState.phaseDurationLeft + " days left)");
        }
    }

    // --- THIS WAS MISSING: was private and never called ---
    public void startGame() throws IOException {
        // Safety: if loaded save is already past chapter 7, end it
        if (gameState.currentChapter > MAX_CHAPTERS) {
            showGameComplete();
            return;
        }

        boolean exitGame = false;

        while (!exitGame) {
            // 1. Play one day
            manager.playDay();

            // 2. Check chapter end
            // playDay() already did currentDay++
            // If we finished all days of this chapter, move to next chapter
            if (gameState.currentDay > DAYS_PER_CHAPTER) {
                gameState.currentDay = 1;
                gameState.currentChapter++;

                if (gameState.currentChapter > MAX_CHAPTERS) {
                    showGameComplete();
                    return;
                }

                System.out.println("\n=== CHAPTER " + gameState.currentChapter + " BEGINS ===");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }

            kingdomDisplay();

            // 3. After-day menu - THIS IS THE NEW PART
            int choice = showAfterDayMenu();

            switch (choice) {
                case 1 -> {
                    // Next Day - just loop again
                }
                case 2 -> {
                    // Save - stay in game
                    saveFlow();
                    System.out.println("Press Enter to continue to next day...");
                    scanner.nextLine();
                }
                case 3 -> {
                    // Save and Exit
                    saveFlow();
                    System.out.println("Game saved. Returning to Main Menu...");
                    exitGame = true;
                }
                case 4 -> {
                    // Exit without saving
                    System.out.print("Exit without saving? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        exitGame = true;
                    }
                }
            }
        }
    }

    private int showAfterDayMenu() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println(" ".repeat(8) + "WHAT NEXT?");
        System.out.println("=".repeat(30));
        System.out.println("1. Continue to Next Day");
        System.out.println("2. Save Game");
        System.out.println("3. Save and Exit to Main Menu");
        System.out.println("4. Exit without Saving");
        System.out.println("=".repeat(30));

        while (true) {
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                int c = scanner.nextInt();
                scanner.nextLine(); // consume newline - IMPORTANT, else nextLine() skips
                if (c >= 1 && c <= 4) return c;
            } else {
                scanner.next(); // discard bad input
            }
            System.out.println("Invalid! Enter 1-4");
        }
    }

    private void saveFlow() throws IOException {
        registry.displaySlots();
        System.out.print("Enter Slot Number (1-10, 0 to cancel): ");
        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.println("Save cancelled.");
            return;
        }
        int slot = scanner.nextInt();
        scanner.nextLine();
        if (slot == 0) {
            System.out.println("Save cancelled.");
            return;
        }
        if (slot < 1 || slot > registry.MAX_SLOTS) {
            System.out.println("Invalid slot.");
            return;
        }
        // NOTE: This needs RegistryManagement to allow overwrite - see fix #3 below
        registry.handleSlot(slot, slotOperation.SAVE);
        System.out.println("Game Saved in Slot " + slot);
    }

    private void showGameComplete() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("  CONGRATULATIONS! YOU COMPLETED ALL " + MAX_CHAPTERS + " CHAPTERS!");
        System.out.println("=".repeat(40));
        kingdomDisplay();
        System.out.println("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }
}