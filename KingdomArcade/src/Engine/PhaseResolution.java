package Engine;

import Game.GameState;
import ENUMS.*;

import java.util.Map;
import java.io.IOException;

public class PhaseResolution {
    private final CalculateConsequence consequence;
    private final Engine engine = new Engine();
    private final int LIMIT_VALUE = 500, WIDTH = 70;
    private final Report report;

    public PhaseResolution(GameState state) {
        this.consequence = new CalculateConsequence(state);
        this.report = new Report(state);
    }

    public void resolvePhase(GameState state, boolean isNaturallyEnded) throws IOException{
        Map<String, Object> phaseCost = state.activePhaseCost;
        Object m = phaseCost.get("multiplier");
        double multiplier = m instanceof Number ? ((Number) m).doubleValue() : 1.0;

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
                resolveTrade(state, impactValue, isNaturallyEnded);
                break;
            case DEVELOPMENT:
                resolveDevelopment(state, impactValue, isNaturallyEnded);
                break;
        }

        consequence.clampAll();
    }

    private void resolveWar(GameState state, int impact, boolean isNatural) throws IOException{
        report.addManual("\n=== WAR REPORT ===");

        // not sure about the victory
        boolean isVictory = isNatural && Math.random() < 0.5;

        warOutcome outcome;
        if(!isNatural){
            outcome = warOutcome.LOSS;
        }else if(isVictory){
            outcome = warOutcome.NATURAL_VICTORY;
        }else{
            outcome = warOutcome.NATURAL_LOSS;
        }

        if(outcome == warOutcome.NATURAL_VICTORY){
            report.addManual(prefix.VICTORY, " The war ended and our kingdom emerged victorious!");
        }else if(outcome == warOutcome.NATURAL_LOSS){
            report.addManual(prefix.DEFEAT, " The war ended but our kingdom was defeated.");
        }else{
            report.addManual(prefix.DEFEAT, " The war collapsed early. It is a total loss");
        }

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

        if(rawCasualties > 0){
            report.addReasons_forWar(outcome, warReasonType.CASUALTIES);
            report.addManual(prefix.ARMY, " Affected Margin: Casualties: -" + rawCasualties + ".");
            report.addManual(prefix.KINGDOM_AFFECT, " Social Casualties -" + percentageCasualties + ".");
        }else{
            report.addManual("Miraculously, no major casualties were reported.");
            report.addManual(prefix.CASUALTIES, "Affected Margin: Casualties 0.");
        }

        // handle resources
        state.gold = Math.max(0, state.gold + finalResourceImpact);
        state.stone = Math.max(0, state.stone + finalResourceImpact);
        state.wood = Math.max(0, state.wood + finalResourceImpact);
        state.iron = Math.max(0, state.iron + finalResourceImpact);
        state.wood = Math.max(0, state.wood + finalResourceImpact);
        state.treasury = Math.max(0, state.treasury + (finalResourceImpact / 3));

        if (isVictory) {
            report.addManual(prefix.LOOT, " War loot and captured supplies flow into the kingdom.");
            report.addManual("Affected Margin: Gold " + (finalResourceImpact >= 0 ? "+" : "") + finalResourceImpact
                    + ", Stone " + (finalResourceImpact >= 0 ? "+" : "") + finalResourceImpact
                    + ", Wood " + (finalResourceImpact >= 0 ? "+" : "") + finalResourceImpact
                    + ", Iron " + (finalResourceImpact >= 0 ? "+" : "") + finalResourceImpact
                    + ", Treasury " + ((finalResourceImpact / 3) >= 0 ? "+" : "") + (finalResourceImpact / 3) + ".\n");
        } else {
            report.addManual(prefix.LOOT, " War drained the kingdom's stores. Supplies were lost or burned.");
            report.addManual("Affected Margin: Gold " + finalResourceImpact
                    + ", Stone " + finalResourceImpact
                    + ", Wood " + finalResourceImpact
                    + ", Iron " + finalResourceImpact
                    + ", Treasury " + (finalResourceImpact / 3) + ".\n");
        }

        // handle population and army
        int popGain = (isVictory ? (int) (finalResourceImpact / 3.5) : 0);
        int popChange = popGain - rawCasualties;
        state.population = Math.max(0, state.population - popChange);

        // army loss are double if it's a defeat
        int armyLoss = isVictory ? rawCasualties : rawCasualties * 2;
        state.army = Math.max(0, state.army - armyLoss);

        report.addReasons_forWar(outcome, warReasonType.POP_CHANGE);
        if(popChange >= 0){
            report.addManual(prefix.POPULATION, " New people have joined the kingdom after the war.");
            report.addManual("Affected Margin: Population +" + popChange + " (Gain +" + popGain + ", Casualties -" + rawCasualties + ").\n");
        } else {
            report.addManual(prefix.POPULATION, " The kingdom lost many of its people in the war.");
            report.addManual("Affected Margin: Population " + popChange + " (Gain +" + popGain + ", Casualties -" + rawCasualties + ").\n");
        }

        report.addReasons_forWar(outcome, warReasonType.ARMY_CHANGE);
        if (isVictory) {
            report.addManual(prefix.ARMY, " Our army returns home, tired but victorious.");
            report.addManual("Affected Margin: Army -" + armyLoss + " soldiers lost.\n");
        } else {
            report.addManual(prefix.ARMY, " Our army was broken and forced to retreat.");
            report.addManual("Affected Margin: Army -" + armyLoss + " soldiers lost (double loss on defeat).\n");
        }

        int totalSocialChange = finalSocialImpact - Math.abs(percentageCasualties);

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingHealth = Math.clamp(state.kingHealth + (totalSocialChange / 2), 0, 100);

        report.addReasons_forWar(outcome, warReasonType.SOCIAL_CHANGE);
        report.addManual(prefix.KINGDOM_AFFECT, isVictory
                ? " The people celebrate the victory."
                : " The defeat weighs heavily on the kingdom.");
        report.addManual("Affected Margin: Happiness " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Faith " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Stability " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Public Approval " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Military Morale " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Kingdom Strength " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Reputation " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", King Health " + ((totalSocialChange / 2) >= 0 ? "+" : "") + (totalSocialChange / 2) + ".\n");

        // crime rate and unemployment increases when there are casualties
        int crimeChange = -finalSocialImpact + Math.abs(percentageCasualties);
        state.crimeRate = Math.clamp(state.crimeRate + crimeChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + crimeChange, 0, 100);

        // --- 6. RATE_UNEMPLOYMENT ---
        report.addReasons_forWar(outcome, warReasonType.RATE_UNEMPLOYMENT);
        if (crimeChange > 0) {
            report.addManual(prefix.KINGDOM_AFFECT, " With soldiers gone and workshops ruined, crime and joblessness rise.");
        } else {
            report.addManual(prefix.KINGDOM_AFFECT, " With peace returning, crime falls and people return to work.");
        }
        report.addManual("Affected Margin: Crime Rate " + (crimeChange >= 0 ? "+" : "") + crimeChange
                + ", Unemployment " + (crimeChange >= 0 ? "+" : "") + crimeChange + ".\n");

        consequence.clampAll();
    }

    private void resolveMining(GameState state, int impact, boolean isNatural) throws IOException {
        report.addManual("\n=== MINING REPORT ===");

        boolean isVictory = isNatural && (Math.random() < 0.5);

        warOutcome outcome;
        if(!isNatural){
            outcome = warOutcome.LOSS;
        }else if(isVictory){
            outcome = warOutcome.NATURAL_VICTORY;
        }else{
            outcome = warOutcome.NATURAL_LOSS;
        }

        if (outcome == warOutcome.NATURAL_VICTORY) {
            report.addManual(prefix.VICTORY, " The mining operation ended and rich ore was secured!");
        } else if (outcome == warOutcome.NATURAL_LOSS) {
            report.addManual(prefix.DEFEAT, " The mining operation ended, but the mine failed.");
        } else {
            report.addManual(prefix.DEFEAT, " The mining operation collapsed early. It is a total loss.");
        }

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

        if (hasCasualties) {
            report.addReasons_forMining(outcome, generalReasonType.CASUALTIES);
            report.addManual(prefix.CASUALTIES, " Miners were lost in the tunnels.");
            report.addManual("Affected Margin: Casualties -" + rawCasualties + ", Social Penalty -" + percentageCasualties + ".\n");
        } else {
            report.addManual("Miraculously, no miners died in this operation.");
            report.addManual(prefix.CASUALTIES, "Affected Margin: Casualties 0.\n");
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

        if (isVictory) {
            report.addManual(prefix.LOOT, " Rich ore was hauled out of the mine.");
            report.addManual("Affected Margin: Gold " + (finalResourceAffect >= 0 ? "+" : "") + finalResourceAffect
                    + ", Iron " + (finalResourceAffect >= 0 ? "+" : "") + finalResourceAffect
                    + ", Stone " + (finalResourceAffect >= 0 ? "+" : "") + finalResourceAffect
                    + ", Treasury " + (treasuryAffect >= 0 ? "+" : "") + treasuryAffect + ".\n");
        } else {
            report.addManual(prefix.LOOT, " The mine yielded nothing. Tools and ore were lost.");
            report.addManual("Affected Margin: Gold " + finalResourceAffect
                    + ", Iron " + finalResourceAffect
                    + ", Stone " + finalResourceAffect
                    + ", Treasury " + treasuryAffect + ".\n");
        }

        // population drops from casualties, but a win can attract new settlers
        int popGain = isVictory ? (rawAffect / 4) : 0;
        int popChange = popGain - rawCasualties;
        state.population = Math.max(0, state.population + popChange);

        if (popChange >= 0) {
            report.addReasons_forMining(outcome, generalReasonType.POP_JOIN);
            report.addManual("Affected Margin: Population +" + popChange + " (Gain +" + popGain + ", Casualties -" + rawCasualties + ").\n");
        } else {
            report.addReasons_forMining(outcome, generalReasonType.POP_LEFT);
            report.addManual("Affected Margin: Population " + popChange + " (Gain +" + popGain + ", Casualties -" + rawCasualties + ").\n");
        }


        // army loses the by hearing the word, if lose than some might leave, if win then some might join
        int armyChange = isVictory ? 2 : -2;
        state.army = Math.max(0, state.army + armyChange);

        if (armyChange >= 0) {
            report.addReasons_forMining(outcome, generalReasonType.ARMY_JOIN);
            report.addManual("Affected Margin: Army +" + armyChange + " soldiers joined.\n");
        } else {
            report.addReasons_forMining(outcome, generalReasonType.ARMY_LEFT);
            report.addManual("Affected Margin: Army " + armyChange + " soldiers left.\n");
        }

        int totalSocialChange = (sign * percentAffect) - Math.abs(percentageCasualties);

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        report.addManual(prefix.KINGDOM_AFFECT, isVictory
                ? " The kingdom celebrates the rich harvest from the mines."
                : " The failed mine weighs heavily on the kingdom.");
        report.addManual("Affected Margin: Happiness " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Faith " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Stability " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Public Approval " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Reputation " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Kingdom Strength " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Military Morale " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange + ".\n");


        // crime rate and unemployment go up with casualties
        int crimeChange = -(sign * percentAffect) + Math.abs(percentageCasualties);
        state.crimeRate = Math.clamp(state.crimeRate + crimeChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + crimeChange, 0, 100);

        if (crimeChange > 0) {
            report.addManual(prefix.KINGDOM_AFFECT, " With miners jobless and the town in chaos, crime and unemployment rise.");
        } else {
            report.addManual(prefix.KINGDOM_AFFECT, " With steady work in the mines, crime falls and people return to jobs.");
        }
        report.addManual("Affected Margin: Crime Rate " + (crimeChange >= 0 ? "+" : "") + crimeChange
                + ", Unemployment " + (crimeChange >= 0 ? "+" : "") + crimeChange + ".\n");

        consequence.clampAll();
    }

    private void resolveExploration(GameState state, int impact, boolean isNatural) throws IOException{
        env environment = env.valueOf((String) state.activePhaseInfo.get("environment"));
        envType environment_type = envType.valueOf((String) state.activePhaseInfo.get("environment_type"));

        report.addManual("\n=== EXPLORATION REPORT ===\n");
        report.addManual("Expedition to " + environment + " (" + environment_type + ") has concluded.\n");

        // state 0: neither fail nor pass
        if (Math.random() < 0.5) {
            int disappointment = (int) (Math.random() * 2) + 1;  // 1 or 2
            report.addReasons_forExploration(environment, environment_type, eventType.valueOf("FOUND_NOTHING"));

            state.faith = Math.clamp(state.faith - disappointment, 0, 100);
            state.stability = Math.clamp(state.stability - disappointment, 0, 100);
            state.happiness = Math.clamp(state.happiness - disappointment, 0, 100);
            state.publicApproval = Math.clamp(state.publicApproval - disappointment, 0, 100);
            report.addManual(prefix.KINGDOM_AFFECT, " Disappointment spreads after fruitless expedition. All morale stats dropped.");
            report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Faith - " + disappointment + ", Stability - " + disappointment + ", Happiness - " + disappointment + ", Public Approval - " + disappointment + ".\n");

            consequence.clampAll();
            return;
        }

        boolean isVictory = isNatural && (Math.random() < 0.5);

        int constantValue = 100 + (int) (Math.random() * 151);
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((absoluteImpact * constantValue) / 100, LIMIT_VALUE);
        int percentAffect = (int) (rawEffect * 0.2245);

        boolean hasAmbush = Math.random() < 0.21;
        int ambushAffect = 0;

        if(hasAmbush){
            report.addReasons_forExploration(environment, environment_type,eventType.valueOf("AMBUSH"));

            ambushAffect = 10 + (int)(Math.random() * 40); // 10 to 49 soldiers lost
            state.army = Math.max(0, state.army - ambushAffect);

            // ambush also reduces the overall impact
            rawEffect = Math.max(0, rawEffect - ambushAffect);

            report.addManual(prefix.COMBAT, " Affected Margin: " + ambushAffect + " soldiers lost");
        }

        if (isVictory) {
            report.addManual(prefix.VICTORY, " The expedition was success!\n");

            // 75% chance of finding the treasure of the amount 350 to 4500
            boolean foundTreasure = Math.random() < 0.75;
            int treasureAmount = 0;

            if (foundTreasure) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("TREASURE"));

                treasureAmount = 350 + (int) (Math.random() * 4151);
                state.treasury += treasureAmount;
                report.addManual(prefix.TREASURY, " Affected Margin: Treasury +" + treasureAmount + ".");

                // gold 68% chance, only if the treasure is found
                if (Math.random() < 0.68) {
                    report.addReasons_forExploration(environment, environment_type, eventType.valueOf("GOLD_WITH_TREASURE"));
                    int goldAmount = 450 + (int) (Math.random() * 6551);
                    state.gold += goldAmount;
                    report.addManual(prefix.LOOT, " Affected Margin: Gold +" + goldAmount + ".");
                }
            }

            // if found gold without treasure
            if (Math.random() < 0.41) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("SUDDEN_GOLD"));
                int goldAmount = 360 + (int) (Math.random() * 3421);
                state.gold += goldAmount;
                report.addManual(prefix.LOOT, " Affected Margin: Gold +" + goldAmount + ".");
            }

            // stone 80% chance from 1500 to 9000
            if (Math.random() < 0.80) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("STONE"));
                int stoneAmount = 1500 + (int) (Math.random() * 7501);
                state.stone += stoneAmount;
                report.addManual(prefix.LOOT, " Affected Margin: Stone +" + stoneAmount + ".");
            }

            // iron 78% chance from 1430 to 8600
            if (Math.random() < 0.78) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("IRON"));
                int ironAmount = 1430 + (int) (Math.random() * 7171);
                state.iron += ironAmount;
                report.addManual(prefix.LOOT, " Affected Margin: Stone +" + ironAmount + ".");
            }

            // weapons 61% chance from 200 to 4120
            if (Math.random() < 0.61) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("WEAPONS"));
                int weaponAmount = 200 + (int) (Math.random() * 3921);
                state.weapons += weaponAmount;
                report.addManual(prefix.LOOT, " Affected Margin: Weapons +" + weaponAmount + ".");

                // convert weapons into kingdom strength (weapons * 0.2)
                int bonusStrength = (int) (weaponAmount * 0.2);
                state.kingdomStrength = Math.clamp(state.kingdomStrength + bonusStrength, 0, 100);
                report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Kingdom Strength +" + bonusStrength + ".");
            }

            // army 74% from 2 to 560
            if (Math.random() < 0.74) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("ARMY"));
                int newSoldiers = 2 + (int) (Math.random() * 559);
                state.army += newSoldiers;
                report.addManual(prefix.ARMY, " Affected Margin: +" + newSoldiers + ".");

                // convert new soldiers into kingdom strength
                int armyStrength = (int) (newSoldiers * 0.15);
                state.kingdomStrength = Math.clamp(state.kingdomStrength + armyStrength, 0, 100);
                report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: +" + armyStrength + ".");
            }

            // population or refuges 40% chance to find
            if (Math.random() < 0.40) {
            int refugeeAmount = 50 + (int) (Math.random() * 200);
                state.population += refugeeAmount;
                if (Math.random() < 0.72) {
                    report.addReasons_forExploration(environment, environment_type, eventType.valueOf("AMBUSH_REQUEST"));
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

                    report.addManual(prefix.POPULATION, " Affected Margin: Population +" + refugeeAmount + ".");
                    report.addManual(prefix.COMBAT, " Affected Margin: Army -" + combatCasualties + ".");
                    report.addManual(prefix.LOOT, " Affected Margin: Iron +" + lootIron + ", Gold +" + lootGold + ", Weapons +" + lootWeapons);
                    report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Military Morale +" + heroicBonus + ", Public Approval +" + heroicBonus + "Kingdom Reputation +" + heroicBonus + ".");

                } else {
                    report.addReasons_forExploration(environment, environment_type, eventType.valueOf("REFUGEES"));

                    // small happiness boost
                    state.happiness = Math.clamp(state.happiness + 2, 0, 100);
                    report.addManual(prefix.POPULATION, " Affected Margin: Population +" + refugeeAmount + ".\n");
                    report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin Happiness +2.");
                }
            }

            // add percentage impacts
            state.faith = Math.clamp(state.faith + percentAffect, 0, 100);
            state.stability = Math.clamp(state.stability + percentAffect, 0, 100);
            state.happiness = Math.clamp(state.happiness + percentAffect, 0, 100);
            state.publicApproval = Math.clamp(state.publicApproval + percentAffect, 0, 100);
            state.kingdomReputation = Math.clamp(state.kingdomReputation + percentAffect, 0, 100);
            report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Faith +" + percentAffect + ", Stability +" + percentAffect + ", Happiness +" + percentAffect + ".");
            report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Public Approval +" + percentAffect + ", Kingdom Reputation +" + percentAffect + ".");

            // reduce crime and unemployment from successful exploration
            state.crimeRate = Math.max(0, state.crimeRate - percentAffect);
            state.Unemployment = Math.max(0, state.Unemployment - percentAffect);
            report.addManual(prefix.ARMY, " Affected Margin: Crime Rate +" + percentAffect + ", Unemployment " + percentAffect + ".");
        } else {
            report.addManual(prefix.DEFEAT, " The expedition failed.");
            report.addReasons_forExploration(environment, environment_type, eventType.valueOf("MISSION_FAIL"));

            int socialPenalty = percentAffect;

            // casualties are guaranteed on early failure, but natural failure can also have them
            int rawCasualties = 0;
            int percentageCasualties = 0;
            if (Math.random() < 0.5) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("CASUALTIES_ON_RETURN"));
                rawCasualties = 50 + (int) (Math.random() * 101);
                percentageCasualties = (int) (rawCasualties * 0.2245);

                report.addManual(prefix.CASUALTIES, " Affected Margin: Casualties -" + rawCasualties + "people.");
            }

            // 37% chance some return alive;
            int returningSoldiers = 0;
            int returningExplorers = 0;
            if (Math.random() < 0.37) {
                report.addReasons_forExploration(environment, environment_type, eventType.valueOf("RETURNING_ONES"));
                returningSoldiers = (int) (Math.random() * 10);
                returningExplorers = (int) (Math.random() * 21);

                state.population += returningExplorers;
                state.army += returningSoldiers;

                report.addManual(prefix.POPULATION, " Affected Margin: Population +" + returningExplorers + ".");
                report.addManual(prefix.ARMY, " Affected Margin: Army +" + returningSoldiers + ".");
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

            report.addManual("The failed expedition hurt the kingdom's morale");
            report.addManual(prefix.KINGDOM_AFFECT, " Affected Margin: Gold -" + rawCasualties + ", Food -" + rawCasualties + ", Wood -" + rawCasualties + ", Treasury -" + rawCasualties + ", Stone -" + rawCasualties + ", Iron -" + rawCasualties + ", Weapons -" + rawCasualties + ".");
            report.addManual(" Population -" + rawCasualties + ", Army -" + rawCasualties + ".");
            report.addManual(" Happiness -" + totalSocialPenalty + ", Faith -" + totalSocialPenalty + ", Stability -" + totalSocialPenalty + ", Public Approval -" + totalSocialPenalty + ", Morale -" + totalSocialPenalty + ", Strength -" + totalSocialPenalty + ", Reputation -" + totalSocialPenalty + ".");
            report.addManual(" Crime Rate +" + totalSocialPenalty + ", Unemployment +" + totalSocialPenalty + ".");
        }

        consequence.clampAll();
    }

    private void resolveConstruction(GameState state, int impact, boolean isNatural) throws IOException {
        report.addManual("\n=== CONSTRUCTION RESOLUTION ===");

        // determine victory or defeat
        boolean isVictory = isNatural && (Math.random() < 0.5);

        warOutcome outcome;
        if (!isNatural) {
            outcome = warOutcome.LOSS;
        } else if (isVictory) {
            outcome = warOutcome.NATURAL_VICTORY;
        } else {
            outcome = warOutcome.NATURAL_LOSS;
        }

        if (outcome == warOutcome.NATURAL_VICTORY) {
            report.addManual(prefix.VICTORY, " The construction finished and the grand building stands tall!");
        } else if (outcome == warOutcome.NATURAL_LOSS) {
            report.addManual(prefix.DEFEAT, " The construction finished, but the project failed.");
        } else {
            report.addManual(prefix.DEFEAT, " The construction collapsed early. It is a total loss.");
        }

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

        if (hasCasualties) {
            report.addReasons_forConstruction(outcome, generalReasonType.CASUALTIES);
            report.addManual(prefix.CASUALTIES, " Workers died in a construction accident.");
            report.addManual("Affected Margin: Casualties -" + rawCasualties + ", Social Penalty -" + percentCasualties + ".\n");
        } else {
            report.addManual("Miraculously, no workers died during construction.");
            report.addManual(prefix.CASUALTIES, "Affected Margin: Casualties 0.\n");
        }

        // determine final social impact
        int sign = isVictory ? 1 : -1;
        int totalSocialChange = (sign * percentAffect) - Math.abs(percentCasualties);

        boolean highStat = state.kingdomReputation >= 65 && state.kingdomStrength >= 65;

        int armyLeft = 0, moraleReduce = 0;
        int popLeft = 0, popJoin = 0;
        int armyJoin = 0;

        if (!highStat || !isVictory) {
            // the word spread, might be heard by army by the chance of 83% and if the reputation and strength are below than 65
            if (Math.random() < 0.83) {
                armyLeft = 1 + (int) (Math.random() * 21); // 1 to 21 soldiers might leave the country
                moraleReduce = (int) (armyLeft * 0.2);
                state.army = Math.max(0, state.army - armyLeft);
                state.militaryMorale = Math.clamp(state.militaryMorale - moraleReduce, 0, 100);
            }

            // same effect goes for the population as well
            if (Math.random() < 0.53) {
                popLeft = 1 + (int) (Math.random() * 16); // 1 to 15
                state.population = Math.max(state.population - popLeft, 0);
            }
        } else if (highStat && isVictory) {
            // if there's a victory than, might some be added
            if (Math.random() < 0.58) {
                popJoin = 1 + (int) (Math.random() * 13); // 1 to 12
                state.population = Math.max(state.population + popJoin, 0);
            }

            // High reputation + victory = soldiers want to guard the new building
            if (Math.random() < 0.30) {
                armyJoin = 1 + (int) (Math.random() * 5); // 1 to 5
                state.army = Math.max(0, state.army + armyJoin);
            }
        }

        if (armyLeft > 0) {
            report.addReasons_forConstruction(outcome, generalReasonType.ARMY_LEFT);
            report.addManual(prefix.ARMY, " Soldiers left, losing faith in the construction.");
            report.addManual("Affected Margin: Army -" + armyLeft + " soldiers left, Military Morale -" + moraleReduce + ".\n");
        } else if (armyJoin > 0) {
            report.addReasons_forConstruction(outcome, generalReasonType.ARMY_JOIN);
            report.addManual(prefix.ARMY, " New soldiers joined to guard the grand new building.");
            report.addManual("Affected Margin: Army +" + armyJoin + " soldiers joined.\n");
        } else {
            report.addManual(prefix.ARMY, " The army stayed at its post.");
            report.addManual("Affected Margin: Army 0.\n");
        }

        if (popLeft > 0) {
            report.addReasons_forConstruction(outcome, generalReasonType.POP_LEFT);
            report.addManual(prefix.POPULATION, " People left the town, frightened by the construction failure.");
            report.addManual("Affected Margin: Population -" + popLeft + ".\n");
        } else if (popJoin > 0) {
            report.addReasons_forConstruction(outcome, generalReasonType.POP_JOIN);
            report.addManual(prefix.POPULATION, " New settlers arrived, drawn by the new grand building.");
            report.addManual("Affected Margin: Population +" + popJoin + ".\n");
        } else {
            report.addManual(prefix.POPULATION, " No major population change from construction.");
            report.addManual("Affected Margin: Population 0 (excluding casualties).\n");
        }

        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        report.addManual(prefix.KINGDOM_AFFECT, isVictory
                ? " The people celebrate the new building."
                : " The failed construction weighs heavily on the kingdom.");
        report.addManual("Affected Margin: Happiness " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Faith " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Stability " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Public Approval " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Reputation " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Kingdom Strength " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Military Morale " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange + ".\n");

        // crime rate and unemployment: if victory go down, if defeat go up
        int crimeChange = -(sign * percentAffect) + Math.abs(percentCasualties);
        state.crimeRate = Math.clamp(state.crimeRate + crimeChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + crimeChange, 0, 100);

        if (crimeChange > 0) {
            report.addManual(prefix.KINGDOM_AFFECT, " With workers jobless and the site in ruins, crime and unemployment rise.");
        } else {
            report.addManual(prefix.KINGDOM_AFFECT, " With steady work on the new building, crime falls and people return to jobs.");
        }
        report.addManual("Affected Margin: Crime Rate " + (crimeChange >= 0 ? "+" : "") + crimeChange
                + ", Unemployment " + (crimeChange >= 0 ? "+" : "") + crimeChange + ".\n");


        consequence.clampAll();
    }

    private void resolveDevelopment(GameState state, int impact, boolean isNatural) throws IOException {
        report.addManual("\n=== DEVELOPMENT RESOLUTION ===\n");

        boolean isVictory = isNatural && (Math.random() < 0.5);

        warOutcome outcome;
        if (!isNatural) {
            outcome = warOutcome.LOSS;
        } else if (isVictory) {
            outcome = warOutcome.NATURAL_VICTORY;
        } else {
            outcome = warOutcome.NATURAL_LOSS;
        }

        if (outcome == warOutcome.NATURAL_VICTORY) {
            report.addManual(prefix.VICTORY, " Development succeeded! Scholars, teachers and artists flourished.");
        } else if (outcome == warOutcome.NATURAL_LOSS) {
            report.addManual(prefix.DEFEAT, " Development ended, but the project failed.");
        } else {
            report.addManual(prefix.DEFEAT, " Development collapsed early. It is a total loss.");
        }

        int constantValue = 100 + (int) (Math.random() * 151); // 100 to 250
        int absoluteImpact = Math.abs(impact);
        int rawEffect = Math.min((constantValue * absoluteImpact) / 100, LIMIT_VALUE);

        int percentageEffect = (int) (rawEffect * 0.2245);

        int sign = isVictory ? 1 : -1;

        int totalSocialChange = sign * percentageEffect;

        boolean highStat = (state.kingdomReputation >= 65 && state.kingdomStrength >= 65);

        int armyLeft = 0, moraleReduce = 0;
        int popLeft = 0, popJoin = 0;
        int armyJoin = 0;

        // Development has no battlefield casualties
        int rawCasualties = 0;

        // --- 1. CASUALTIES (always 0 in Development) ---
        report.addReasons_forDevelopment(outcome, generalReasonType.CASUALTIES);
        report.addManual(prefix.CASUALTIES, " No lives were lost during development.");
        report.addManual("Affected Margin: Casualties 0.");

        if (!highStat) {
            // if kingdom is weak or hated, people might leave

            // 73% percent chance army hear the rumors
            if (Math.random() < 0.73) {
                armyLeft = 1 + (int) (Math.random() * 15);
                moraleReduce = (int) (armyLeft * 0.2);

                state.army = Math.max(0, state.army - armyLeft);
                state.militaryMorale = Math.clamp(state.militaryMorale - moraleReduce, 0, 100);
            }

            // 63% chance
            if (Math.random() < 0.63) {
                // FIXED: was + popChange (added people when they should leave)
                // Now correctly POP_LEFT
                popLeft = 1 + (int) (Math.random() * 26); // 1 to 25
                state.population = Math.max(0, state.population - popLeft);
            }
        } else if (highStat && isVictory) {
            if (Math.random() < 0.68) {
                popJoin = 1 + (int) (Math.random() * 25);
                state.population = Math.max(0, state.population + popJoin);
            }

            if (Math.random() < 0.45) {
                armyJoin = 1 + (int) (Math.random() * 10);
                state.army = Math.max(0, state.army + armyJoin);
            }
        }

        if (armyLeft > 0) {
            report.addReasons_forDevelopment(outcome, generalReasonType.ARMY_LEFT);
            report.addManual(prefix.ARMY, " Soldiers left, doubting the King's focus on scholars.");
            report.addManual("Affected Margin: Army -" + armyLeft + " soldiers left, Military Morale -" + moraleReduce + ".");
        } else if (armyJoin > 0) {
            report.addReasons_forDevelopment(outcome, generalReasonType.ARMY_JOIN);
            report.addManual(prefix.ARMY, " New soldiers joined, proud to protect a progressing kingdom.");
            report.addManual("Affected Margin: Army +" + armyJoin + " soldiers joined.");
        } else {
            report.addManual(prefix.ARMY, " The army stayed at its post.");
            report.addManual("Affected Margin: Army 0.\n");
        }

        if (popLeft > 0) {
            report.addReasons_forDevelopment(outcome, generalReasonType.POP_LEFT);
            report.addManual(prefix.POPULATION, " People left, saying the King cares more for books than bread.");
            report.addManual("Affected Margin: Population -" + popLeft + ".");
        } else if (popJoin > 0) {
            report.addReasons_forDevelopment(outcome, generalReasonType.POP_JOIN);
            report.addManual(prefix.POPULATION, " New scholars, artists and settlers joined the kingdom.");
            report.addManual("Affected Margin: Population +" + popJoin + ".");
        } else {
            report.addManual(prefix.POPULATION, " No major population change from development.");
            report.addManual("Affected Margin: Population 0.");
        }

        // percentage stats
        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        report.addManual(prefix.KINGDOM_AFFECT, isVictory
                ? " The people celebrate the kingdom's progress."
                : " The failed development weighs heavily on the kingdom.");
        report.addManual("Affected Margin: Happiness " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Faith " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Stability " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Public Approval " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Reputation " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Kingdom Strength " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Military Morale " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange + ".");

        // crime rate and unemployment: if victory go down, if defeat go up
        int crimeChange = -totalSocialChange;
        state.crimeRate = Math.clamp(state.crimeRate + crimeChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + crimeChange, 0, 100);

        // --- 5. RATE (crime + unemployment) ---
        if (crimeChange > 0) {
            report.addManual(prefix.KINGDOM_AFFECT, " With scholars jobless and projects abandoned, crime and unemployment rise.");
        } else {
            report.addManual(prefix.KINGDOM_AFFECT, " With schools and temples thriving, crime falls and people return to work.");
        }
        report.addManual("Affected Margin: Crime Rate " + (crimeChange >= 0 ? "+" : "") + crimeChange
                + ", Unemployment " + (crimeChange >= 0 ? "+" : "") + crimeChange + ".");

        consequence.clampAll();
    }

    private void resolveTrade(GameState state, int impact, boolean isNatural) throws IOException {
        report.addManual("\n=== TRADE REPORT ===\n");

        boolean is_rewarding = false;

        if (state.activePhaseCost != null && state.activePhaseCost.containsKey("receive")) {
            Map<String, Object> recieveMap = (Map<String, Object>) state.activePhaseCost.get("receive");

            if (recieveMap != null) {
                // check if the gold in the recieve map and value greater than 0
                if (recieveMap.containsKey("gold") && ((Number) recieveMap.get("gold")).intValue() > 0) {
                    is_rewarding = true;
                }
                // same for treasury
                else if (recieveMap.containsKey("treasury") && ((Number) recieveMap.get("treasury")).intValue() > 0) {
                    is_rewarding = true;
                }
            }
        }

        boolean isVictory = isNatural && (Math.random() < 0.5);

        // --- SAME 3-OUTCOME LOGIC AS ALL OTHER PHASES ---
        warOutcome outcome;
        if (!isNatural) {
            outcome = warOutcome.LOSS;
        } else if (isVictory) {
            outcome = warOutcome.NATURAL_VICTORY;
        } else {
            outcome = warOutcome.NATURAL_LOSS;
        }

        if (outcome == warOutcome.NATURAL_VICTORY) {
            report.addManual(prefix.VICTORY, " The trade mission succeeded!");
        } else if (outcome == warOutcome.NATURAL_LOSS) {
            report.addManual(prefix.DEFEAT, " The trade mission ended, but the deal failed.");
        } else {
            report.addManual(prefix.DEFEAT, " The trade route collapsed early. It is a total loss.");
        }

        // Trade reason is direct - no categories, just one story line
        // This is the ONLY reason call in Trade, and it affects population/social story
        report.addReasons_forTrade(outcome);

        if (is_rewarding) {
            report.addManual(prefix.LOOT, " This trade was rewarding - gold / treasury was expected in return.");
            report.addManual("Affected Margin: Trade Type = Rewarding.\n");
        } else {
            report.addManual(prefix.LOOT, " This trade was not rewarding - no gold / treasury was promised.");
            report.addManual("Affected Margin: Trade Type = Non-Rewarding.\n");
        }

        // base values
        int constantValue = 100 + (int) (Math.random() * 151); // 100 to 250
        int absoluteImpact = Math.abs(impact);
        int rawAffect = Math.min((constantValue * absoluteImpact) / 100, LIMIT_VALUE);

        int percentageAffect = (int) (rawAffect * 0.2761);

        int totalSocialChange = 0;
        if (isVictory && is_rewarding) {
            // For rewarding trades the social boost is halved
            totalSocialChange = (int) (percentageAffect * 0.5);
            report.addManual(prefix.KINGDOM_AFFECT, " Rewarding trade, but merchants take a cut - social boost is halved.");
            report.addManual("Affected Margin: Base Social Affect +" + percentageAffect + " -> Halved to +" + totalSocialChange + ".\n");
        } else {
            totalSocialChange = -percentageAffect;
            if (isVictory) {
                report.addManual(prefix.KINGDOM_AFFECT, " Trade succeeded but was not rewarding, so morale still drops.");
            } else {
                report.addManual(prefix.KINGDOM_AFFECT, " Trade failed, morale drops.");
            }
            report.addManual("Affected Margin: Social Affect " + totalSocialChange + ".\n");
        }

        // apply percentage stats
        state.happiness = Math.clamp(state.happiness + totalSocialChange, 0, 100);
        state.faith = Math.clamp(state.faith + totalSocialChange, 0, 100);
        state.stability = Math.clamp(state.stability + totalSocialChange, 0, 100);
        state.publicApproval = Math.clamp(state.publicApproval + totalSocialChange, 0, 100);
        state.kingdomReputation = Math.clamp(state.kingdomReputation + totalSocialChange, 0, 100);
        state.kingdomStrength = Math.clamp(state.kingdomStrength + totalSocialChange, 0, 100);
        state.militaryMorale = Math.clamp(state.militaryMorale + totalSocialChange, 0, 100);

        // --- SOCIAL CHANGE ---
        report.addManual(prefix.KINGDOM_AFFECT, isVictory && is_rewarding
                ? " The successful rewarding trade lifts the kingdom's spirits."
                : " The failed trade weighs on the kingdom.");
        report.addManual("Affected Margin: Happiness " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Faith " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Stability " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Public Approval " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Reputation " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Kingdom Strength " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange
                + ", Military Morale " + (totalSocialChange >= 0 ? "+" : "") + totalSocialChange + ".\n");

        // crime rate and unemployment: if victory go down, if defeat go up
        int crimeChange = -totalSocialChange;
        state.crimeRate = Math.clamp(state.crimeRate + crimeChange, 0, 100);
        state.Unemployment = Math.clamp(state.Unemployment + crimeChange, 0, 100);

        // --- RATE ---
        if (crimeChange > 0) {
            report.addManual(prefix.KINGDOM_AFFECT, " With trade failing, merchants jobless, crime and unemployment rise.");
        } else {
            report.addManual(prefix.KINGDOM_AFFECT, " With trade thriving, crime falls and merchants return to work.");
        }
        report.addManual("Affected Margin: Crime Rate " + (crimeChange >= 0 ? "+" : "") + crimeChange
                + ", Unemployment " + (crimeChange >= 0 ? "+" : "") + crimeChange + ".\n");

        // strong kingdom + rewarding trade = Population Growth
        // FIXED: you had kingdomStrength >= 65 && kingdomStrength >= 65 (twice)
        // Should be Reputation + Strength
        int settlers = 0;
        if (isVictory && is_rewarding && state.kingdomReputation >= 65 && state.kingdomStrength >= 65) {
            if (Math.random() < 0.43) {
                settlers = 5 + (int) (Math.random() * 15);
                state.population += settlers;
            }
        }

        // --- POPULATION - the ONLY direct population effect in Trade ---
        // This is why TradeReasons.json is direct: the reason IS the population story
        if (settlers > 0) {
            report.addManual(prefix.POPULATION, " Strong kingdom + rewarding trade attracted new settlers.");
            report.addManual("Affected Margin: Population +" + settlers + " settlers joined.\n");
        } else if (isVictory && is_rewarding) {
            report.addManual(prefix.POPULATION, " Trade was rewarding, but kingdom was not strong enough to attract settlers.");
            report.addManual("Affected Margin: Population 0 (Reputation or Strength < 65, or 43% chance failed).\n");
        } else {
            report.addManual(prefix.POPULATION, " No settlers arrived from this trade.");
            report.addManual("Affected Margin: Population 0.\n");
        }

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