package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IBenchGenRep extends IGeneralRepository {

    Response removeContestantFromPosition(VectClock clientClock, int team, int pos) throws RemoteException;

    Response updateStrengths(VectClock clientClock, int teamID, int[] strength) throws RemoteException;
}
