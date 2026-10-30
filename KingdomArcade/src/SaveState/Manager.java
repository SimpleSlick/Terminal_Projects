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
    public int gold, food, treasury, army;

    // population and society
    public int population, happiness, faith, stability;

    // game Progress
    public String gameDifficulty;
    public int eventsCompleted, decisionMade;

    public void loadKing(){
        try{
            File file = new File("Resources/SaveLog1/King.txt");

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while((line = reader.readLine()) != null){
                String[] data = line.split("=", 2);

                System.out.println(data[0] + " : " + data[1]);
            }

            reader.close();
        }catch (IOException e){
            System.out.println("Unable to locate the file");
        }
    }
}