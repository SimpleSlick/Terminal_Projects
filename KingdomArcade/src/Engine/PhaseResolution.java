package Engine;

import Game.GameState;
import java.util.Map;

public class PhaseResolution {
    private final CalculateConsequence consequence;
    private final Engine engine = new Engine();
    private final int LIMIT_VALUE = 500, WIDTH = 70;

    public PhaseResolution(GameState state){
        this.consequence = new CalculateConsequence(state);
    }

    public void resolvePhase(GameState state, boolean isNaturallyEnded){
        Map<String, Object> phaseCost = state.activePhaseCost;
        double multiplier = 1.0;

        if(phaseCost != null && phaseCost.containsKey("multiplier")){
            multiplier = (double) phaseCost.get("multiplier");
        }

        int impactValue = calculateImpact(multiplier, state.phaseTotalDuration, state.phaseDurationLeft, isNaturallyEnded);

        engine.animateText("\n=== " + state.currentPhase + " RESOLUTION ===", 20, WIDTH);
        engine.animateText("Calculated Impact Force: " + impactValue, 20, WIDTH);

        switch (state.currentPhase){
            case WAR:
                resolveWar(state, impactValue, isNaturallyEnded);
                break;
            case MINING:
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

    private void resolveWar(GameState state, int impact, boolean isNatural){
        engine.animateText("\n=== WAR RESOLUTION ===\n", 25, WIDTH);

        boolean isVictory = false;

        if(isNatural){
            // 50% chance of win or lose, even after natural end
            isVictory = Math.random() < 0.5;
        }else{
            isVictory = false;
        }

        int constantValue = 100 + (int)(Math.random() * 151);  // 100 to 250
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentAffect = (int)(rawEffect * 0.2245); // 22.45%

        int sign = isVictory ? 1 : -1;

        int finalResourceAffect = sign * rawEffect;
        int finalSocialAffect = sign * percentAffect;

        // Resources
        state.gold = Math.max(0, state.gold + finalResourceAffect);
        state.stone = Math.max(0, state.stone + finalResourceAffect);
        state.wood = Math.max(0, state.wood + finalResourceAffect);
        state.iron = Math.max(0, state.iron + finalResourceAffect);
        state.weapons = Math.max(0, state.weapons + finalResourceAffect);
        state.treasury = Math.max(0, state.treasury + (finalResourceAffect / 3));

        // population and army
        state.population = Math.max(0, state.population + (int)(finalResourceAffect / 3.5));
        state.army = Math.max(0, state.army + (isVictory ? (rawEffect / 4) + 4 : -((rawEffect / 4) + 4)));

        // percentage stats
        state.happiness = Math.clamp(state.happiness + finalSocialAffect, 0, 100);
        state.faith = Math.clamp(state.faith + finalSocialAffect, 0, 100);
        state.stability = Math.clamp(state.stability + finalSocialAffect, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + finalSocialAffect, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + finalSocialAffect, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + finalSocialAffect, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + finalSocialAffect, 0, 100);
        state.kingHealth = Math.clamp(state.kingHealth + (finalSocialAffect / 2), 0, 100);

        // in war, victory lowers the crime Rate and unemployment, defaet raises them
        state.crimeRate = Math.clamp(state.crimeRate - finalSocialAffect, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment - finalSocialAffect, 0, 100);

        consequence.clampAll();
    }

    private int calculateImpact(double multiplier, int totalDuration, int leftDuration, boolean Natural){
        double safeMultiplier = Math.max(0.1, multiplier);
        int safeTotal = Math.max(1, totalDuration);
        int safeLeft = Math.max(1, leftDuration);

        if(Natural){
            // formula : limitValue / (multiplier * totalDuration)
            double base = (double) LIMIT_VALUE / (safeTotal * safeMultiplier);

            double finalImpact = base * safeMultiplier * safeTotal;
            return (int) Math.min(finalImpact, LIMIT_VALUE);
        }else{
            double temp1 = (double) LIMIT_VALUE / (safeMultiplier * safeTotal);
            double temp2 = (double) LIMIT_VALUE / (safeMultiplier * safeLeft);

            double base = temp1 - temp2;

            /*
            If the event is finished by interruption than no need for showing positive effect
            just keep it negative
             */
            if(base > 0){
                base = -Math.abs(base);
            }

            double finalImpact = base * multiplier * totalDuration;

            return (int) Math.max(finalImpact, -LIMIT_VALUE);
        }
    }
}