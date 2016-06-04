package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGeneralRepository extends Remote {

    Response updateCoachState(VectClock clientClock, String state, int teamID) throws RemoteException;

    Response updateContestantState(VectClock clientClock, String state, int gameMemberID, int teamID) throws RemoteException;

    void updateRefState(VectClock clientClock, String state) throws RemoteException;
    boolean requestToDie() throws RemoteException;

}
