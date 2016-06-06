package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface every remote general repository must implement.
 */
public interface IGeneralRepository extends Remote {

    /**
     * Update Coach State.
     * @param clientClock Remote client's current clock.
     * @param state Coach's new state.
     * @param teamID Coach's team.
     * @throws RemoteException A remote exception occurred.
     */
    void updateCoachState(VectClock clientClock, String state, int teamID) throws RemoteException;

    /**
     * Update Contestant state.
     * @param clientClock Remote client's current clock.
     * @param state Contestant's new state.
     * @param gameMemberID Contestant's number.
     * @param teamID Contestant's team.
     * @throws RemoteException A remote exception occurred.
     */
    void updateContestantState(VectClock clientClock, String state, int gameMemberID, int teamID) throws RemoteException;

    /**
     * Update Referee State.
     * @param clientClock Remote client's current clock.
     * @param state Referee's new state.
     * @throws RemoteException A remote exception occurred.
     */
    void updateRefState(VectClock clientClock, String state) throws RemoteException;

    /**
     * Close connection to General Repository.
     * @throws RemoteException A remote exception occurred.
     */
    void closeConnection() throws RemoteException;

}
