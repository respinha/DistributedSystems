package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Referee;

public interface IRefPlay extends IPlayground{

    int startTrial(Referee referee);
    boolean assertTrialDecision() throws InterruptedException;
    void announceNewGame();

}
