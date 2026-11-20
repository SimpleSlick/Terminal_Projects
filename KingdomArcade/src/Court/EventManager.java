package Court;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import JSON.JSONFileReader;
import Engine.*;
import Game.GameState;

public class EventManager {
    private final GameState gameState;
    private final JSONFileReader fileReader = new JSONFileReader();
    private final Engine engine = new Engine();
    public final int WIDTH = 70;
    private final Scanner scanner = new Scanner(System.in);
    private final ActivityConsequence activity;

    public EventManager(GameState gameState) {
        this.gameState = gameState;
        this.activity = new ActivityConsequence(gameState);
    }

    private String getCurrentDayFilePath() {
        // Example: Resources/GameplayData/Chapter1/Day1.json
        return String.format("Resources/GameplayData/%s/Chapter%d/Day%d.json", gameState.gameDifficulty, gameState.currentChapter, gameState.currentDay);
    }

    public void playDay() throws IOException {
        // Take snapshot at the start of the day
        gameState.captureSnapShot();
        activity.applyDailyPhase(gameState);

        String currentFile = getCurrentDayFilePath();
        Map<String, Object> dayData;

        try {
            dayData = fileReader.parse(fileReader.readFile(currentFile));
        } catch (IOException e) {
            System.out.println("No events found for this day (" + currentFile + "). Ending chapter...");

            return;
        }

        List<Object> events = (List<Object>) dayData.get("dayEvents");
        int totalEvents = events.size();

        engine.animateCentered("---CHAPTER " + gameState.currentChapter + " | DAY " + gameState.currentDay + " ---", WIDTH, 20);
        System.out.println();
        int currentEventIndex;

        // Loop through the events
        for (int i = 0; i < totalEvents; i++) {
            currentEventIndex = i + 1;
            Map<String, Object> eventData = (Map<String, Object>) events.get(i);
            showEvent(eventData, currentEventIndex, totalEvents);
            gameState.eventsCompleted++;
        }

        // Show the summary at the end of the day
        System.out.println("\n The Day has ended.");
        System.out.println(gameState.Consequence(false));

        // Increment Day
        gameState.currentDay++;
        System.out.println("\nPress Enter to Continue");
        scanner.nextLine();
    }

    protected void showHeader() throws IOException {
        engine.animateDivider(WIDTH, 10, true);
        engine.animateCentered("ROYAL COURT", WIDTH, 40);
        engine.animateDivider(WIDTH, 10, true);
        System.out.println();
    }

    protected void showChoices(List<Object> choices) {
        engine.animateDivider(WIDTH, 15, false);
        engine.animateCentered("YOUR CHOICES", WIDTH, 40);
        engine.animateDivider(WIDTH, 15, false);

        for (Object choice : choices) {
            Map<String, Object> choiceData = (Map<String, Object>) choice;

            int choiceID = (Integer) choiceData.get("choiceId");
            String choiceName = (String) choiceData.get("choiceName");
            String description = (String) choiceData.get("description");

            System.out.println();
            engine.animateText("[" + choiceID + "]" + choiceName, 35, WIDTH);
            engine.animateText("   " + description, 35, WIDTH);
        }
    }

    private int selectChoice(int total_choice) {
        int choice;
        while (true) {
            System.out.print("Enter your decision: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();

                if (choice >= 1 && choice <= total_choice) {
                    gameState.decisionMade += 1;
                    return choice;
                }

                System.out.println("Invalid choice! Please enter a number between 1 and " + total_choice + ".");
            } else {
                System.out.println("Invalid input! Please enter a number");
                scanner.next();
            }
        }
    }

    public void showEvent(Map<String, Object> SelectedEventData, int currentEventNum, int totalEvents) throws IOException {
        List<Object> choices = (List<Object>) SelectedEventData.get("choices");

        showHeader();
        engine.animateCentered("EVENT " + currentEventNum + " of " + totalEvents, WIDTH, 20);
        engine.animateCentered((String) SelectedEventData.get("eventName"), WIDTH, 35);
        System.out.println();

        engine.animateText((String) SelectedEventData.get("description"), 40, WIDTH);

        showChoices(choices);
        int selectedChoice = selectChoice(choices.size());

        Map<String, Object> choiceData = (Map<String, Object>) choices.get(selectedChoice - 1);
        Map<String, Object> consequences = (Map<String, Object>) choiceData.get("consequences");

        if (consequences != null) {
            gameState.applyConsequences(consequences);
        }

        if(choiceData.containsKey("phaseTrigger") && choiceData.containsKey("phaseCost")){
            String phaseName = (String) choiceData.get("phaseTrigger");
            Map<String, Object> phaseCost = (Map<String, Object>) choiceData.get("phaseCost");

            gameState.currentPhase = ENUMS.ActivityType.valueOf(phaseName);
            gameState.activePhaseCost = phaseCost;

            gameState.phaseDurationLeft = phaseCost.containsKey("duration") ? (Integer) phaseCost.get("duration") : 1;

            System.out.println("\n-> [" + phaseName + " Phase started! It will last for " + gameState.phaseDurationLeft + " days.]");
        }

        System.out.println(" \nYour decision is been recorded");
        System.out.println();
    }
}