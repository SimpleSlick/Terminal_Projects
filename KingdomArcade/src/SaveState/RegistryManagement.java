package SaveState;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import JSON.*;
import Game.GameState;

public class RegistryManagement {
    public enum slotOperation{
        SAVE, LOAD, DELETE
    }

    private final JSONFileReader reader = new JSONFileReader();
    private final JSONFileWriter writer = new JSONFileWriter();
    private final Manager manager = new Manager();
    private final GameState gameState = new GameState();

    public final int MAX_SLOTS = 10;

    private static final String REGISTRY_PATH = "Resources/SaveFiles/SaveRegistry.json";

    private Map<String, Object> readRegistry() throws IOException {
        String content = reader.readFile(REGISTRY_PATH);

        Map<String, Object> saveData = reader.parse(content);

        return saveData;
    }

    public void displaySlots() throws IOException{
        Map<String, Object> gameData = readRegistry();

        for(int i = 1; i <= MAX_SLOTS; i++){
            String slotName = "Slot" + i;

            Map<String, Object> selectedData = (Map<String, Object>) gameData.get(slotName);

            System.out.printf("[%02d]", i);
            if(selectedData == null || !(Boolean) selectedData.get("occupied")){
                System.out.print("Empty \n");
            }else{
                System.out.print("Kingdom: " + selectedData.get("kingdomName") + "\n");
                System.out.print(" ".repeat(9) + "King Name: " + selectedData.get("kingName") + "\n");
                System.out.print(" ".repeat(9) +  "Current Day: " + selectedData.get("currentDay") + " Current Year: " + selectedData.get("currentYear") + "\n");
            }
        }
    }

    public void handleSlot(int slotNumber, slotOperation operation) throws IOException{
        if(slotNumber < 1 || slotNumber > MAX_SLOTS){
            System.out.println("Invalid slot number");
            return;
        }

        switch(operation){
            case LOAD:
                loadSlot(slotNumber);
                break;
            case SAVE:
                saveSlot(slotNumber);
                break;
            case DELETE:
                deleteSlot(slotNumber);
                break;
        }
    }

    private void loadSlot(int slot) throws IOException{
        Map<String, Object> registry = readRegistry();

        String slotName = "Slot" + slot;
        Map<String, Object> selectedData = (Map<String, Object>) registry.get(slotName);

        if(selectedData == null || !(Boolean) selectedData.get("occupied")){
            System.out.println("The slot is empty");
            return;
        }

        String filePath = (String) selectedData.get("filePath");
        manager.loadGame(filePath);
    }

    private void saveSlot(int slot) throws IOException{
        Map<String, Object> registry = readRegistry();
        String slotName = "Slot" + slot;

        String SlotPath = "Resources/SaveFiles/saveFile" + slot + "/gameInfo.json";
        Map<String, Object> selectedData = (Map<String, Object>) registry.get(slotName);

        if(selectedData == null){
            System.out.println("Invalid slot");
            return;
        }

        if(Boolean.TRUE.equals(selectedData.get("occupied"))){
            System.out.println("The slot is occupied");
            return;
        }

        Map<String, Object> innerData = new LinkedHashMap<>();

        innerData.put("occupied", true);
        innerData.put("kingName", gameState.kingName);
        innerData.put("kingdomName", gameState.kingdomName);
        innerData.put("currentDay", gameState.currentDay);
        innerData.put("currentYear", gameState.currentYear);
        innerData.put("difficulty", gameState.gameDifficulty);
        innerData.put("filePath", SlotPath);

        manager.saveGame(SlotPath);
        registry.put(slotName, innerData);
        writeRegistry(registry, REGISTRY_PATH);
    }

    private void deleteSlot(int slot) throws IOException{
        Map<String, Object> registry = readRegistry();
        String slotName = "Slot" + slot;

        Map<String, Object> selectedData = (Map<String, Object>) registry.get(slotName);

        if(selectedData == null || !Boolean.TRUE.equals(selectedData.get("occupied"))){
            System.out.println("The slot is empty");
            return;
        }

        String filePath = (String) selectedData.get("filePath");

        manager.clearData(filePath);

        Map<String, Object> emptyData = new LinkedHashMap<>();
        emptyData.put("occupied", false);

        registry.put(slotName, emptyData);
        writeRegistry(registry, REGISTRY_PATH);
        System.out.println("Saved Slot Deleted Successfully");
    }

    private void writeRegistry(Map<String, Object> registry, String path) throws IOException{
        writer.writeFile(path, registry);
    }
}