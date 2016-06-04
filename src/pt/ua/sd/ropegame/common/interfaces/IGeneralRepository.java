package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGeneralRepository extends Remote {

    Response updateCoachState(String state, int teamID) throws RemoteException;

    Response updateContestantState(String state, int gameMemberID, int teamID) throws RemoteException;

    void updateRefState(String state) throws RemoteException;
    boolean requestToDie() throws RemoteException;

}
