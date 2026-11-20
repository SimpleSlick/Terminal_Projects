package Game;

import ENUMS.ConsequenceType;
import Engine.CalculateConsequence;
import JSON.JSONFileReader;
import ENUMS.ActivityType;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

public class GameState {
    // king information
    public String kingName;
    public int KingAge, kingHealth;

    // kingdom information
    public String kingdomName;
    public int currentDay, currentYear, kingdomReputation, kingdomStrength;

    // kingdom resources
    public int gold, food, treasury, army, wood, stone, iron, militaryMorale, weapons;

    // population and society
    public int population, happiness, faith, stability, crimeRate, Unemployment, publicApproval;

    // game Progress
    public String gameDifficulty;
    public int eventsCompleted, decisionMade, currentChapter;

    // Temporary Storage for Difference Showing
    public int oldGold, oldFood, oldTreasury, oldArmy, oldWood, oldStone, oldIron, oldMorale, oldReputation, oldStrength;
    public int oldWeapons, oldPopulation, oldHappiness, oldFaith, oldStability, oldCrimeRate, oldUnemployment, oldApproval;

    // Phase
    public ActivityType currentPhase;

    JSONFileReader jsonFileReader = new JSONFileReader();
    protected EnumSet<ConsequenceType> modifiedVariables = EnumSet.noneOf(ConsequenceType.class);

    protected CalculateConsequence calculateConsequence = new CalculateConsequence(this);

    public void assignValues() throws IOException {
        String content = jsonFileReader.readFile("Resources/GameData/InitialValue.json");

        Map<String, Object> gameData = jsonFileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get(this.gameDifficulty);

        if(selectedData == null){
            throw new IllegalStateException("Unknown Game Difficulty: " + this.gameDifficulty);
        }

        this.gold = (Integer) selectedData.get("gold");
        this.food = (Integer) selectedData.get("food");
        this.treasury = (Integer) selectedData.get("treasury");
        this.army = (Integer) selectedData.get("army");
        this.wood = (Integer) selectedData.get("wood");
        this.stone = (Integer) selectedData.get("stone");
        this.iron = (Integer) selectedData.get("iron");
        this.weapons = (Integer) selectedData.get("weapons");

        this.population = (Integer) selectedData.get("population");
        this.happiness = (Integer) selectedData.get("happiness");
        this.faith = (Integer) selectedData.get("faith");
        this.stability = (Integer) selectedData.get("stability");
        this.crimeRate = (Integer) selectedData.get("crimeRate");
        this.Unemployment = (Integer) selectedData.get("unemployment");
        this.publicApproval = (Integer) selectedData.get("publicApproval");

        this.militaryMorale = (Integer) selectedData.get("militaryMorale");
        this.kingHealth = (Integer) selectedData.get("kingHealth");
        this.currentDay = (Integer) selectedData.get("currentDay");
        this.currentYear = (Integer) selectedData.get("currentYear");
        this.kingdomReputation = (Integer) selectedData.get("kingdomReputation");

        this.eventsCompleted = (Integer) selectedData.get("eventsCompleted");
        this.currentChapter = (Integer) selectedData.get("currentChapter");
    }

    // basic variable assigning
    public void applyConsequences(Map<String, Object> consequencesData){
        modifiedVariables.clear();

        for(Map.Entry<String, Object> entry: consequencesData.entrySet()){
            String key = entry.getKey();
            int value = (Integer) entry.getValue();

            switch(key){
                case "food":
                    this.food += value;
                    modifiedVariables.add(ConsequenceType.FOOD);
                    break;
                case "gold":
                    this.gold += value;
                    modifiedVariables.add(ConsequenceType.GOLD);
                    break;
                case "happinessChange":
                    this.happiness += value;
                    modifiedVariables.add(ConsequenceType.HAPPINESS);
                    break;
                case "population":
                    this.population += value;
                    modifiedVariables.add(ConsequenceType.POPULATION);
                    break;
                case "army":
                    this.army += value;
                    modifiedVariables.add(ConsequenceType.ARMY);
                    break;
                case "stone":
                    this.stone += value;
                    modifiedVariables.add(ConsequenceType.STONE);
                    break;
                case "wood":
                    this.wood += value;
                    modifiedVariables.add(ConsequenceType.WOOD);
                    break;
                case "iron":
                    this.iron += value;
                    modifiedVariables.add(ConsequenceType.IRON);
                    break;
                case "weapons":
                    this.weapons += value;
                    modifiedVariables.add(ConsequenceType.WEAPONS);
                    break;
            }
        }
        calculateConsequence.calculateImpacts(modifiedVariables);
    }

    public void captureSnapShot(){
        this.oldFood = this.food;
        this.oldTreasury = this.treasury;
        this.oldGold = this.gold;
        this.oldArmy = this.army;
        this.oldWood = this.wood;
        this.oldStone = this.stone;
        this.oldMorale = this.militaryMorale;
        this.oldIron = this.iron;
        this.oldReputation = this.kingdomReputation;
        this.oldStrength = this.kingdomStrength;
        this.oldWeapons = this.weapons;
        this.oldPopulation = this.population;
        this.oldFaith = this.faith;
        this.oldHappiness = this.happiness;
        this.oldStability = this.stability;
        this.oldCrimeRate = this.crimeRate;
        this.oldApproval = this.publicApproval;
        this.oldUnemployment = this.Unemployment;
    }
}