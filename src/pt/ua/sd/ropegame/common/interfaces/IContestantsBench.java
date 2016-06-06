package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * This interface contains all the methods a Bench-like object must implement to allow the contestants to interact with a remote bench.
 */
public interface IContestantsBench extends IBench {

    /**
     * Checks to see if a contestant was picked by his coach.
     * @param clientClock Contestant's current clock.
     * @param gameMemberID Contestant's number.
     * @param teamID Contestant's team.
     * @return updated clock + strength + has more operations (boolean)
     * @throws InterruptedException The thread was interrupted.
     * @throws RemoteException A remote exception has occurred.
     */
    Response waitForContestantCall(VectClock clientClock, int gameMemberID, int teamID) throws InterruptedException, RemoteException;

    /**
     * Seat a contestant at the bench.
     * @param clientClock Contestant's current clock.
     * @param gameMemberID Contestant's number.
     * @param teamID Contestant's team.
     * @param strength Contestant's strength.
     * @param position Contestant's position.
     * @param gameOver True if game is over.
     * @return Updated clock + new state.
     * @throws RemoteException A remote exception has occurred.
     */
    Response seatDown(VectClock clientClock, int gameMemberID, int teamID, int strength, int position, boolean gameOver) throws RemoteException;

    /**
     * Check if the contestant has more operations.
     * @return updated clock + true if contestant has more operations.
     * @throws RemoteException A remote exception has occurred.
     */
    Response contestantsHaveMoreOperations() throws RemoteException;

    /**
     * Close connection to a remote bench.
     * @throws RemoteException The bench was shut down or another remote exception has occurred.
     */
    void closeBenchConnection() throws RemoteException;
}
