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
    GameState gameState;
    JSONFileReader fileReader = new JSONFileReader();
    JSONFileWriter writer = new JSONFileWriter();

    public Manager(GameState gameState){
        this.gameState = gameState;
    }

    public void loadGame(String path) throws IOException{
        String content = fileReader.readFile(path);
        Map<String, Object> gameData = fileReader.parse(content);

        // king info
        Map<String, Object> king = (Map<String, Object>) gameData.get("kingInfo");
        gameState.kingName = (String) king.get("KING_NAME");
        gameState.KingAge = (Integer) king.get("KING_AGE");
        gameState.kingHealth = (Integer) king.get("KING_HEALTH");

        // kingdom info
        Map<String, Object> kingdom = (Map<String, Object>) gameData.get("kingdomInfo");
        gameState.kingdomName = (String) kingdom.get("KINGDOM_NAME");
        gameState.currentDay = (Integer) kingdom.get("CURRENT_DAY");
        gameState.currentYear = (Integer) kingdom.get("CURRENT_YEAR");
        gameState.kingdomReputation = (Integer) kingdom.get("KINGDOM_REPUTATION");
        gameState.kingdomStrength = (Integer) kingdom.get("KINGDOM_STRENGTH");

        // resources info
        Map<String, Object> resource = (Map<String, Object>) gameData.get("resourcesInfo");

        gameState.gold = (Integer) resource.get("GOLD");
        gameState.food = (Integer) resource.get("FOOD");
        gameState.treasury = (Integer) resource.get("TREASURY");
        gameState.army = (Integer) resource.get("ARMY");
        gameState.wood = (Integer) resource.get("WOOD");
        gameState.stone = (Integer) resource.get("STONE");
        gameState.iron = (Integer) resource.get("IRON");
        gameState.militaryMorale = (Integer) resource.get("MILITARY_MORALE");
        gameState.weapons = (Integer) resource.get("WEAPONS");

        // population and society info
        Map<String, Object> populationAndSociety = (Map<String, Object>) gameData.get("populationAndSocietyInfo");

        gameState.population = (Integer) populationAndSociety.get("POPULATION");
        gameState.happiness = (Integer) populationAndSociety.get("HAPPINESS");
        gameState.faith = (Integer) populationAndSociety.get("FAITH");
        gameState.stability = (Integer) populationAndSociety.get("STABILITY");
        gameState.crimeRate = (Integer) populationAndSociety.get("CRIME_RATE");
        gameState.Unemployment = (Integer) populationAndSociety.get("UNEMPLOYMENT");
        gameState.publicApproval = (Integer) populationAndSociety.get("PUBLIC_APPROVAL");

        Map<String, Object> gameProgess = (Map<String, Object>) gameData.get("gameStatusInfo");

        gameState.gameDifficulty = (String) gameProgess.get("GAME_DIFFICULTY");
        gameState.eventsCompleted = (Integer) gameProgess.get("EVENTS_COMPLETED");
        gameState.decisionMade = (Integer) gameProgess.get("DECISION_MADE");
        gameState.currentChapter = (Integer) gameProgess.get("CURRENT_CHAPTER");

        Map<String, Object> phase = (Map<String, Object>) gameData.get("phaseInfo");
        gameState.fromPhaseMap(phase);
    }

    public void saveGame(String path) throws IOException {
        Map<String, Object> gameData = new LinkedHashMap<>();

        Map<String, Object> kingInfo = new LinkedHashMap<>();
        kingInfo.put("KING_NAME", gameState.kingName);
        kingInfo.put("KING_AGE", gameState.KingAge);
        kingInfo.put("KING_HEALTH", gameState.kingHealth);
        gameData.put("kingInfo", kingInfo);

        Map<String, Object> kingdomInfo = new LinkedHashMap<>();
        kingdomInfo.put("KINGDOM_NAME", gameState.kingdomName);
        kingdomInfo.put("CURRENT_DAY", gameState.currentDay);
        kingdomInfo.put("CURRENT_YEAR", gameState.currentYear);
        kingdomInfo.put("KINGDOM_REPUTATION", gameState.kingdomReputation);
        kingdomInfo.put("KINGDOM_STRENGTH", gameState.kingdomStrength);
        gameData.put("kingdomInfo", kingdomInfo);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("GOLD", gameState.gold);
        res.put("FOOD", gameState.food);
        res.put("TREASURY", gameState.treasury);
        res.put("ARMY", gameState.army);
        res.put("WOOD", gameState.wood);
        res.put("STONE", gameState.stone);
        res.put("IRON", gameState.iron);
        res.put("MILITARY_MORALE", gameState.militaryMorale);
        res.put("WEAPONS", gameState.weapons);
        gameData.put("resourcesInfo", res); // FIXED key

        Map<String, Object> pop = new LinkedHashMap<>();
        pop.put("POPULATION", gameState.population);
        pop.put("HAPPINESS", gameState.happiness);
        pop.put("FAITH", gameState.faith);
        pop.put("STABILITY", gameState.stability);
        pop.put("CRIME_RATE", gameState.crimeRate);
        pop.put("UNEMPLOYMENT", gameState.Unemployment);
        pop.put("PUBLIC_APPROVAL", gameState.publicApproval);
        gameData.put("populationAndSocietyInfo", pop);

        Map<String, Object> prog = new LinkedHashMap<>();
        prog.put("GAME_DIFFICULTY", gameState.gameDifficulty);
        prog.put("EVENTS_COMPLETED", gameState.eventsCompleted);
        prog.put("DECISION_MADE", gameState.decisionMade);
        prog.put("CURRENT_CHAPTER", gameState.currentChapter);
        gameData.put("gameStatusInfo", prog);

        // PHASE - THIS WAS MISSING
        gameData.put("phaseInfo", gameState.toPhaseMap());

        writer.writeFile(path, gameData);
    }

    public void clearData(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }
}