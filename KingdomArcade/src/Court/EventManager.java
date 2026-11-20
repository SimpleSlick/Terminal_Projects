package Court;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import JSON.JSONFileReader;
import Engine.Engine;
import Game.GameState;

public class EventManager {
    private final GameState gameState;
    private final JSONFileReader fileReader = new JSONFileReader();
    private final Engine engine = new Engine();
    public final int WIDTH = 70;
    private final Scanner scanner = new Scanner(System.in);

    public EventManager(GameState gameState) {
        this.gameState = gameState;
    }

    private String getCurrentDayFilePath() {
        // Example: Resources/GameplayData/Chapter1/Day1.json
        return String.format("Resources/GameplayData/%s/Chapter%d/Day%d.json", gameState.gameDifficulty, gameState.currentChapter, gameState.currentDay);
    }

    public void playDay() throws IOException {
        // Take snapshot at the start of the day
        gameState.captureSnapShot();

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
        System.out.println(Consequence(false));

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

        gameState.applyConsequences(consequences);

        System.out.println(" \nYour decision is been recorded");
        System.out.println();
    }

    public String Consequence(boolean isPhase) {
        StringBuilder sb = new StringBuilder();

        if(isPhase){
            sb.append("\n=== ").append(gameState.currentPhase).append(" IMPACT ===\n");
        }else{
        sb.append("\n=== DAY").append(gameState.currentDay).append(" SUMMARY ===\n");
        }

        sb.append(formatStatLine("Food", gameState.oldFood, gameState.food));
        sb.append(formatStatLine("Gold", gameState.oldGold, gameState.gold));
        sb.append(formatStatLine("Treasury", gameState.oldTreasury, gameState.treasury));
        sb.append(formatStatLine("Happiness", gameState.oldHappiness, gameState.happiness));
        sb.append(formatStatLine("Population", gameState.oldPopulation, gameState.population));
        sb.append(formatStatLine("Army", gameState.oldArmy, gameState.army));
        sb.append(formatStatLine("Wood", gameState.oldWood, gameState.wood));
        sb.append(formatStatLine("Stone", gameState.oldStone, gameState.stone));
        sb.append(formatStatLine("Iron", gameState.oldIron, gameState.iron));
        sb.append(formatStatLine("Weapons", gameState.oldWeapons, gameState.weapons));
        sb.append(formatStatLine("Stability", gameState.oldStability, gameState.stability));
        sb.append(formatStatLine("Faith", gameState.oldFaith, gameState.faith));
        sb.append(formatStatLine("Crime Rate", gameState.oldCrimeRate, gameState.crimeRate));
        sb.append(formatStatLine("Unemployment", gameState.oldUnemployment, gameState.Unemployment));
        sb.append(formatStatLine("Public Approval", gameState.oldApproval, gameState.publicApproval));
        sb.append(formatStatLine("Kingdom Reputation", gameState.oldReputation, gameState.kingdomReputation));
        sb.append(formatStatLine("Kingdom Strength", gameState.oldStrength, gameState.kingdomStrength));
        sb.append(formatStatLine("Military Morale", gameState.oldMorale, gameState.militaryMorale));

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
}