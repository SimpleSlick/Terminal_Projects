package SaveState;

public class Manager {
    // king information
    public String kingName;
    public int KingAge, kingHealth;

    // kingdom information
    public String kingdomName;
    public int currentDay, currentYear, kingdomReputation;

    // kingdom resources
    public int gold, food, treasury, army;

    // population and society
    public int population, happiness, faith, stability;

    // game Progress
    public String gameDifficulty;
    public int eventsCompleted, decisionMade;
}