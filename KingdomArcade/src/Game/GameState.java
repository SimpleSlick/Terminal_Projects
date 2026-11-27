package Game;

import ENUMS.ConsequenceType;
import Engine.CalculateConsequence;
import JSON.JSONFileReader;
import ENUMS.ActivityType;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
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
    public int phaseDurationLeft = 0, phaseTotalDuration = 0;
    public Map<String, Object> activePhaseCost = null, activePhaseInfo = null;

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
        this.kingdomStrength = (Integer) selectedData.get("kingdomStrength");

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

    public String Consequence(boolean isPhase) {
        StringBuilder sb = new StringBuilder();

        if(isPhase){
            sb.append("\n=== ").append(this.currentPhase).append(" IMPACT ===\n");
        }else{
            sb.append("\n=== DAY").append(this.currentDay).append(" SUMMARY ===\n");
        }

        sb.append(formatStatLine("Food", this.oldFood, this.food));
        sb.append(formatStatLine("Gold", this.oldGold, this.gold));
        sb.append(formatStatLine("Treasury", this.oldTreasury, this.treasury));
        sb.append(formatStatLine("Happiness", this.oldHappiness, this.happiness));
        sb.append(formatStatLine("Population", this.oldPopulation, this.population));
        sb.append(formatStatLine("Army", this.oldArmy, this.army));
        sb.append(formatStatLine("Wood", this.oldWood, this.wood));
        sb.append(formatStatLine("Stone", this.oldStone, this.stone));
        sb.append(formatStatLine("Iron", this.oldIron, this.iron));
        sb.append(formatStatLine("Weapons", this.oldWeapons, this.weapons));
        sb.append(formatStatLine("Stability", this.oldStability, this.stability));
        sb.append(formatStatLine("Faith", this.oldFaith, this.faith));
        sb.append(formatStatLine("Crime Rate", this.oldCrimeRate, this.crimeRate));
        sb.append(formatStatLine("Unemployment", this.oldUnemployment, this.Unemployment));
        sb.append(formatStatLine("Public Approval", this.oldApproval, this.publicApproval));
        sb.append(formatStatLine("Kingdom Reputation", this.oldReputation, this.kingdomReputation));
        sb.append(formatStatLine("Kingdom Strength", this.oldStrength, this.kingdomStrength));
        sb.append(formatStatLine("Military Morale", this.oldMorale, this.militaryMorale));

        return sb.toString();
    }

    private String formatStatLine(String statName, int oldValue, int value) {
        int diff = value - oldValue;

        if (diff == 0) {
            return ""; // if nothing changed, stat hidden
        }

        String sign = diff > 0 ? "+" : "";
        return String.format("- %s: %d -> %d (%s%d)\n", statName, oldValue, value, sign, diff);
    }

    public Map<String, Object> toPhaseMap(){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("currentPhase", this.currentPhase == null ? "NONE" : currentPhase.name());
        map.put("phaseDurationLeft", this.phaseDurationLeft);
        map.put("phaseTotalDuration", this.phaseTotalDuration);
        map.put("activePhaseCost", this.activePhaseCost == null ? "" : this.activePhaseCost);
        map.put("activePhaseInfo", this.activePhaseInfo == null ? "" : this.activePhaseInfo);


        return map;
    }

    public void fromPhaseMap(Map<String, Object> map) {
        if (map == null) {
            currentPhase = ActivityType.NONE;
            phaseDurationLeft = 0;
            phaseTotalDuration = 0;
            activePhaseCost = null;
            activePhaseInfo = null;
            return;
        }
        String phase = (String) map.get("currentPhase");
        currentPhase = phase == null ? ActivityType.NONE : ActivityType.valueOf(phase);
        phaseDurationLeft = map.get("phaseDurationLeft") == null ? 0 : (Integer) map.get("phaseDurationLeft");
        phaseTotalDuration = map.get("phaseTotalDuration") == null ? 0 : (Integer) map.get("phaseTotalDuration");
        activePhaseCost = (Map<String, Object>) map.get("activePhaseCost");
        activePhaseInfo = (Map<String, Object>) map.get("activePhaseInfo");
    }
}