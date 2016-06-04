package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefRefSite extends IRefSite {

    Response announceNewGameRefSite(VectClock clientClock) throws RemoteException;

    Response declareGameWinner(VectClock clientClock, int ntrials, int ropePos, boolean knockout) throws RemoteException;
    Response declareMatchWinner(VectClock clientClock) throws RemoteException;

    Response startTrialRefSite(VectClock clientClock) throws InterruptedException, RemoteException;

    Response startTheMatch(VectClock clientClock) throws RemoteException;

    Response refHasMoreOperations() throws RemoteException;

    void closeRefSite() throws RemoteException;
}
