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

        int constantValue = 70 + (int) (Math.random() * 141);  // 70 to 140
        int absoluteImpact = Math.abs(impact);
        int rawAffect = Math.min((constantValue * absoluteImpact) / 100, LIMIT_VALUE);
        int percentageAffect = (int) (rawAffect * 0.2254); // 22.45%

        int sign = isVictory ? 1 : -1;
        int finalResourceAffect = sign * rawAffect;
        int finalSocialAffect = sign * percentageAffect;

        int rawCasualties = 0;
        int percentageCasualties = 0;
        boolean hasCasualties = false;

        if (isVictory) {
            if (Math.random() < 0.35) {
                hasCasualties = true;
            }
        } else {
            if (Math.random() < 0.65) {
                hasCasualties = true;
            }
        }

        if(hasCasualties){
            rawCasualties = 40 + (int)(Math.random() * 111); // 40 to 110
            percentageCasualties = (int)(rawCasualties * 0.2245);
        }

        /*
        If Victory than, casualties eat into the positive impact
        If Lost than, casualties add into the penalty affect
        */
        if(isVictory){
            finalResourceAffect -= rawCasualties;
            finalSocialAffect -= percentageCasualties;
        }else{
            finalResourceAffect -= rawCasualties;
            finalSocialAffect -= percentageCasualties;
        }

        // apply absolute resources
        state.gold = Math.max(0, state.gold + finalResourceAffect);
        state.population = Math.max(0, state.population + (int)(finalResourceAffect / 3.5));

        int treasuryChange = 0;
        if(isVictory && (Math.random() < 0.5)){
            treasuryChange -= (1 + (int)(Math.random() * 5));
        }else if(!isVictory && Math.random() < 0.5){
            treasuryChange -= (1 + (int)(Math.random() * 5));
        }

        state.treasury = Math.max(0, state.treasury + treasuryChange);

        int armyChange = 0;
        if(isVictory){
            armyChange = (int)(rawAffect / 4);

            if(Math.random() < 0.5) armyChange -= (5 + (int)(Math.random() * 10));
            else armyChange += (5 + (int)(Math.random() * 10));
        }else{
            armyChange = (int)(rawAffect / 4);
            if(Math.random() < 0.5) armyChange -= (5 + (int)(Math.random() * 10));
        }

        state.army = Math.max(0, state.army + armyChange);

        // percentage stats
        state.happiness = Math.clamp(state.happiness + finalSocialAffect, 0, 100);
        state.faith = Math.clamp(state.faith + finalSocialAffect, 0, 100);
        state.stability = Math.clamp(state.stability + finalSocialAffect, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + finalSocialAffect, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + finalSocialAffect, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + finalSocialAffect, 0, 100);
        state.crimeRate = Math.clamp(state.crimeRate + finalSocialAffect, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + finalSocialAffect, 0, 100);

        int moraleChange = 0;
        if(Math.random() < 0.5) moraleChange = isVictory ? 1 : -2;
        else moraleChange = isVictory ? 2 : -1;

        state.militaryMorale = Math.clamp(state.militaryMorale + moraleChange, 0, 100);

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