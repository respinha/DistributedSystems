package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IPlaygroundGenRep extends IGeneralRepository {


    Response updateContestantPosition(int contestantID, int teamID, int position) throws RemoteException;
    Response updateTrial(int trial) throws RemoteException;
    Response updateRopePosition(int ropePos) throws RemoteException;
}
