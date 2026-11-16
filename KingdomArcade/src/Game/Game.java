package Game;

import java.io.IOException;
import java.util.Scanner;
import SaveState.RegistryManagement;
import ENUMS.slotOperation;

public class Game {
    GameState gameState = new GameState();
    Scanner scanner = new Scanner(System.in);
    RegistryManagement management = new RegistryManagement();

    public Game() throws IOException {
        mainMenu();
    }

    public void mainMenu() throws IOException{
        int choice;

        do {
            System.out.println("=".repeat(30));
            System.out.println(" ".repeat(8) + "KINGDOM ARCADE");
            System.out.println("=".repeat(30));

            System.out.println(" ".repeat(8) + "1. New Game");
            System.out.println(" ".repeat(8) + "2. Continue Game");
            System.out.println(" ".repeat(8) + "3. Exit");

            System.out.println("=".repeat(30));
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    newGame();
                    break;
                case 2:
                    continueGame();
                    break;
                case 3:
                    System.out.println("Application Exits");
                    break;
                default:
                    System.out.println("Enter a valid choice");
                    break;
            }
        }while(choice != 3);
    }

    private void newGame() throws IOException {
        System.out.println("What would be the King's Name?");
        System.out.print("-> ");
        gameState.kingName = scanner.nextLine().trim();

        System.out.println("What would the Kingdom Called?");
        System.out.print("-> ");
        gameState.kingdomName = scanner.nextLine().trim();

        System.out.println("What is the King's Age?");
        System.out.print("-> ");
        gameState.KingAge = scanner.nextInt();

        int choice;
        do {
            System.out.println("Select the Game Difficulty:");
            System.out.println("1. Easy");
            System.out.println("2. Medium");
            System.out.println("3. Hard");
            System.out.println("4. Demon");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
        }while(choice < 1 || choice > 4);

        switch(choice){
            case 1:
                gameState.gameDifficulty = "Easy";
                gameState.assignValues();
                break;
            case 2:
                gameState.gameDifficulty = "Medium";
                gameState.assignValues();
                break;
            case 3:
                gameState.gameDifficulty = "Hard";
                gameState.assignValues();
                break;
            case 4:
                gameState.gameDifficulty = "Demon";
                gameState.assignValues();
                break;
        }
    }

    private void continueGame() throws IOException{
        management.displaySlots();

        int choice;
        do{
            System.out.print("Enter the operation (1. LOAD, 2. SAVE, 3. DELETE): ");

            while(!scanner.hasNextInt()){
                System.out.println("Invalid input! Enter a number.");
                scanner.next();
                System.out.print("Enter the operation: ");
            }

            choice = scanner.nextInt();
            if(choice < 1 || choice > 3){
                System.out.println("Invalid Operation! Chose between 1 and 3");
            }
        }while(choice < 1 || choice > 3);

        int slotNumber;
        do{
            System.out.print("Enter the Slot Number (1-10): ");

            while(!scanner.hasNextInt()){
                System.out.println("Invalid input! Enter a number");
                scanner.next();
                System.out.print("Enter the Slot Number (1-10): ");
            }

            slotNumber = scanner.nextInt();
            if(slotNumber < 1 || slotNumber > management.MAX_SLOTS){
                System.out.println("Invalid slot! Chose between 1 and 10 (inclusive)");
            }
        }while(slotNumber < 1 || slotNumber > management.MAX_SLOTS);

        switch (choice){
            case 1:
                management.handleSlot(slotNumber, slotOperation.LOAD);
                break;
            case 2:
                management.handleSlot(slotNumber, slotOperation.SAVE);
                break;
            case 3:
                management.handleSlot(slotNumber, slotOperation.DELETE);
        }
    }
}