package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IBenchGenRep extends IGeneralRepository {

    Response removeContestantFromPosition(int team, int pos) throws RemoteException;

    Response updateStrengths(int teamID, int[] strength) throws RemoteException;
}
