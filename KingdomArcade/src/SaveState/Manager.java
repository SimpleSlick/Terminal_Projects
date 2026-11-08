package SaveState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import Game.GameState;
import JSON.JSONFileReader;
import JSON.JSONFileWriter;

public class Manager {
    GameState gameState = new GameState();
    JSONFileReader fileReader = new JSONFileReader();
    JSONFileWriter writer = new JSONFileWriter();

    // load function for game
    private void loadKing(String path) throws IOException {
        String content = fileReader.readFile(path);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("kingInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.kingName = (String) selectedData.get("KING_NAME");
        gameState.KingAge = (Integer) selectedData.get("KING_AGE");
        gameState.kingHealth = (Integer) selectedData.get("KING_HEALTH");
    }

    private void loadKingdom(String path) throws IOException{
        String content = fileReader.readFile(path);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("kingdomInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.kingName = (String) selectedData.get("KINGDOM_NAME");
        gameState.currentDay = (Integer) selectedData.get("CURRENT_DAY");
        gameState.currentYear = (Integer) selectedData.get("CURRENT_YEAR");
        gameState.kingdomReputation = (Integer) selectedData.get("KINGDOM_REPUTATION");
        gameState.kingdomStrength = (Integer) selectedData.get("KINGDOM_STRENGTH");
    }

    private void loadResources(String path) throws IOException {
        String content = fileReader.readFile(path);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("resourcesInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.gold = (Integer) selectedData.get("GOLD");
        gameState.food = (Integer) selectedData.get("FOOD");
        gameState.treasury = (Integer) selectedData.get("TREASURY");
        gameState.army = (Integer) selectedData.get("ARMY");
        gameState.wood = (Integer) selectedData.get("WOOD");
        gameState.stone = (Integer) selectedData.get("STONE");
        gameState.iron = (Integer) selectedData.get("IRON");
        gameState.militaryMorale = (Integer) selectedData.get("MILITARY_MORALE");
        gameState.weapons = (Integer) selectedData.get("WEAPONS");
    }

    private void loadPopulationAndSociety(String path) throws IOException {
        String content = fileReader.readFile(path);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("populationAndSocietyInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.population = (Integer) selectedData.get("POPULATION");
        gameState.happiness = (Integer) selectedData.get("HAPPINESS");
        gameState.faith = (Integer) selectedData.get("FAITH");
        gameState.stability = (Integer) selectedData.get("STABILITY");
        gameState.crimeRate = (Integer) selectedData.get("CRIME_RATE");
        gameState.Unemployment = (Integer) selectedData.get("UNEMPLOYMENT");
        gameState.publicApproval = (Integer) selectedData.get("PUBLIC_APPROVAL");
    }

    private void loadGameProgress(String path) throws IOException {
        String content = fileReader.readFile(path);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("gameStatusInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.gameDifficulty = (String) selectedData.get("GAME_DIFFICULTY");
        gameState.eventsCompleted = (Integer) selectedData.get("EVENTS_COMPLETED");
        gameState.decisionMade = (Integer) selectedData.get("DECISION_MADE");
        gameState.currentChapter = (Integer) selectedData.get("CURRENT_CHAPTER");
    }

    public void loadGame(String path) throws IOException{
        loadKing(path);
        loadKingdom(path);
        loadResources(path);
        loadPopulationAndSociety(path);
        loadGameProgress(path);
    }

    private Map<String, Object> saveKingInfo() throws IOException{
        Map<String, Object> kingInfo = new LinkedHashMap<>();

        kingInfo.put("KING_NAME", gameState.kingName);
        kingInfo.put("KING_AGE", gameState.KingAge);
        kingInfo.put("KING_HEALTH", gameState.kingHealth);

        Map<String, Object> gameData = new LinkedHashMap<>();
        gameData.put("kingInfo", kingInfo);

        return gameData;
    }

    private Map<String, Object> saveKingdomInfo() throws IOException{
        Map<String, Object> kingdomInfo = new LinkedHashMap<>();

        kingdomInfo.put("KINGDOM_NAME", gameState.kingdomName);
        kingdomInfo.put("CURRENT_DAY", gameState.currentDay);
        kingdomInfo.put("CURRENT_YEAR", gameState.currentYear);
        kingdomInfo.put("KINGDOM_REPUTATION", gameState.kingdomReputation);
        kingdomInfo.put("KINGDOM_STRENGTH", gameState.kingdomStrength);

        Map<String, Object> gameData = new LinkedHashMap<>();
        gameData.put("kingdomInfo", kingdomInfo);

        return gameData;
    }

    private Map<String, Object> saveKingdomResourceInfo() throws IOException{
        Map<String, Object> kingdomResourceInfo = new LinkedHashMap<>();

        kingdomResourceInfo.put("GOLD", gameState.gold);
        kingdomResourceInfo.put("FOOD", gameState.food);
        kingdomResourceInfo.put("TREASURY", gameState.treasury);
        kingdomResourceInfo.put("ARMY", gameState.army);
        kingdomResourceInfo.put("WOOD", gameState.wood);
        kingdomResourceInfo.put("STONE", gameState.stone);
        kingdomResourceInfo.put("IRON", gameState.iron);
        kingdomResourceInfo.put("MILITARY_MORALE", gameState.militaryMorale);
        kingdomResourceInfo.put("WEAPONS", gameState.weapons);

        Map<String, Object> gameData = new LinkedHashMap<>();
        gameData.put("kingdomResourceInfo", kingdomResourceInfo);

        return gameData;
    }

    private Map<String, Object> savePopulationAndSocietyInfo() throws IOException{
        Map<String, Object> populationAndSocietyInfo = new LinkedHashMap<>();

        populationAndSocietyInfo.put("POPULATION", gameState.population);
        populationAndSocietyInfo.put("HAPPINESS", gameState.happiness);
        populationAndSocietyInfo.put("FAITH", gameState.faith);
        populationAndSocietyInfo.put("STABILITY", gameState.stability);
        populationAndSocietyInfo.put("CRIME_RATE", gameState.crimeRate);
        populationAndSocietyInfo.put("UNEMPLOYMENT", gameState.Unemployment);
        populationAndSocietyInfo.put("PUBLIC_APPROVAL", gameState.publicApproval);

        Map<String, Object> gameData = new LinkedHashMap<>();
        gameData.put("populationAndSocietyInfo", populationAndSocietyInfo);

        return gameData;
    }

    private Map<String, Object> saveGameStatusInfo() throws IOException{
        Map<String, Object> gameStatusInfo = new LinkedHashMap<>();

        gameStatusInfo.put("GAME_DIFFICULTY", gameState.gameDifficulty);
        gameStatusInfo.put("EVENTS_COMPLETED", gameState.eventsCompleted);
        gameStatusInfo.put("DECISION_MADE", gameState.decisionMade);
        gameStatusInfo.put("CURRENT_CHAPTER", gameState.currentChapter);

        Map<String, Object> gameData = new LinkedHashMap<>();
        gameData.put("gameStatusInfo", gameStatusInfo);


        return gameData;
    }

    public void saveGame(String path) throws IOException{
        Map<String, Object> gameData = new LinkedHashMap<>();

        gameData.putAll(saveKingInfo());
        gameData.putAll(saveKingdomInfo());
        gameData.putAll(saveKingdomResourceInfo());
        gameData.putAll(savePopulationAndSocietyInfo());
        gameData.putAll(saveGameStatusInfo());

        writer.writeFile(path, gameData);
    }

    public void clearData(String filePath) throws IOException{
        Path path = Path.of(filePath);

        Files.deleteIfExists(path);
    }
}