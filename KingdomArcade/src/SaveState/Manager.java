package SaveState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;

public class Manager {
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

    // load function for game
    public void loadKing(){
        try{
            File file = new File("Resources/SaveLog1/King.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]){
                    case "KING_NAME":
                        kingName = data[1];
                        break;
                    case "KING_AGE":
                        KingAge = Integer.parseInt(data[1]);
                        break;
                    case "KING_HEALTH":
                        kingHealth = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }

    public void loadKingdom(){
        try{
            File file = new File("Resources/SaveLog1/Kingdom.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]){
                    case "KINGDOM_NAME":
                        kingdomName = data[1];
                        break;
                    case "CURRENT_YEAR":
                        currentYear = Integer.parseInt(data[1]);
                        break;
                    case "CURRENT_DAY":
                        currentDay = Integer.parseInt(data[1]);
                        break;
                    case "KINGDOM_REPUTATION":
                        kingdomReputation = Integer.parseInt(data[1]);
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }

    public void loadResources(){
        try{
            File file = new File("Resources/SaveLog1/KingdomResources.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]){
                    case "GOLD":
                        gold = Integer.parseInt(data[1]);
                        break;
                    case "FOOD":
                        food = Integer.parseInt(data[1]);
                        break;
                    case "TREASURY":
                        treasury = Integer.parseInt(data[1]);
                        break;
                    case "ARMY":
                        army = Integer.parseInt(data[1]);
                        break;
                    case "WOOD":
                        wood = Integer.parseInt(data[1]);
                        break;
                    case "IRON":
                        iron = Integer.parseInt(data[1]);
                        break;
                    case "STONE":
                        stone = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }

    public void loadPopulationAndSociety(){
        try{
            File file = new File("Resources/SaveLog1/PopulationAndSociety.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]){
                    case "POPULATION":
                        population = Integer.parseInt(data[1]);
                        break;
                    case "HAPPINESS":
                        happiness = Integer.parseInt(data[1]);
                        break;
                    case "FAITH":
                        faith = Integer.parseInt(data[1]);
                        break;
                    case "STABILITY":
                        stability = Integer.parseInt(data[1]);
                        break;
                    case "CRIME_RATE":
                        crimeRate = Integer.parseInt(data[1]);
                        break;
                    case "UNEMPLOYMENT":
                        Unemployment = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }

    public void loadGameProgress(){
        try{
            File file = new File("Resources/SaveLog1/GameStatus.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                data[0] = data[0].trim();
                data[1] = data[1].trim();

                switch (data[0]){
                    case "GAME_DIFFICULTY":
                        gameDifficulty = data[1];
                        break;
                    case "EVENTS_COMPLETED":
                        eventsCompleted = Integer.parseInt(data[1]);
                        break;
                    case "DECISION_MADE":
                        decisionMade = Integer.parseInt(data[1]);
                        break;
                    case "CURRENT_CHAPTER":
                        currentChapter = Integer.parseInt(data[1]);
                        break;
                    default:
                        System.out.println("Error in data loading");
                }
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }
}