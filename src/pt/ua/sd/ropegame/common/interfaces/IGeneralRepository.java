package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGeneralRepository extends Remote {

    void updateCoachState(VectClock clientClock, String state, int teamID) throws RemoteException;

    void updateContestantState(VectClock clientClock, String state, int gameMemberID, int teamID) throws RemoteException;

    void updateRefState(VectClock clientClock, String state) throws RemoteException;
    void closeConnection() throws RemoteException;

}
