package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * This interface contains all the methods required by the coach to interact with a remote playground Server.
 */
public interface ICoachPlay extends IPlayground {

    /**
     * Called by coach in the end of a trial to change strategy based on what happened during the previous trial.
     * @param clientClock Coach's current clock.
     * @param teamID Coach's team.
     * @return Updated clock + Previous trial result + Kockout (true if game was won by knockout) + Current trial
     * @throws InterruptedException The thread was interrupted.
     * @throws RemoteException A remote exception occurred.
     */
    Response reviewNotes(VectClock clientClock, int teamID) throws InterruptedException, RemoteException;

    /**
     * Move coach to the playground and wait for all the contestants to arrive.
     * @param clientClock Coach's current clock.
     * @param teamID Coach's team.
     * @return Updated clock.
     * @throws InterruptedException The thread was interrupted.
     * @throws RemoteException A remote exception occurred.
     */
    Response moveCoachToPlayground(VectClock clientClock, int teamID) throws InterruptedException, RemoteException;

    /**
     * Close a connection to playground.
     * @throws RemoteException The playground was closed.
     */
    void closePlaygroundConnection() throws RemoteException;
}
