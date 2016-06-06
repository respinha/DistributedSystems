package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * This interface contains all the methods required by the coach to interact with a remote Bench Server.
 */
public interface ICoachBench extends ITeamBench {

    /**
     * Updates a coach's team players strengths.
     * @param teamID The coach's team.
     * @param trial Current trial.
     * @param clientClock Coach's current clock.
     * @param knockout True if game was finished due to knockout.
     * @return Updated clock + new Coach state.
     * @throws RemoteException A remote exception occurred.
     */
    Response reviewNotes(VectClock clientClock, int teamID, int trial, boolean knockout) throws RemoteException;

    /**
     * Picks team contestants based on coach's current strategy.
     * @param clientClock Coach's current clock.
     * @param teamID The coach's team.
     * @param strategy The coach's current strategy.
     * @return Updated clock.
     * @throws RemoteException A remote exception occurred.
     */
    Response callContestants(VectClock clientClock, int teamID, String strategy) throws RemoteException;

    /**
     * Wait for the referee to call this coach.
     * @param clientClock Coach's current clock.
     * @return Updated clock.
     * @throws InterruptedException The coach thread was interrupted.
     * @throws RemoteException A remote exception occurred.
     */
    Response waitForCoachCall(VectClock clientClock) throws InterruptedException, RemoteException;

    /**
     * Checks if a coach has more operations.
     * @return updated clock + true if coach has more operations; false otherwise.
     * @throws RemoteException A remote exception occurred. 
     */
    Response coachesHaveMoreOperations() throws RemoteException;

}
