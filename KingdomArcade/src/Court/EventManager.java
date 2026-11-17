package Court;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import JSON.JSONFileReader;
import Engine.Engine;
import Game.GameState;

public class EventManager {
    private final GameState gameState = new GameState();
    private final JSONFileReader fileReader = new JSONFileReader();
    private final Engine engine = new Engine();
    private final String path = "Resources/GameplayData/Events.json";
    private final int WIDTH = 70;
    private final Scanner scanner = new Scanner(System.in);

    protected void showHeader() throws IOException {
        engine.animateDivider(WIDTH, 10, true);
        engine.animateCentered("ROYAL COURT", WIDTH, 40);
        engine.animateDivider(WIDTH, 10, true);
        System.out.println();
    }

    protected void showChoices(List<Object> choices){
        engine.animateDivider(WIDTH, 15, false);
        engine.animateCentered("YOUR CHOICES", WIDTH, 40);
        engine.animateDivider(WIDTH, 15, false);

        for(Object choice: choices){
            Map<String, Object> choiceData = (Map<String, Object>) choice;

            int choiceID = (Integer) choiceData.get("choiceId");
            String choiceName = (String) choiceData.get("choiceName");
            String description = (String) choiceData.get("description");

            System.out.println();
            engine.animateText("[" + choiceID + "]" + choiceName, 35, WIDTH);
            engine.animateText("   " + description, 35, WIDTH);
        }
    }

    private int selectChoice(int total_choice){
        int choice;
        while(true){
            System.out.print("Enter your decision: ");

            if(scanner.hasNextInt()){
                choice = scanner.nextInt();

                if(choice >= 1 && choice <= total_choice){
                    gameState.decisionMade += 1;
                    return choice;
                }

                System.out.println("Invalid choice! Please enter a number between 1 and " + total_choice + ".");
            }else{
                System.out.println("Invalid input! Please enter a number");
                scanner.next();
            }
        }
    }

    public void showEvent(int eventNumber) throws IOException{
        Map<String, Object> eventData = fileReader.parse(fileReader.readFile(path));
        String event = "event" + eventNumber;

        Map<String, Object> SelectedEventData = (Map<String, Object>) eventData.get(event);
        List<Object> choices = (List<Object>) SelectedEventData.get("choices");

        showHeader();
        engine.animateCentered((String) SelectedEventData.get("eventName"), WIDTH, 35);
        System.out.println();
        engine.animateText((String) SelectedEventData.get("description"), 40, WIDTH);

        showChoices(choices);
        int selectedChoice = selectChoice(choices.size());

        Map<String, Object> choiceData = (Map<String, Object>) choices.get(selectedChoice - 1);
        Map<String, Object> consequences = (Map<String, Object>) choiceData.get("consequences");

        gameState.applyConsequences(consequences);
    }
}