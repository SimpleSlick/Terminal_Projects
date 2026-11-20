package Engine;

import Game.GameState;
import Court.EventManager;

public class ActivityConsequence {
    private CalculateConsequence consequence;
    private EventManager manager;
    private Engine engine = new Engine();

    public ActivityConsequence(GameState state) {
        this.consequence = new CalculateConsequence(state);
        this.manager = new EventManager(state);
    }

    // call everytime before starting the new day
    public void applyDailyPhase(GameState gameState) {
        if (gameState.currentPhase == null) return;

        switch (gameState.currentPhase) {
            case WAR:
                DailyWarRequirements(gameState);
                break;
            case CONSTRUCTION:
                handleDailyConstructionCost(gameState);
                break;
            case MINING:
                handleDailyMiningCost(gameState);
                break;
            case EXPLORATION:
                handleExplorationDaily(gameState);
                break;
        }
    }

    private void DailyWarRequirements(GameState state) {
        // Daily cost of war
        int costFood = 150;
        int costGold = 95;
        int costWeapons = 15;
        int minimumArmyCost = 50;

        // handle food
        if (state.food >= costFood) {
            state.food -= costFood;
        } else {
            state.food = 0; // sent all food and left is 0
            state.militaryMorale -= 15;
        }

        // handle weapons
        if (state.weapons >= costWeapons) {
            state.weapons -= costWeapons;
        } else {
            int deficient = costWeapons - state.weapons;

            int requiredIron = deficient * 3, requiredWood = deficient, requiredStone = deficient * 2;

            if (state.iron >= requiredIron && state.stone >= requiredStone && state.wood >= requiredWood) {
                // successfully forged the weapons and then sent to the army, but kingdom has 0
                state.wood -= requiredWood;
                state.iron -= requiredIron;
                state.stone -= requiredStone;
                state.weapons = 0;
            } else {
                state.weapons = 0;
                state.militaryMorale -= 20;
                state.kingdomStrength -= 5;
            }
        }

        // handle gold
        if (state.gold >= costGold) {
            state.gold -= costGold;
        } else {
            int deficient = costGold - state.gold;

            int treasuryNeed = (int) Math.ceil((double) deficient / 110);

            if (state.treasury >= treasuryNeed) {
                state.treasury -= treasuryNeed;
                state.gold += (treasuryNeed * 110);
                state.gold -= costGold;

                state.stability -= 2;
                state.kingdomReputation -= 1;
            } else {
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
        if (state.army < minimumArmyCost) {
            int neededArmy = minimumArmyCost - state.army;

            if (state.population >= neededArmy) {
                state.population -= neededArmy;
                state.army += neededArmy;

                // forcing citizens to fight affect approval and happiness
                state.publicApproval -= 5;
                state.happiness -= 3;
                state.militaryMorale -= 7;   // untrained army turns down overall morale
            } else {
                // force whatever is left to fight
                state.army = state.population;
                state.population = 0;
                state.militaryMorale = 0;
                state.stability = 0;
            }
        }

        // psychological impact
        if (state.kingdomReputation >= 65 && state.kingdomStrength >= 65) {

        } else {
            int approvalDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? 2 : 1;

            int populationDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? (state.population / 100) : (state.population / 200);

            state.publicApproval -= approvalDrop;
            state.population -= populationDrop;
            state.happiness -= 3;
        }

        consequence.clampAll();
        engine.animateCentered(manager.Consequence(true), manager.WIDTH, 20);
    }

    private void handleDailyConstructionCost(GameState state) {
        // daily Cost of construction
        int costWood = 80, costStone = 100, costIron = 20, costFood = 50, costGold = 100;

        boolean lackResources = false;

        // handle basic resources
        if (state.wood >= costWood) {
            state.wood -= costWood;
        } else {
            state.wood = 0;
            lackResources = true;
        }
        if (state.stone >= costStone) {
            state.stone -= costStone;
        } else {
            state.stone = 0;
            lackResources = true;
        }
        if (state.iron >= costIron) {
            state.iron -= costIron;
        } else {
            state.iron = 0;
            lackResources = true;
        }
        if (state.food >= costFood) {
            state.food -= costFood;
        } else {
            state.food = 0;
            lackResources = true;
        }

        if (state.gold >= costGold) {
            state.gold -= costGold;
        } else {
            int deficient = costGold - state.gold;
            int treasuryNeeded = (int) Math.ceil((double) deficient / 100);

            if (state.treasury >= treasuryNeeded) {
                state.treasury -= treasuryNeeded;
                state.gold += (treasuryNeeded * 110);
                state.gold -= costGold;

                // due to resource conversion. the reputation is slightly affected
                state.kingdomReputation -= 1;
            } else {
                state.gold = 0;
                state.treasury = 0;
                lackResources = true;
            }
        }

        // apply the results
        if (!lackResources) {
            // Society improves because people are employed
            state.Unemployment = Math.max(0, state.Unemployment - 4);
            state.crimeRate = Math.max(0, state.crimeRate - 2);

            if (state.kingdomReputation >= 60 && state.kingdomStrength >= 65) {
                // citizen are proud of the construction initiative
                state.happiness += 3;
                state.faith += 2;
                state.stability += 2;
                state.publicApproval += 2;
            } else {
                // questions the citizen, why initiating build when the kingdom is not strong
                state.happiness -= 2;
                state.faith -= 1;
                state.stability -= 1;
                state.publicApproval -= 1;
            }
        } else {
            // if there is a lack of resources, construction halts -> worker laid of immediately
            state.Unemployment += 7;
            state.happiness -= 5;
            state.stability -= 4;
            state.publicApproval -= 3;

            // abandon sites look terrible -> affects reputation
            state.kingdomReputation -= 2;
        }

        consequence.clampAll();
        engine.animateCentered(manager.Consequence(true), manager.WIDTH, 20);
    }

    private void handleDailyMiningCost(GameState state) {
        // daily Cost of mining
        int costWood = 150, costStone = 80, costIron = 60, costFood = 110, costGold = 120;

        boolean miningSuccessful = true;

        // handle basic resources
        if (state.wood >= costWood) {
            state.wood -= costWood;
        } else {
            state.wood = 0;
            miningSuccessful = false;
        }
        if (state.stone >= costStone) {
            state.stone -= costStone;
        } else {
            state.stone = 0;
            miningSuccessful = false;
        }
        if (state.iron >= costIron) {
            state.iron -= costIron;
        } else {
            state.iron = 0;
            miningSuccessful = false;
        }
        if (state.food >= costFood) {
            state.food -= costFood;
        } else {
            state.food = 0;
            miningSuccessful = false;
        }

        if (state.gold >= costGold) {
            state.gold -= costGold;
        } else {
            int deficient = costGold - state.gold;
            int treasuryNeeded = (int) Math.ceil((double) deficient / 100);

            if (state.treasury >= treasuryNeeded) {
                state.treasury -= treasuryNeeded;
                state.gold += (treasuryNeeded * 110);
                state.gold -= costGold;

                // due to resource conversion. the reputation is slightly affected
                state.kingdomReputation -= 1;
            } else {
                state.gold = 0;
                state.treasury = 0;
                miningSuccessful = false;
            }
        }

        // apply the results
        if (miningSuccessful) {
            // Society improves because people are employed
            state.Unemployment = Math.max(0, state.Unemployment - 4);
            state.crimeRate = Math.max(0, state.crimeRate - 2);

            if (state.kingdomReputation >= 60 && state.kingdomStrength >= 65) {
                // citizen are proud of the mining initiative
                state.happiness += 2;
                state.faith += 2;
                state.stability += 1;
                state.publicApproval += 1;
            } else {
                // questions the citizen, why initiating build when the kingdom is not strong
                state.happiness -= 1;
                state.faith -= 1;
                state.stability -= 2;
                state.publicApproval -= 1;

                if (Math.random() < 0.2) {
                    int casualties = 2 + (int) (Math.random() * 5);

                    state.population -= casualties;
                    state.militaryMorale -= 2;   // word spread of poor leadership
                }
            }
        } else {
            // penalties of failed mining
            state.Unemployment += 9;
            state.happiness -= 5;
            state.stability -= 4;
            state.publicApproval -= 3;

            // kingdom looks incompetent
            state.kingdomReputation -= 3;
        }

        consequence.clampAll();
        engine.animateCentered(manager.Consequence(true), manager.WIDTH, 20);
    }

    private void handleExplorationDaily(GameState state) {
        // daily cost of exploration
        int costFood = 80, costWood = 30, costStone = 20, costIron = 15, costWeapons = 10;

        boolean explorationFailed = false;

        if (state.wood >= costWood) {
            state.wood -= costWood;
        } else {
            state.wood = 0;
            explorationFailed = true;
        }
        if (state.stone >= costStone) {
            state.stone -= costStone;
        } else {
            state.stone = 0;
            explorationFailed = true;
        }
        if (state.iron >= costIron) {
            state.iron -= costIron;
        } else {
            state.iron = 0;
            explorationFailed = true;
        }
        if (state.food >= costFood) {
            state.food -= costFood;
        } else {
            state.food = 0;
            explorationFailed = true;
        }

        if (state.weapons >= costWeapons) {
            state.weapons -= costWeapons;
        } else {
            int deficient = costWeapons - state.weapons;
            int reqIron = deficient * 3, reqStone = deficient * 2, reqWood = deficient;

            // check if enough materials to forge missing weapons
            if (state.iron >= reqIron && state.stone >= reqStone && state.wood >= reqWood) {
                state.iron -= reqIron;
                state.stone -= reqStone;
                state.wood -= reqWood;
                state.weapons = 0;
            } else {
                state.weapons = 0;
                explorationFailed = true;
            }
        }

        if (!explorationFailed) {
            state.Unemployment = Math.max(0, state.Unemployment - 1);
            state.crimeRate = Math.max(0, state.crimeRate - 1);

            // deploy escort
            boolean hasEscort = state.army >= 10;
            if (hasEscort) {
                state.army -= 10;
            }

            // 40% percent chance that an encounter may occur
            if (Math.random() < 0.4) {
                // 50% chance of getting hurt
                if (Math.random() < 0.5) {
                    int explorerCasualties = 1 + (int) (Math.random() * 3);  // 1 to 3 explorer
                    state.population -= explorerCasualties;
                }

                // 60% chance that army might get hurt
                if(Math.random() < 0.6){
                    int armyCasualties = 1 + (int)(Math.random() * 5);   // 1 to 5 solders

                    state.army -= armyCasualties;
                    state.militaryMorale -= 1;  // losing solders decreases the overall morale
                }else if(!hasEscort){
                    state.population -= 1; // penalty for not sending an escort
                }
            }

            // psychological impact
            if(state.kingdomReputation >= 55 && state.kingdomStrength >= 60){
                state.happiness += 2;
                state.faith += 1;
                state.stability += 1;
                state.publicApproval += 1;
            }else{
                state.publicApproval -= 1;
                state.stability -= 1;
                state.faith -= 1;
                state.stability -= 1;
            }
        } else{
            // the exploration halts because lack of food, wepons, resources etc
            state.happiness -= 3;
            state.publicApproval -= 2;
            state.kingdomReputation -= 2;
            state.stability -= 2;

            int lostExplorers = 1 + (int)(Math.random() * 3);
            state.population -= lostExplorers;
        }

        consequence.clampAll();
        engine.animateCentered(manager.Consequence(true), manager.WIDTH, 20);
    }
}
