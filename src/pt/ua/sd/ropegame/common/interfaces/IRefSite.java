package pt.ua.sd.ropegame.common.interfaces;

public interface IRefSite {

    void announceNewGameRefSite();

    boolean declareGameWinner(int ntrials, int ropePos, boolean knockout);
    void declareMatchWinner();

    void startTrialRefSite() throws InterruptedException;

    boolean assertTrialDecisionRefSite(int currentTrial, boolean knockout);

    void startTheMatch();

    boolean refHasMoreOperations();

    void closeRefSite();
}
