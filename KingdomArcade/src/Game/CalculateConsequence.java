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
        // Faith impact
        double targetFaith = 70.0 - (gameState.happiness * 0.3);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.2);
        gameState.faith = Math.clamp(gameState.faith, 0, 100);

        // stability impact
        double targetStability = 30.0 + (gameState.happiness * 0.7);

        gameState.happiness += (int) Math.round((targetStability - gameState.stability) * 0.2);
        gameState.happiness = Math.clamp(gameState.stability, 0, 100);
    }

    private void calculateMilitaryImpact(){
        // Faith impact
        double armyRatio = gameState.population > 0 ? (double) gameState.army / gameState.population : 0.0;
        double targetFaith = 50.0 + (armyRatio * 30.0) + (Math.min(gameState.weapons, 200) * 0.5);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.15);
        gameState.faith = Math.clamp(gameState.faith, 0, 100);

        // Stability Impact
        double targetStability = 50.0 + (armyRatio * 15.0) + (Math.min(gameState.weapons, 200) * 0.05);

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.1);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);
    }

    private void calculatePopulationImpact(){
        // Faith impact
        double targetFaith = 50.0 + (gameState.population > 1000 ? 10.0 : -5.0);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.1);
        gameState.faith = Math.clamp(gameState.faith, 0, 100);

        // Stability Impact
        double targetStability = gameState.population > 1500 ? 45.0 : 60.0;

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.1);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);
    }

    private void calculateEconomicImpact(){
        // Faith impact
        double targetFaith = gameState.treasury > 500 ? 60.0 : 40.0;

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.2);
        gameState.faith = (int) Math.clamp(gameState.faith, 0, 100);

        // Stability impact
        double targetStability = 50.0 + (gameState.treasury > 0 ? 10.0 : -20.0);

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.2);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);
    }

    private void calculateResourceImpact(){
        double foodPerCapita = gameState.population > 0 ? (double) gameState.food / gameState.population : 1.0;
        double rawMaterials = gameState.iron + gameState.stone + gameState.wood;

        // Faith impact
        double targetFaith = 50.0 + (foodPerCapita >= 1.5 ? 15.0 : -15.0);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.2);
        gameState.faith = (int) Math.clamp(gameState.faith, 0, 100);

        // Stability impact
        double targetStability = 40.0 + (foodPerCapita * 15.0) + (Math.min(rawMaterials, 500) * 0.02);

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.2);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);
    }
}