package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IRefRefSite extends IRefSite {

    void announceNewGameRefSite() throws RemoteException;

    boolean declareGameWinner(int ntrials, int ropePos, boolean knockout) throws RemoteException;
    void declareMatchWinner() throws RemoteException;

    void startTrialRefSite() throws InterruptedException, RemoteException;

    boolean assertTrialDecisionRefSite(int currentTrial, boolean knockout) throws RemoteException;

    void startTheMatch() throws RemoteException;

    boolean refHasMoreOperations() throws RemoteException;

    void closeRefSite() throws RemoteException;
}
