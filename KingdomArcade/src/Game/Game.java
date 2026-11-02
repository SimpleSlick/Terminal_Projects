package Game;

import java.util.Scanner;

public class Game {
    public void mainMenu(){
        Scanner scanner = new Scanner(System.in);
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

            switch (choice) {
                case 1:
                    System.out.println("New Game Selected");
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
}