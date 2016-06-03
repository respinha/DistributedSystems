package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.Remote;

public interface IRefPlay extends Remote {

    int getRopePos();
    int startTrialPlayground();
    boolean assertTrialDecisionPlayground() throws InterruptedException;
    void announceNewGamePlayground();

}
