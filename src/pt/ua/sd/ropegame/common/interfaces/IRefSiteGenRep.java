package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefSiteGenRep extends IGeneralRepository {

    Response updateGame(int game) throws RemoteException;
    Response updateMatchWinner(int winner, int[] results) throws RemoteException;
    Response updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout) throws RemoteException;
    void generateLogFile() throws RemoteException;

}
