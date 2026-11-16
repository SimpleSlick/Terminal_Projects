package Game;

import java.util.EnumSet;
import ENUMS.ConsequenceType;

public class CalculateConsequence {
    private final GameState gameState;

    public CalculateConsequence(GameState gameState){
        this.gameState = gameState;
    }

    public void calculateImpacts(EnumSet<ConsequenceType> modifiedVariables){
        for(ConsequenceType variable: modifiedVariables){
            switch (variable){
                case HAPPINESS:
                    calculateSocialImpact();
                    break;
                case ARMY:
                case WEAPONS:
                    calculateMilitaryImpact();
                    break;
                case POPULATION:
                    calculatePopulationImpact();
                    break;
                case GOLD:
                case FOOD:
                    calculateEconomicImpact();
                    break;
                case WOOD:
                case STONE:
                case IRON:
                    calculateResourceImpact();
                    break;
            }
        }
    }

    private void calculateSocialImpact(){
    }

    private void calculateMilitaryImpact(){
    }

    private void calculatePopulationImpact(){
    }

    private void calculateEconomicImpact(){
    }

    private void calculateResourceImpact(){
    }
}
