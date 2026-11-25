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

        Map<String, Object> envTypeData = (Map<String, Object>) environmentData.get(type.toString());

        List<Object> reasons = (List<Object>) envTypeData.get(event.toString());

        int index = randomChoice(reasons);
        String reason = reasons.get(index).toString();

        sb.append(reason);
    }

    public void addManual(String message){
        sb.append(message);
    }

    public String getReport(){
        return sb.toString();
    }
}
