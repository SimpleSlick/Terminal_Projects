package Engine;

import Game.GameState;

public class PhaseResolution {
    private final CalculateConsequence consequence;

    public PhaseResolution(GameState state){
        this.consequence = new CalculateConsequence(state);
    }
}
