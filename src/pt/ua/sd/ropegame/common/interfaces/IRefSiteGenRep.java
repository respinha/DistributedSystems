package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefSiteGenRep extends IGeneralRepository {

    Response updateGame(VectClock clientClock, int game) throws RemoteException;
    Response updateMatchWinner(VectClock clientClock, int winner, int[] results) throws RemoteException;
    Response updateGameWinner(VectClock clientClock, int currentGame, int gameWinner, int ntrials, boolean knockout) throws RemoteException;

}
