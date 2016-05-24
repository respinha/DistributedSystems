package pt.ua.sd.ropegame.common.interfaces;

public interface IRefPlay {

    int getRopePos();
    int startTrialPlayground();
    boolean assertTrialDecisionPlayground() throws InterruptedException;
    void announceNewGamePlayground();

}
