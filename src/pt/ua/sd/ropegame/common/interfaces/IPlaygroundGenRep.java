package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import java.rmi.RemoteException;

/**
 * An interface every General Repository-like object must implement to allow a connection between a playground and a remote genrep.
 */
public interface IPlaygroundGenRep extends IGeneralRepository {

    /**
     * Update Contestant's current position.
     * @param clientClock This object's current clock.
     * @param contestantID Contestant's number.
     * @param teamID Contestant's Team.
     * @param position Contestant's position.
     * @throws RemoteException A remote exception occurred.
     */
    void updateContestantPosition(VectClock clientClock, int contestantID, int teamID, int position) throws RemoteException;

    /**
     * Update current trial.
     * @param clientClock This object's current clock.
     * @param trial New trial value.
     * @throws RemoteException A remote exception occurred.
     */
    void updateTrial(VectClock clientClock, int trial) throws RemoteException;

    /**
     * Update rope position.
     * @param clientClock This object's current clock.
     * @param ropePos New rope position.
     * @throws RemoteException A remote exception occurred.
     */
    void updateRopePosition(VectClock clientClock, int ropePos) throws RemoteException;
}
