import java.util.Scanner;
import Game.Game;

public class Main{
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);

        System.out.println("=".repeat(40));
        System.out.println(" ".repeat(8) + "Welcome to Kingdom Arcade");
        System.out.println("=".repeat(40));

        Game g = new Game();

        scan.close();
    }
}