package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.Remote;

public interface IRefSite extends Remote {

    void announceNewGameRefSite();

    boolean declareGameWinner(int ntrials, int ropePos, boolean knockout);
    void declareMatchWinner();

    void startTrialRefSite() throws InterruptedException;

    boolean assertTrialDecisionRefSite(int currentTrial, boolean knockout);

    void startTheMatch();

    boolean refHasMoreOperations();

    void closeRefSite();
}
