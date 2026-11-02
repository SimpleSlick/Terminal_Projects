package Game;

public class GameState {
    // king information
    public String kingName;
    public int KingAge, kingHealth;

    // kingdom information
    public String kingdomName;
    public int currentDay, currentYear, kingdomReputation;

    // kingdom resources
    public int gold, food, treasury, army, wood, stone, iron;

    // population and society
    public int population, happiness, faith, stability, crimeRate, Unemployment;

    // game Progress
    public String gameDifficulty;
    public int eventsCompleted, decisionMade, currentChapter;
}