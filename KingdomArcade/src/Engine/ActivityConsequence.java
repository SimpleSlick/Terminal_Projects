package Engine;

import Game.GameState;
import Court.EventManager;

public class ActivityConsequence {
    private CalculateConsequence consequence;
    private EventManager manager;
    private Engine engine = new Engine();

    public ActivityConsequence(GameState state){
        this.consequence = new CalculateConsequence(state);
        this.manager = new EventManager(state);
    }

    // call everytime before starting the new day
    public void applyDailyPhase(GameState gameState){
        if(gameState.currentPhase == null) return;

        switch (gameState.currentPhase){
            case WAR:
                DailyWarRequirements(gameState);
                break;
        }
    }

    private void DailyWarRequirements(GameState state){
        // Daily cost of war
        int costFood = 150;
        int costGold = 95;
        int costWeapons = 15;
        int minimumArmyCost = 50;

        // handle food
        if(state.food >= costFood){
            state.food -= costFood;
        }else{
            state.food = 0; // sent all food and left is 0
            state.militaryMorale -= 15;
        }

        // handle weapons
        if(state.weapons >= costWeapons){
            state.weapons -= costWeapons;
        }else{
            int deficient = costWeapons - state.weapons;

            int requiredIron = deficient * 3, requiredWood = deficient, requiredStone = deficient * 2;

            if(state.iron >= requiredIron && state.stone >= requiredStone && state.wood >= requiredWood){
                // successfully forged the weapons and then sent to the army, but kingdom has 0
                state.wood -= requiredWood;
                state.iron -= requiredIron;
                state.stone -= requiredStone;
                state.weapons = 0;
            }else{
                state.weapons = 0;
                state.militaryMorale -= 20;
                state.kingdomStrength -= 5;
            }
        }

        // handle gold
        if(state.gold >= costGold){
            state.gold -= costGold;
        }else{
            int deficient = costGold - state.gold;

            int treasuryNeed = (int) Math.ceil((double) deficient / 110);

            if(state.treasury >= treasuryNeed){
                state.treasury -= treasuryNeed;
                state.gold += (treasuryNeed * 110);
                state.gold -= costGold;

                state.stability -= 2;
                state.kingdomReputation -= 1;
            }else{
                state.gold = 0;
                state.treasury = 0;
                state.militaryMorale -= 25;
                state.stability -= 10;
                state.crimeRate += 6;
            }
        }

        // handle army; 2% army dies every day from the situation of war
        int casualties = Math.max(1, state.army / 50);
        state.army -= casualties;

        // army falls below safe minimum, draft citizens
        if(state.army < minimumArmyCost){
            int neededArmy = minimumArmyCost - state.army;

            if(state.population >= neededArmy){
                state.population -= neededArmy;
                state.army += neededArmy;

                // forcing citizens to fight affect approval and happiness
                state.publicApproval -= 5;
                state.happiness -= 3;
                state.militaryMorale -= 7;   // untrained army turns down overall morale
            }else{
                // force whatever is left to fight
                state.army = state.population;
                state.population = 0;
                state.militaryMorale = 0;
                state.stability = 0;
            }
        }

        // psychological impact
        if(state.kingdomReputation >= 65 && state.kingdomStrength >= 65){

        }else{
            int approvalDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? 2 : 1;

            int populationDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? (state.population / 100) : (state.population / 200);

            state.publicApproval -= approvalDrop;
            state.population -= populationDrop;
            state.happiness -= 3;
        }

        consequence.clampAll();
        engine.animateCentered(manager.Consequence(true), manager.WIDTH, 20);
    }


}
