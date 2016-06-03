package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IRefSiteGenRep extends IGeneralRepository {

    void updateGame(int game) throws RemoteException;
    void updateMatchWinner(int winner, int[] results) throws RemoteException;
    void updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout) throws RemoteException;
    void generateLogFile() throws RemoteException;

}
