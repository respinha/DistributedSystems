package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import java.rmi.RemoteException;

/**
 * Interface a General Repository-like object must implement to allow a bench connection to a remote genrep.
 */
public interface IBenchGenRep extends IGeneralRepository {

    /**
     * Inform the Bench a contestant has left the playground.
     * @param clientClock The contestant's current clock.
     * @param team The contestant's team.
     * @param pos The contestant's current position.
     * @throws RemoteException A remote exception occurred.
     */
    void removeContestantFromPosition(VectClock clientClock, int team, int pos) throws RemoteException;

    /**
     * Update a contestant's strength.
     * @param clientClock The contestant's current clock.
     * @param teamID The contestant's team.
     * @param strength The contestant's new strength.
     * @throws RemoteException A remote exception occurred.
     */
    void updateStrengths(VectClock clientClock, int teamID, int[] strength) throws RemoteException;

    /**
     * Send a message to General Repository informing a clock was updated.
     * @param vectClock The bench's current clock.
     * @throws RemoteException A remote exception occurred.
     */
    void updateClock(VectClock vectClock) throws RemoteException;
}
