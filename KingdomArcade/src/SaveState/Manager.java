package SaveState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import Game.GameState;
import JSON.JSONFileReader;

public class Manager {
    GameState gameState = new GameState();
    JSONFileReader fileReader = new JSONFileReader();

    // load function for game
    private void loadKing(String path) throws IOException {
        String FILE_PATH = path;
        String content = fileReader.readFile(FILE_PATH);

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
        String FILE_PATH = "Resources/saveFile1/gameInfo.json";
        String content = fileReader.readFile(FILE_PATH);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("kingdomInfo");

        if(selectedData == null){
            throw new IllegalStateException("Data not found");
        }

        gameState.kingName = (String) selectedData.get("KINGDOM_NAME");
        gameState.currentDay = (Integer) selectedData.get("CURRENT_DAY");
        gameState.currentYear = (Integer) selectedData.get("CURRENT_YEAR");
        gameState.kingdomReputation = (Integer) selectedData.get("KINGDOM_REPUTATION");
    }

    private void loadResources(String path) throws IOException {
        String FILE_PATH = path;
        String content = fileReader.readFile(FILE_PATH);

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
        String FILE_PATH = path;
        String content = fileReader.readFile(FILE_PATH);

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
        String FILE_PATH = path;
        String content = fileReader.readFile(FILE_PATH);

        Map<String, Object> gameData = fileReader.parse(content);
        Map<String, Object> selectedData = (Map<String, Object>) gameData.get("gameProgressInfo");

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

    private void saveKingInfo() {
        try {
            File file = new File("Resources/SaveLog1/King.txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("KING_NAME = " + gameState.kingName);
            writer.newLine();
            writer.write("KING_AGE = " + gameState.KingAge);
            writer.newLine();
            writer.write("KING_HEALTH = " + gameState.kingHealth);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred file writing the file: " + e.getMessage());
        }
    }

    private void saveKingdomInfo() {
        try {
            File file = new File("Resources/SaveLog1/Kingdom.txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("KINGDOM_NAME = " + gameState.kingdomName);
            writer.newLine();
            writer.write("CURRENT_DAY = " + gameState.currentDay);
            writer.newLine();
            writer.write("CURRENT_YEAR = " + gameState.currentYear);
            writer.newLine();
            writer.write("KINGDOM_REPUTATION = " + gameState.kingdomReputation);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred file writing the file: " + e.getMessage());
        }
    }

    private void saveKingdomResourceInfo() {
        try {
            File file = new File("Resources/SaveLog1/KingdomResources.txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("GOLD = " + gameState.gold);
            writer.newLine();
            writer.write("FOOD = " + gameState.food);
            writer.newLine();
            writer.write("TREASURY = " + gameState.treasury);
            writer.newLine();
            writer.write("ARMY = " + gameState.army);
            writer.newLine();
            writer.write("WOOD = " + gameState.wood);
            writer.newLine();
            writer.write("STONE = " + gameState.stone);
            writer.newLine();
            writer.write("IRON = " + gameState.iron);
            writer.newLine();
            writer.write("MILITARY_MORALE = " + gameState.militaryMorale);
            writer.newLine();
            writer.write("WEAPONS = " + gameState.weapons);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred file writing the file: " + e.getMessage());
        }
    }

    private void savePopulationAndSocietyInfo() {
        try {
            File file = new File("Resources/SaveLog1/PopulationAndSocietyInfo.txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("POPULATION = " + gameState.population);
            writer.newLine();
            writer.write("HAPPINESS = " + gameState.happiness);
            writer.newLine();
            writer.write("FAITH = " + gameState.faith);
            writer.newLine();
            writer.write("STABILITY = " + gameState.stability);
            writer.newLine();
            writer.write("CRIME_RATE = " + gameState.crimeRate);
            writer.newLine();
            writer.write("UNEMPLOYMENT = " + gameState.Unemployment);
            writer.newLine();
            writer.write("PUBLIC_APPROVAL = " + gameState.publicApproval);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred file writing the file: " + e.getMessage());
        }
    }

    private void saveGameStatusInfo() {
        try {
            File file = new File("Resources/SaveLog1/King.txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("GAME_DIFFICULTY = " + gameState.gameDifficulty);
            writer.newLine();
            writer.write("EVENTS_COMPLETED = " + gameState.eventsCompleted);
            writer.newLine();
            writer.write("DECISION_MADE = " + gameState.decisionMade);
            writer.newLine();
            writer.write("CURRENT_CHAPTER = " + gameState.currentChapter);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred file writing the file: " + e.getMessage());
        }
    }

    public void saveGame(){
        saveKingInfo();
        saveKingdomInfo();
        saveKingdomResourceInfo();
        savePopulationAndSocietyInfo();
        saveGameStatusInfo();
    }
}