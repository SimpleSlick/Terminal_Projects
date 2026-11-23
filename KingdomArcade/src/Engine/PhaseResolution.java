package Engine;

import Game.GameState;

import java.util.Map;

public class PhaseResolution {
    private final CalculateConsequence consequence;
    private final Engine engine = new Engine();
    private final int LIMIT_VALUE = 500, WIDTH = 70;

    public PhaseResolution(GameState state) {
        this.consequence = new CalculateConsequence(state);
    }

    public void resolvePhase(GameState state, boolean isNaturallyEnded) {
        Map<String, Object> phaseCost = state.activePhaseCost;
        double multiplier = 1.0;

        if (phaseCost != null && phaseCost.containsKey("multiplier")) {
            multiplier = (double) phaseCost.get("multiplier");
        }

        int impactValue = calculateImpact(multiplier, state.phaseTotalDuration, state.phaseDurationLeft, isNaturallyEnded);

        engine.animateText("\n=== " + state.currentPhase + " RESOLUTION ===", 20, WIDTH);
        engine.animateText("Calculated Impact Force: " + impactValue, 20, WIDTH);

        switch (state.currentPhase) {
            case WAR:
                resolveWar(state, impactValue, isNaturallyEnded);
                break;
            case MINING:
                resolveMining(state, impactValue, isNaturallyEnded);
                break;
            case EXPLORATION:
                resolveExploration(state, impactValue, isNaturallyEnded);
                break;
            case CONSTRUCTION:
                resolveConstruction(state, impactValue, isNaturallyEnded);
                break;
            case TRADE:
                break;
            case DEVELOPMENT:
                resolveDevelopment(state, impactValue, isNaturallyEnded);
                break;
        }

        consequence.clampAll();
    }

    private void resolveWar(GameState state, int impact, boolean isNatural) {
        engine.animateText("\n=== WAR RESOLUTION ===\n", 25, WIDTH);

        // not sure about the victory
        boolean isVictory = isNatural && Math.random() < 0.5;

        int constantValue = 100 + (int) (Math.random() * 151);  // 100 to 250
        int absoluteImpact = Math.abs(impact);
        int rawAffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentageAffect = (int) (rawAffect * 0.2245); // 22.45%

        int sign = isVictory ? 1 : -1;
        int finalResourceImpact = sign * rawAffect;
        int finalSocialImpact = sign * percentageAffect;

        int rawCasualties = 0;
        int percentageCasualties = 0;

        if (Math.random() < 0.651) {
            rawCasualties = 30 + (int) (Math.random() * 101); // 30 to 100
            percentageCasualties = (int) (rawCasualties * 0.2245); // 22.45%
        }

        // handle resources
        state.gold = Math.max(0, state.gold + finalResourceImpact);
        state.stone = Math.max(0, state.stone + finalResourceImpact);
        state.wood = Math.max(0, state.wood + finalResourceImpact);
        state.iron = Math.max(0, state.iron + finalResourceImpact);
        state.wood = Math.max(0, state.wood + finalResourceImpact);
        state.treasury = Math.max(0, state.treasury + (finalResourceImpact / 3));

        // handle population and army
        int popChange = (isVictory ? (int) (finalResourceImpact / 3.5) : 0) - rawCasualties;
        state.population = Math.max(0, state.population - popChange);

        // army loss are double if it's a defeat
        int armyLoss = isVictory ? rawCasualties : rawCasualties * 2;
        state.army = Math.max(0, state.army - armyLoss);

        int totalSocialChange = finalSocialImpact - Math.abs(percentageCasualties);

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingHealth = Math.clamp(state.kingHealth + (totalSocialChange / 2), 0, 100);

        // crime rate and unemployment increases when there are casualties
        state.crimeRate = Math.clamp(state.crimeRate - finalSocialImpact + Math.abs(percentageCasualties), 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment - finalSocialImpact + Math.abs(percentageCasualties), 0, 100);

        consequence.clampAll();
    }

    private void resolveMining(GameState state, int impact, boolean isNatural) {
        engine.animateText("\n=== MINING RESOLUTION ===\n", 25, WIDTH);

        boolean isVictory = isNatural && (Math.random() < 0.5);

        int constantValue = 100 + (int) (Math.random() * 151);  // 100 + 252
        int absoluteImpact = Math.abs(impact);
        int rawAffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentAffect = (int) (rawAffect * 0.2245);  // 22.45%

        int sign = isVictory ? 1 : -1;

        int rawCasualties = 0;
        int percentageCasualties = 0;
        boolean hasCasualties = false;

        if (isNatural) {
            if (Math.random() < 0.35) hasCasualties = true;
        } else {
            hasCasualties = true;
        }

        if (hasCasualties) {
            rawCasualties = 50 + (int) (Math.random() * 101); // 50 to 150
            percentageCasualties = (int) (rawCasualties * 0.2245);
        }

        /*
        Win = you keep the resources even if people died
        Loss = resources are lost and casualties are added as penalty
        */
        int finalResourceAffect = (sign * rawAffect) - (isVictory ? 0 : rawAffect);

        state.gold = Math.max(0, state.gold + finalResourceAffect);
        state.iron = Math.max(0, state.iron + finalResourceAffect);
        state.stone = Math.max(0, state.stone + finalResourceAffect);

        int treasuryAffect = (int) (finalResourceAffect / 3.62);
        state.treasury = Math.max(0, state.treasury + treasuryAffect);

        // population drops from casualties, but a win can attract new settlers
        int popChange = (isVictory ? (rawAffect / 4) : 0) - rawCasualties;
        state.population = Math.max(0, state.population + popChange);

        // army loses the by hearing the word, if lose than some might leave, if win then some might join
        int armyChange = isVictory ? 2 : -2;
        state.army = Math.max(0, state.army + armyChange);

        int totalSocialChange = (sign * percentAffect) - Math.abs(percentageCasualties);

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        // crime rate and unemployment go up with casualties
        state.crimeRate = Math.clamp(state.crimeRate - (sign * percentAffect) + Math.abs(percentageCasualties), 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment - (sign * percentAffect) + Math.abs(percentageCasualties), 0, 100);

        consequence.clampAll();
    }

    private void resolveExploration(GameState state, int impact, boolean isNatural) {
        System.out.println("\n=== EXPLORATION RESOLUTION ===\n");

        // state 0: neither fail nor pass
        if (Math.random() < 0.5) {
            int disappointment = (int) (Math.random() * 2) + 1;  // 1 or 2

            state.faith = Math.clamp(state.faith - disappointment, 0, 100);
            state.stability = Math.clamp(state.stability - disappointment, 0, 100);
            state.happiness = Math.clamp(state.happiness - disappointment, 0, 100);
            state.publicApproval = Math.clamp(state.publicApproval - disappointment, 0, 100);

            consequence.clampAll();
            return;
        }

        boolean isVictory = isNatural && (Math.random() < 0.5);

        int constantValue = 100 + (int) (Math.random() * 151);
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentAffect = (int) (rawEffect * 0.2245);

        if (isVictory) {
            // 75% chance of finding the treasure of the amount 350 to 4500
            boolean foundTreasure = Math.random() < 0.75;
            int treasureAmount = 0;

            if (foundTreasure) {
                treasureAmount = 350 + (int) (Math.random() * 4151);
                state.treasury += treasureAmount;

                // gold 68% chance, only if the treasure is found
                if (Math.random() < 0.68) {
                    int goldAmount = 450 + (int) (Math.random() * 6551);
                    state.gold += goldAmount;
                }
            }

            // if found gold without treasure
            if (Math.random() < 0.41) {
                int goldAmount = 360 + (int) (Math.random() * 3421);
                state.gold += goldAmount;
            }

            // stone 80% chance from 1500 to 9000
            if (Math.random() < 0.80) {
                int stoneAmount = 1500 + (int) (Math.random() * 7501);
                state.stone += stoneAmount;
            }

            // iron 78% chance from 1430 to 8600
            if (Math.random() < 0.78) {
                int ironAmount = 1430 + (int) (Math.random() * 7171);
                state.iron += ironAmount;
            }

            // weapons 61% chance from 200 to 4120
            if (Math.random() < 0.61) {
                int weaponAmount = 200 + (int) (Math.random() * 3921);
                state.weapons += weaponAmount;

                // convert weapons into kingdom strength (weapons * 0.2)
                int bonusStrength = (int) (weaponAmount * 0.2);
                state.kingdomStrength = Math.clamp(state.kingdomStrength + bonusStrength, 0, 100);
            }

            // army 74% from 2 to 560
            if (Math.random() < 0.74) {
                int newSoldiers = 2 + (int) (Math.random() * 559);
                state.army += newSoldiers;

                // convert new soldiers into kingdom strength
                int armyStrength = (int) (newSoldiers * 0.15);
                state.kingdomStrength = Math.clamp(state.kingdomStrength + armyStrength, 0, 100);
            }

            // population or refuges 40% chance to find
            int refugeeAmount = 50 + (int) (Math.random() * 200);
            if (Math.random() < 0.40) {
                state.population += refugeeAmount;
                if (Math.random() < 0.72) {
                    int combatCasualties = 10 + (int) (Math.random() * 40);
                    state.army = Math.max(0, state.army - combatCasualties);

                    /*
                    as the refugees would take shelter due to simple reasons, meaning the reasons might be
                    bandits, climate condition, animal threat and etc this many simple reasons
                    */

                    // victory rewards from the bandits
                    int lootGold = 200 + (int) (Math.random() * 600);
                    int lootIron = 200 + (int) (Math.random() * 300);
                    int lootWeapons = 200 + (int) (Math.random() * 150);

                    state.gold += lootGold;
                    state.iron += lootIron;
                    state.weapons += lootWeapons;

                    // people proud of the heroic act
                    int heroicBonus = (int) (Math.random() * 0.5);
                    state.militaryMorale = Math.clamp(state.militaryMorale + heroicBonus, 0, 100);
                    state.publicApproval = Math.clamp(state.publicApproval + heroicBonus, 0, 100);
                    state.kingdomReputation = Math.clamp(state.kingdomReputation + heroicBonus, 0, 100);
                } else {
                    state.population += refugeeAmount;

                    // small happiness boost
                    state.happiness = Math.clamp(state.happiness + 2, 0, 100);
                }
            }

            // add percentage impacts
            state.faith = Math.clamp(state.faith + percentAffect, 0, 100);
            state.stability = Math.clamp(state.stability + percentAffect, 0, 100);
            state.happiness = Math.clamp(state.happiness + percentAffect, 0, 100);
            state.publicApproval = Math.clamp(state.publicApproval + percentAffect, 0, 100);
            state.kingdomReputation = Math.clamp(state.kingdomReputation + percentAffect, 0, 100);

            // reduce crime and unemployment from successful exploration
            state.crimeRate = Math.max(0, state.crimeRate - percentAffect);
            state.Unemployment = Math.max(0, state.Unemployment - percentAffect);
        } else {
            int penalty = rawEffect;
            int socialPenalty = percentAffect;

            // casualties are guaranteed on early failure, but natural failure can also have them
            int rawCasualties = 0;
            int percentageCasualties = 0;
            if (Math.random() < 0.5) {
                rawCasualties = 50 + (int) (Math.random() * 101);
                percentageCasualties = (int) (rawCasualties * 0.2245);
            }

            // 37% chance some return alive;
            int returningSoldiers = 0;
            int returningExplorers = 0;
            if (Math.random() < 0.37) {
                returningSoldiers = (int) (Math.random() * 10);
                returningExplorers = (int) (Math.random() * 21);

                state.population += returningExplorers;
                state.army += returningSoldiers;
            }

            // penalties to all physical resources
            state.gold = Math.max(0, state.gold - rawCasualties);
            state.food = Math.max(0, state.food - rawCasualties);
            state.treasury = Math.max(0, state.treasury - rawCasualties);
            state.wood = Math.max(0, state.wood - rawCasualties);
            state.stone = Math.max(0, state.stone - rawCasualties);
            state.iron = Math.max(0, state.iron - rawCasualties);
            state.weapons = Math.max(0, state.weapons - rawCasualties);

            // apply casualties
            state.population = Math.max(0, state.population - rawCasualties);
            state.army = Math.max(0, state.army - rawCasualties);

            int totalSocialPenalty = socialPenalty + Math.abs(percentageCasualties);

            state.happiness = Math.clamp(state.happiness - totalSocialPenalty, 0, 100);
            state.faith = Math.clamp(state.faith - totalSocialPenalty, 0, 100);
            state.stability = Math.clamp(state.stability - totalSocialPenalty, 0, 100);
            state.publicApproval = Math.clamp(state.publicApproval - totalSocialPenalty, 0, 100);
            state.militaryMorale = Math.clamp(state.militaryMorale - totalSocialPenalty, 0, 100);
            state.kingdomStrength = Math.clamp(state.kingdomStrength - totalSocialPenalty, 0, 100);
            state.kingdomReputation = Math.clamp(state.kingdomReputation - totalSocialPenalty, 0, 100);

            // crime rate and unemployment escalates from defeat
            state.crimeRate = Math.clamp(state.crimeRate + totalSocialPenalty, 0, 100);
            state.Unemployment = Math.clamp(state.Unemployment + totalSocialPenalty, 0, 100);
        }

        consequence.clampAll();
    }

    private void resolveConstruction(GameState state, int impact, boolean isNatural) {
        System.out.println("\n=== CONSTRUCTION RESOLUTION ===\n");

        // determine victory or defeat
        boolean isVictory = isNatural && (Math.random() < 0.5);

        int constantValue = 100 + (int) (Math.random() * 171); // 100 to 270
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((constantValue * absoluteImpact) / 100, LIMIT_VALUE);

        int percentAffect = (int) (rawEffect * 0.2245); // 22.45%

        // casualties
        int rawCasualties = 0, percentCasualties = 0;
        boolean hasCasualties = false;

        // 43% chacne of accidents if natural
        if (isNatural) {
            if (Math.random() < 0.43) hasCasualties = true;
        } else {
            hasCasualties = true;
        }

        if (hasCasualties) {
            rawCasualties = 25 + (int) (Math.random() * 101);   // 25 to 100 workers die
            state.population = Math.max(0, (state.population - rawCasualties));
            percentCasualties = (int) (rawCasualties * 0.2245);  // 22.45%
        }

        // determine final social impact
        int sign = isVictory ? 1 : -1;
        int totalSocialChange = (sign * percentAffect) - Math.abs(percentCasualties);

        boolean highStat = state.kingdomReputation >= 65 && state.kingdomStrength >= 65;

        if (!highStat || !isVictory) {
            // the word spread, might be heard by army by the chance of 83% and if the reputation and strength are below than 65
            if (Math.random() < 0.83) {
                int armyLeft = 1 + (int) (Math.random() * 21); // 1 to 21 soldiers might leave the country
                int moraleReduce = (int) (armyLeft * 0.2);
                state.army = Math.max(0, state.army - armyLeft);
                state.militaryMorale = Math.clamp(state.militaryMorale - moraleReduce, 0, 100);
            }

            // same effect goes for the population as well
            if(Math.random() < 0.53){
                int popLeft = 1 + (int)(Math.random() * 16); // 1 to 15
                state.population = Math.max(state.population - popLeft, 0);
            }
        }else if(highStat && isVictory){
            // if there's a victory than, might some be added
            if(Math.random() < 0.58){
                int popJoin = 1 + (int)(Math.random() * 13); // 1 to 12
                state.population = Math.max(state.population + popJoin, 0);
            }
        }

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        // crime rate and unemployment: if victory go down, if defeat go up
        state.crimeRate = Math.clamp(state.crimeRate - (sign * percentAffect) + Math.abs(percentCasualties), 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment - (sign * percentAffect) + Math.abs(percentCasualties), 0, 100);

        consequence.clampAll();
    }

    private void resolveDevelopment(GameState state, int impact, boolean isNatural){
        System.out.println("\n=== DEVELOPMENT RESOLUTION ===\n");

        boolean isVictory = isNatural && (Math.random() < 0.5);

        int constantValue = 100 + (int)(Math.random() * 151); // 100 to 250
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((constantValue * absoluteImpact) / 100, LIMIT_VALUE);

        int percentageEffect = (int)(rawEffect * 0.2245);

        int sign = isVictory ? 1 : -1;

        int totalSocialChange = sign * percentageEffect;

        boolean highStat = (state.kingdomReputation >= 65 && state.kingdomStrength >= 65);

        if(!highStat){
            // if kingdom is weak or hated, people might leave

            // 73% percent chance army hear the rumors
            if(Math.random() < 0.73){
                int armyLeft = 1 + (int)(Math.random() * 15);
                int moralReduce = (int)(armyLeft * 0.2);

                state.army = Math.max(0, state.army - armyLeft);
                state.militaryMorale = Math.clamp(state.militaryMorale - moralReduce, 0, 100);
            }

            // 63 % chacne
            if(Math.random() < 0.63){
                int popChange = 1 + (int)(Math.random() * 26); // 1 to 25
                state.population = Math.max(0, state.population + popChange);
            }
        }else if(highStat && isVictory){
            if(Math.random() < 0.68){
                int popJoin = 1 + (int)(Math.random() * 25);
                state.population = Math.max(0, state.population + popJoin);
            }

            if(Math.random() < 0.45){
                int armyJoin = 1 + (int)(Math.random() * 10);
                state.army = Math.max(0, state.army + armyJoin);
            }
        }

        // percentage stats
        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        // crime rate and unemployment: if victory go down, if defeat go up
        state.crimeRate = Math.clamp(state.crimeRate - totalSocialChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment - totalSocialChange, 0, 100);
    }

    private int calculateImpact(double multiplier, int totalDuration, int leftDuration, boolean Natural) {
        double safeMultiplier = Math.max(0.1, multiplier);
        int safeTotal = Math.max(1, totalDuration);
        int safeLeft = Math.max(1, leftDuration);

        if (Natural) {
            // formula : limitValue / (multiplier * totalDuration)
            double base = (double) LIMIT_VALUE / (safeTotal * safeMultiplier);

            double finalImpact = base * safeMultiplier * safeTotal;
            return (int) Math.min(finalImpact, LIMIT_VALUE);
        } else {
            double temp1 = (double) LIMIT_VALUE / (safeMultiplier * safeTotal);
            double temp2 = (double) LIMIT_VALUE / (safeMultiplier * safeLeft);

            double base = temp1 - temp2;

            /*
            If the event is finished by interruption than no need for showing positive effect
            just keep it negative
             */
            if (base > 0) {
                base = -Math.abs(base);
            }

            double finalImpact = base * multiplier * totalDuration;

            return (int) Math.max(finalImpact, -LIMIT_VALUE);
        }
    }
}