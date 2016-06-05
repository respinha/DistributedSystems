package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IBenchGenRep extends IGeneralRepository {

    void removeContestantFromPosition(VectClock clientClock, int team, int pos) throws RemoteException;

    void updateStrengths(VectClock clientClock, int teamID, int[] strength) throws RemoteException;

    void updateClock(VectClock vectClock) throws RemoteException;
}
