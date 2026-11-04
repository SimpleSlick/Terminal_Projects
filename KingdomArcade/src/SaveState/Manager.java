package SaveState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Game.GameState;

public class Manager {
    GameState gameState = new GameState();
    // load function for game
    public void loadKing() {
        try {
            File file = new File("Resources/SaveLog1/King.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]) {
                    case "KING_NAME":
                        gameState.kingName = data[1];
                        break;
                    case "KING_AGE":
                        gameState.KingAge = Integer.parseInt(data[1]);
                        break;
                    case "KING_HEALTH":
                        gameState.kingHealth = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to locate the file");
        }
    }

    public void loadKingdom() {
        try {
            File file = new File("Resources/SaveLog1/Kingdom.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]) {
                    case "KINGDOM_NAME":
                        gameState.kingdomName = data[1];
                        break;
                    case "CURRENT_YEAR":
                        gameState.currentYear = Integer.parseInt(data[1]);
                        break;
                    case "CURRENT_DAY":
                        gameState.currentDay = Integer.parseInt(data[1]);
                        break;
                    case "KINGDOM_REPUTATION":
                        gameState.kingdomReputation = Integer.parseInt(data[1]);
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to locate the file");
        }
    }

    public void loadResources() {
        try {
            File file = new File("Resources/SaveLog1/KingdomResources.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]) {
                    case "GOLD":
                        gameState.gold = Integer.parseInt(data[1]);
                        break;
                    case "FOOD":
                        gameState.food = Integer.parseInt(data[1]);
                        break;
                    case "TREASURY":
                        gameState.treasury = Integer.parseInt(data[1]);
                        break;
                    case "ARMY":
                        gameState.army = Integer.parseInt(data[1]);
                        break;
                    case "WOOD":
                        gameState.wood = Integer.parseInt(data[1]);
                        break;
                    case "IRON":
                        gameState.iron = Integer.parseInt(data[1]);
                        break;
                    case "STONE":
                        gameState.stone = Integer.parseInt(data[1]);
                        break;
                    case "MILITARY_MORALE":
                        gameState.militaryMorale = Integer.parseInt(data[1]);
                        break;
                    case "WEAPONS":
                        gameState.weapons = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to locate the file");
        }
    }

    public void loadPopulationAndSociety() {
        try {
            File file = new File("Resources/SaveLog1/PopulationAndSociety.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]) {
                    case "POPULATION":
                        gameState.population = Integer.parseInt(data[1]);
                        break;
                    case "HAPPINESS":
                        gameState.happiness = Integer.parseInt(data[1]);
                        break;
                    case "FAITH":
                        gameState.faith = Integer.parseInt(data[1]);
                        break;
                    case "STABILITY":
                        gameState.stability = Integer.parseInt(data[1]);
                        break;
                    case "CRIME_RATE":
                        gameState.crimeRate = Integer.parseInt(data[1]);
                        break;
                    case "UNEMPLOYMENT":
                        gameState.Unemployment = Integer.parseInt(data[1]);
                        break;
                    case "PUBLIC_APPROVAL":
                        gameState.publicApproval = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to locate the file");
        }
    }

    public void loadGameProgress() {
        try {
            File file = new File("Resources/SaveLog1/GameStatus.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]) {
                    case "GAME_DIFFICULTY":
                        gameState.gameDifficulty = data[1];
                        break;
                    case "EVENTS_COMPLETED":
                        gameState.eventsCompleted = Integer.parseInt(data[1]);
                        break;
                    case "DECISION_MADE":
                        gameState.decisionMade = Integer.parseInt(data[1]);
                        break;
                    case "CURRENT_CHAPTER":
                        gameState.currentChapter = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to locate the file");
        }
    }

    public void loadGame() {
        loadKing();
        loadKingdom();
        loadResources();
        loadPopulationAndSociety();
        loadGameProgress();
    }

    public void saveKingInfo() {
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

    public void saveKingdomInfo() {
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

    public void saveKingdomResourceInfo() {
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

    public void savePopulationAndSocietyInfo() {
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

    public void saveGameStatusInfo() {
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