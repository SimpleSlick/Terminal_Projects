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

    public void addManual(String message){
        sb.append(message);
    }

    public String getReport(){
        return sb.toString();
    }
}
