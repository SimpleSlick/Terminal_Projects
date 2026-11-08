package Court;

import Game.GameState;

public class KingdomCourt {
    private GameState gameState;

    public KingdomCourt(GameState State){
        this.gameState = State;
    }

    public void kingdomDisplay(){
        System.out.println("=".repeat(30));
        System.out.println(" ".repeat(8) + "KINGDOM ARCADE");
        System.out.println("=".repeat(30));

        System.out.println("-".repeat(30));
        System.out.println(" ".repeat(8) + "KINGDOM STATUS");
        System.out.println("-".repeat(30));

        System.out.println();
        System.out.printf("%-25s%-25s%n", "DAY: " + gameState.currentDay, "YEAR: " + gameState.currentYear);

        System.out.println();
        System.out.printf("%-25s%-25s%n", "GOLD: " + String.format("%,d", gameState.gold), "TREASURY: " + String.format("%,d", gameState.treasury));
        System.out.printf("%-25s%-25s%n", "FOOD: " + String.format("%,d", gameState.food), "POPULATION: " + String.format("%,d", gameState.population));
        System.out.printf("%-25s%-25s%n", "ARMY: " + String.format("%,d", gameState.army), "WEAPONS: " + String.format("%,d", gameState.weapons));

        System.out.println();
        System.out.printf("%-25s%-25s%n", "HAPPINESS: " + gameState.happiness + "%", "STABILITY: " + gameState.stability + "%");

        System.out.printf("%-25s%-25s%n", "FAITH: " + gameState.faith + "%", "REPUTATION: " + gameState.kingdomReputation + "%");
    }
}
