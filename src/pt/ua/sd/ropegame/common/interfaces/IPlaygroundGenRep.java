package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IPlaygroundGenRep extends IGeneralRepository {


    void updateContestantPosition(VectClock clientClock, int contestantID, int teamID, int position) throws RemoteException;
    void updateTrial(VectClock clientClock, int trial) throws RemoteException;
    void updateRopePosition(VectClock clientClock, int ropePos) throws RemoteException;
}
