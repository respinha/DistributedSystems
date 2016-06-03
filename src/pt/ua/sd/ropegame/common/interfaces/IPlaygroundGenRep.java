package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IPlaygroundGenRep extends IGeneralRepository {


    void updateContestantPosition(int contestantID, int teamID, int position) throws RemoteException;
    void updateTrial(int trial) throws RemoteException;
    void updateRopePosition(int ropePos) throws RemoteException;
}
