package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IRefPlay extends IPlayground {

    int getRopePos() throws RemoteException;
    int startTrialPlayground() throws RemoteException;
    boolean assertTrialDecisionPlayground() throws InterruptedException, RemoteException;
    void announceNewGamePlayground() throws RemoteException;

}
