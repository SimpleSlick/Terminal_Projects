package Engine;

import java.util.EnumSet;

import ENUMS.ActivityType;
import ENUMS.ConsequenceType;
import Game.GameState;

public class CalculateConsequence {
    private final GameState gameState;

    public CalculateConsequence(GameState gameState) {
        this.gameState = gameState;
    }

    private double baseMultiplier = 1.0;
    private double positiveFactor = 1.0;
    private double negativeFactor = 1.0;

    public void calculateImpacts(EnumSet<ConsequenceType> modifiedVariables) {
        resolveMultiplier();
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

    private void resolveMultiplier(){
        this.baseMultiplier = (gameState.currentPhase != null
                && gameState.currentPhase != ActivityType.NONE
                && gameState.activePhaseCost != null
                && gameState.activePhaseCost.containsKey("multiplier"))
                ? ((Number) gameState.activePhaseCost.get("multiplier")).doubleValue()
                : 1.0;

        double pos = 1.0, neg = 1.0;

        switch(gameState.currentPhase){
            case WAR -> {
                // Positive = less than usual, Negative = more than usual
                pos = 0.5;
                neg = 1.5;
            }
            case TRADE -> {
                // Reverse of WAR
                pos = 1.5;
                neg = 0.5;
            }
            case CONSTRUCTION -> {
                // Half of WAR's effect
                // WAR: 0.5 / 1.5  ->  CONSTRUCTION: 0.75 / 1.25
                pos = 0.75;
                neg = 1.25;
            }
            case MINING, EXPLORATION -> {
                // Moderate - almost normal, slight boost to positives
                pos = 1.15;
                neg = 0.9;
            }
            case DEVELOPMENT -> {
                pos = 1.2;
                neg = 0.85;
            }
            case NONE -> {
                pos = 1.0;
                neg = 1.0;
            }
        }

        this.positiveFactor = baseMultiplier * pos;
        this.negativeFactor = baseMultiplier * neg;
    }

    private int scale(int diff){
        if(diff > 0) return (int) Math.round(diff * positiveFactor);
        if(diff < 0) return (int) Math.round(diff * negativeFactor);
        return 0;
    }

    private int scaleHalf(int diff){
        return scale(diff) / 2;
    }

    // Rule1: Happiness affects Faith, Stability, Public Approval, Reputation
    private void rule1_SocialImpact() {
        int diff = scale(gameState.happiness - gameState.oldHappiness);

        if (diff > 0) {
            gameState.faith += Math.max(1, diff / 2);
            gameState.stability += Math.max(1, diff / 2);
            gameState.publicApproval += diff;
            gameState.kingdomReputation += Math.max(1, diff / 2);
        } else if (diff < 0) {
            int penalty = Math.abs(diff);
            gameState.faith -= Math.max(1, penalty / 2);
            gameState.stability -= penalty;
            gameState.publicApproval -= Math.max(1, penalty);
            gameState.kingdomReputation -= Math.max(1, penalty / 2);
        }
    }

    // Rule2: Army/Weapons change affects Military Morale, Kingdom Strength, Faith, Stability, Reputation
    private void rule2_MilitaryImpact(){
        int armyDiff = scale(gameState.army - gameState.oldArmy);
        int weaponsDiff = scale(gameState.weapons - gameState.oldWeapons);

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
        int populationDiff = scale(gameState.population - gameState.oldPopulation);

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
        int foodDiff = scale(gameState.food - gameState.oldFood);
        int goldDiff = scale(gameState.gold - gameState.oldGold);

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
        int woodDiff = scale(gameState.wood - gameState.oldWood);
        int stoneDiff = scale(gameState.stone - gameState.oldStone);
        int ironDiff = scale(gameState.iron - gameState.oldIron);

        int totalResourceDiff = woodDiff + stoneDiff + ironDiff;

        if (totalResourceDiff > 0) {
            // Gained resources -> Industry thrives -> Jobs created
            gameState.stability += 1;
            gameState.Unemployment = Math.max(0, gameState.Unemployment - 2);
            gameState.kingdomReputation += 1;
        } else if (totalResourceDiff < 0) {
            // Resources spent -> Fewer materials for the people
            gameState.stability -= 1;
            gameState.Unemployment += 1;
        }
    }

    public void clampAll() {
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
        gameState.treasury = Math.max(0, gameState.treasury);
        gameState.population = Math.max(0, gameState.population);
    }
}