package Game;

import java.util.Scanner;

public class Game {
    Scanner scan = new Scanner(System.in);

    public void start(){
        initialize();
    }

    private void initialize(){
        System.out.println("Loading Kingdom Arcade.......");
        System.out.println("Initialization Complete!!!");
    }
}