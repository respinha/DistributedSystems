package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefSiteGenRep extends IGeneralRepository {

    void updateGame(VectClock clientClock, int game) throws RemoteException;
    void updateMatchWinner(VectClock clientClock, int winner, int[] results) throws RemoteException;
    void updateGameWinner(VectClock clientClock, int currentGame, int gameWinner, int ntrials, boolean knockout) throws RemoteException;

}
