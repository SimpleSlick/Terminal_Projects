package Engine_Essentials;

import java.util.Scanner;

public class Engine {
    public void animateText(String input, int delay) {
        for (char c : input.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
            ;
        }
        System.out.println();
    }

    public static void pressEnter(Scanner scan) {
        System.out.println("\n [ Press ENTER to continue ]");
        scan.nextLine();

        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public void animateMidLine(String input, int delay) {
        for (char c : input.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
            ;
        }
    }

    public void linePause(int pauseVal){
        try {
            Thread.sleep(pauseVal);
        } catch (InterruptedException e) {}
    }
}