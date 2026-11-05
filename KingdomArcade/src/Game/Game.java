package Game;

import java.io.IOException;
import java.util.Scanner;

public class Game {
    GameState gameState = new GameState();
    Scanner scanner = new Scanner(System.in);

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
                    System.out.println("New Game Selected");
                    newGame();
                    break;
                case 2:
                    System.out.println("Continue Game Selected");
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
}