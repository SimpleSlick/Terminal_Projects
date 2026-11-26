package Engine;

import Game.GameState;
import JSON.JSONFileReader;
import ENUMS.*;
import java.util.Map;
import java.util.List;
import java.io.IOException;

public class Report {
    private StringBuilder sb = new StringBuilder();
    private JSONFileReader reader = new JSONFileReader();
    private GameState gameState;

    public Report(GameState state){
        this.gameState = state;
    }

    private int randomChoice(List<Object> list){
        boolean isNull = list == null || list.isEmpty();

        if(isNull){
            return -1;
        }else{
            int randomIndex = (int)(Math.random() * list.size());
            return randomIndex;
        }
    }

    public void addReasons_forExploration(env env, envType type, eventType event) throws IOException{
        Map<String, Object> baseData = reader.parse(reader.readFile("Resources/ReportReason/ExplorationReasons.json"));

        Map<String, Object> environmentData = (Map<String, Object>) baseData.get(env.toString());
        if(environmentData == null){
            addManual("The expedition team explored the area.");
            return;
        }

        Map<String, Object> envTypeData = (Map<String, Object>) environmentData.get(type.toString());
        if(envTypeData == null){
            addManual("The expedition continued its search");
            return;
        }

        List<Object> reasons = (List<Object>) envTypeData.get(event.toString());

        int index = randomChoice(reasons);
        if(index == -1){
            addManual("The expedition returned with mixed results");
            return;
        }

        String reason = reasons.get(index).toString();

        sb.append(reason).append("\n");
    }

    public void addReasons_forWar(warOutcome outcome, warReasonType reasonType) throws IOException{
        Map<String, Object> baseData = reader.parse(reader.readFile("Resources/ReportReason/WarReasons.json"));

        Map<String, Object> reasonOutcome = (Map<String, Object>) baseData.get(outcome.toString());

        List<Object> reasons = (List<Object>) reasonOutcome.get(reasonType.toString());

        int index = randomChoice(reasons);
        if(index == -1){
            return;
        }

        String reason = reasons.get(index).toString();
        sb.append(reason).append("\n");
    }

    public void addManual(String message){
        sb.append(message);
    }

    public void addManual(prefix preMsg,String message){
        String prefix = getPrefix(preMsg);
        sb.append(prefix + message);
    }

    public String getReport(){
        return sb.toString();
    }

    private String getPrefix(prefix msg){
        switch (msg){
            case ARMY:
                return "[ARMY]";
            case LOOT:
                return "[LOOT]";
            case COMBAT:
                return "[COMBAT]";
            case DEFEAT:
                return "[DEFEAT]";
            case VICTORY:
                return "[VICTORY]";
            case TREASURY:
                return "[TREASURY]";
            case CASUALTIES:
                return "[CASUALTIES]";
            case POPULATION:
                return "[POPULATION]";
            case KINGDOM_AFFECT:
                return "[KINGDOM AFFECT]";
        }
        return "";
    }
}
