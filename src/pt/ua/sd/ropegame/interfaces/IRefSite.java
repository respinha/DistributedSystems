package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Referee;

public interface IRefSite {

    void announceNewGame(Referee referee) throws InterruptedException;

    boolean declareGameWinner(Referee referee, int currentTrial, boolean knockout);
    void declareMatchWinner(Referee referee);

    void startTrial() throws InterruptedException;

    boolean assertTrialDecision(Referee referee, int currentTrial, int ropePos, boolean knockout);

    void startTheMatch(Referee referee);

    boolean refHasMoreOperations();
}
