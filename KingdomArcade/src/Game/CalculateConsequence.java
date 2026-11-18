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

        // Public Approval impact
        double targetApproval = (double) gameState.happiness;

        gameState.publicApproval += (int) Math.round((targetApproval - gameState.publicApproval) * 0.25);
        gameState.publicApproval = (int) Math.clamp(gameState.publicApproval, 0, 100);

        // Kingdom Reputation Impact
        double targetReputation = 40.0 + (gameState.happiness * 0.4);

        gameState.kingdomReputation += (int) Math.round((targetReputation - gameState.kingdomReputation) * 0.15);
        gameState.kingdomReputation = (int) Math.clamp(gameState.kingdomReputation, 0, 100);
    }

    private void calculateMilitaryImpact(){
        double armyRatio = gameState.population > 0 ? (double) gameState.army / gameState.population : 0.0;

        // Faith impact
        double targetFaith = 50.0 + (armyRatio * 30.0) + (Math.min(gameState.weapons, 200) * 0.5);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.15);
        gameState.faith = Math.clamp(gameState.faith, 0, 100);

        // Stability Impact
        double targetStability = 50.0 + (armyRatio * 15.0) + (Math.min(gameState.weapons, 200) * 0.05);

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.1);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);

        // Kingdom Strength
        double targetStrength = (gameState.army * 0.5) + (gameState.weapons * 0.3) + (gameState.militaryMorale * 0.2);

        gameState.kingdomStrength += (int) Math.round((targetStrength - gameState.kingdomStrength) * 0.25);
        gameState.kingdomStrength = (int) Math.clamp(gameState.kingdomStrength, 0, 100);

        // Kingdom Reputation Impact
        double targetReputation = 50.0 + (armyRatio * 20.0) + (Math.min(gameState.weapons, 300) * 0.05);

        gameState.kingdomReputation += (int) Math.round((targetReputation - gameState.kingdomReputation) * 0.15);
        gameState.kingdomReputation = (int) Math.clamp(gameState.kingdomReputation, 0, 100);
    }

    private void calculatePopulationImpact(){
        double foodPerCapita = gameState.population > 0 ? (double) gameState.food / gameState.population : 1.0;

        // Faith impact
        double targetFaith = 50.0 + (gameState.population > 1000 ? 10.0 : -5.0);

        gameState.faith += (int) Math.round((targetFaith - gameState.faith) * 0.1);
        gameState.faith = Math.clamp(gameState.faith, 0, 100);

        // Stability Impact
        double targetStability = gameState.population > 1500 ? 45.0 : 60.0;

        gameState.stability += (int) Math.round((targetStability - gameState.stability) * 0.1);
        gameState.stability = (int) Math.clamp(gameState.stability, 0, 100);

        // Public Approval Impact
        double targetApproval = 50.0 + (foodPerCapita * 10.0) - (gameState.crimeRate * 0.5);

        gameState.publicApproval += (int) Math.round((targetApproval - gameState.publicApproval) * 0.15);
        gameState.publicApproval = (int) Math.clamp(gameState.publicApproval, 0, 100);

        // Crime Rate impact
        double targetCrime = 20.0 + (gameState.Unemployment * 0.4) - (gameState.stability * 0.3) + (gameState.population / 200.0);

        gameState.crimeRate += (int) Math.round((targetCrime - gameState.crimeRate) * 0.2);
        gameState.crimeRate = (int) Math.clamp(gameState.crimeRate, 0, 100);

        // Unemployment impact
        double totalResources = gameState.iron + gameState.stone + gameState.wood;
        double resourcesPerCapita = gameState.population > 0 ? totalResources / gameState.population : 1.0;

        double targetUnemployment = Math.max(0.0, 60 - (resourcesPerCapita * 20.0));

        gameState.Unemployment += (int) Math.round((targetUnemployment - gameState.Unemployment) * 0.2);
        gameState.Unemployment = (int) Math.clamp(gameState.Unemployment, 0, 100);

        // Kingdom Reputation Impact
        double targetReputation = 50.0 + (gameState.stability * 0.2) + (gameState.publicApproval * 0.3);

        gameState.kingdomReputation += (int) Math.round((targetReputation - gameState.kingdomReputation) * 0.15);
        gameState.kingdomReputation = (int) Math.clamp(gameState.kingdomReputation, 0, 100);
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

        // Public Approval impact
        double targetApproval = 50.0 - (gameState.Unemployment * 0.4) - (gameState.crimeRate * 0.3) + (gameState.stability * 0.3);

        gameState.publicApproval += (int) Math.round((targetApproval - gameState.publicApproval) * 0.15);
        gameState.publicApproval = (int) Math.clamp(gameState.publicApproval, 0, 100);

        // Military Morale impact
        if(gameState.army > 0){
            double requireFood = gameState.army * 1.5;
            double foodRatio = Math.min(1.0, (double) gameState.food / requireFood);

            double requiredTreasury = gameState.army * 2.0;
            double payRatio = Math.min(1.0, (double) gameState.treasury / requiredTreasury);

            double targetMorale = 20.0 + (foodRatio * 50.0) + (payRatio * 30.0);

            gameState.militaryMorale += (int) Math.round((targetMorale - gameState.militaryMorale) * 0.3);
            gameState.militaryMorale = (int) Math.clamp(gameState.militaryMorale, 0, 100);
        }else{
            gameState.militaryMorale += (int) Math.round((50.0 - gameState.militaryMorale) * 0.2);
            gameState.militaryMorale = (int) Math.clamp(gameState.militaryMorale, 0, 100);
        }

        // Crime Rate impact
        double targetCrime = 30.0 + (gameState.Unemployment * 0.4) - (gameState.stability * 0.3) - (gameState.happiness * 0.2);

        gameState.crimeRate += (int) Math.round((targetCrime - gameState.crimeRate) * 0.2);
        gameState.crimeRate = (int) Math.clamp(gameState.crimeRate, 0, 100);

        // Kingdom Strength impact
        double requiredFood = Math.max(1.0, gameState.army * 1.5);
        double foodLogisticsRatio = Math.min(1.0, (double) gameState.food / requiredFood);

        double targetStrength = gameState.kingdomStrength * (0.7 + (foodLogisticsRatio * 0.3));

        gameState.kingdomStrength += (int) Math.round((targetStrength - gameState.kingdomStrength) * 0.2);
        gameState.kingdomStrength = (int) Math.clamp(gameState.kingdomStrength, 0, 100);

        // Kingdom Reputation impact
        double targetReputation = (gameState.publicApproval * 0.5) + (gameState.kingdomStrength * 0.5);

        gameState.kingdomReputation += (int) Math.round((targetReputation - gameState.kingdomReputation) * 0.15);
        gameState.kingdomReputation = (int) Math.clamp(gameState.kingdomReputation, 0, 100);
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

        // Kingdom Reputation impact
        double targetReputation = 50.0 + (gameState.stability * 0.3) + (Math.min(rawMaterials, 1000) * 0.01);

        gameState.kingdomReputation += (int) Math.round((targetReputation - gameState.kingdomReputation) * 0.15);
        gameState.kingdomReputation = (int) Math.clamp(gameState.kingdomReputation, 0, 100);
    }
}