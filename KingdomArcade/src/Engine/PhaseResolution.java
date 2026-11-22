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
                break;
            case CONSTRUCTION:
                break;
            case TRADE:
                break;
            case DEVELOPMENT:
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

       int constantValue = 100 + (int)(Math.random() * 151);  // 100 + 252
        int absoluteImpact = Math.abs(impact);
        int rawAffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentAffect = (int)(rawAffect * 0.2245);  // 22.45%

        int sign = isVictory ? 1 : -1;

        int rawCasualties = 0;
        int percentageCasualties = 0;
        boolean hasCasualties = false;

        if(isNatural){
            if(Math.random() < 0.35) hasCasualties = true;
        }else{
            hasCasualties = true;
        }

        if(hasCasualties){
            rawCasualties = 50 + (int)(Math.random() * 101); // 50 to 150
            percentageCasualties = (int)(rawCasualties * 0.2245);
        }

        /*
        Win = you keep the resources even if people died
        Loss = resources are lost and casualties are added as penalty
        */
        int finalResourceAffect = (sign * rawAffect) - (isVictory ? 0 : rawAffect);

        state.gold = Math.max(0, state.gold + finalResourceAffect);
        state.iron = Math.max(0, state.iron + finalResourceAffect);
        state.stone = Math.max(0, state.stone + finalResourceAffect);

        int treasuryAffect = (int)(finalResourceAffect / 3.62);
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