package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IBenchGenRep extends IGeneralRepository {

    void removeContestantFromPosition(int team, int pos) throws RemoteException;

    void updateStrengths(int teamID, int[] strength) throws RemoteException;
}
