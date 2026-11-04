package Game;

import JSON.JSONFileReader;

public class GameState {
    // king information
    public String kingName;
    public int KingAge, kingHealth;

    // kingdom information
    public String kingdomName;
    public int currentDay, currentYear, kingdomReputation;

    // kingdom resources
    public int gold, food, treasury, army, wood, stone, iron, militaryMorale, weapons;

    // population and society
    public int population, happiness, faith, stability, crimeRate, Unemployment, publicApproval;

    // game Progress
    public String gameDifficulty;
    public int eventsCompleted, decisionMade, currentChapter;

    public void assignValues(){
        switch(this.gameDifficulty){
            case "Easy":
                break;
            case "Medium":
                break;
            case "Hard":
                break;
            case "Demon":
                break;
            default:
                throw new IllegalStateException("Unknown Game Difficulty: " + this.gameDifficulty);
        }
    }
}