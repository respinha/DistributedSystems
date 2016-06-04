package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefRefSite extends IRefSite {

    Response announceNewGameRefSite() throws RemoteException;

    Response declareGameWinner(int ntrials, int ropePos, boolean knockout) throws RemoteException;
    Response declareMatchWinner() throws RemoteException;

    Response startTrialRefSite() throws InterruptedException, RemoteException;

    Response assertTrialDecisionRefSite(int currentTrial, boolean knockout) throws RemoteException;

    Response startTheMatch() throws RemoteException;

    Response refHasMoreOperations() throws RemoteException;

    void closeRefSite() throws RemoteException;
}
