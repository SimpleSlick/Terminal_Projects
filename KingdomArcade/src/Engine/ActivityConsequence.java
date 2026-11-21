package Engine;

import java.util.Map;

import ENUMS.ActivityType;
import Game.GameState;
import Court.EventManager;

public class ActivityConsequence {
    private CalculateConsequence consequence;
    private int WIDTH = 70;
    private Engine engine = new Engine();

    public ActivityConsequence(GameState state) {
        this.consequence = new CalculateConsequence(state);
    }

    // call everytime before starting the new day
    public void applyDailyPhase(GameState gameState) {
        if (gameState.currentPhase == null || gameState.currentPhase == ActivityType.NONE || gameState.activePhaseCost == null)
            return;

        engine.animateDivider(WIDTH, 20, true);
        engine.animateCentered("MORNING REPORT: " + gameState.currentPhase + " (" + gameState.phaseDurationLeft + " days left)", WIDTH, 25);
        engine.animateDivider(WIDTH, 20, true);

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
            case DEVELOPMENT:
                handleDailyDevelopmentCost(gameState);
                break;
            case TRADE:
                handleDailyTrade(gameState);
                break;
            case NONE:
            default:
                break;

        }

        // decrease the duration and check if it ended
        gameState.phaseDurationLeft--;
        if (gameState.phaseDurationLeft <= 0) {
            engine.animateCentered("The " + gameState.currentPhase + " phase has concluded naturally.", WIDTH, 25);
            boolean isNatural = true;
            resolveAndEndPhase(gameState, isNatural);
        }

        engine.animateText(gameState.Consequence(true), 30, WIDTH);
    }

    private void DailyWarRequirements(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");
        // Daily cost of war
        int costFood = giveMap.containsKey("food") ? Math.abs((Integer) giveMap.get("food")) : 150;
        int costGold = giveMap.containsKey("gold") ? Math.abs((Integer) giveMap.get("gold")) : 95;
        int costWeapons = giveMap.containsKey("weapons") ? Math.abs((Integer) giveMap.get("weapons")) : 15;
        int minimumArmyCost = 50;

        boolean phaseFailed = false;

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
                phaseFailed = true;
            }
        }

        // psychological impact
        if (!(state.kingdomReputation >= 65 && state.kingdomStrength >= 65)) {
            int approvalDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? 2 : 1;

            int populationDrop = (state.kingdomStrength < 40 || state.kingdomReputation < 40) ? (state.population / 100) : (state.population / 200);

            state.publicApproval -= approvalDrop;
            state.population -= populationDrop;
            state.happiness -= 3;
        }

        if (phaseFailed) {
            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

    private void handleDailyConstructionCost(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");

        // daily Cost of construction
        int costWood = giveMap.containsKey("wood") ? Math.abs((Integer) giveMap.get("wood")) : 80;
        int costStone = giveMap.containsKey("stone") ? Math.abs((Integer) giveMap.get("stone")) : 100;
        int costIron = giveMap.containsKey("iron") ? Math.abs((Integer) giveMap.get("iron")) : 20;
        int costFood = giveMap.containsKey("food") ? Math.abs((Integer) giveMap.get("food")) : 50;
        int costGold = giveMap.containsKey("gold") ? Math.abs((Integer) giveMap.get("gold")) : 100;

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

            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

    private void handleDailyMiningCost(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");
        // daily Cost of mining
        int costWood = giveMap.containsKey("wood") ? Math.abs((Integer) giveMap.get("wood")) : 150;
        int costStone = giveMap.containsKey("stone") ? Math.abs((Integer) giveMap.get("stone")) : 80;
        int costIron = giveMap.containsKey("iron") ? Math.abs((Integer) giveMap.get("iron")) : 60;
        int costFood = giveMap.containsKey("food") ? Math.abs((Integer) giveMap.get("food")) : 110;
        int costGold = giveMap.containsKey("gold") ? Math.abs((Integer) giveMap.get("gold")) : 120;

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

            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

    private void handleExplorationDaily(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");

        // daily cost of exploration
        int costFood = giveMap.containsKey("food") ? Math.abs((Integer) giveMap.get("food")) : 80;
        int costWood = giveMap.containsKey("wood") ? Math.abs((Integer) giveMap.get("wood")) : 30;
        int costStone = giveMap.containsKey("stone") ? Math.abs((Integer) giveMap.get("stone")) : 20;
        int costIron = giveMap.containsKey("iron") ? Math.abs((Integer) giveMap.get("iron")) : 15;
        int costWeapons = giveMap.containsKey("weapons") ? Math.abs((Integer) giveMap.get("weapons")) : 10;

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
                if (Math.random() < 0.6) {
                    int armyCasualties = 1 + (int) (Math.random() * 5);   // 1 to 5 solders

                    state.army -= armyCasualties;
                    state.militaryMorale -= 1;  // losing solders decreases the overall morale
                } else if (!hasEscort) {
                    state.population -= 1; // penalty for not sending an escort
                }
            }

            // psychological impact
            if (state.kingdomReputation >= 55 && state.kingdomStrength >= 60) {
                state.happiness += 2;
                state.faith += 1;
                state.stability += 1;
                state.publicApproval += 1;
            } else {
                state.publicApproval -= 1;
                state.stability -= 1;
                state.faith -= 1;
                state.stability -= 1;
            }
        } else {
            // the exploration halts because lack of food, wepons, resources etc
            state.happiness -= 3;
            state.publicApproval -= 2;
            state.kingdomReputation -= 2;
            state.stability -= 2;

            int lostExplorers = 1 + (int) (Math.random() * 3);
            state.population -= lostExplorers;

            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

    private void handleDailyTrade(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");
        Map<String, Object> recieveMap = (Map<String, Object>) state.activePhaseCost.get("receive");

        boolean tradeFailed = false;

        if (giveMap != null) {
            for (Map.Entry<String, Object> entryData : giveMap.entrySet()) {
                int amount = (Integer) entryData.getValue();
                if (amount < 0 && getStat(state, entryData.getKey()) < Math.abs(amount)) {
                    tradeFailed = true;
                    break;
                }
            }
        }

        if (!tradeFailed) {
            if (giveMap != null) {
                for (Map.Entry<String, Object> entry : giveMap.entrySet()) {
                    applyBasicStat(state, entry.getKey(), (Integer) entry.getValue());
                }
            }

            if (recieveMap != null) {
                for (Map.Entry<String, Object> entry : recieveMap.entrySet()) {
                    applyBasicStat(state, entry.getKey(), (Integer) entry.getValue());
                }
            }
        } else {
            state.kingdomReputation -= 5;
            state.happiness -= 3;

            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

    private void handleDailyDevelopmentCost(GameState state) {
        Map<String, Object> giveMap = (Map<String, Object>) state.activePhaseCost.get("give");

        // daily cost of development
        int costGold = giveMap.containsKey("gold") ? (Integer) giveMap.get("gold") : 150;
        boolean developmentFailed = false;

        if (state.gold >= costGold) {
            state.gold -= costGold;
        } else {
            int deficient = costGold - state.gold;
            int treasuryNeeded = (int) Math.ceil((double) deficient / 110);

            if (state.treasury >= treasuryNeeded) {
                state.treasury -= treasuryNeeded;
                state.gold += (treasuryNeeded * 110);
                state.gold -= costGold;

                // Converting treasury for research/culture hurts reputation slightly
                state.kingdomReputation -= 1;
            } else {
                state.gold = 0;
                state.treasury = 0;
                developmentFailed = true;
            }
        }

        // apply the results
        if (!developmentFailed) {
            // Successful Development: Scholars, teachers, and artists are working
            state.Unemployment = Math.max(0, state.Unemployment - 3);
            state.crimeRate = Math.max(0, state.crimeRate - 2);

            // Check Kingdom Strength and Reputation
            if (state.kingdomStrength >= 50 && state.kingdomReputation >= 50) {
                // The kingdom is strong, the people are proud of the progress
                state.happiness += 3;
                state.faith += 2;
                state.stability += 2;
                state.publicApproval += 2;

                // Development slowly grows the population (better living standards, education)
                int populationGrowth = Math.max(5, state.population / 200); // 0.5% growth
                state.population += populationGrowth;
            } else {
                // The kingdom is weak, people question why the king is funding scholars instead of fixing problems
                state.happiness -= 2;
                state.faith -= 1;
                state.stability -= 1;
                state.publicApproval -= 2;
            }
        } else {
            // Failed Development (ran out of gold/treasury)
            state.Unemployment += 5;
            state.happiness -= 4;
            state.stability -= 3;
            state.publicApproval -= 3;
            state.kingdomReputation -= 2; // Foreign nations see the kingdom as bankrupt

            resolveAndEndPhase(state, false);
        }

        consequence.clampAll();
    }

        private void applyBasicStat (GameState state, String key ,int value){
            if (value == 0) return;
            switch (key) {
                case "food":
                    state.food += value;
                    break;
                case "gold":
                    state.gold += value;
                    break;
                case "wood":
                    state.wood += value;
                    break;
                case "stone":
                    state.stone += value;
                    break;
                case "iron":
                    state.iron += value;
                    break;
                case "weapons":
                    state.weapons += value;
                    break;
                case "army":
                    state.army += value;
                    break;
                case "population":
                    state.population += value;
                    break;
                case "happinessChange":
                    state.happiness += value;
                    break;
            }
        }

        private int getStat (GameState state, String key){
            switch (key) {
                case "food":
                    return state.food;
                case "gold":
                    return state.gold;
                case "wood":
                    return state.wood;
                case "stone":
                    return state.stone;
                case "iron":
                    return state.iron;
                case "weapons":
                    return state.weapons;
                case "army":
                    return state.army;
                case "population":
                    return state.population;
                default:
                    return 0;
            }
        }

        private void resolveAndEndPhase(GameState state, boolean Natural){
            new PhaseResolution(state).resolvePhase(state, Natural);

            state.activePhaseCost = null;
            state.currentPhase = ActivityType.NONE;
            state.phaseTotalDuration = 0;
            state.phaseDurationLeft = 0;
        }
    }