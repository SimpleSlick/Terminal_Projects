package Game;

import JSON.JSONFileReader;
import java.io.IOException;
import java.util.Map;

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

    JSONFileReader jsonFileReader = new JSONFileReader();

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
}