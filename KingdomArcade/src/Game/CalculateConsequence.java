package Game;

import java.util.EnumSet;

import ENUMS.ConsequenceType;

public class CalculateConsequence {
    private final GameState gameState;

    public CalculateConsequence(GameState gameState) {
        this.gameState = gameState;
    }

    public void calculateImpacts(EnumSet<ConsequenceType> modifiedVariables) {
        // We use a loop to apply impacts. 
        // Note: We process the primary variables (Happiness, Army, Food, etc.)
        for (ConsequenceType variable : modifiedVariables) {
            switch (variable) {
                case HAPPINESS:
                    rule1_SocialImpact();
                    break;
                case ARMY:
                case WEAPONS:
                    rule2_MilitaryImpact();
                    break;
                case POPULATION:
                    rule3_PopulationImpact();
                    break;
                case GOLD:
                case FOOD:
                    rule5_EconomicImpact();
                    break;
                case WOOD:
                case STONE:
                case IRON:
                    rule6_ResourceImpact();
                    break;
            }
        }

        // Always ensure stats don't drop below 0 or exceed 100
        clampAll();
    }

    // Rule1: Happiness affects Faith, Stability, Public Approval, Reputation
    private void rule1_SocialImpact() {
        int diff = gameState.happiness - gameState.oldHappiness;

        if (diff > 0) {
            gameState.faith += Math.max(1, diff / 2);
            gameState.stability += Math.max(1, diff / 2);
            gameState.publicApproval += diff;
            gameState.kingdomReputation += Math.max(1, diff / 2);
        } else if (diff < 0) {
            int penalty = Math.abs(diff);
            gameState.faith -= Math.max(1, penalty / 2);
            gameState.stability -= penalty;
            gameState.publicApproval += Math.max(1, penalty);
            gameState.kingdomReputation -= Math.max(1, penalty / 2);
        }
    }

    // Rule2: Army/Weapons change affects Military Morale, Kingdom Strength, Faith, Stability, Reputation
    private void rule2_MilitaryImpact(){
        int armyDiff = gameState.army - gameState.oldArmy;
        int weaponsDiff = gameState.weapons - gameState.oldWeapons;

        if (armyDiff > 0 || weaponsDiff > 0) {
            // Building Military
            gameState.militaryMorale += 2;
            gameState.kingdomStrength += (armyDiff / 10) + (weaponsDiff / 5);
            gameState.stability += 1;
            gameState.kingdomReputation += 1;
        } else if (armyDiff < 0 || weaponsDiff < 0) {
            // Losing military
            gameState.militaryMorale -= 3;
            gameState.kingdomStrength -= Math.abs(armyDiff / 10) + Math.abs(weaponsDiff / 5);
            gameState.stability -= 2;
        }
    }

    // Rule3: Population change affect Faith, Stability, Crime, Unemployment, Approval, Reputation
    private void rule3_PopulationImpact(){
        int populationDiff = gameState.population - gameState.oldPopulation;

        if(populationDiff > 0){
            // Population boom
            gameState.stability -= 2;   // harder to control large population
            gameState.crimeRate += 2;
            gameState.Unemployment += 2;
            gameState.faith -= 1;
        }else if(populationDiff < 0){
            // Population loss
            gameState.stability += 2; // easier to control
            gameState.Unemployment += Math.max(0, gameState.Unemployment - 1);
            gameState.crimeRate += Math.max(0, gameState.crimeRate - 1);
            gameState.kingdomStrength -= 1;
        }
    }

    // Rule5: Gold/Food changes affect Army, Morale, Stability, Faith, Reputation, Strength
    private void rule5_EconomicImpact(){
        int foodDiff = gameState.food - gameState.oldFood;
        int goldDiff = gameState.gold - gameState.oldGold;

        // if food drops below population, starvation occurs
        if(gameState.food < (gameState.population / 2)){
            gameState.stability -= 5;
            gameState.militaryMorale -= 4;
            gameState.kingdomReputation -= 3;
        }

        if(foodDiff > 0){
            gameState.stability += 1;
            gameState.militaryMorale += 2;
        }else if(foodDiff < 0 && gameState.food >= (gameState.population / 2)){
            gameState.stability -= 1;
        }

        if(goldDiff < 0){
            gameState.militaryMorale -= 1;
            gameState.kingdomReputation -= 1;
        }else if(goldDiff > 0){
            gameState.kingdomReputation += 1;
        }
    }

    // Rule6: Resources (Wood, Stone, Iron) affect Stability, Reputation, Population (Calculated via unemployment)
    private void rule6_ResourceImpact() {
        int woodDiff = gameState.wood - gameState.oldWood;
        int stoneDiff = gameState.stone - gameState.oldStone;
        int ironDiff = gameState.iron - gameState.oldIron;

        int totalResourceDiff = woodDiff + stoneDiff + ironDiff;

        if (totalResourceDiff > 0) {
            // Gained resources -> Industry thrives -> Jobs created
            gameState.stability += 1;
            gameState.Unemployment = Math.max(0, gameState.Unemployment - 2);
            gameState.kingdomReputation += 1;
        } else if (totalResourceDiff < 0) {
            // Resources spent -> Less materials for the people
            gameState.stability -= 1;
            gameState.Unemployment += 1;
        }
    }

    private void clampAll() {
        gameState.faith = Math.clamp(gameState.faith, 0, 100);
        gameState.stability = Math.clamp(gameState.stability, 0, 100);
        gameState.publicApproval = Math.clamp(gameState.publicApproval, 0, 100);
        gameState.kingdomReputation = Math.clamp(gameState.kingdomReputation, 0, 100);
        gameState.kingdomStrength = Math.clamp(gameState.kingdomStrength, 0, 100);
        gameState.militaryMorale = Math.clamp(gameState.militaryMorale, 0, 100);
        gameState.crimeRate = Math.clamp(gameState.crimeRate, 0, 100);
        gameState.Unemployment = Math.clamp(gameState.Unemployment, 0, 100);
        gameState.happiness = Math.clamp(gameState.happiness, 0, 100);

        // Prevent negative resources
        gameState.wood = Math.max(0, gameState.wood);
        gameState.stone = Math.max(0, gameState.stone);
        gameState.iron = Math.max(0, gameState.iron);
        gameState.weapons = Math.max(0, gameState.weapons);
        gameState.army = Math.max(0, gameState.army);
        gameState.food = Math.max(0, gameState.food);
        gameState.gold = Math.max(0, gameState.gold);
        gameState.population = Math.max(0, gameState.population);
    }
}